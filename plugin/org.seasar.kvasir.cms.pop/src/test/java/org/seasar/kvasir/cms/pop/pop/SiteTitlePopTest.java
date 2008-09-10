package org.seasar.kvasir.cms.pop.pop;

import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.test.PopTestCase;
import org.seasar.kvasir.page.mock.MockPage;


public class SiteTitlePopTest extends PopTestCase<SiteTitlePop>
{
    @Override
    protected Class<SiteTitlePop> getPopClass()
    {
        return SiteTitlePop.class;
    }


    @Override
    protected String getPopId()
    {
        return SiteTitlePop.ID;
    }


    @Override
    protected String getPluginId()
    {
        return PopPlugin.ID;
    }


    public void testRender_画像がない場合()
        throws Exception
    {
        RenderedPop actual = newPopInstance().render(
            newContext(registerPage(new MockPage(1000, "/path/to/page"))),
            new String[0]);

        assertEquals(readText("testRender_expected.txt"), normalizeText(actual
            .getBody()));
    }


    public void testRender2_画像あり_右寄せ_高さ幅指定あり()
        throws Exception
    {
        setProperty(SiteTitlePop.PROP_IMAGEURL, "page:/image.jpg");
        setProperty(SiteTitlePop.PROP_IMAGEALIGN, "right");
        setProperty(SiteTitlePop.PROP_IMAGEWIDTH, "200");
        setProperty(SiteTitlePop.PROP_IMAGEHEIGHT, "100");

        RenderedPop actual = newPopInstance().render(
            newContext(registerPage(new MockPage(1000, "/path/to/page"))),
            new String[0]);

        assertEquals(readText("testRender2_expected.txt"), normalizeText(actual
            .getBody()));
    }


    public void testRender3_画像あり_左寄せ_高さ幅指定あり()
        throws Exception
    {
        setProperty(SiteTitlePop.PROP_IMAGEURL, "page:/image.jpg");
        setProperty(SiteTitlePop.PROP_IMAGEALIGN, "left");
        setProperty(SiteTitlePop.PROP_IMAGEWIDTH, "200");
        setProperty(SiteTitlePop.PROP_IMAGEHEIGHT, "100");

        RenderedPop actual = newPopInstance().render(
            newContext(registerPage(new MockPage(1000, "/path/to/page"))),
            new String[0]);

        assertEquals(readText("testRender3_expected.txt"), normalizeText(actual
            .getBody()));
    }


    public void testRender4_画像あり_左寄せ_幅指定あり()
        throws Exception
    {
        setProperty(SiteTitlePop.PROP_IMAGEURL, "page:/image.jpg");
        setProperty(SiteTitlePop.PROP_IMAGEALIGN, "left");
        setProperty(SiteTitlePop.PROP_IMAGEWIDTH, "200");

        RenderedPop actual = newPopInstance().render(
            newContext(registerPage(new MockPage(1000, "/path/to/page"))),
            new String[0]);

        assertEquals(readText("testRender4_expected.txt"), normalizeText(actual
            .getBody()));
    }


    public void testRender5_画像あり_左寄せ_高さ指定あり()
        throws Exception
    {
        setProperty(SiteTitlePop.PROP_IMAGEURL, "page:/image.jpg");
        setProperty(SiteTitlePop.PROP_IMAGEALIGN, "left");
        setProperty(SiteTitlePop.PROP_IMAGEHEIGHT, "100");

        RenderedPop actual = newPopInstance().render(
            newContext(registerPage(new MockPage(1000, "/path/to/page"))),
            new String[0]);

        assertEquals(readText("testRender5_expected.txt"), normalizeText(actual
            .getBody()));
    }
}
