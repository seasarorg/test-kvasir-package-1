package org.seasar.kvasir.base.webapp;

import junit.framework.Test;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class WebappPluginIT extends KvasirPluginTestCase<WebappPlugin>
{
    @Override
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.base.webapp";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(WebappPluginIT.class);
    }
}
