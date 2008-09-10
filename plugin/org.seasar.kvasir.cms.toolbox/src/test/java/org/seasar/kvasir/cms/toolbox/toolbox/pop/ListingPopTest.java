package org.seasar.kvasir.cms.toolbox.toolbox.pop;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.test.PopTestCase;
import org.seasar.kvasir.cms.toolbox.ToolboxPlugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.mock.MockContent;
import org.seasar.kvasir.page.ability.content.mock.MockContentAbility;
import org.seasar.kvasir.page.auth.mock.MockAuthPlugin;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.mock.MockPage;
import org.seasar.kvasir.page.type.mock.MockDirectory;


public class ListingPopTest extends PopTestCase<ListingPop>
{
    private TimeZone defaultTimeZone_;

    private Locale defaultLocale_;


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        defaultLocale_ = Locale.getDefault();
        defaultTimeZone_ = TimeZone.getDefault();
    }


    @Override
    protected void tearDown()
        throws Exception
    {
        if (defaultTimeZone_ != null) {
            TimeZone.setDefault(defaultTimeZone_);
        }
        if (defaultLocale_ != null) {
            Locale.setDefault(defaultLocale_);
        }

        super.tearDown();
    }


    @Override
    protected Class<ListingPop> getPopClass()
    {
        return ListingPop.class;
    }


    @Override
    protected String getPopId()
    {
        return ListingPop.ID;
    }


    @Override
    protected String getPluginId()
    {
        return ToolboxPlugin.ID;
    }


    public void testRender()
        throws Exception
    {
        Locale.setDefault(Locale.ENGLISH);
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

        setProperty(ListingPop.PROP_SUMMARYLENGTH, String.valueOf(16));

        final Page[] children = new Page[2];
        Page page = registerPage(new MockDirectory(1000, "/dir") {
            @Override
            public Page[] getChildren(PageCondition cond)
            {
                return children;
            }
        });
        MockPage page1 = registerPage(new MockPage(1001, "/dir/page1"));
        children[0] = page1;
        page1.registerAbility(ContentAbility.class, new MockContentAbility(
            page1).setLatestContent(Locale.ENGLISH, new MockContent()
            .setBodyHTMLString("This is Summary1!")));
        page1.setModifyDate(new Date(0L));

        MockPage page2 = registerPage(new MockPage(1002, "/dir/page2"));
        children[1] = page2;
        page2.registerAbility(ContentAbility.class, new MockContentAbility(
            page1).setLatestContent(Locale.ENGLISH, new MockContent()
            .setBodyHTMLString("Summary2")));
        page2.setModifyDate(new Date(0L));

        ListingPop target = newPopInstance();
        target.setAuthPlugin(new MockAuthPlugin());
        RenderedPop actual = target.render(newContext(null, page),
            new String[0]);

        assertEquals("Pages", actual.getTitle());
        assertEquals(readText("testRender_expected.txt"), normalizeText(actual
            .getBody()));
    }


    public void testRender2_concealedなページは一覧に含めないこと()
        throws Exception
    {
        Locale.setDefault(Locale.ENGLISH);
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

        setProperty(ListingPop.PROP_SUMMARYLENGTH, String.valueOf(16));

        final Page[] children = new Page[2];
        Page page = registerPage(new MockDirectory(1000, "/dir") {
            @Override
            public Page[] getChildren(PageCondition cond)
            {
                if (cond.isIncludeConcealed()) {
                    return children;
                } else {
                    return new Page[] { children[0] };
                }
            }
        });
        MockPage page1 = registerPage(new MockPage(1001, "/dir/page1"));
        children[0] = page1;
        page1.registerAbility(ContentAbility.class, new MockContentAbility(
            page1).setLatestContent(Locale.ENGLISH, new MockContent()
            .setBodyHTMLString("This is Summary1!")));
        page1.setModifyDate(new Date(0L));

        MockPage page2 = registerPage(new MockPage(1002, "/dir/page2")
            .setConcealed(true));
        children[1] = page2;
        page2.registerAbility(ContentAbility.class, new MockContentAbility(
            page1).setLatestContent(Locale.ENGLISH, new MockContent()
            .setBodyHTMLString("Summary2")));
        page2.setModifyDate(new Date(0L));

        ListingPop target = newPopInstance();
        target.setAuthPlugin(new MockAuthPlugin());
        RenderedPop actual = target.render(newContext(null, page),
            new String[0]);

        assertEquals("Pages", actual.getTitle());
        assertEquals(readText("testRender2_expected.txt"), normalizeText(actual
            .getBody()));
    }


    public void testRender3_administratorでログインしている場合はconcealedなページも一覧に含めること()
        throws Exception
    {
        Locale.setDefault(Locale.ENGLISH);
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

        setProperty(ListingPop.PROP_SUMMARYLENGTH, String.valueOf(16));

        final Page[] children = new Page[2];
        Page page = registerPage(new MockDirectory(1000, "/dir") {
            @Override
            public Page[] getChildren(PageCondition cond)
            {
                if (cond.isIncludeConcealed()) {
                    return children;
                } else {
                    return new Page[] { children[0] };
                }
            }
        });
        MockPage page1 = registerPage(new MockPage(1001, "/dir/page1"));
        children[0] = page1;
        page1.registerAbility(ContentAbility.class, new MockContentAbility(
            page1).setLatestContent(Locale.ENGLISH, new MockContent()
            .setBodyHTMLString("This is Summary1!")));
        page1.setModifyDate(new Date(0L));

        MockPage page2 = registerPage(new MockPage(1002, "/dir/page2")
            .setConcealed(true));
        children[1] = page2;
        page2.registerAbility(ContentAbility.class, new MockContentAbility(
            page1).setLatestContent(Locale.ENGLISH, new MockContent()
            .setBodyHTMLString("Summary2")));
        page2.setModifyDate(new Date(0L));

        ListingPop target = newPopInstance();
        MockAuthPlugin authPlugin = new MockAuthPlugin();
        authPlugin.actAs(getPageAlfr().getAdministrator());
        target.setAuthPlugin(authPlugin);
        RenderedPop actual = target.render(newContext(null, page),
            new String[0]);

        assertEquals("Pages", actual.getTitle());
        assertEquals(readText("testRender3_expected.txt"), normalizeText(actual
            .getBody()));
    }
}
