package org.seasar.kvasir.base.plugin.descriptor;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;


public class ImportTest extends XOMBeanTestCase
{
    public void testToXML()
        throws Exception
    {
        Import actual = new Import();
        actual.setPlugin("PLUGIN");
        assertBeanEquals("<import plugin=\"PLUGIN\" />", actual);
    }
}
