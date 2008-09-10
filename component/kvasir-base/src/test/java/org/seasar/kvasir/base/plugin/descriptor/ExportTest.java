package org.seasar.kvasir.base.plugin.descriptor;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;


public class ExportTest extends XOMBeanTestCase
{
    public void testToXML()
        throws Exception
    {
        Export actual = new Export();
        actual.setName("NAME");
        assertBeanEquals("<export name=\"NAME\" />", actual);
    }
}
