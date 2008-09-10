package org.seasar.kvasir.base.classloader;

import junit.framework.TestCase;


public class CompositeClassLoaderTest extends TestCase
{
    public void test1()
        throws Exception
    {
        CompositeClassLoader target = new CompositeClassLoader(
            new ClassLoader[0]);
        try {
            target.loadClass(CompositeClassLoader.class.getName());
            fail("空にした場合は何も見えないこと");
        } catch (ClassNotFoundException expected) {
        }
        try {
            target.loadClass(Object.class.getName());
        } catch (ClassNotFoundException expected) {
            fail("空にしてもシステムクラスは見えること");
        }
    }
}
