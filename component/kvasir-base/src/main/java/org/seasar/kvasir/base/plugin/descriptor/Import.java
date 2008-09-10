package org.seasar.kvasir.base.plugin.descriptor;

import net.skirnir.xom.annotation.Parent;


public class Import extends PluginDependency
{
    private Requires parent_;


    public Requires getParent()
    {
        return parent_;
    }


    @Parent
    public void setParent(Requires requires)
    {
        parent_ = requires;
    }
}
