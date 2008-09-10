package org.seasar.kvasir.cms;

import junit.framework.Test;

import org.seasar.kvasir.cms.setting.CmsPluginSettings;
import org.seasar.kvasir.cms.setting.HeimElement;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.type.Directory;
import org.seasar.kvasir.test.KvasirPluginTestCase;


/**
 * @author YOKOTA Takehiko
 */
public class CmsPluginIT extends KvasirPluginTestCase<CmsPlugin>
{
    protected String getTargetPluginId()
    {
        return "org.seasar.kvasir.cms";
    }


    public static Test suite()
        throws Exception
    {
        return createTestSuite(CmsPluginIT.class);
    }


    public void testGetHeimIds()
        throws Exception
    {
        int[] heimIds = getPlugin().getHeimIds();
        assertEquals(1, heimIds.length);
        assertEquals(PathId.HEIM_MIDGARD, heimIds[0]);
    }


    public void testPrepareForHeims_設定にHeimを追加するとHeimのルートが生成されること()
        throws Exception
    {
        PageAlfr pageAlfr = getKvasir().getPluginAlfr().getPlugin(
            PagePlugin.class).getPageAlfr();

        Page rootPage = pageAlfr.getRootPage(2);
        if (rootPage != null) {
            pageAlfr.deletePage(rootPage);
        }

        CmsPluginSettings oldSettings = getPlugin().getSettings();
        Page actual = null;
        try {
            CmsPluginSettings settings = getPlugin().getSettingsForUpdate();
            HeimElement heim = new HeimElement();
            heim.setId(2);
            heim.addSite("localhost");
            settings.addHeim(heim);
            getPlugin().storeSettings(settings);

            actual = pageAlfr.getRootPage(2);
            assertNotNull(actual);
            assertNull(actual.getParent());
            assertEquals("", actual.getName());
            assertTrue(actual.isLord());
            assertEquals(Directory.TYPE, actual.getType());
        } finally {
            try {
                getPlugin().storeSettings(oldSettings);
            } catch (Throwable ignore) {
                ignore.printStackTrace();
            }
            if (actual != null) {
                try {
                    pageAlfr.deletePage(actual);
                } catch (Throwable ignore) {
                    ignore.printStackTrace();
                }
            }
        }
    }
}
