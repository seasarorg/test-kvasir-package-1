package org.seasar.kvasir.cms.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.mock.MockPropertyAbility;
import org.seasar.kvasir.page.mock.MockPage;
import org.seasar.kvasir.page.test.PageTestCase;
import org.seasar.kvasir.page.type.mock.MockDirectory;
import org.seasar.kvasir.webapp.mock.MockHttpServletRequest;
import org.seasar.kvasir.webapp.mock.MockHttpServletResponse;


public class PresentationUtilsTest extends PageTestCase
{
    HttpServletRequest request_;

    HttpServletResponse response_;

    Page page_;


    @Override
    protected void tearDown()
        throws Exception
    {
        PageUtils.setPageAlfr(null);

        super.tearDown();
    }


    private void setUpForReplacePageURITest()
    {
        request_ = new MockHttpServletRequest() {
            @Override
            public String getContextPath()
            {
                return "/context";
            }
        };
        response_ = new MockHttpServletResponse() {
            @Override
            public String encodeURL(String url)
            {
                return url + ";jsessionid=X";
            }
        };

        MockDirectory lordPage = new MockDirectory(1000, "/path/to");
        lordPage.setAsLord(true);
        registerPage(lordPage);
        page_ = registerPage(new MockDirectory(1001, "/path/to/dir"));
        registerPage(new MockDirectory(1001, "/path/to/page"));
        registerPage(new MockDirectory(1001, "/path/to/dir/page"));
        registerPage(new MockDirectory(1001, "/path/to/path/to/page"));

        PageUtils.setPageAlfr(getPageAlfr());
    }


    public void testReplacePageURI()
        throws Exception
    {
        setUpForReplacePageURITest();

        assertEquals("\"/context/path/to/page;jsessionid=X\"",
            PresentationUtils.replacePageURI("\"page:/path/to/page\"",
                request_, response_, page_));
        assertEquals("'/context/path/to/page;jsessionid=X'", PresentationUtils
            .replacePageURI("'page:/path/to/page'", request_, response_, page_));
        assertEquals("(/context/path/to/page;jsessionid=X)", PresentationUtils
            .replacePageURI("(page:/path/to/page)", request_, response_, page_));
        assertEquals("\"/context/path/to/page;jsessionid=X?a=b\"",
            PresentationUtils.replacePageURI("\"page:/path/to/page?a=b\"",
                request_, response_, page_));
    }


    public void testReplacePageURI2()
        throws Exception
    {
        setUpForReplacePageURITest();

        assertEquals("絶対パスが正しく置換できること",
            "<a href=\"/context/path/to/page;jsessionid=X\">LINK</a>",
            PresentationUtils.replacePageURI(
                "<a href=\"page:/path/to/page\">LINK</a>", request_, response_,
                page_));

        assertEquals("相対パスが正しく置換できること",
            "<a href=\"/context/path/to/dir/page;jsessionid=X\">LINK</a>",
            PresentationUtils.replacePageURI("<a href=\"page:page\">LINK</a>",
                request_, response_, page_));

        assertEquals("Lord相対パスが正しく置換できること",
            "<a href=\"/context/path/to/path/to/page;jsessionid=X\">LINK</a>",
            PresentationUtils.replacePageURI(
                "<a href=\"page:@/path/to/page\">LINK</a>", request_,
                response_, page_));

        assertEquals("ルートパスが正しく置換できること",
            "<a href=\"/context/;jsessionid=X\">LINK</a>", PresentationUtils
                .replacePageURI("<a href=\"page:\">LINK</a>", request_,
                    response_, page_));

        request_ = new MockHttpServletRequest() {
            @Override
            public String getContextPath()
            {
                return "";
            }
        };

        assertEquals("ルートパス＋コンテキストルートが空文字列、が正しく置換できること",
            "<a href=\"/;jsessionid=X\">LINK</a>", PresentationUtils
                .replacePageURI("<a href=\"page:\">LINK</a>", request_,
                    response_, page_));
    }


    public void testReplacePageURI3_存在しないページへのパスを指定された場合はページが存在しないことを表すクラス属性を追加すること()
        throws Exception
    {
        setUpForReplacePageURITest();

        assertEquals(
            "<a href=\"/context/path/to/dir/none;jsessionid=X\" class=\"pageNotFound\">LINK</a>",
            PresentationUtils.replacePageURI("<a href=\"page:none\">LINK</a>",
                request_, response_, page_));

        assertEquals(
            "<a class=\"link pageNotFound\" href=\"/context/path/to/dir/none;jsessionid=X\">LINK</a>",
            PresentationUtils.replacePageURI(
                "<a class=\"link\" href=\"page:none\">LINK</a>", request_,
                response_, page_));
    }


    public void testGetIconURL_page接頭辞をつけるとページ相対パスとみなすこと()
        throws Exception
    {
        MockPage rootPage = new MockPage(1, "");
        rootPage.setAsLord(true);
        MockPage page = new MockPage(1000, "/path/to/page")
            .setParent(new MockPage(1001, "/path/to").setParent(new MockPage(
                1002, "/path").setParent(rootPage)));
        MockPropertyAbility ability = new MockPropertyAbility(page);
        ability.setProperty(PresentationUtils.PROP_PAGE_ICON, "page:icon.jpg");
        page.registerAbility(PropertyAbility.class, ability);
        assertEquals("/context/path/to/page/icon.jpg", PresentationUtils
            .getIconURL(page, "/context"));
    }
}
