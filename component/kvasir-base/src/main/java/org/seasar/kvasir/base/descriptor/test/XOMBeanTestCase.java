package org.seasar.kvasir.base.descriptor.test;

import junit.framework.TestCase;

import org.seasar.kvasir.base.util.XOMUtils;


abstract public class XOMBeanTestCase extends TestCase
{
    public static final String XMLHEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";


    protected void assertBeanEquals(String expectedByXML, Object actual)
        throws Exception
    {
        assertBeanEquals(null, expectedByXML, actual);
    }


    protected void assertBeanEquals(String message, String expectedByXML,
        Object actual)
        throws Exception
    {
        assertEquals(message,
            XOMUtils.toXML(XOMUtils.toElement(expectedByXML)), XOMUtils
                .toXML(XOMUtils.toElement(XOMUtils.toXML(actual))));
    }
}
