package org.seasar.kvasir.base.cache;

import junit.framework.Test;
import org.seasar.kvasir.test.KvasirPluginTestCase;


public class CachePluginIT extends KvasirPluginTestCase<CachePlugin>
{
    protected String getTargetPluginId()
    {
        return CachePlugin.ID;
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(CachePluginIT.class, false);
    }
}
