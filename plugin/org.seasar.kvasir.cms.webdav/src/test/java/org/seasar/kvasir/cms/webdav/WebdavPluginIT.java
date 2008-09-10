package org.seasar.kvasir.cms.webdav;

import junit.framework.Test;

import org.seasar.kvasir.cms.webdav.setting.WebdavPluginSettings;
import org.seasar.kvasir.test.KvasirPluginTestCase;


public class WebdavPluginIT extends KvasirPluginTestCase<WebdavPlugin>
{
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.cms.webdav";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(WebdavPluginIT.class);
    }


    public void testGetSettings()
        throws Exception
    {
        WebdavPluginSettings settings = getPlugin().getSettings();

        assertNotNull(settings);
        assertTrue(settings.isEmbeddedContent("test.rst"));
    }
}
