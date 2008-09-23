package org.seasar.kvasir.page.search.impl;

import junit.framework.TestCase;


public class PositionRecorderImplTest extends TestCase
{
    private PositionRecorderImpl target = new PositionRecorderImpl();


    public void testGetRawPosition()
        throws Exception
    {
        assertEquals(0, target.getRawPosition(0));

        target.record(0, 0);
        target.record(1, 2);
        target.record(2, 3);
        target.record(3, 5);
        target.record(4, 6);

        assertEquals(0, target.getRawPosition(0));
        assertEquals(2, target.getRawPosition(1));
        assertEquals(3, target.getRawPosition(2));
        assertEquals(5, target.getRawPosition(3));
        assertEquals(6, target.getRawPosition(4));
        assertEquals(7, target.getRawPosition(5));
        assertEquals(8, target.getRawPosition(6));

        target.record(5, 8);

        assertEquals(8, target.getRawPosition(5));
        assertEquals(9, target.getRawPosition(6));
    }
}
