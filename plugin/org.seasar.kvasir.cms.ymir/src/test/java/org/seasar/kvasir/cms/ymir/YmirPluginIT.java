package org.seasar.kvasir.cms.ymir;

import junit.framework.Test;

import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.extension.PageProcessorElement;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class YmirPluginIT extends KvasirPluginTestCase<YmirPlugin>
{
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.cms.ymir";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(YmirPluginIT.class);
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
                && "org.seasar.kvasir.cms.zpt".equals(elements[i].getPlugin()
                    .getId())) {
                exists = true;
                break;
            }
        }
        assertTrue("ベースとなるzptPageProcessorがPageProcessorとして登録されていること", exists);
    }
}
