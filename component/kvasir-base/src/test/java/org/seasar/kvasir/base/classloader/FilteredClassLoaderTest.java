package org.seasar.kvasir.base.classloader;

import junit.framework.TestCase;

import org.seasar.kvasir.base.classloader.sub.C;


public class FilteredClassLoaderTest extends TestCase
{
    private FilteredClassLoader newFilteredClassLoader(String pattern)
    {
        return new FilteredClassLoader(getClass().getClassLoader(),
            new String[] { pattern }, new String[] {});
    }


    public void testLoadClass1()
        throws Exception
    {
        FilteredClassLoader cl = newFilteredClassLoader(A.class.getName());
        try {
            cl.loadClass(A.class.getName());
        } catch (ClassNotFoundException ex) {
            fail();
        }
        try {
            cl.loadClass(B.class.getName());
            fail();
        } catch (ClassNotFoundException expected) {
        }
    }


    public void testLoadClass2()
        throws Exception
    {
        FilteredClassLoader cl = new FilteredClassLoader(getClass()
            .getClassLoader(), new String[] { "!" + A.class.getName(), "**" },
            new String[] {});
        try {
            cl.loadClass(A.class.getName());
            fail();
        } catch (ClassNotFoundException expected) {
        }
        try {
            cl.loadClass(B.class.getName());
        } catch (ClassNotFoundException ex) {
            fail();
        }
    }


    public void testLoadClass3()
        throws Exception
    {
        FilteredClassLoader cl = newFilteredClassLoader("org.seasar.kvasir.base.classloader.*");
        try {
            cl.loadClass(A.class.getName());
        } catch (ClassNotFoundException ex) {
            fail();
        }
        try {
            cl.loadClass(C.class.getName());
            fail();
        } catch (ClassNotFoundException expected) {
        }
    }


    public void testLoadClass4()
        throws Exception
    {
        FilteredClassLoader cl = newFilteredClassLoader("org.seasar.kvasir.base.classloader.**");
        try {
            cl.loadClass(A.class.getName());
        } catch (ClassNotFoundException ex) {
            fail();
        }
        try {
            cl.loadClass(C.class.getName());
        } catch (ClassNotFoundException ex) {
            fail();
        }
    }
}
