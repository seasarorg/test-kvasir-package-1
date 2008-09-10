package org.seasar.kvasir.maven.plugin.spike;

import org.apache.maven.project.MavenProject;


/**
 * @goal runtime
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class RuntimeMojo extends AbstractSpikeMojo
{
    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;


    protected MavenProject getProject()
    {
        return project;
    }
}
