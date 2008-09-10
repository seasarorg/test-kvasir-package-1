/*
 * Copyright (c) 2005 ARK Systems Co.,Ltd. All rights reserved.
 */

package org.seasar.kvasir.system.plugin;

import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.plugin.PluginCandidate;


/**
 * @author manhole
 */
public class PluginCandidateImpl
    implements PluginCandidate
{

    private String id_;

    private Version version_;

    private PluginCandidate[] pluginCandidate_;


    public String getId()
    {
        return id_;
    }


    public void setId(final String id)
    {
        id_ = id;
    }


    public Version getVersion()
    {
        return version_;
    }


    public void setVersion(final Version version)
    {
        version_ = version;
    }


    public PluginCandidate[] getDependencies()
    {
        // TODO Auto-generated method stub
        return pluginCandidate_;
    }


    public void setDependencies(final PluginCandidate[] pluginCandidate)
    {
        pluginCandidate_ = pluginCandidate;
    }

}
