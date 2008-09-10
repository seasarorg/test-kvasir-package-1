package org.seasar.kvasir.base.descriptor;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;


public class ExtensionElementTest extends XOMBeanTestCase
{
    public void testToXML()
        throws Exception
    {
        assertBeanEquals("<element />", new Element());
    }
}
