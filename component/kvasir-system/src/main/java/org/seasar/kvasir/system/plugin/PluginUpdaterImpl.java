package org.seasar.kvasir.system.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataRetrievalException;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.metadata.ResolutionGroup;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataResolutionException;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.ArtifactNotFoundException;
import org.apache.maven.artifact.resolver.ArtifactResolutionException;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderException;
import org.apache.maven.embedder.MavenEmbedderLogger;
import org.apache.maven.model.Model;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.kvasir.base.Globals;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.base.plugin.PluginAlfrSettings;
import org.seasar.kvasir.base.plugin.PluginCandidate;
import org.seasar.kvasir.base.plugin.PluginUpdater;
import org.seasar.kvasir.base.plugin.PluginVersions;
import org.seasar.kvasir.base.plugin.PluginVersionsImpl;
import org.seasar.kvasir.base.plugin.RemoteRepository;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceUtils;
import org.seasar.kvasir.util.io.impl.ZipReaderResource;


/**
 * @author manhole
 */
public class PluginUpdaterImpl
    implements PluginUpdater
{

    private static final KvasirLog log_ = KvasirLogFactory
        .getLog(PluginUpdaterImpl.class);

    private static final String TYPE = "zip";

    private static final String OUR_PACKAGING = "kvasir-plugin";

    private static final String OUR_TYPE = "zip";

    private PluginAlfr pluginAlfr_;

    private Kvasir kvasir_;


    @SuppressWarnings("unchecked")
    public List<Version> getAvailableVersions(final String pluginId,
        final boolean excludeSnapshot)
    {
        final Metadata metadata = getMetadata(pluginId);
        final Versioning versioning = metadata.getVersioning();
        if (versioning == null) {
            return new ArrayList<Version>();
        }
        final List<String> versions = versioning.getVersions();
        final List<Version> v = new ArrayList<Version>();
        for (final String versionStr : versions) {
            final Version version = new Version(versionStr);
            if (excludeSnapshot && version.isSnapshot()) {
                continue;
            }
            v.add(version);
        }
        Collections.sort(v);
        return v;
    }


    public Version getNewerVersion(final PluginDescriptor pluginDescriptor)
    {
        final Metadata metadata = getMetadata(pluginDescriptor.getId());
        final Versioning versioning = metadata.getVersioning();
        final Version newestVersion = getNewestVersion(versioning);
        if (pluginDescriptor.getVersion().compareTo(newestVersion) < 0) {
            return newestVersion;
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    private List<Version> getNewerVersions(
        final PluginDescriptor pluginDescriptor, final boolean excludeSnapshot)
    {
        final Metadata metadata = getMetadata(pluginDescriptor.getId());
        final Version currentVersion = pluginDescriptor.getVersion();
        final Versioning versioning = metadata.getVersioning();
        // レポジトリにモノが無い場合はnullが返る
        if (versioning == null) {
            return Collections.emptyList();
        }
        final List<String> versions = versioning.getVersions();
        final ArrayList<Version> l = new ArrayList<Version>();
        for (final String version : versions) {
            final Version v = new Version(version);
            final int compared = currentVersion.compareTo(v);
            if ((compared == 0) && currentVersion.isSnapshot()
                && !excludeSnapshot) {
                l.add(v);
            } else if (compared < 0) {
                if (v.isSnapshot() && excludeSnapshot) {
                } else {
                    l.add(v);
                }
            }
        }
        Collections.sort(l);
        return l;
    }


    public PluginVersions[] getUpdatedPlugins(final boolean excludeSnapshot)
    {
        final PluginDescriptor[] allPluginDescriptors = getPluginAlfr()
            .getAllPluginDescriptors();
        final ArrayList<PluginVersions> l = new ArrayList<PluginVersions>();
        for (final PluginDescriptor pluginDescriptor : allPluginDescriptors) {
            final PluginVersionsImpl pluginVersions = new PluginVersionsImpl();
            pluginVersions.setId(pluginDescriptor.getId());
            final List<Version> newerVersions = getNewerVersions(
                pluginDescriptor, excludeSnapshot);
            if (newerVersions.size() == 0) {
                continue;
            }
            pluginVersions.setVersions(newerVersions
                .toArray(new Version[newerVersions.size()]));
            l.add(pluginVersions);
        }
        return l.toArray(new PluginVersions[l.size()]);
    }


    @SuppressWarnings("unchecked")
    public void installPlugin(final String pluginId, final String version,
        final boolean excludeSnapshot)
    {
        final Resource pluginsStagingDirectory = getPluginsStagingDirectory();
        final MavenEmbedder maven = createMavenEmbedder();
        try {
            maven.start();

            final PluginAlfr alfr = getPluginAlfr();

            final PluginAlfrSettings settings = alfr.getSettings();
            final ArtifactRepository localRepository = createLocalRepository(
                settings, maven);
            final List<ArtifactRepository> remoteRepositories = createEnabledRemoteRepositories(
                settings, maven);

            /*
             * snapshotを含めないということは、SNAPSHOTをSNAPSHOTとしてではなく
             * 正式なバージョン番号して扱うと言うこと。
             */
            final List<Artifact> installArtifacts = collectArtifacts(pluginId,
                version, maven, localRepository, remoteRepositories,
                excludeSnapshot);

            for (final Artifact a : installArtifacts) {
                maven.resolve(a, remoteRepositories, localRepository);
                extractPluginArtifactTo(a, pluginsStagingDirectory);
            }

            maven.stop();

        } catch (final MavenEmbedderException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final ComponentLookupException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final ArtifactResolutionException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final ArtifactNotFoundException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final IOException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final ArtifactMetadataRetrievalException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final XmlPullParserException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        }
    }


    private Resource getPluginsStagingDirectory()
    {
        final Resource stagingDirectory = getKvasir().getStagingDirectory();
        final Resource pluginsStagingDirectory = stagingDirectory
            .getChildResource(Globals.PLUGINS_DIR);
        if (!pluginsStagingDirectory.exists()) {
            if (!pluginsStagingDirectory.mkdir()) {
                // TODO
                throw new RuntimeException("can't create directory: "
                    + pluginsStagingDirectory);
            }
        }
        return pluginsStagingDirectory;
    }


    @SuppressWarnings("unchecked")
    private List<Artifact> collectArtifacts(final String pluginId,
        final String version, final MavenEmbedder maven,
        final ArtifactRepository localRepository,
        final List<ArtifactRepository> remoteRepositories,
        final boolean snapshotAsOfficial)
        throws ComponentLookupException, ArtifactMetadataRetrievalException,
        XmlPullParserException, FileNotFoundException, IOException,
        ArtifactResolutionException, ArtifactNotFoundException
    {
        final List<Artifact> installArtifacts = new ArrayList<Artifact>();
        final Embedder embedder = getEmbedder(maven);
        final Artifact artifact = maven.createArtifact(pluginId, pluginId,
            version, null, TYPE);

        final ArtifactMetadataSource artifactMetadataSource = (ArtifactMetadataSource)embedder
            .lookup(ArtifactMetadataSource.class.getName());
        final ResolutionGroup resolutionGroup = artifactMetadataSource
            .retrieve(artifact, localRepository, remoteRepositories);

        // このartifactのpom.xmlファイル自身
        final Artifact pomArtifact = resolutionGroup.getPomArtifact();
        final File pom = pomArtifact.getFile();
        final Model model = maven.readModel(pom);
        if (!OUR_PACKAGING.equals(model.getPackaging())) {
            // TODO 例外にするのが良いかなぁ
            return installArtifacts;
        }

        installArtifacts.add(artifact);

        /*
         * 依存先も解決に行く。
         */
        final ArtifactCollector artifactCollector = (ArtifactCollector)embedder
            .lookup(ArtifactCollector.class.getName());
        final ArtifactResolutionResult artifactResolutionResult = artifactCollector
            .collect(resolutionGroup.getArtifacts(), artifact, localRepository,
                remoteRepositories, artifactMetadataSource, null, Collections
                    .emptyList());
        final Set<Artifact> dependencyArtifacts = artifactResolutionResult
            .getArtifacts();
        for (final Artifact dependencyArtifact : dependencyArtifacts) {
            final ResolutionGroup dependencyResolutionGroup = artifactMetadataSource
                .retrieve(dependencyArtifact, localRepository,
                    remoteRepositories);
            final Artifact dependencyPomArtifact = dependencyResolutionGroup
                .getPomArtifact();
            /*
             * 既にDefaultMavenProjectBuilderにキャッシュされている場合は
             * pomファイルがnullになるようだ。
             * その場合はしょうがないのでEmbedderから取得にいく。
             */
            File dependencyPom = dependencyPomArtifact.getFile();
            if (dependencyPom == null) {
                maven.resolve(dependencyPomArtifact, remoteRepositories,
                    localRepository);
                dependencyPom = dependencyPomArtifact.getFile();
                if (dependencyPom == null) {
                    continue;
                }
            }
            final Model dependencyModel = maven.readModel(dependencyPom);
            if (!OUR_PACKAGING.equals(dependencyModel.getPackaging())) {
                continue;
            }
            if (!OUR_TYPE.equals(dependencyArtifact.getType())) {
                continue;
            }
            if (isInstalled(dependencyArtifact, snapshotAsOfficial)) {
                continue;
            }

            installArtifacts.add(dependencyArtifact);
        }
        return installArtifacts;
    }


    private boolean isInstalled(final Artifact candidate,
        final boolean snapshotAsOfficial)
    {
        final String artifactId = candidate.getArtifactId();
        final PluginDescriptor installedDescriptor = findInstalledPlugin(artifactId);
        if (installedDescriptor == null) {
            return false;
        }

        final Version candidateVersion = new Version(candidate.getVersion());
        final Version installedVersion = installedDescriptor.getVersion();
        final int compared = installedVersion.compareTo(candidateVersion);
        if ((0 < compared)) {
            return true;
        } else if (compared == 0) {
            if (!candidateVersion.isSnapshot()) {
                return true;
            } else {
                if (snapshotAsOfficial) {
                    return true;
                }
            }
        }
        return false;
    }


    public PluginCandidate getInstallCandidate(final String pluginId,
        final String version, final boolean excludeSnapshot)
    {
        final PluginCandidateImpl candidate = new PluginCandidateImpl();

        final MavenEmbedder maven = createMavenEmbedder();
        try {
            maven.start();

            final PluginAlfrSettings settings = getPluginAlfrSettings();
            final ArtifactRepository localRepository = createLocalRepository(
                settings, maven);
            final List<ArtifactRepository> remoteRepositories = createEnabledRemoteRepositories(
                settings, maven);

            final List<Artifact> installArtifacts = collectArtifacts(pluginId,
                version, maven, localRepository, remoteRepositories,
                excludeSnapshot);

            {
                final Artifact a = installArtifacts.get(0);
                candidate.setId(a.getArtifactId());
                candidate.setVersion(new Version(a.getVersion()));
            }
            final List<PluginCandidate> l = new ArrayList<PluginCandidate>();
            for (int i = 1; i < installArtifacts.size(); i++) {
                final Artifact a = installArtifacts.get(i);
                final PluginCandidateImpl dependency1 = new PluginCandidateImpl();
                dependency1.setId(a.getArtifactId());
                dependency1.setVersion(new Version(a.getVersion()));
                final PluginCandidate dependency = dependency1;
                l.add(dependency);
            }
            candidate.setDependencies(l.toArray(new PluginCandidate[l.size()]));

            maven.stop();
            return candidate;

        } catch (final MavenEmbedderException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final ComponentLookupException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final ArtifactResolutionException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final ArtifactNotFoundException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final IOException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final ArtifactMetadataRetrievalException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final XmlPullParserException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        }
    }


    private void extractPluginArtifactTo(final Artifact artifact,
        final Resource pluginsDir)
        throws ZipException, IOException
    {
        final File file = artifact.getFile();
        final ZipFile zipFile = new ZipFile(file);
        try {
            final ZipReaderResource zipReaderResource = new ZipReaderResource(
                zipFile);
            ResourceUtils.copy(zipReaderResource, pluginsDir);
        } finally {
            zipFile.close();
        }
    }


    private Metadata getMetadata(final String pluginId)
    {
        final RepositoryMetadata repositoryMetadata = getRepositoryMetadata(pluginId);
        final Metadata metadata = repositoryMetadata.getMetadata();
        return metadata;
    }


    @SuppressWarnings("unchecked")
    private Version getNewestVersion(final Versioning versioning)
    {
        final List<String> versions = versioning.getVersions();
        Version newest = null;
        for (final String version : versions) {
            final Version v = new Version(version);
            if ((newest == null) || (newest.compareTo(v) < 0)) {
                newest = v;
            }
        }
        return newest;
    }


    private RepositoryMetadata getRepositoryMetadata(final String pluginId)
    {
        final PluginAlfrSettings settings = getPluginAlfrSettings();
        final MavenEmbedder maven = createMavenEmbedder();
        try {
            maven.start();
            final ArtifactRepository localRepository = createLocalRepository(
                settings, maven);
            final List<ArtifactRepository> remoteRepositories = createEnabledRemoteRepositories(
                settings, maven);
            final Artifact artifact = maven.createArtifact(pluginId, pluginId,
                Artifact.RELEASE_VERSION, null, TYPE);

            final Embedder embedder = getEmbedder(maven);

            final RepositoryMetadataManager repositoryMetadataManager = (RepositoryMetadataManager)embedder
                .lookup(RepositoryMetadataManager.class.getName());
            final RepositoryMetadata repositoryMetadata = new ArtifactRepositoryMetadata(
                artifact);
            repositoryMetadataManager.resolve(repositoryMetadata,
                remoteRepositories, localRepository);

            maven.stop();

            return repositoryMetadata;
        } catch (final MavenEmbedderException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final ComponentLookupException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        } catch (final RepositoryMetadataResolutionException ex) {
            // TODO Auto-generated catch block
            throw new RuntimeException(ex);
        }
    }


    private List<ArtifactRepository> createEnabledRemoteRepositories(
        final PluginAlfrSettings settings, final MavenEmbedder maven)
        throws ComponentLookupException
    {
        final List<ArtifactRepository> l = new ArrayList<ArtifactRepository>();
        final RemoteRepository[] remoteRepositories = settings
            .getRemoteRepositories();
        for (final RemoteRepository remoteRepository : remoteRepositories) {
            if (remoteRepository.isEnabled()) {
                final ArtifactRepository artifactRepository = maven
                    .createRepository(remoteRepository.getUrl(),
                        remoteRepository.getRepositoryId());
                l.add(artifactRepository);
            }
        }
        return l;
    }


    private ArtifactRepository createLocalRepository(
        final PluginAlfrSettings settings, final MavenEmbedder maven)
        throws ComponentLookupException
    {
        final Kvasir kvasir = getKvasir();
        final Resource runtimeWorkDirectory = kvasir.getRuntimeWorkDirectory();
        final Resource m2Resource = runtimeWorkDirectory.getChildResource("m2");
        final File localRepo = m2Resource.toFile();
        final ArtifactRepository localRepository = maven
            .createLocalRepository(localRepo);
        return localRepository;
    }


    private MavenEmbedder createMavenEmbedder()
    {
        final MavenEmbedder maven = new MavenEmbedder();
        //maven.setClassLoader(Thread.currentThread().getContextClassLoader());
        maven.setClassLoader(MavenEmbedder.class.getClassLoader());
        maven.setLogger(new KvasirLogAdapter());
        return maven;
    }


    private PluginAlfrSettings getPluginAlfrSettings()
    {
        return getPluginAlfr().getSettings();
    }


    /*
     * Embedderがprivateフィールドなのでしょうがない。
     */
    private Embedder getEmbedder(final MavenEmbedder maven)
    {
        final Field field = ClassUtil.getDeclaredField(MavenEmbedder.class,
            "embedder");
        field.setAccessible(true);
        final Embedder embedder = (Embedder)FieldUtil.get(field, maven);
        return embedder;
    }


    public boolean containsPlugin(final String pluginId)
    {
        final PluginDescriptor found = findInstalledPlugin(pluginId);
        if (found != null) {
            return true;
        }
        return false;
    }


    private PluginDescriptor findInstalledPlugin(final String pluginId)
    {
        final PluginAlfr alfr = getPluginAlfr();
        final PluginDescriptor[] allPluginDescriptors = alfr
            .getAllPluginDescriptors();
        for (final PluginDescriptor descriptor : allPluginDescriptors) {
            if (descriptor.getId().equals(pluginId)) {
                return descriptor;
            }
        }
        return null;
    }


    public PluginAlfr getPluginAlfr()
    {
        return pluginAlfr_;
    }


    @Binding(bindingType = BindingType.MUST)
    public void setPluginAlfr(final PluginAlfr pluginAlfr)
    {
        pluginAlfr_ = pluginAlfr;
    }


    public Kvasir getKvasir()
    {
        return kvasir_;
    }


    @Binding(bindingType = BindingType.MUST)
    public void setKvasir(final Kvasir kvasir)
    {
        kvasir_ = kvasir;
    }


    private static class KvasirLogAdapter
        implements MavenEmbedderLogger
    {

        public void debug(final String message)
        {
            log_.debug(message);
        }


        public void debug(final String message, final Throwable throwable)
        {
            log_.debug(message, throwable);
        }


        public void error(final String message)
        {
            log_.error(message);
        }


        public void error(final String message, final Throwable throwable)
        {
            log_.error(message, throwable);
        }


        public void fatalError(final String message)
        {
            log_.fatal(message);
        }


        public void fatalError(final String message, final Throwable throwable)
        {
            log_.fatal(message, throwable);
        }


        public int getThreshold()
        {
            if (!log_.isTraceEnabled()) {
                return MavenEmbedderLogger.LEVEL_DISABLED;
            }
            if (!log_.isDebugEnabled()) {
                return MavenEmbedderLogger.LEVEL_DEBUG;
            }
            if (!log_.isInfoEnabled()) {
                return MavenEmbedderLogger.LEVEL_DEBUG;
            }
            if (!log_.isWarnEnabled()) {
                return MavenEmbedderLogger.LEVEL_INFO;
            }
            if (!log_.isErrorEnabled()) {
                return MavenEmbedderLogger.LEVEL_WARN;
            }
            if (!log_.isFatalEnabled()) {
                return MavenEmbedderLogger.LEVEL_ERROR;
            }
            return MavenEmbedderLogger.LEVEL_FATAL;
        }


        public void setThreshold(final int threshold)
        {
            if (log_.isDebugEnabled()) {
                log_.debug("setThreshold ignore: " + threshold);
            }
        }


        public void info(final String message)
        {
            log_.info(message);
        }


        public void info(final String message, final Throwable throwable)
        {
            log_.info(message, throwable);
        }


        public boolean isDebugEnabled()
        {
            return log_.isDebugEnabled();
        }


        public boolean isErrorEnabled()
        {
            return log_.isErrorEnabled();
        }


        public boolean isFatalErrorEnabled()
        {
            return log_.isFatalEnabled();
        }


        public boolean isInfoEnabled()
        {
            return log_.isInfoEnabled();
        }


        public boolean isWarnEnabled()
        {
            return log_.isWarnEnabled();
        }


        public void warn(final String message)
        {
            log_.warn(message);
        }


        public void warn(final String message, final Throwable throwable)
        {
            log_.warn(message, throwable);
        }
    }

}
