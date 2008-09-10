package org.seasar.kvasir.page.ability.content;

import junit.framework.Test;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class ContentAbilityPluginIT extends
    KvasirPluginTestCase<ContentAbilityPlugin>
{
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.page.ability.content";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(ContentAbilityPluginIT.class);
    }
}
