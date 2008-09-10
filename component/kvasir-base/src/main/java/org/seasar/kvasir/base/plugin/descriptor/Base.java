package org.seasar.kvasir.base.plugin.descriptor;

import net.skirnir.xom.annotation.Parent;


public class Base extends PluginDependency
{
    private PluginDescriptor parent_;


    public PluginDescriptor getParent()
    {
        return parent_;
    }


    @Parent
    public void setParent(PluginDescriptor parent)
    {
        parent_ = parent;
    }
}
