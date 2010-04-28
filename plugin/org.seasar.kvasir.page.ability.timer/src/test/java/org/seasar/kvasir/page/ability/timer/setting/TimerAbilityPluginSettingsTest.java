package org.seasar.kvasir.page.ability.timer.setting;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;
import org.seasar.kvasir.page.ability.timer.setting.TimerAbilityPluginSettings;


public class TimerAbilityPluginSettingsTest extends XOMBeanTestCase
{
    public void test_toXML()
        throws Exception
    {
        assertBeanEquals("<timer-ability-plugin-settings />", new TimerAbilityPluginSettings());
    }
}
