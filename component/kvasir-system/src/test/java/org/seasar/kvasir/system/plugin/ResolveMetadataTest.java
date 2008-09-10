package org.seasar.kvasir.system.plugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import junitx.framework.FileAssert;
import junitx.framework.ListAssert;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.metadata.ArtifactRepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadata;
import org.apache.maven.artifact.repository.metadata.RepositoryMetadataManager;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.embedder.MavenEmbedderConsoleLogger;
import org.codehaus.plexus.embed.Embedder;
import org.codehaus.plexus.util.FileUtils;
import org.seasar.framework.util.ClassUtil;
import org.seasar.framework.util.FieldUtil;
import org.seasar.framework.util.ResourceUtil;


public class ResolveMetadataTest extends TestCase
{

    private File classFile_;


    protected void setUp()
        throws Exception
    {
        super.setUp();
        classFile_ = ResourceUtil.getResourceAsFile(ResolveMetadataTest.class
            .getName(), "class");
    }


    public void testDownloadMetadataOnly()
        throws Exception
    {
        // ## Arrange ##
        final File remoteRepo = new File(classFile_.getParentFile(), "remote-2");
        final File localRepo = new File(classFile_.getParentFile(), "local-1");
        FileUtils.deleteDirectory(localRepo);
        assertEquals(false, localRepo.exists());

        // ## Act ##
        final MavenEmbedder maven = new MavenEmbedder();
        maven.setClassLoader(Thread.currentThread().getContextClassLoader());
        maven.setLogger(new MavenEmbedderConsoleLogger());
        maven.start();

        final ArtifactRepository localRepository = maven
            .createLocalRepository(localRepo);
        final String remoteRepoId = "testingRemoteRepo";
        final ArtifactRepository remoteRepository = maven.createRepository(
            remoteRepo.toURI().toURL().toExternalForm(), remoteRepoId);
        System.out.println("localRepository=" + localRepository);
        System.out.println("remoteRepository=" + remoteRepository);

        final Artifact artifact = maven.createArtifact("com.example",
            "com.example", Artifact.RELEASE_VERSION, null, "jar");
        assertEquals(false, localRepo.exists());
        final File downloadedJar = new File(localRepo,
            "com/example/com.example/1.0.1/com.example-1.0.1.jar");
        final File downloadedMetadata = new File(localRepo,
            "com/example/com.example/maven-metadata-" + remoteRepoId + ".xml");
        final File repoMetadata = new File(remoteRepo,
            "com/example/com.example/maven-metadata.xml");
        assertEquals(true, repoMetadata.exists());
        assertEquals(false, downloadedJar.exists());
        assertEquals(false, downloadedMetadata.exists());

        final List<ArtifactRepository> remoteRepositories = Arrays
            .asList(new ArtifactRepository[] { remoteRepository });

        final Embedder embedder = getEmbedder(maven);

        final RepositoryMetadataManager repositoryMetadataManager = (RepositoryMetadataManager)embedder
            .lookup(RepositoryMetadataManager.class.getName());
        final RepositoryMetadata repositoryMetadata = new ArtifactRepositoryMetadata(
            artifact);
        repositoryMetadataManager.resolve(repositoryMetadata,
            remoteRepositories, localRepository);

        maven.stop();

        // ## Assert ##
        assertEquals("maven-metadata-testingRemoteRepo.xmlが作成されていること", true,
            downloadedMetadata.exists());
        FileAssert
            .assertBinaryEquals(
                "maven-metadata.xmlがmaven-metadata-testingRemoteRepo.xmlとしてDLされていること",
                repoMetadata, downloadedMetadata);
        assertEquals("jarはDLされていないこと", false, downloadedJar.exists());

        /*
         * 以下、今後のメモとして属性情報を記録しておく。
         */
        assertEquals("com.example", repositoryMetadata.getArtifactId());
        assertEquals("com.example", repositoryMetadata.getGroupId());
        assertEquals("maven-metadata.xml", repositoryMetadata
            .getRemoteFilename());
        assertEquals("artifact com.example:com.example", repositoryMetadata
            .getKey());
        assertEquals(null, repositoryMetadata.getBaseVersion());
        final Metadata metadata = repositoryMetadata.getMetadata();
        assertEquals("com.example", metadata.getArtifactId());
        assertEquals("com.example", metadata.getGroupId());
        assertEquals("UTF-8", metadata.getModelEncoding());
        assertEquals(0, metadata.getPlugins().size());
        assertEquals(Artifact.RELEASE_VERSION, metadata.getVersion());
        final Versioning versioning = metadata.getVersioning();
        assertEquals(null, versioning.getLatest());
        assertEquals("UTF-8", versioning.getModelEncoding());
        assertEquals("1.0.1", versioning.getRelease());
        assertEquals(null, versioning.getSnapshot());
        ListAssert.assertEquals(Arrays.asList(new String[] { "1.0.0", "1.0.1",
            "1.0.2" }), versioning.getVersions());
    }


    /*
     * 
     */
    private Embedder getEmbedder(final MavenEmbedder maven)
    {
        final Field field = ClassUtil.getDeclaredField(MavenEmbedder.class,
            "embedder");
        field.setAccessible(true);
        final Embedder embedder = (Embedder)FieldUtil.get(field, maven);
        return embedder;
    }

}
