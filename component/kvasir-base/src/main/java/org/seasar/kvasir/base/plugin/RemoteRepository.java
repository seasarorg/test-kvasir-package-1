package org.seasar.kvasir.base.plugin;

import net.skirnir.xom.annotation.Attribute;


/**
 * @author manhole
 */
public class RemoteRepository
{

    private String url_;

    private String repositoryId_;

    private boolean enabled_;


    public String getRepositoryId()
    {
        return repositoryId_;
    }


    @Attribute
    public void setRepositoryId(final String repositoryId)
    {
        repositoryId_ = repositoryId;
    }


    public String getUrl()
    {
        return url_;
    }


    @Attribute
    public void setUrl(final String url)
    {
        url_ = url;
    }


    public boolean isEnabled()
    {
        return enabled_;
    }


    @Attribute
    public void setEnabled(final boolean enable)
    {
        enabled_ = enable;
    }

}
