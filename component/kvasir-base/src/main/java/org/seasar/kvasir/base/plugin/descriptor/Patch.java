package org.seasar.kvasir.base.plugin.descriptor;

import org.seasar.kvasir.base.Identifier;
import org.seasar.kvasir.base.Version;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Required;


public class Patch
{
    private String id_;

    private String versionString_;

    private Version version_;

    private PluginDescriptor plugin_;


    public String getId()
    {
        return id_;
    }


    @Attribute
    @Required
    public void setId(String id)
    {
        id_ = id;
    }


    public String getVersionString()
    {
        return versionString_;
    }


    @Attribute("version")
    @Required
    public void setVersionString(String versionString)
    {
        versionString_ = versionString;
        version_ = new Version(versionString);
    }


    public Version getVersion()
    {
        return version_;
    }


    public PluginDescriptor getPlugin()
    {
        return plugin_;
    }


    @Child
    public void setPlugin(PluginDescriptor plugin)
    {
        plugin_ = plugin;
    }


    public Identifier getPluginIdentifier()
    {
        return plugin_.getIdentifier();
    }
}
