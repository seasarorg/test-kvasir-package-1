package org.seasar.kvasir.util.io.impl;

import org.seasar.kvasir.util.io.Resource;

import junit.framework.TestCase;


public class JavaResourceTest extends TestCase
{
    public void test_ルート直下のリソースのParentのChildが自分自身になること()
        throws Exception
    {
        Resource target = new JavaResource("resource.txt");

        assertEquals(target.getParentResource().getChildResource(
            target.getName()), target);
    }
}
