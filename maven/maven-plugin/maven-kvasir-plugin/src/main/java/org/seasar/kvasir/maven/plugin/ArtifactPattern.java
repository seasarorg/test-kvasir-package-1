package org.seasar.kvasir.maven.plugin;


/**
 * @author YOKOTA Takehiko
 */
public class ArtifactPattern
{
    private String      groupId_;
    private String      artifactId_;
    private String      type_;
    private String      version_;
    private String      scope_;
    private String      destination_;


    public ArtifactPattern(String groupId, String artifactId, String type,
        String version, String scope, String destination)
    {
        groupId_ = groupId;
        artifactId_ = artifactId;
        type_ = type;
        version_ = version;
        scope_ = scope;
        destination_ = destination;
    }


    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (groupId_ != null) {
            sb.append(groupId_);
        }
        if (artifactId_ != null) {
            if (sb.length() > 0) {
                sb.append(":");
            }
            sb.append(artifactId_);
        }
        if (type_ != null) {
            if (sb.length() > 0) {
                sb.append(":");
            }
            sb.append(type_);
        }
        if (version_ != null) {
            if (sb.length() > 0) {
                sb.append(":");
            }
            sb.append(version_);
        }
        if (scope_ != null) {
            if (sb.length() > 0) {
                sb.append(":");
            }
            sb.append(scope_);
        }
        if (destination_ != null) {
            sb.append('@').append(destination_);
        }
        return sb.toString();
    }


    public String getArtifactId()
    {
        return artifactId_;
    }


    public void setArtifactId(String artifactId)
    {
        artifactId_ = artifactId;
    }


    public String getGroupId()
    {
        return groupId_;
    }


    public void setGroupId(String groupId)
    {
        groupId_ = groupId;
    }


    public String getType()
    {
        return type_;
    }


    public void setType(String type)
    {
        type_ = type;
    }


    public String getVersion()
    {
        return version_;
    }


    public void setVersion(String version)
    {
        version_ = version;
    }


    public String getScope()
    {
        return scope_;
    }


    public void setScope(String scope)
    {
        scope_ = scope;
    }


    public String getDestination()
    {
        return destination_;
    }


    public void setDestination(String destination)
    {
        destination_ = destination;
    }
}
