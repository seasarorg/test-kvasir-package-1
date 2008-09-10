package org.seasar.kvasir.base.classloader;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;


/**
 * @author YOKOTA Takehiko
 */
public class OverriddenURLClassLoaderTest extends TestCase
{
    private URL getResourceURL(String name)
        throws MalformedURLException
    {
        URL url = getClass().getResource(name);
        String str = url.toExternalForm();
        if (!str.endsWith("/")) {
            url = new URL(str + "/");
        }
        return url;
    }


    public void test1_1()
        throws Exception
    {
        ClassLoader parent = new OverriddenURLClassLoader(
            new URL[] { getResourceURL("classes1/") });
        ClassLoader cl = new OverriddenURLClassLoader(
            new URL[] { getResourceURL("classes2/") }, parent);
        InputStream in = cl.getResourceAsStream("resource.properties");
        assertNotNull(in);
        Properties prop = new Properties();
        prop.load(in);
        in.close();
        assertEquals("子のリソースが見つかること", "classes2", prop.getProperty("name"));
    }


    public void test1_2()
        throws Exception
    {
        ClassLoader parent = new OverriddenURLClassLoader(
            new URL[] { getResourceURL("classes1/") });
        ClassLoader cl = new OverriddenURLClassLoader(
            new URL[] { getResourceURL("classes2/") }, parent);
        List<URL> urlList = new ArrayList<URL>();
        for (Enumeration<URL> enm = cl.getResources("resource.properties"); enm
            .hasMoreElements();) {
            urlList.add(enm.nextElement());
        }
        assertEquals("クラスローダの範囲外のリソースが見つからないこと", 2, urlList.size());
        assertTrue("子のリソースが先に見つかること", urlList.get(0).toExternalForm().indexOf(
            "classes2") >= 0);
        assertTrue(urlList.get(1).toExternalForm().indexOf("classes1") >= 0);
    }


    public void test2()
        throws Exception
    {
        ClassLoader cl = new OverriddenURLClassLoader(new URL[0]);
        try {
            Class.forName("org.seasar.kvasir.base.Adaptable", false, cl);
            fail("クラスローダの範囲外のクラスは見つからないこと");
        } catch (ClassNotFoundException expected) {
        }
    }


    public void test3()
        throws Exception
    {
        ClassLoader cl = new OverriddenURLClassLoader(new URL[0]);
        try {
            Class.forName("java.lang.Object", false, cl);
        } catch (ClassNotFoundException expected) {
            fail("システムの持つクラスは見つかること");
        }
    }


    public void test4()
        throws Exception
    {
        ClassLoader cl = new OverriddenURLClassLoader(new URL[0]);
        assertNull("クラスローダの範囲外のリソースは見つからないこと", cl
            .getResource(OverriddenURLClassLoader.class.getName().replace('.',
                '/')
                + ".class"));
    }
}
