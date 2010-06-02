package org.seasar.kvasir.page.condition;

import junit.framework.TestCase;


public class PageConditionWithHeimIdTest extends TestCase
{
    public void test_hashCode()
        throws Exception
    {
        assertEquals(new PageConditionWithHeimId(new PageCondition(), 0)
            .hashCode(), new PageConditionWithHeimId(new PageCondition(), 0)
            .hashCode());
    }
}
