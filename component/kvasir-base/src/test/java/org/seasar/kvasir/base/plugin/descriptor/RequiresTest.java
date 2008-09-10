package org.seasar.kvasir.base.plugin.descriptor;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;


public class RequiresTest extends XOMBeanTestCase
{
    public void testToXML()
        throws Exception
    {
        assertBeanEquals("<requires />", new Requires());
    }
}
