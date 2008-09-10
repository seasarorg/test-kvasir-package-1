package org.seasar.kvasir.cms.ymir.impl;

import java.util.Locale;

import org.seasar.cms.ymir.Messages;
import org.seasar.kvasir.base.plugin.Plugin;


public class KvasirMessages
    implements Messages
{
    private Plugin<?> plugin_;


    public void setPlugin(Plugin<?> plugin)
    {
        plugin_ = plugin;
    }


    public String getProperty(String name, Locale locale)
    {
        return plugin_.getProperty(name, locale);
    }


    public String getProperty(String name)
    {
        return plugin_.getProperty(name);
    }
}
