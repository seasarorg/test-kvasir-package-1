package org.seasar.kvasir.cms.webdav.naming.page;

import java.util.Hashtable;

import org.seasar.kvasir.cms.webdav.naming.InvalidPathException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbilityAlfr;
import org.seasar.kvasir.page.mock.MockPage;
import org.seasar.kvasir.page.test.PageTestCase;


public class PageElementDirContextTest extends PageTestCase
{
    private PageElementDirContext target_;


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        PageElementFactory[] pageElementFactories = new PageElementFactory[] {
            new BasePageElementFactory() {
                @Override
                protected Content getLatestContent(Page page, String variant)
                {
                    return null;
                }
            }, new ContentElementFactory() {
                @Override
                protected boolean resourceExists(PageElement element)
                {
                    return false;
                }


                @Override
                protected Content getLatestContent(Page page, String variant)
                {
                    return null;
                }
            }, new TemplateElementFactory() {
                @Override
                protected boolean resourceExists(PageElement element)
                {
                    return false;
                }
            } };
        target_ = new PageElementDirContext(new Hashtable<Object, Object>(),
            pageElementFactories, null, getPageAlfr()) {
            @Override
            public int getHeimId()
            {
                return PathId.HEIM_MIDGARD;
            }


            @Override
            Content getLatestDefaultContent(Page page)
            {
                return null;
            }
        };
        for (int i = 0; i < pageElementFactories.length; i++) {
            pageElementFactories[i].setDirContext(target_);
            pageElementFactories[i].setPageAlfr(getPageAlfr());
        }

        registerPage(new MockPage(1000, "/path/to/page"));
        registerPage(new MockPage(1001, "/path/to/page.html"));
    }


    public void testGetAttachedDirectoryName()
        throws Exception
    {
        assertNull(target_.getAttachedDirectoryName(null));

        assertEquals("page$attached", target_.getAttachedDirectoryName("page"));

        assertEquals("page.html$attached", target_
            .getAttachedDirectoryName("page.html"));
    }


    public void testGetNameFromAttachedDirectoryName()
        throws Exception
    {

        assertNull(target_.getNameFromAttachedDirectoryName(null));

        assertNull(target_.getNameFromAttachedDirectoryName("page"));

        assertEquals("page", target_
            .getNameFromAttachedDirectoryName("page$attached"));

        assertEquals("page.html", target_
            .getNameFromAttachedDirectoryName("page.html$attached"));
    }


    public void testGetPathnameFromPath()
        throws Exception
    {
        assertNull(target_.getPathnameFromPath(null));

        assertEquals("", target_.getPathnameFromPath(""));

        assertEquals("/path/to/page", target_
            .getPathnameFromPath("/path/to/page"));

        assertEquals("/path/to/page", target_
            .getPathnameFromPath("/path$attached/to/page$attached"));
    }


    public void testNewElement1()
        throws Exception
    {
        try {
            target_.newElement("");
        } catch (IllegalArgumentException ex) {
            fail();
        }
    }


    public void testNewElement2()
        throws Exception
    {
        try {
            target_.newElement("/hoehoe");
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals(InvalidPathException.class, expected.getCause()
                .getClass());
        }
    }


    public void testNewElement3()
        throws Exception
    {
        try {
            target_.newElement("/root2");
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals(InvalidPathException.class, expected.getCause()
                .getClass());
        }
    }


    public void testNewElement4()
        throws Exception
    {
        String path = "/root";
        PageElement actual = (PageElement)target_.newElement(path);
        assertEquals(path, actual.getPath());
        assertEquals("", actual.getPathname());
        assertEquals(Page.VARIANT_DEFAULT, actual.getVariant());
        assertEquals("page", actual.getAttribute());
        assertEquals("", actual.getType());
    }


    public void testNewElement5()
        throws Exception
    {
        String path = "/root$$";
        PageElement actual = (PageElement)target_.newElement(path);
        assertEquals(path, actual.getPath());
        assertEquals("", actual.getPathname());
        assertEquals(Page.VARIANT_DEFAULT, actual.getVariant());
        assertEquals(ContentAbilityAlfr.SHORTID, actual.getAttribute());
        assertEquals("", actual.getType());
    }


    public void testNewElement6()
        throws Exception
    {
        String path = "/root$$ja";
        PageElement actual = (PageElement)target_.newElement(path);
        assertEquals(path, actual.getPath());
        assertEquals("", actual.getPathname());
        assertEquals("ja", actual.getVariant());
        assertEquals(ContentAbilityAlfr.SHORTID, actual.getAttribute());
        assertEquals("", actual.getType());
    }


    public void testNewElement7()
        throws Exception
    {
        String path = "/root/skirnir_ja.jpg";
        PageElement actual = (PageElement)target_.newElement(path);
        assertEquals(path, actual.getPath());
        assertEquals("/skirnir_ja.jpg", actual.getPathname());
        assertEquals(Page.VARIANT_DEFAULT, actual.getVariant());
        assertEquals("page", actual.getAttribute());
        assertEquals("", actual.getType());
    }


    public void testNewElement8()
        throws Exception
    {
        String path = "/root/path/to/page$attached";
        PageElement actual = (PageElement)target_.newElement(path);
        assertEquals(path, actual.getPath());
        assertEquals("/path/to/page", actual.getPathname());
        assertEquals(Page.VARIANT_DEFAULT, actual.getVariant());
        assertEquals("page", actual.getAttribute());
        assertEquals("attached", actual.getType());
        assertTrue(actual.isCollection());
    }


    public void testNewElement9()
        throws Exception
    {
        String path = "/root/path/to$attached/page";
        PageElement actual = (PageElement)target_.newElement(path);
        assertEquals(path, actual.getPath());
        assertEquals("/path/to/page", actual.getPathname());
        assertEquals(Page.VARIANT_DEFAULT, actual.getVariant());
        assertEquals("page", actual.getAttribute());
        assertEquals("", actual.getType());
    }


    public void testNewElement10()
        throws Exception
    {
        String path = "/root/path/to/page.html$attached";
        PageElement actual = (PageElement)target_.newElement(path);
        assertEquals("/path/to/page.html", actual.getPathname());
        assertEquals(Page.VARIANT_DEFAULT, actual.getVariant());
        assertEquals("page", actual.getAttribute());
        assertEquals("attached", actual.getType());
    }
}
