package org.seasar.kvasir.maven.plugin;

import java.io.File;
import java.io.IOException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.ResourceUtils;
import org.seasar.kvasir.util.io.impl.FileResource;


/**
 * @author YOKOTA Takehiko
 * @goal resourcesDistribution
 * @phase process-resources
 * @requiresDependencyResolution test
 */
public class ResourcesDistributionMojo extends AbstractMojo
{
    public static final String DEPLOYMENT_PATH = "webapp";

    public static final String BASE_LIBRARY_PATH = "WEB-INF/lib";

    public static final String HOME_PATH = DEPLOYMENT_PATH + "/kvasir";

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
    private String basedir;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String buildDirectory;

    /**
     * @todo Convert to File
     *
     * @parameter
     */
    private String deploymentSourceDirectory;

    /**
     * @parameter alias="includes"
     */
    private String deploymentSourceIncludes = "**";

    /**
     * @parameter alias="excludes"
     */
    private String deploymentSourceExcludes;

    /**
     * @todo Convert to File
     *
     * @parameter
     */
    private String deploymentDirectory;

    /**
     * @todo Convert to File
     *
     * @parameter
     */
    private String homeDirectory;

    /**
     * @todo Convert to File
     *
     * @parameter
     */
    private String baseLibraryDirectory;

    /**
     * @parameter
     */
    private String baseLibraries;

    /**
     * @parameter
     */
    private String additionalBaseLibraries;

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
        try {
            performProcessResources();
        } catch (Exception e) {
            // TODO: improve error handling
            throw new MojoExecutionException("Error processing resources", e);
        }
    }


    private void performProcessResources()
        throws IOException, ArchiverException, ManifestException,
        DependencyResolutionRequiredException, MojoExecutionException,
        ArtifactNotFoundException
    {
        getLog().info("Assemble deployment resources");

        File deploymentSourceDirectory = MojoUtils.getAsFile(basedir,
            this.deploymentSourceDirectory, "src/main/" + DEPLOYMENT_PATH);
        File deploymentDirectory = MojoUtils.getAsFile(buildDirectory,
            this.deploymentDirectory, DEPLOYMENT_PATH);
        deploymentDirectory.mkdirs();
        File homeDirectory = MojoUtils.getAsFile(buildDirectory,
            this.homeDirectory, HOME_PATH);
        homeDirectory.mkdirs();
        File baseLibraryDirectory = MojoUtils.getAsFile(deploymentDirectory,
            this.baseLibraryDirectory, BASE_LIBRARY_PATH);
        baseLibraryDirectory.mkdirs();

        KvasirPluginUtils.deployKvasirResources(homeDirectory,
            KvasirPluginUtils.concatLibrariesString(commonLibraries,
                additionalCommonLibraries), KvasirPluginUtils
                .concatLibrariesString(systemLibraries,
                    additionalSystemLibraries), getLog(), project);

        if (deploymentSourceDirectory.exists()) {
            try {
                KvasirPluginUtils.copyDirectoryStructure(getLog(),
                    deploymentSourceDirectory, deploymentDirectory,
                    deploymentSourceIncludes, deploymentSourceExcludes);
            } catch (IOException ex) {
                throw new MojoExecutionException(
                    "Can't assemble deployment resources from "
                        + deploymentSourceDirectory + " to "
                        + deploymentDirectory, ex);
            }
        }

        String actualBaseLibraries = KvasirPluginUtils.concatLibrariesString(
            baseLibraries, additionalBaseLibraries);
        getLog().info(
            "Copy base libraries to " + baseLibraryDirectory + ": "
                + actualBaseLibraries);

        Artifact[] artifacts = ArtifactUtils.getMatchedArtifacts(getLog(),
            project, ArtifactUtils.parseLibraries(actualBaseLibraries, "jar"));
        for (int i = 0; i < artifacts.length; i++) {
            Artifact artifact = artifacts[i];

            try {
                getLog().info("COPY: " + artifact);
                ResourceUtils.copy(new FileResource(artifact.getFile()),
                    new FileResource(baseLibraryDirectory)
                        .getChildResource(ArtifactUtils
                            .getArtifactNameExceptForVersion(artifact)));
            } catch (IORuntimeException ex) {
                throw new MojoExecutionException("Can't copy base library: "
                    + artifact.getGroupId() + ":" + artifact.getArtifactId(),
                    ex);
            }
        }
    }
}
