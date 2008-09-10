package org.seasar.kvasir.base.webapp.multibyte;

import junit.framework.Test;
import org.seasar.kvasir.test.KvasirPluginTestCase;


public class MultibytePluginIT extends KvasirPluginTestCase<MultibytePlugin>
{
    protected String getTargetPluginId()
    {
        return MultibytePlugin.ID;
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(MultibytePluginIT.class);
    }
}
