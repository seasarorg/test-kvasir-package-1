package org.seasar.kvasir.base.plugin.descriptor;

import org.seasar.kvasir.base.descriptor.test.XOMBeanTestCase;


public class LibraryTest extends XOMBeanTestCase
{
    public void testToXML()
        throws Exception
    {
        Library actual = new Library();
        actual.setName("NAME");
        assertBeanEquals("<library name=\"NAME\" />", actual);
    }
}
