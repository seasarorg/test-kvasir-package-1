package org.seasar.kvasir.page.ability.timer.impl;

import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.page.ability.timer.TimerAbilityPlugin;
import org.seasar.kvasir.page.ability.timer.setting.TimerAbilityPluginSettings;


public class TimerAbilityPluginImpl extends AbstractPlugin<TimerAbilityPluginSettings>
    implements TimerAbilityPlugin
{
    @Override
    public Class<TimerAbilityPluginSettings> getSettingsClass()
    {
        return TimerAbilityPluginSettings.class;
    }


    protected boolean doStart()
    {
        return true;
    }


    protected void doStop()
    {
    }
}
