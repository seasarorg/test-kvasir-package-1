package org.seasar.kvasir.cms.webdav.naming.page;

import junit.framework.TestCase;

import org.seasar.kvasir.cms.webdav.naming.page.PageElementFactory.ResourceEntry;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.mock.MockPageAlfr;


public class TemplateElementFactoryTest extends TestCase
{
    private TemplateElementFactory target_ = new TemplateElementFactory() {
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
        target_.setPageAlfr(new MockPageAlfr());
    }


    public void testAnalyze1()
        throws Exception
    {
        ResourceEntry bag = target_.analyzeResourceName(null);
        assertNull(bag);
    }


    public void testAnalyze2()
        throws Exception
    {
        ResourceEntry bag = target_.analyzeResourceName("root$template.zpt");
        assertNotNull(bag);
        assertEquals("root", bag.getName());
        assertEquals("zpt", bag.getType());
        assertEquals(Page.VARIANT_DEFAULT, bag.getVariant());
    }


    public void testAnalyze3()
        throws Exception
    {
        ResourceEntry bag = target_.analyzeResourceName("page.html$template.zpt");
        assertNotNull(bag);
        assertEquals("page.html", bag.getName());
        assertEquals("zpt", bag.getType());
        assertEquals(Page.VARIANT_DEFAULT, bag.getVariant());
    }


    public void testAnalyze4()
        throws Exception
    {
        ResourceEntry bag = target_.analyzeResourceName("page.html$template.zpt.java");
        assertNotNull(bag);
        assertEquals("page.html", bag.getName());
        assertEquals("zpt", bag.getType());
        assertEquals("java", bag.getVariant());
    }
}
