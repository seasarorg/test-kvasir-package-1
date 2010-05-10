package org.seasar.kvasir.base.timer.setting;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;


public class TimerPluginSettingsTest extends XOMBeanTestCase
{
    public void test_toXML()
        throws Exception
    {
        assertBeanEquals("<timer-plugin-settings />", new TimerPluginSettings());
    }


    public void test_toXML2()
        throws Exception
    {
        TimerPluginSettings target = new TimerPluginSettings();
        target.setThreadPoolSize(3);

        assertBeanEquals("<timer-plugin-settings thread-pool-size=\"3\" />",
            target);
    }


    public void test_toXML3()
        throws Exception
    {
        TimerPluginSettings target = new TimerPluginSettings();
        target.setThreadPoolSize(5);

        assertBeanEquals("<timer-plugin-settings />", target);
    }
}
