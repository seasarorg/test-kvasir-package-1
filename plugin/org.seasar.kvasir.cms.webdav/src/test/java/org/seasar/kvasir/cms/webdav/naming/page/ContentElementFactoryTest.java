package org.seasar.kvasir.cms.webdav.naming.page;

import junit.framework.TestCase;

import org.seasar.kvasir.cms.webdav.naming.page.PageElementFactory.ResourceEntry;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.mock.MockPageAlfr;


public class ContentElementFactoryTest extends TestCase
{
    private ContentElementFactory target_ = new ContentElementFactory() {
        @Override
        protected int getHeimId()
        {
            return PathId.HEIM_MIDGARD;
        }


        @Override
        protected boolean resourceExists(PageElement element)
        {
            return false;
        }
    };


    @Override
    protected void setUp()
        throws Exception
    {
        target_.setPageAlfr(new MockPageAlfr());
    }


    public void testAnalyze1()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName(null);
        assertNull(actual);
    }


    public void testAnalyze2()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName("");
        assertNull(actual);
    }


    public void testAnalyze3()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName("hoehoe");
        assertNull(actual);
    }


    public void testAnalyze5()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName("page$$ja.html");
        assertNotNull(actual);
        assertEquals("page.html", actual.getName());
        assertEquals("", actual.getType());
        assertEquals("ja", actual.getVariant());
    }


    public void testAnalyze6()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName("dir$$");
        assertNotNull(actual);
        assertEquals("dir", actual.getName());
        assertEquals("", actual.getType());
        assertEquals(Page.VARIANT_DEFAULT, actual.getVariant());
    }


    public void testAnalyze7()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName("dir$$ja");
        assertNotNull(actual);
        assertEquals("dir", actual.getName());
        assertEquals("", actual.getType());
        assertEquals("ja", actual.getVariant());
    }


    public void testAnalyze8()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName("direc$$.tory");
        assertNotNull(actual);
        assertEquals("direc.tory", actual.getName());
        assertEquals("", actual.getType());
        assertEquals(Page.VARIANT_DEFAULT, actual.getVariant());
    }


    public void testAnalyze9()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName("page$attached");
        assertNull(actual);
    }


    public void testAnalyze10()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName("icon.gif$attached");
        assertNull(actual);
    }
}
