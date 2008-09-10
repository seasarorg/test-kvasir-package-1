package org.seasar.kvasir.cms.toolbox;

import junit.framework.Test;
import org.seasar.kvasir.test.KvasirPluginTestCase;


public class ToolboxPluginIT extends KvasirPluginTestCase<ToolboxPlugin>
{
    protected String getTargetPluginId()
    {
        return ToolboxPlugin.ID;
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(ToolboxPluginIT.class);
    }
}
