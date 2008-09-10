package org.seasar.kvasir.base;

import junit.framework.Test;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class BasePluginIT extends KvasirPluginTestCase<Plugin<?>>
{
    @Override
    protected String getTargetPluginId()
    {
        return Kvasir.ID;
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(BasePluginIT.class);
    }
}
