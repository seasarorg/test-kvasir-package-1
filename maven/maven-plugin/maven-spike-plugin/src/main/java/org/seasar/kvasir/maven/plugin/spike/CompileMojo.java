package org.seasar.kvasir.maven.plugin.spike;

import org.apache.maven.project.MavenProject;


/**
 * @goal compile
 * @phase package
 * @requiresDependencyResolution compile
 */
public class CompileMojo extends AbstractSpikeMojo
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
