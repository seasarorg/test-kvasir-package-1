package org.seasar.kvasir.maven.plugin.spike;

import org.apache.maven.project.MavenProject;


/**
 * @goal provided
 * @phase package
 * @requiresDependencyResolution provided
 */
public class ProvidedMojo extends AbstractSpikeMojo
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
