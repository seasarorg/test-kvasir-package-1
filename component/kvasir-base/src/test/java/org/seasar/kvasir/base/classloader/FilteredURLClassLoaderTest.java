package org.seasar.kvasir.base.classloader;

import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.TestCase;

import org.seasar.kvasir.util.ClassUtils;


public class FilteredURLClassLoaderTest extends TestCase
{
    public void testFilterResource()
        throws Exception
    {
        URL url = ClassUtils.getURLForURLClassLoader(ClassUtils
            .getBaseDirectory(getClass()));
        FilteredURLClassLoader target = new FilteredURLClassLoader(
            new FilteredURLClassLoader.FilteredURL[] { new FilteredURLClassLoader.FilteredURL(
                url, new String[0], new String[0]) }, new URLClassLoader(
                new URL[] { url }, null), null);

        assertNull("リソースフィルタ文字列を指定しなかった場合は何も見えないこと", target
            .getResource("kvasir.xproperties"));
    }


    public void testFilterResource2()
        throws Exception
    {
        URL url = ClassUtils.getURLForURLClassLoader(ClassUtils
            .getBaseDirectory(getClass()));
        FilteredURLClassLoader target = new FilteredURLClassLoader(
            new FilteredURLClassLoader.FilteredURL[] { new FilteredURLClassLoader.FilteredURL(
                url, new String[0], new String[] { "*.xproperties" }) },
            new URLClassLoader(new URL[] { url }, null), null);

        assertNotNull("リソースフィルタ文字列にマッチしたリソースは見えること", target
            .getResource("kvasir.xproperties"));
    }
}
