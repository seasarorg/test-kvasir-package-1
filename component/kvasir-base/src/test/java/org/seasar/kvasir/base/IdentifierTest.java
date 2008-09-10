package org.seasar.kvasir.base;

import junit.framework.TestCase;


public class IdentifierTest extends TestCase
{
    public void testEquals()
        throws Exception
    {
        assertEquals(new Identifier("hoge-1.0.0"), new Identifier("hoge-1.0.0"));
    }
}
