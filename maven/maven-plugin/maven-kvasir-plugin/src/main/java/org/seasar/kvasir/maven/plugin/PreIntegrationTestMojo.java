package org.seasar.kvasir.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.artifact.AttachedArtifact;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.impl.FileResource;


/**
 * @author YOKOTA Takehiko
 * @goal preIntegrationTest
 * @phase pre-integration-test
 * @requiresDependencyResolution test
 */
public class PreIntegrationTestMojo extends AbstractMojo
{
    public static final String TESTHOME = "test-home";

    public static final String PLUGINS_TARGET = KvasirPluginUtils.PLUGINS
        + "/target";

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${basedir}"
     * @required
     */
    private File basedir;

    /**
     * @parameter expression="${basedir}/src/main/plugin"
     * @required
     */
    private File pluginSourceDirectory;

    /**
     * @parameter expression="${basedir}/src/test/test-home"
     * @required
     */
    private File testHomeSourceDirectory;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String buildDirectory;

    /**
     * @parameter expression="${project.build.finalName}"
     * @required
     * @readonly
     */
    private String pluginName;

    /**
     * @parameter
     */
    private String pluginOuterLibraries;

    /**
     * @parameter expression="lib"
     */
    private String pluginOuterLibrariesDirectory;

    /**
     * @parameter
     */
    private String commonLibraries;

    /**
     * @parameter
     */
    private String additionalCommonLibraries;

    /**
     * @parameter
     */
    private String systemLibraries;

    /**
     * @parameter
     */
    private String additionalSystemLibraries;


    public void execute()
        throws MojoExecutionException
    {
        if ("true".equalsIgnoreCase(System.getProperty("maven.test.skip"))) {
            return;
        }

        File homeDirectory = new File(buildDirectory, TESTHOME);
        try {
            KvasirPluginUtils.deployKvasirResources(homeDirectory,
                KvasirPluginUtils.concatLibrariesString(commonLibraries,
                    additionalCommonLibraries), KvasirPluginUtils
                    .concatLibrariesString(systemLibraries,
                        additionalSystemLibraries), getLog(), project);

            if (testHomeSourceDirectory.exists()) {
                try {
                    KvasirPluginUtils.copyDirectoryStructure(getLog(),
                        testHomeSourceDirectory, homeDirectory, null, null);
                } catch (IOException ex) {
                    throw new MojoExecutionException(
                        "Can't deploy distribution resources from "
                            + testHomeSourceDirectory + " to " + homeDirectory,
                        ex);
                }
            }
        } catch (IOException ex) {
            throw new MojoExecutionException("Can't deploy test resources ", ex);
        } catch (ArtifactNotFoundException ex) {
            throw new MojoExecutionException("Can't deploy test resources ", ex);
        }

        File pluginDirectory = new File(homeDirectory, PLUGINS_TARGET);
        getLog().info("Copy target plugin resources to " + pluginDirectory);

        Artifact pluginLibrary = null;
        List attachedList = project.getAttachedArtifacts();
        for (Iterator itr = attachedList.iterator(); itr.hasNext();) {
            Artifact attached = (AttachedArtifact)itr.next();
            if ("jar".equals(attached.getType())) {
                pluginLibrary = attached;
                break;
            }
        }

        Map libraryMap = ArtifactUtils.parseLibraries(pluginOuterLibraries);
        OutputStream os = null;
        try {
            Resource librariesPropResource = new FileResource(buildDirectory)
                .getChildResource("outerLibraries.properties");
            os = librariesPropResource.getOutputStream();
            KvasirPluginUtils.storeOuterLibrariesProperties(getLog(),
                libraryMap, project.getArtifacts(), os);
        } catch (ArtifactNotFoundException ex) {
            throw new MojoExecutionException("Can't assemble outer libraries: "
                + pluginOuterLibraries, ex);
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Can't generate outerLibraries.properties", ex);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ignore) {
                }
            }
        }

        try {
            KvasirPluginUtils.deployPluginResources(pluginSourceDirectory,
                pluginDirectory, null, null, null, pluginLibrary, null,
                getLog(), project);
        } catch (IOException ex) {
            throw new MojoExecutionException(
                "Can't deploy plugin resources from " + pluginSourceDirectory
                    + " to " + pluginDirectory, ex);
        } catch (ArtifactNotFoundException ex) {
            throw new MojoExecutionException(
                "Can't deploy plugin resources from " + pluginSourceDirectory
                    + " to " + pluginDirectory, ex);
        }
    }
}
