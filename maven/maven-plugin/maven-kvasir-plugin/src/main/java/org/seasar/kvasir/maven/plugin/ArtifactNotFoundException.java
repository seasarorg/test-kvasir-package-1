package org.seasar.kvasir.maven.plugin;

public class ArtifactNotFoundException extends Exception
{
    private static final long serialVersionUID = 979733172564764622L;

    private ArtifactPattern[] artifactPatterns_;


    public ArtifactNotFoundException(String message)
    {
        this(message, null);
    }


    public ArtifactNotFoundException(String message,
        ArtifactPattern[] artifactPatterns)
    {
        super(message);
        artifactPatterns_ = artifactPatterns;
    }


    public ArtifactPattern[] getArtifactPatterns()
    {
        return artifactPatterns_;
    }
}
