package org.seasar.kvasir.cms.toolbox.impl;

import org.seasar.kvasir.base.plugin.AbstractPlugin;

import org.seasar.kvasir.cms.toolbox.ToolboxPlugin;
import org.seasar.kvasir.cms.toolbox.setting.ToolboxPluginSettings;


public class ToolboxPluginImpl extends AbstractPlugin<ToolboxPluginSettings>
    implements ToolboxPlugin
{
    @Override
    public Class<ToolboxPluginSettings> getSettingsClass()
    {
        return ToolboxPluginSettings.class;
    }


    protected boolean doStart()
    {
        return true;
    }


    protected void doStop()
    {
    }
}
