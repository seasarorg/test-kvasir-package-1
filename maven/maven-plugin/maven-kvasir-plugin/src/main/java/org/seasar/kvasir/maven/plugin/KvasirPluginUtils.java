package org.seasar.kvasir.maven.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipUnArchiver;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.ResourceUtils;
import org.seasar.kvasir.util.io.impl.FileResource;

import net.skirnir.xom.IllegalSyntaxException;
import net.skirnir.xom.ValidationException;
import net.skirnir.xom.XMLDocument;
import net.skirnir.xom.XMLParser;
import net.skirnir.xom.XMLParserFactory;
import net.skirnir.xom.XOMapper;
import net.skirnir.xom.XOMapperFactory;
import net.skirnir.xom.annotation.impl.AnnotationBeanAccessorFactory;


/**
 * @author YOKOTA Takehiko
 */
public class KvasirPluginUtils
{
    public static final String PLUGIN_XML = "plugin.xml";

    public static final String COMMON = "common";

    public static final String COMMON_CLASSES = COMMON + "/classes";

    public static final String COMMON_LIB = COMMON + "/lib";

    public static final String SYSTEM = "system";

    public static final String SYSTEM_CLASSES = SYSTEM + "/classes";

    public static final String SYSTEM_LIB = SYSTEM + "/lib";

    public static final String PLUGINS = "plugins";

    public static final String RTWORK = "rtwork";

    public static final String RTWORK_LOG = RTWORK + "/log";

    public static final String METAINF_KVASIR = "META-INF/kvasir/";

    public static final String METAINF_KVASIR_EXTENSIONPOINTS = METAINF_KVASIR
        + "extension-points/";

    private static XMLParser parser_ = XMLParserFactory.newInstance();

    private static XOMapper mapper_ = XOMapperFactory.newInstance()
        .setBeanAccessorFactory(new AnnotationBeanAccessorFactory());


    private KvasirPluginUtils()
    {
    }


    public static void deployPluginResources(File pluginSourceDirectory,
        File pluginDirectory, String libraryDirectoryPath, String includes,
        String excludes, Artifact library, Map libraryMap, Log log,
        MavenProject project)
        throws IOException, MojoExecutionException, ArtifactNotFoundException
    {
        if (pluginSourceDirectory.equals(pluginDirectory)) {
            return;
        }

        log.info("Copy plugin resources to "
            + pluginDirectory.getAbsolutePath());

        if (!pluginSourceDirectory.exists()) {
            log.info("Plugin resource directory does not exist: "
                + pluginSourceDirectory);
            return;
        }

        // prepare plugin directory
        pluginDirectory.mkdirs();

        copyDirectoryStructure(log, pluginSourceDirectory, pluginDirectory,
            includes, excludes);

        //        preparePluginXML(pluginSourceDirectory, pluginDirectory, project);

        log.info("Copy libraries");

        if (library != null) {
            log.debug("COPY(1): " + library);
            ResourceUtils.copy(new FileResource(library.getFile()),
                new FileResource(pluginDirectory)
                    .getChildResource(ArtifactUtils
                        .getArtifactNameExceptForVersion(library)));
        }

        if (libraryDirectoryPath != null && libraryMap != null) {
            ArtifactUtils.copyPluginOuterLibraries(log, pluginDirectory,
                libraryDirectoryPath, libraryMap, project.getArtifacts());
        }
    }


    public static void copyDirectoryStructure(Log log, File sourceDirectory,
        File destinationDirectory, String includes, String excludes)
        throws IOException
    {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(sourceDirectory);
        if (includes != null) {
            scanner.setIncludes(toArray(includes));
        }
        if (excludes != null) {
            scanner.setExcludes(toArray(excludes));
        }
        scanner.addDefaultExcludes();
        scanner.scan();

        String[] includedDirectories = scanner.getIncludedDirectories();
        for (int i = 0; i < includedDirectories.length; i++) {
            File directory = new File(destinationDirectory,
                includedDirectories[i]);
            log.debug("MKDIR: " + directory);
            directory.mkdirs();
        }

        String[] includedFiles = scanner.getIncludedFiles();
        for (int i = 0; i < includedFiles.length; i++) {
            File sourceFile = new File(sourceDirectory, includedFiles[i]);
            File destinationFile = new File(destinationDirectory,
                includedFiles[i]);
            log.debug("COPY " + sourceFile + " TO " + destinationFile);
            FileUtils.copyFile(sourceFile, destinationFile);
        }
    }


    private static String[] toArray(String str)
    {
        if (str.trim().length() == 0) {
            return new String[0];
        }
        String[] strs = str.split(",");
        for (int i = 0; i < strs.length; i++) {
            strs[i] = strs[i].trim();
        }
        return strs;
    }


    public static void deployKvasirResources(File homeDirectory,
        String commonLibraries, String systemLibraries, Log log,
        MavenProject project)
        throws IOException, MojoExecutionException, ArtifactNotFoundException
    {
        log.info("Deploy resources to: " + homeDirectory);

        homeDirectory.mkdirs();
        File commonDirectory = new File(homeDirectory, COMMON);
        commonDirectory.mkdir();
        File commonClassesDirectory = new File(homeDirectory, COMMON_CLASSES);
        commonClassesDirectory.mkdir();
        File commonLibDirectory = new File(homeDirectory, COMMON_LIB);
        commonLibDirectory.mkdir();
        File systemDirectory = new File(homeDirectory, SYSTEM);
        systemDirectory.mkdir();
        File systemClassesDirectory = new File(homeDirectory, SYSTEM_CLASSES);
        systemClassesDirectory.mkdir();
        File systemLibDirectory = new File(homeDirectory, SYSTEM_LIB);
        systemLibDirectory.mkdir();
        File pluginsDirectory = new File(homeDirectory, PLUGINS);
        pluginsDirectory.mkdir();
        File rtworkDirectory = new File(homeDirectory, RTWORK);
        rtworkDirectory.mkdir();
        File logDirectory = new File(homeDirectory, RTWORK_LOG);
        logDirectory.mkdir();

        log.info("Copy common libraries to " + commonLibDirectory);

        Artifact[] artifacts = ArtifactUtils.getMatchedArtifacts(log, project,
            ArtifactUtils.parseLibraries(commonLibraries, "jar"));
        for (int i = 0; i < artifacts.length; i++) {
            Artifact artifact = artifacts[i];

            try {
                log.debug("COPY: " + artifact);
                ResourceUtils.copy(new FileResource(artifact.getFile()),
                    new FileResource(commonLibDirectory)
                        .getChildResource(ArtifactUtils
                            .getArtifactNameExceptForVersion(artifact)));
            } catch (IORuntimeException ex) {
                throw new MojoExecutionException("Can't copy common library: "
                    + artifact.getGroupId() + ":" + artifact.getArtifactId(),
                    ex);
            }
        }

        log.info("Copy system libraries to " + systemLibDirectory);

        artifacts = ArtifactUtils.getMatchedArtifacts(log, project,
            ArtifactUtils.parseLibraries(systemLibraries, "jar"));
        for (int i = 0; i < artifacts.length; i++) {
            Artifact artifact = artifacts[i];

            try {
                log.debug("COPY: " + artifact);
                ResourceUtils.copy(new FileResource(artifact.getFile()),
                    new FileResource(systemLibDirectory)
                        .getChildResource(ArtifactUtils
                            .getArtifactNameExceptForVersion(artifact)));
            } catch (IORuntimeException ex) {
                throw new MojoExecutionException("Can't copy system library: "
                    + artifact.getGroupId() + ":" + artifact.getArtifactId(),
                    ex);
            }
        }

        log.info("Extract dependent plugins into " + pluginsDirectory);

        Map patternMap = new HashMap();
        patternMap.put(null, new ArtifactPattern(null, null, "zip", null,
            Artifact.SCOPE_RUNTIME, null));
        artifacts = ArtifactUtils.getMatchedArtifacts(log, project, patternMap);
        for (int i = 0; i < artifacts.length; i++) {
            Artifact artifact = artifacts[i];
            if (!isPluginArtifact(artifact)) {
                log.debug("IGNORE: " + artifact);
                continue;
            }

            log.debug("EXTRACT: " + artifact);

            ZipUnArchiver unarchiver = new ZipUnArchiver();
            unarchiver.setSourceFile(artifact.getFile());
            unarchiver.setDestDirectory(pluginsDirectory);
            unarchiver.setOverwrite(true);
            unarchiver.enableLogging(new ConsoleLogger(Logger.LEVEL_DISABLED,
                ""));
            try {
                unarchiver.extract();
            } catch (ArchiverException ex) {
                throw new MojoExecutionException(
                    "Can't extract kvasir-plugin: " + artifact.getGroupId()
                        + ":" + artifact.getArtifactId() + ", file="
                        + artifact.getFile(), ex);
            } catch (IOException ex) {
                throw new MojoExecutionException(
                    "Can't extract kvasir-plugin: " + artifact.getGroupId()
                        + ":" + artifact.getArtifactId(), ex);
            }
        }

        log.info("Deployment successfully finished.");
    }


    public static boolean isPluginArtifact(Artifact artifact)
    {
        return Artifact.SCOPE_RUNTIME.equals(artifact.getScope())
            && "zip".equals(artifact.getType())
            && artifact.getGroupId().equals(artifact.getArtifactId());
    }


    public static void filterFile(File source, File dest, String encoding,
        Map map)
        throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(
            new FileInputStream(source), encoding));
        StringWriter sw = new StringWriter();
        try {
            char[] buf = new char[4096];
            int len;
            while ((len = br.read(buf)) != -1) {
                sw.write(buf, 0, len);
            }
        } finally {
            br.close();
        }

        String content = sw.toString();
        for (Iterator itr = map.entrySet().iterator(); itr.hasNext();) {
            Map.Entry entry = (Map.Entry)itr.next();
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            content = content.replaceAll("@" + key + "@", value);
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(dest), encoding));
        try {
            bw.write(content);
        } finally {
            bw.close();
        }
    }


    public static List getDefaultExcludes()
    {
        List defaultExcludes = new ArrayList();
        defaultExcludes.add("**/*~");
        defaultExcludes.add("**/#*#");
        defaultExcludes.add("**/.#*");
        defaultExcludes.add("**/%*%");
        defaultExcludes.add("**/._*");

        // CVS
        defaultExcludes.add("**/CVS");
        defaultExcludes.add("**/CVS/**");
        defaultExcludes.add("**/.cvsignore");

        // SCCS
        defaultExcludes.add("**/SCCS");
        defaultExcludes.add("**/SCCS/**");

        // Visual SourceSafe
        defaultExcludes.add("**/vssver.scc");

        // Subversion
        defaultExcludes.add("**/.svn");
        defaultExcludes.add("**/.svn/**");

        // Mac
        defaultExcludes.add("**/.DS_Store");

        return defaultExcludes;
    }


    public static String concatLibrariesString(String libraries1,
        String libraries2)
    {
        StringBuffer sb = new StringBuffer();
        String delim = "";
        if (libraries1 != null && libraries1.trim().length() > 0) {
            sb.append(libraries1);
            delim = ",";
        }
        if (libraries2 != null && libraries2.trim().length() > 0) {
            sb.append(delim).append(libraries2);
        }
        return sb.toString();
    }


    public static PluginDescriptor getPluginDescriptor(InputStream is)
        throws IOException, IllegalSyntaxException, ValidationException
    {
        if (is == null) {
            throw new IOException("Input stream is null");
        }
        try {
            // FIXME XOMがXMLファイル中のencodingが読み取れるようになったら修正しよう。
            XMLDocument document = parser_.parse(new InputStreamReader(is,
                "UTF-8"));
            return (PluginDescriptor)mapper_.toBean(document.getRootElement(),
                PluginDescriptor.class);
        } finally {
            try {
                is.close();
            } catch (IOException ignore) {
            }
        }
    }


    public static XOMapper getXOMapper()
    {
        return mapper_;
    }


    public static void storeOuterLibrariesProperties(Log log,
        String outerLibraries, Set artifactSet, OutputStream os)
        throws ArtifactNotFoundException, IOException
    {
        storeOuterLibrariesProperties(log, ArtifactUtils
            .parseLibraries(outerLibraries), artifactSet, os);
    }


    public static void storeOuterLibrariesProperties(Log log, Map libraryMap,
        Set artifactSet, OutputStream os)
        throws ArtifactNotFoundException, IOException
    {
        Properties prop = getOuterLibrariesProperties(log, libraryMap,
            artifactSet);
        prop.store(os, null);
    }


    public static Properties getOuterLibrariesProperties(Log log,
        String outerLibraries, Set artifactSet)
        throws ArtifactNotFoundException, IOException
    {
        return getOuterLibrariesProperties(log, ArtifactUtils
            .parseLibraries(outerLibraries), artifactSet);
    }


    public static Properties getOuterLibrariesProperties(Log log,
        Map libraryMap, Set artifactSet)
        throws ArtifactNotFoundException, IOException
    {
        Artifact[] artifacts = ArtifactUtils.getMatchedArtifacts(log,
            artifactSet, libraryMap);

        Properties librariesProp = new Properties();
        StringBuffer sb = new StringBuffer();
        String delim = "";
        for (int i = 0; i < artifacts.length; i++) {
            sb.append(delim).append(artifacts[i].getFile().getCanonicalPath());
            delim = ",";
        }
        librariesProp.setProperty("outerLibraries", sb.toString());
        return librariesProp;
    }
}
