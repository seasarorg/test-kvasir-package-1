package org.seasar.kvasir.base.mime.impl;

import junit.framework.TestCase;

import org.seasar.kvasir.base.mime.MimeMapping;
import org.seasar.kvasir.base.mime.extension.MimeMappingElement;


public class MimeMappingsImplTest extends TestCase
{
    private MimeMappingsImpl target_;


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        target_ = new MimeMappingsImpl(new MimeMapping[] {
            new MimeMappingElement("txt", "text/plain"),
            new MimeMappingElement("xml", "text/xml") });

    }


    public void testGetMappings()
        throws Exception
    {
        MimeMapping[] actual = target_.getMappings();

        assertEquals(2, actual.length);
        assertEquals("txt", actual[0].getExtension());
        assertEquals("text/plain", actual[0].getMimeType());
        assertEquals("xml", actual[1].getExtension());
        assertEquals("text/xml", actual[1].getMimeType());
    }


    public void testGetExtension()
        throws Exception
    {
        assertEquals("xml", target_.getExtension("text/xml"));
        assertNull(target_.getExtension("hoehoe"));
    }


    public void testGetMimeType()
        throws Exception
    {
        assertEquals("text/xml", target_.getMimeType("file.xml"));
        assertNull(target_.getMimeType("xml"));
        assertNull(target_.getMimeType("file.hoehoe"));
    }
}
