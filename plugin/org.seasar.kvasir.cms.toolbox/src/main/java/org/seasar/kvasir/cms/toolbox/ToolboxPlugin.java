package org.seasar.kvasir.cms.toolbox;

import org.seasar.kvasir.base.plugin.Plugin;

import org.seasar.kvasir.cms.toolbox.setting.ToolboxPluginSettings;


public interface ToolboxPlugin extends Plugin<ToolboxPluginSettings>
{
    String ID = "org.seasar.kvasir.cms.toolbox";
    String ID_PATH = ID.replace('.', '/');
}
