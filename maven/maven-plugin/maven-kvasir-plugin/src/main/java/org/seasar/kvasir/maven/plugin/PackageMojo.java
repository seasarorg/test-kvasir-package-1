package org.seasar.kvasir.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.project.artifact.AttachedArtifact;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;


/**
 * @author YOKOTA Takehiko
 * @goal package
 * @phase package
 * @requiresDependencyResolution test
 */
public class PackageMojo extends AbstractMojo
{
    public static final String LIB = "lib";

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
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String buildDirectory;

    /**
     * @todo Convert to File
     * 
     * @parameter expression="${pluginDirectory}"
     */
    private String pluginDirectory;

    /**
     * @todo Convert to File
     * 
     * @parameter expression="${project.build.directory}/plugin"
     * @required
     */
    private String defaultPluginDirectory;

    /**
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String finalName;

    /**
     * @todo Convert to File
     * 
     * @parameter expression="${basedir}/src/main/plugin"
     * @required
     */
    private String pluginSourceDirectory;

    /**
     * @parameter alias="includes"
     */
    private String pluginSourceIncludes = "**";

    /**
     * @parameter alias="excludes"
     */
    private String pluginSourceExcludes;

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


    public void execute()
        throws MojoExecutionException
    {
        File jarFile = new File(buildDirectory, pluginName + ".jar");
        File pluginFile = new File(buildDirectory, pluginName + ".zip");

        // workaround for a Maven-2.0.6's issue
        Artifact artifact = project.getArtifact();
        try {
            Artifact copied = org.apache.maven.artifact.ArtifactUtils
                .copyArtifact(artifact);
            copied.setScope(Artifact.SCOPE_COMPILE);
            project.setArtifact(copied);
            projectHelper.attachArtifact(project, "jar", "", jarFile);
        } finally {
            project.setArtifact(artifact);
        }

        try {
            performPackaging(pluginFile, jarFile);
        } catch (Exception e) {
            // TODO: improve error handling
            throw new MojoExecutionException("Error assembling PLUGIN", e);
        }
    }


    private void performPackaging(File pluginFile, File jarFile)
        throws IOException, ArchiverException, ManifestException,
        DependencyResolutionRequiredException, MojoExecutionException,
        ArtifactNotFoundException
    {
        File pluginDirectory = new File(new File(
            this.pluginDirectory != null ? this.pluginDirectory
                : this.defaultPluginDirectory), finalName);

        Artifact pluginLibrary = null;
        List attachedList = project.getAttachedArtifacts();
        for (Iterator itr = attachedList.iterator(); itr.hasNext();) {
            Artifact attached = (AttachedArtifact)itr.next();
            if ("jar".equals(attached.getType())) {
                pluginLibrary = attached;
                break;
            }
        }

        KvasirPluginUtils.deployPluginResources(
            new File(pluginSourceDirectory), pluginDirectory,
            pluginOuterLibrariesDirectory, pluginSourceIncludes,
            pluginSourceExcludes, pluginLibrary, ArtifactUtils
                .parseLibraries(pluginOuterLibraries), getLog(), project);

        getLog().info(
            "Generating plugin archive " + pluginFile.getAbsolutePath());

        String[] excludes = (String[])KvasirPluginUtils.getDefaultExcludes()
            .toArray(new String[0]);
        ZipArchiver zipArchiver = new ZipArchiver();
        zipArchiver.setDestFile(pluginFile);
        zipArchiver.addDirectory(pluginDirectory.getParentFile(), null,
            excludes);
        zipArchiver.createArchive();

        project.getArtifact().setFile(pluginFile);
    }
}
