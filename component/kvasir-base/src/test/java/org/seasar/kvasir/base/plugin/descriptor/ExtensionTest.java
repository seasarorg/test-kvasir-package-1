package org.seasar.kvasir.base.plugin.descriptor;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;


public class ExtensionTest extends XOMBeanTestCase
{
    public void testToXML()
        throws Exception
    {
        Extension actual = new Extension();
        actual.setPoint("POINT");
        assertBeanEquals("<extension point=\"POINT\" />", actual);
    }
}
