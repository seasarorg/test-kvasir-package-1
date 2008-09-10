package org.seasar.kvasir.base.plugin;

import org.seasar.kvasir.base.Version;


/**
 * @author manhole
 */
public class PluginVersionsImpl
    implements PluginVersions
{

    private String id;

    private Version[] versions;


    public String getId()
    {
        return id;
    }


    public void setId(final String id)
    {
        this.id = id;
    }


    public Version[] getVersions()
    {
        return versions;
    }


    public void setVersions(final Version[] versions)
    {
        this.versions = versions;
    }

}
