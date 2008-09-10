package net.skirnir.freyja;

import junit.framework.Test;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class FreyjaPluginIT extends KvasirPluginTestCase<Plugin<?>>
{
    protected String getTargetPluginId()
    {
        return "net.skirnir.freyja";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(FreyjaPluginIT.class);
    }
}
