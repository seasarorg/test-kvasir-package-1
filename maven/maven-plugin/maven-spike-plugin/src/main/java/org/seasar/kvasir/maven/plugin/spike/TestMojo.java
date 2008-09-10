package org.seasar.kvasir.maven.plugin.spike;

import org.apache.maven.project.MavenProject;


/**
 * @goal test
 * @phase package
 * @requiresDependencyResolution test
 */
public class TestMojo extends AbstractSpikeMojo
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
