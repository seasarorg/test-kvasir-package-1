package org.seasar.kvasir.maven.plugin.spike;

import java.util.Collection;
import java.util.Iterator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;


abstract public class AbstractSpikeMojo extends AbstractMojo
{
    public void execute()
        throws MojoExecutionException
    {
        print("ALL", getProject().getArtifacts());
        print("compile", getProject().getCompileArtifacts());
        print("test", getProject().getTestArtifacts());
        print("runtime", getProject().getRuntimeArtifacts());
    }


    abstract protected MavenProject getProject();


    void print(String scope, Collection artifacts)
    {
        System.out.println("SCOPE: " + scope);
        for (Iterator itr = artifacts.iterator(); itr.hasNext();) {
            Artifact artifact = (Artifact)itr.next();
            System.out.println("\t" + artifact.toString());
            System.out.println("\t\tFILE=" + artifact.getFile());
        }
    }
}
