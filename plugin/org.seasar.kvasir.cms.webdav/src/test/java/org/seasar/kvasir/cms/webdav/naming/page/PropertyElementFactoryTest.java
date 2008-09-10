package org.seasar.kvasir.cms.webdav.naming.page;

import org.seasar.kvasir.cms.webdav.naming.page.PageElementFactory.ResourceEntry;

import junit.framework.TestCase;


public class PropertyElementFactoryTest extends TestCase
{
    private PropertyElementFactory target_ = new PropertyElementFactory();


    public void testAnalyzeResourceName1()
        throws Exception
    {
        assertNull(target_.analyzeResourceName(null));
    }


    public void testAnalyzeResourceName2()
        throws Exception
    {
        assertNull(target_.analyzeResourceName("page.html"));
    }


    public void testAnalyzeResourceName3()
        throws Exception
    {
        ResourceEntry actual = target_
            .analyzeResourceName("page.html$property.xproperties");
        assertNotNull(actual);
        assertEquals("page.html", actual.getName());
        assertEquals("", actual.getVariant());
    }


    public void testAnalyzeResourceName4()
        throws Exception
    {
        ResourceEntry actual = target_
            .analyzeResourceName("page.html$property$ja.xproperties");
        assertNotNull(actual);
        assertEquals("page.html", actual.getName());
        assertEquals("ja", actual.getVariant());
    }
}
