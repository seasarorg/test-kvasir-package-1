package org.seasar.kvasir.page;

import junit.framework.TestCase;

import org.seasar.kvasir.page.mock.MockPage;
import org.seasar.kvasir.page.mock.MockPageAlfr;


/**
 * @author YOKOTA Takehiko
 */
public class PageUtilsTest extends TestCase
{
    /*
     * boolean isLooped のテスト中のクラス(String, String)
     */
    public void testIsLoopedStringString()
    {
        assertTrue("ループと判定されること", PageUtils.isLooped("/a", "/a"));
        assertTrue("ループと判定されること", PageUtils.isLooped("/path/to/page",
            "/path/to/page/sub"));
        assertTrue("ループと判定されること", PageUtils.isLooped("", "/path/to/page/sub"));
        assertFalse("ループと判定されないこと", PageUtils.isLooped("/path/to/page/sub",
            "/path/to/page"));
        assertFalse("ループと判定されないこと", PageUtils.isLooped("/path/to/page/sub", ""));
    }


    public void testGetAbsolutePathname()
        throws Exception
    {
        Page page = new MockPage(100, PathId.HEIM_MIDGARD, "/path/to/dir") {
            @Override
            public boolean isNode()
            {
                return true;
            }


            @Override
            public Page getLord()
            {
                return new MockPage(101, PathId.HEIM_MIDGARD, "/path/to") {};
            }
        };
        Page nonNodePage = new MockPage(100, PathId.HEIM_MIDGARD,
            "/path/to/page") {
            @Override
            public boolean isNode()
            {
                return false;
            }


            @Override
            public Page getLord()
            {
                return new MockPage(101, PathId.HEIM_MIDGARD, "/path/to") {};
            }
        };

        assertEquals("絶対パスが正しく置換できること", "/path/to/page", PageUtils
            .getAbsolutePathname("/path/to/page", page));

        assertEquals("ベースPageがnodeである場合に相対パスが正しく置換できること", "/path/to/dir/page",
            PageUtils.getAbsolutePathname("page", page));

        assertEquals("ベースPageがnodeでない場合に相対パスが正しく置換できること", "/path/to/page/page",
            PageUtils.getAbsolutePathname("page", nonNodePage));

        assertEquals("Lord相対パスが正しく置換できること", "/path/to/path/to/page", PageUtils
            .getAbsolutePathname("@/path/to/page", page));

        assertEquals("ルートパスが正しく置換できること", "", PageUtils.getAbsolutePathname("",
            page));

        assertEquals("親ページへのパスが正しく置換できること", "/path/to", PageUtils
            .getAbsolutePathname("..", page));

        assertEquals("親ページへのパスが正しく置換できること2", "/path/page", PageUtils
            .getAbsolutePathname("../../page", page));

        assertEquals("カレントページへのパスが正しく置換できること", "/path/to/dir", PageUtils
            .getAbsolutePathname(".", page));
    }


    public void testDecodeToPage_指定されたPageと同じHeimに見つからなかった場合はMIDGARDを見に行くこと()
        throws Exception
    {
        final Page expected = new MockPage(1, PathId.HEIM_MIDGARD, "/roles/all");
        PageAlfr pageAlfr = new MockPageAlfr() {
            @Override
            public Page getPage(int heimId, String pathname)
            {
                if (heimId == PathId.HEIM_MIDGARD
                    && pathname.equals("/roles/all")) {
                    return expected;
                } else {
                    return null;
                }
            }
        };
        Page basePage = new MockPage(2, PathId.HEIM_ALFHEIM, "/hoe");
        PageUtils.setPageAlfr(pageAlfr);

        assertSame(expected, PageUtils.decodeToPage(basePage, "/roles/all"));
    }


    public void testDecodeToPage_ベースPageの位置からの相対パスから作成した絶対パスに対応するPageがない場合はルートからの相対パスとみなしてPageを見に行くこと()
        throws Exception
    {
        final Page expected = new MockPage(1, PathId.HEIM_MIDGARD, "/roles/all");
        PageAlfr pageAlfr = new MockPageAlfr() {
            @Override
            public Page getPage(int heimId, String pathname)
            {
                if (heimId == PathId.HEIM_MIDGARD
                    && pathname.equals("/roles/all")) {
                    return expected;
                } else {
                    return null;
                }
            }
        };
        Page basePage = new MockPage(2, PathId.HEIM_ALFHEIM, "/hoe");
        PageUtils.setPageAlfr(pageAlfr);

        assertSame(expected, PageUtils.decodeToPage(basePage, "roles/all"));
    }


    public void testGetFilePath()
        throws Exception
    {
        assertEquals("/010", PageUtils.getFilePath(10));
        assertEquals("/535/065", PageUtils.getFilePath(65535));
    }


    public void testEncodePathname_DecodeToPage_操作が対称であること()
        throws Exception
    {
        final Page rootPage = new MockPage(1, PathId.HEIM_MIDGARD, "") {
            @Override
            public boolean isRoot()
            {
                return true;
            }


            @Override
            public Page getLord()
            {
                return this;
            }
        };
        final Page lordPage = new MockPage(10, PathId.HEIM_MIDGARD, "/lord") {
            @Override
            public Page getLord()
            {
                return this;
            }


            @Override
            public Page getParent()
            {
                return rootPage;
            }
        };
        final Page dirPage = new MockPage(10, PathId.HEIM_MIDGARD, "/lord/dir") {
            @Override
            public Page getLord()
            {
                return lordPage;
            }


            @Override
            public Page getParent()
            {
                return lordPage;
            }
        };
        final Page page = new MockPage(10, PathId.HEIM_MIDGARD,
            "/lord/dir/page") {
            @Override
            public Page getLord()
            {
                return lordPage;
            }


            @Override
            public Page getParent()
            {
                return dirPage;
            }
        };
        PageAlfr pageAlfr = new MockPageAlfr() {
            @Override
            public Page getPage(int heimId, String pathname)
            {
                if (heimId != PathId.HEIM_MIDGARD) {
                    return null;
                }
                if (pathname.equals("")) {
                    return rootPage;
                } else if (pathname.equals("/lord")) {
                    return lordPage;
                } else if (pathname.equals("/lord/dir")) {
                    return dirPage;
                } else if (pathname.equals("/lord/dir/page")) {
                    return page;
                } else {
                    return null;
                }
            }
        };
        PageUtils.setPageAlfr(pageAlfr);

        assertEquals(page, PageUtils.decodeToPage(page, PageUtils
            .encodePathname(page, "/lord/dir/page")));
        assertEquals(dirPage, PageUtils.decodeToPage(page, PageUtils
            .encodePathname(page, "/lord/dir")));
        assertEquals(lordPage, PageUtils.decodeToPage(page, PageUtils
            .encodePathname(page, "/lord")));
        assertEquals(rootPage, PageUtils.decodeToPage(page, PageUtils
            .encodePathname(page, "")));

        assertEquals(page, PageUtils.decodeToPage(dirPage, PageUtils
            .encodePathname(dirPage, "/lord/dir/page")));
        assertEquals(dirPage, PageUtils.decodeToPage(dirPage, PageUtils
            .encodePathname(dirPage, "/lord/dir")));
        assertEquals(lordPage, PageUtils.decodeToPage(dirPage, PageUtils
            .encodePathname(dirPage, "/lord")));
        assertEquals(rootPage, PageUtils.decodeToPage(dirPage, PageUtils
            .encodePathname(dirPage, "")));

        assertEquals(page, PageUtils.decodeToPage(lordPage, PageUtils
            .encodePathname(lordPage, "/lord/dir/page")));
        assertEquals(dirPage, PageUtils.decodeToPage(lordPage, PageUtils
            .encodePathname(lordPage, "/lord/dir")));
        assertEquals(lordPage, PageUtils.decodeToPage(lordPage, PageUtils
            .encodePathname(lordPage, "/lord")));
        assertEquals(rootPage, PageUtils.decodeToPage(lordPage, PageUtils
            .encodePathname(lordPage, "")));

        assertEquals(page, PageUtils.decodeToPage(rootPage, PageUtils
            .encodePathname(rootPage, "/lord/dir/page")));
        assertEquals(dirPage, PageUtils.decodeToPage(rootPage, PageUtils
            .encodePathname(rootPage, "/lord/dir")));
        assertEquals(lordPage, PageUtils.decodeToPage(rootPage, PageUtils
            .encodePathname(rootPage, "/lord")));
        assertEquals(rootPage, PageUtils.decodeToPage(rootPage, PageUtils
            .encodePathname(rootPage, "")));
    }


    public void testTokenizePathname()
        throws Exception
    {
        String[] actual = PageUtils.tokenizePathname("/a/b/c/");

        assertNotNull(actual);
        assertEquals(5, actual.length);
        int idx = 0;
        assertEquals("", actual[idx++]);
        assertEquals("a", actual[idx++]);
        assertEquals("b", actual[idx++]);
        assertEquals("c", actual[idx++]);
        assertEquals("", actual[idx++]);
    }


    public void testToRelativePathname()
        throws Exception
    {
        assertEquals("絶対URLの場合", "http://localhost:8080/hoe/fuga", PageUtils
            .toRelativePathname("http://localhost:8080/hoe/fuga", PageUtils
                .tokenizePathname("/a/b/c.html")));

        assertEquals("相対URLの場合", "a/b/c", PageUtils.toRelativePathname("a/b/c",
            PageUtils.tokenizePathname("/a/b/c.html")));

        assertEquals("同一階層の場合", "./d.html", PageUtils.toRelativePathname(
            "/a/b/d.html", PageUtils.tokenizePathname("/a/b/c.html")));

        assertEquals("ベースよりも変換対象が長い場合", "../../e/f/g/h.html", PageUtils
            .toRelativePathname("/a/b/e/f/g/h.html", PageUtils
                .tokenizePathname("/a/b/c/d/e.html")));

        assertEquals("ベースよりも変換対象が短い場合", "../../d.html", PageUtils
            .toRelativePathname("/a/b/c/d.html", PageUtils
                .tokenizePathname("/a/b/c/d/e/f.html")));

        assertEquals("ベースとルートしか一致しない場合", "../../d/e/f.html", PageUtils
            .toRelativePathname("/d/e/f.html", PageUtils
                .tokenizePathname("/a/b/c.html")));
    }
}
