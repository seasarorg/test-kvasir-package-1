package org.seasar.kvasir.page.condition;

import junit.framework.TestCase;


public class PageConditionTest extends TestCase
{
    public void testSetOrder()
        throws Exception
    {
        PageCondition target = new PageCondition();

        target.setOrder(new Order("1"));
        Order[] actual = target.getOrders();

        assertEquals(1, actual.length);
        int idx = 0;
        assertEquals("1", actual[idx++].getFieldName());
    }


    public void testSetOrders()
        throws Exception
    {
        PageCondition target = new PageCondition();

        target.setOrders(new Order[] { new Order("1"), new Order("2") });
        Order[] actual = target.getOrders();

        assertEquals(2, actual.length);
        int idx = 0;
        assertEquals("1", actual[idx++].getFieldName());
        assertEquals("2", actual[idx++].getFieldName());
    }


    public void test_hashCode()
        throws Exception
    {
        assertEquals(new PageCondition().hashCode(), new PageCondition()
            .hashCode());
    }
}
