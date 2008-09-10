package org.seasar.kvasir.base.classloader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class CachedClassLoader extends ClassLoader
{
    private Map<String, Class<?>> classCache_ = Collections
        .synchronizedMap(new HashMap<String, Class<?>>());

    private Map<String, URL> resourceCache_ = Collections
        .synchronizedMap(new HashMap<String, URL>());

    private Map<String, Vector<URL>> resourcesCache_ = Collections
        .synchronizedMap(new HashMap<String, Vector<URL>>());

    private ClassLoader classLoader_;


    public CachedClassLoader(ClassLoader classLoader)
    {
        classLoader_ = classLoader;
    }


    @Override
    public String toString()
    {
        // XXX xml-apis.jarでClassLoader#toString()が呼ばれるために、
        // 内包するClassLoaderのtoString()の処理が重い場合にシステム全体の処理速度が
        // 低下してしまう。これを避けるために、ここでは最低限の情報しか返さないようにしている。
        //        return "CACHED[" + classLoader_.toString() + "]";
        return "CACHED[" + classLoader_.getClass().getName() + "]";
    }


    public ClassLoader getClassLoader()
    {
        return classLoader_;
    }


    @Override
    public URL getResource(String name)
    {
        if (resourceCache_.containsKey(name)) {
            return resourceCache_.get(name);
        }
        URL resource = classLoader_.getResource(name);
        resourceCache_.put(name, resource);
        return resource;
    }


    @Override
    public Enumeration<URL> getResources(String name)
        throws IOException
    {
        if (resourcesCache_.containsKey(name)) {
            return resourcesCache_.get(name).elements();
        }
        Vector<URL> resources = new Vector<URL>();
        for (Enumeration<URL> enm = classLoader_.getResources(name); enm
            .hasMoreElements();) {
            resources.add(enm.nextElement());
        }
        resourcesCache_.put(name, resources);
        return resources.elements();
    }


    @Override
    public InputStream getResourceAsStream(String name)
    {
        URL resource = getResource(name);
        if (resource != null) {
            try {
                return resource.openStream();
            } catch (IOException ex) {
                throw new RuntimeException(
                    "Can't open resource as stream: name=" + name + ", url="
                        + resource, ex);
            }
        } else {
            return null;
        }
    }


    @Override
    public Class<?> loadClass(String name)
        throws ClassNotFoundException
    {
        return loadClass(name, false);
    }


    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        if (classCache_.containsKey(name)) {
            Class<?> clazz = classCache_.get(name);
            if (clazz != null) {
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            } else {
                throw new ClassNotFoundException(name);
            }
        }
        Class<?> clazz = null;
        try {
            clazz = classLoader_.loadClass(name);
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        } finally {
            classCache_.put(name, clazz);
        }
    }
}
