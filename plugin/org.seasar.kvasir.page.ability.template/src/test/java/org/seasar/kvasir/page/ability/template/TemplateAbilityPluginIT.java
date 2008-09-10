package org.seasar.kvasir.page.ability.template;

import junit.framework.Test;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class TemplateAbilityPluginIT extends
    KvasirPluginTestCase<TemplateAbilityPlugin>
{
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.page.ability.template";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(TemplateAbilityPluginIT.class);
    }
}
