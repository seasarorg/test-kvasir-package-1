package org.seasar.kvasir.maven.plugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.seasar.kvasir.util.io.ResourceUtils;
import org.seasar.kvasir.util.io.impl.FileResource;


/**
 * @author YOKOTA Takehiko
 * @goal resources
 * @phase process-resources
 */
public class ResourcesMojo extends AbstractMojo
{
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${basedir}/src/main/plugin"
     * @required
     */
    private File pluginSourceDirectory;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File buildDirectory;


    public void execute()
        throws MojoExecutionException
    {
        ResourceUtils.copy(new FileResource(new File(pluginSourceDirectory,
            KvasirPluginUtils.PLUGIN_XML)),
            new FileResource(new File(buildDirectory, "/classes/"
                + KvasirPluginUtils.METAINF_KVASIR + project.getArtifactId()
                + "/" + KvasirPluginUtils.PLUGIN_XML)));
    }
}
