package org.seasar.kvasir.cms.webdav.naming.page;

import junit.framework.TestCase;

import org.seasar.kvasir.cms.webdav.naming.page.PageElementFactory.ResourceEntry;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;


public class BasePageElementFactoryTest extends TestCase
{
    private BasePageElementFactory target_ = new BasePageElementFactory() {
        @Override
        protected int getHeimId()
        {
            return PathId.HEIM_MIDGARD;
        }
    };


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        target_.setDirContext(new PageElementDirContext(null,
            new PageElementFactory[0], null, null));
    }


    public void testAnalyze()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName(null);
        assertNotNull(actual);
        assertEquals(null, actual.getName());
        assertEquals("superroot", actual.getType());
        assertEquals(Page.VARIANT_DEFAULT, actual.getVariant());
    }


    public void testAnalyze2()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName("page$attached");

        assertNotNull(actual);
        assertEquals("page", actual.getName());
        assertEquals("", actual.getVariant());
        assertEquals("attached", actual.getType());
    }


    public void testAnalyze3()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName("");
        assertNotNull(actual);
        assertEquals("", actual.getName());
        assertEquals("", actual.getType());
        assertEquals(Page.VARIANT_DEFAULT, actual.getVariant());
    }


    public void testAnalyze4()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName("hoehoe");
        assertNotNull(actual);
        assertEquals("hoehoe", actual.getName());
        assertEquals("", actual.getType());
        assertEquals(Page.VARIANT_DEFAULT, actual.getVariant());
    }


    public void testAnalyze5()
        throws Exception
    {
        ResourceEntry actual = target_.analyzeResourceName("page.html");
        assertNotNull(actual);
        assertEquals("page.html", actual.getName());
        assertEquals("", actual.getType());
        assertEquals(Page.VARIANT_DEFAULT, actual.getVariant());
    }

}
