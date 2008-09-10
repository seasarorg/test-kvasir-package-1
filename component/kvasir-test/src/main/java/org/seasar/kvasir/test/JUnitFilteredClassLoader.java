package org.seasar.kvasir.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;


public class JUnitFilteredClassLoader extends ClassLoader
{
    private ClassLoader classLoader_;


    public JUnitFilteredClassLoader(ClassLoader classLoader, ClassLoader parent)
    {
        super(parent);
        classLoader_ = classLoader;
    }


    public URL getResource(String name)
    {
        URL resource = super.getResource(name);
        if (resource == null) {
            if (name.startsWith("junit")) {
                resource = classLoader_.getResource(name);
            }
        }
        return resource;
    }


    public Enumeration<URL> getResources(String name)
        throws IOException
    {
        List<URL> resourceList = new ArrayList<URL>();
        for (Enumeration<URL> enm = super.getResources(name); enm
            .hasMoreElements();) {
            resourceList.add(enm.nextElement());
        }
        if (name.startsWith("junit")) {
            for (Enumeration<URL> enm = classLoader_.getResources(name); enm
                .hasMoreElements();) {
                resourceList.add(enm.nextElement());
            }
        }
        return Collections.enumeration(resourceList);
    }


    public InputStream getResourceAsStream(String name)
    {
        InputStream is = super.getResourceAsStream(name);
        if (is == null) {
            if (name.startsWith("junit")) {
                is = classLoader_.getResourceAsStream(name);
            }
        }
        return is;
    }


    public Class<?> loadClass(String name)
        throws ClassNotFoundException
    {
        return loadClass(name, false);
    }


    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        Class<?> clazz;
        try {
            clazz = super.loadClass(name, resolve);
        } catch (ClassNotFoundException ex) {
            if (name.startsWith("junit")) {
                clazz = classLoader_.loadClass(name);
            } else {
                throw new ClassNotFoundException(name);
            }
        }
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }
}
