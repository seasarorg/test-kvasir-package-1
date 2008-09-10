package org.seasar.kvasir.cms.manage;

import junit.framework.Test;

import org.seasar.kvasir.cms.manage.tab.Tab;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class ManagePluginIT extends KvasirPluginTestCase<ManagePlugin>
{
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.cms.manage";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(ManagePluginIT.class);
    }


    public void testTab()
        throws Exception
    {
        ManagePlugin plugin = getPlugin();

        assertTrue(plugin.getExtensionComponents(Tab.class).length > 0);

        Tab[] tabs = plugin.getTabs();
        assertNotNull(tabs);
        for (int i = 0; i < tabs.length; i++) {
            assertNotNull(tabs[i]);
            assertNotNull(tabs[i].getName());
        }
    }
}
