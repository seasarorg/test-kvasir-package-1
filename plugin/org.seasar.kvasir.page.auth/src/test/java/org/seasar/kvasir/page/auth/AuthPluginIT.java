package org.seasar.kvasir.page.auth;

import junit.framework.Test;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class AuthPluginIT extends KvasirPluginTestCase<AuthPlugin>
{
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.page.auth";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(AuthPluginIT.class);
    }
}
