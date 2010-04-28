package org.seasar.kvasir.base.timer;

import junit.framework.Test;

import org.seasar.kvasir.base.timer.TimerPlugin;
import org.seasar.kvasir.test.KvasirPluginTestCase;


public class TimerPluginIT extends KvasirPluginTestCase<TimerPlugin>
{
    protected String getTargetPluginId()
    {
        return TimerPlugin.ID;
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(TimerPluginIT.class, false);
    }
}
