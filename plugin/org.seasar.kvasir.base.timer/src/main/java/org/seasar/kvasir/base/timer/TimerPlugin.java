package org.seasar.kvasir.base.timer;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.timer.setting.TimerPluginSettings;


public interface TimerPlugin
    extends Plugin<TimerPluginSettings>
{
    String ID = "org.seasar.kvasir.base.timer";

    String ID_PATH = ID.replace('.', '/');
}
