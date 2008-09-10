package org.seasar.kvasir.cms.toolbox.toolbox.pop;

import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.test.PopTestCase;
import org.seasar.kvasir.cms.toolbox.ToolboxPlugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.mock.MockPage;


public class BreadcrumbsPopTest extends PopTestCase<BreadcrumbsPop>
{
    @Override
    protected Class<BreadcrumbsPop> getPopClass()
    {
        return BreadcrumbsPop.class;
    }


    @Override
    protected String getPopId()
    {
        return BreadcrumbsPop.ID;
    }


    @Override
    protected String getPluginId()
    {
        return ToolboxPlugin.ID;
    }


    public void testRender()
        throws Exception
    {
        Page page = registerPage(new MockPage(1000, "/path/to/page"));

        RenderedPop actual = newPopInstance().render(newContext(null, page),
            new String[0]);

        assertEquals("", actual.getTitle());
        assertEquals(readText("testRender_expected.txt"), normalizeText(actual
            .getBody()));
    }


    public void testRender2_topLinkが空文字列の場合はトップへのリンクを表示しない()
        throws Exception
    {
        Page page = registerPage(new MockPage(1000, "/path/to/page"));
        setProperty(BreadcrumbsPop.PROP_TOPLABEL, "");

        RenderedPop actual = newPopInstance().render(newContext(null, page),
            new String[0]);

        assertEquals("", actual.getTitle());
        assertEquals(readText("testRender2_expected.txt"), normalizeText(actual
            .getBody()));
    }


    public void testRender3_トップページには何も表示しない()
        throws Exception
    {
        RenderedPop actual = newPopInstance().render(
            newContext(null, getPageAlfr().getPage(Page.ID_ROOT)),
            new String[0]);

        assertEquals("", actual.getTitle());
        assertEquals("", actual.getBody());
    }
}
