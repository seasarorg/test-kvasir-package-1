package org.seasar.kvasir.cms.ymir.impl;

import org.seasar.kvasir.cms.GardIdProvider;
import org.seasar.kvasir.cms.ymir.YmirPlugin;


public class YmirGardIdProvider
    implements GardIdProvider
{
    private YmirPlugin plugin_;


    public boolean containsGardId(String gardId)
    {
        return plugin_.containsGardId(gardId);
    }


    public void setPlugin(YmirPlugin plugin)
    {
        plugin_ = plugin;
    }


    public String[] getGardIds()
    {
        return plugin_.getGardIds();
    }
}
