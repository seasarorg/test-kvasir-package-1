package org.seasar.kvasir.base.webapp.multibyte.impl;

import org.seasar.kvasir.base.plugin.AbstractPlugin;

import org.seasar.kvasir.base.webapp.multibyte.MultibytePlugin;
import org.seasar.kvasir.base.webapp.multibyte.setting.MultibytePluginSettings;


public class MultibytePluginImpl extends AbstractPlugin<MultibytePluginSettings>
    implements MultibytePlugin
{
    @Override
    public Class<MultibytePluginSettings> getSettingsClass()
    {
        return MultibytePluginSettings.class;
    }


    protected boolean doStart()
    {
        return true;
    }


    protected void doStop()
    {
    }
}
