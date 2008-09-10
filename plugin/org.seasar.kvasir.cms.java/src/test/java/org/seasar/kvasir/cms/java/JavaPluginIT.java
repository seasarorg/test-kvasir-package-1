package org.seasar.kvasir.cms.java;

import junit.framework.Test;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class JavaPluginIT extends KvasirPluginTestCase<Plugin<?>>
{
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.cms.java";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(JavaPluginIT.class);
    }
}
