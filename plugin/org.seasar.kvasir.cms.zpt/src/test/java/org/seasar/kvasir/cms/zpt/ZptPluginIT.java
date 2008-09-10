package org.seasar.kvasir.cms.zpt;

import junit.framework.Test;

import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.extension.PageProcessorElement;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class ZptPluginIT extends KvasirPluginTestCase<ZptPlugin>
{
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.cms.zpt";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(ZptPluginIT.class);
    }


    public void testZptPageProcessorRegistered()
        throws Exception
    {
        CmsPlugin plugin = getKvasir().getPluginAlfr().getPlugin(
            CmsPlugin.class);
        PageProcessorElement[] elements = plugin
            .getExtensionElements(PageProcessorElement.class);
        boolean exists = false;
        for (int i = 0; i < elements.length; i++) {
            if ("zptPageProcessor".equals(elements[i].getId())
                && getTargetPluginId().equals(elements[i].getPlugin().getId())) {
                exists = true;
                break;
            }
        }
        assertTrue("zptPageProcessorがPageProcessorとして登録されていること", exists);
    }
}
