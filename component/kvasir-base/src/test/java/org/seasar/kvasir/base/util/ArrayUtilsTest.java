package org.seasar.kvasir.base.util;

import junit.framework.TestCase;


public class ArrayUtilsTest extends TestCase
{
    public void testCast()
        throws Exception
    {
        Number[] numbers = new Number[] { new Integer(0), new Integer(1) };
        try {
            Integer[] actual = ArrayUtils.downcast(numbers, Integer.class);
            assertEquals(2, actual.length);
            assertSame(numbers[0], actual[0]);
            assertSame(numbers[1], actual[1]);
        } catch (ClassCastException ex) {
            fail();
        }
    }
}
