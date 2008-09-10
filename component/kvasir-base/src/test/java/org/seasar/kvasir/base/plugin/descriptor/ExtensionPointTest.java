package org.seasar.kvasir.base.plugin.descriptor;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;


public class ExtensionPointTest extends XOMBeanTestCase
{
    public void testToXML()
        throws Exception
    {
        ExtensionPoint actual = new ExtensionPoint();
        actual.setId("ID");
        actual.setElementClassName("ELEMENTCLASS");
        assertBeanEquals(
            "<extension-point element-class=\"ELEMENTCLASS\" id=\"ID\" />",
            actual);
    }
}
