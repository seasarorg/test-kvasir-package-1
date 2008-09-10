package org.seasar.kvasir.maven.plugin;

import java.io.File;
import java.io.IOException;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.archiver.war.WarArchiver;
import org.codehaus.plexus.archiver.zip.ZipArchiver;


/**
 * @author YOKOTA Takehiko
 * @goal packageDistribution
 * @phase package
 * @requiresDependencyResolution test
 */
public class PackageDistributionMojo extends AbstractMojo
{
    public static final String WEB_XML = "WEB-INF/web.xml";

    public static final String DISTRIBUTION_PATH = "distribution";

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

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
     * @todo Convert to File
     *
     * @parameter
     */
    private String deploymentDirectory;

    /**
     * @todo Convert to File
     *
     * @parameter expression="${basedir}/src/main/distribution"
     * @required
     */
    private String distributionSourceDirectory;

    /**
     * @parameter alias="includes"
     */
    private String distributionSourceIncludes = "**";

    /**
     * @parameter alias="excludes"
     */
    private String distributionSourceExcludes;

    /**
     * @todo Convert to File
     *
     * @parameter expression="${project.build.directory}/distribution/${project.build.finalName}"
     * @required
     */
    private String distributionDirectory;

    /**
     * @parameter expression="${project.build.finalName}"
     * @required
     * @readonly
     */
    private String distributionName;


    public void execute()
        throws MojoExecutionException
    {
        File distributionFile = new File(buildDirectory, distributionName
            + ".zip");

        try {
            performPackaging(distributionFile);
        } catch (Exception e) {
            // TODO: improve error handling
            throw new MojoExecutionException("Error packaging distribution", e);
        }
    }


    private void performPackaging(File distributionFile)
        throws IOException, ArchiverException, ManifestException,
        DependencyResolutionRequiredException, MojoExecutionException
    {
        File deploymentSourceDirectory = MojoUtils.getAsFile(basedir,
            this.deploymentSourceDirectory, "src/main/"
                + ResourcesDistributionMojo.DEPLOYMENT_PATH);
        File deploymentDirectory = MojoUtils
            .getAsFile(buildDirectory, this.deploymentDirectory,
                ResourcesDistributionMojo.DEPLOYMENT_PATH);
        File distributionSourceDirectory = new File(
            this.distributionSourceDirectory);
        File distributionDirectory = new File(this.distributionDirectory);
        distributionDirectory.mkdirs();

        String[] excludes = (String[])KvasirPluginUtils.getDefaultExcludes()
            .toArray(new String[0]);

        getLog().info("Generate deployment archive ");

        File webxmlFile = new File(deploymentSourceDirectory, WEB_XML);
        if (webxmlFile.exists()) {
            getLog().info("web.xml was found. Generate archive as WAR ");

            File warFile = new File(distributionDirectory, "kvasir.war");
            projectHelper.attachArtifact(project, "war", "", warFile);

            WarArchiver warArchiver = new WarArchiver();
            warArchiver.setDestFile(warFile);
            warArchiver.addDirectory(deploymentDirectory, null, excludes);
            warArchiver.setWebxml(webxmlFile);
            warArchiver.createArchive();
        } else {
            try {
                KvasirPluginUtils.copyDirectoryStructure(getLog(),
                    deploymentDirectory, distributionDirectory, "**", null);
            } catch (IOException ex) {
                throw new MojoExecutionException(
                    "Can't deploy deployment resources from "
                        + deploymentSourceDirectory + " to "
                        + deploymentDirectory, ex);
            }
        }

        getLog().info("Assemble distribution resources");

        if (distributionSourceDirectory.exists()) {
            try {
                KvasirPluginUtils.copyDirectoryStructure(getLog(),
                    distributionSourceDirectory, distributionDirectory,
                    distributionSourceIncludes, distributionSourceExcludes);
            } catch (IOException ex) {
                throw new MojoExecutionException(
                    "Can't deploy distribution resources from "
                        + distributionSourceDirectory + " to "
                        + distributionDirectory, ex);
            }
        }

        getLog().info(
            "Generate distribution archive "
                + distributionFile.getAbsolutePath());

        ZipArchiver zipArchiver = new ZipArchiver();
        zipArchiver.setDestFile(distributionFile);
        zipArchiver.addDirectory(distributionDirectory.getParentFile(), null,
            excludes);
        zipArchiver.createArchive();

        project.getArtifact().setFile(distributionFile);
    }
}
