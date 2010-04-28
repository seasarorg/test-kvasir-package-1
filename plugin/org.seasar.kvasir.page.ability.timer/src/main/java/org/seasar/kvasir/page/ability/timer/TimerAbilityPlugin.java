package org.seasar.kvasir.page.ability.timer;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.ability.timer.setting.TimerAbilityPluginSettings;


public interface TimerAbilityPlugin
    extends Plugin<TimerAbilityPluginSettings>
{
    String ID = "org.seasar.kvasir.page.ability.timer";

    String ID_PATH = ID.replace('.', '/');
}
