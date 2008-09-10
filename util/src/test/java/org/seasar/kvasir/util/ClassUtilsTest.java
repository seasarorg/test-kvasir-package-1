package org.seasar.kvasir.util;

import junit.framework.TestCase;


public class ClassUtilsTest extends TestCase
{
    public void testGetExtClassLoader()
        throws Exception
    {
        ClassLoader actual = ClassUtils.getExtClassLoader();
        assertNotNull(actual);
        assertSame(Object.class.getClassLoader(), actual.getParent());
    }


    public void testGetSubInterface()
        throws Exception
    {
        assertEquals(ISubHoe.class, ClassUtils.getSubInterface(SubHoe.class,
            IHoe.class));
    }
}
