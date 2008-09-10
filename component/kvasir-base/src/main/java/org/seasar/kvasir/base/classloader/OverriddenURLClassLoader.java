package org.seasar.kvasir.base.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import org.seasar.kvasir.util.ClassUtils;


/**
 * 親クラスローダを後に検索するURLClassLoaderです。
 * <p>クラスの検索を次の順で行ないます。</p>
 * <ol>
 *   <li>システムクラスローダ</li>
 *   <li>ローカルリポジトリ</li>
 *   <li>親クラスローダ</li>
 * </ol>
 * ただし、
 * <code>javax</code>、<code>org.xml.sax</code>、<code>org.w3c.dom</code>、
 * <code>org.apache.xerces</code>、<code>org.apache.xalan</code>
 * パッケージまたはそのサブパッケージに属するクラスについては、
 * 親クラスローダを先に検索します。</p>
 *
 * @author YOKOTA Takehiko
 */
public class OverriddenURLClassLoader extends URLClassLoader
{
    private static final String[] DISALLOWED_PACKAGES = new String[] { "javax", // Java拡張
        "org.xml.sax", // SAX
        "org.w3c.dom", // DOM
        "org.apache.xerces", // Xerces
        "org.apache.xalan", // Xalan
        "org.apache.commons.logging", // commons-logging
        "org.apache.log4j", // log4j
    };

    private URL[] urls_;

    private ClassLoader parent_;

    private ClassLoader ext_ = ClassUtils.getExtClassLoader();


    /*
     * constructors
     */

    public OverriddenURLClassLoader(URL[] urls)
    {
        super(urls);
        urls_ = urls;
    }


    public OverriddenURLClassLoader(URL[] urls, ClassLoader parent)
    {
        super(urls, parent);
        urls_ = urls;
        parent_ = parent;
    }


    /*
     * Object
     */

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("O{ ");
        for (int i = 0; i < urls_.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(urls_[i]);
        }
        sb.append(" }(parent=").append(parent_).append(")");
        return sb.toString();
    }


    /*
     * URLClassLoader
     */

    @Override
    public URL getResource(String name)
    {
        URL url = findResource(name);
        if (url != null) {
            return url;
        }
        if (parent_ != null) {
            return parent_.getResource(name);
        }
        return null;
    }


    @Override
    public Enumeration<URL> getResources(String name)
        throws IOException
    {
        Set<URL> resourceSet = new LinkedHashSet<URL>();
        for (Enumeration<URL> enm = findResources(name); enm.hasMoreElements();) {
            resourceSet.add(enm.nextElement());
        }
        if (parent_ != null) {
            for (Enumeration<URL> enm = parent_.getResources(name); enm
                .hasMoreElements();) {
                resourceSet.add(enm.nextElement());
            }
        }
        return Collections.enumeration(resourceSet);
    }


    public synchronized Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null) {
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
        boolean resolveProcessed = false;
        do {
            // 拡張クラスローダは先に見るようにする。
            clazz = loadClass(name, resolve, ext_);
            if (clazz != null) {
                resolveProcessed = true;
                break;
            }

            // override対象外のパッケージに属するクラスの場合は先に親クラスローダを検索する。
            boolean allowedToOverride = isAllowedToOverride(name);
            if (!allowedToOverride) {
                clazz = loadClass(name, resolve, parent_);
                if (clazz != null) {
                    resolveProcessed = true;
                    break;
                }
            }

            // ローカルリポジトリを検索する。
            try {
                clazz = findClass(name);
                break;
            } catch (ClassNotFoundException ex) {
                if (findResource(name.replace('.', '/').concat(".class")) != null) {
                    throw ex;
                }
            }

            // override対象外のパッケージに属さないクラスの場合は親クラスローダを検索する。
            if (allowedToOverride) {
                clazz = loadClass(name, resolve, parent_);
                if (clazz != null) {
                    resolveProcessed = true;
                    break;
                }
            }
        } while (false);

        if (clazz == null) {
            throw new ClassNotFoundException("Can't load class: " + name);
        } else {
            if (!resolveProcessed && resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
    }


    protected boolean isAllowedToOverride(String name)
    {
        int dot = name.lastIndexOf('.');
        if (dot < 0) {
            return true;
        }
        String packageName = name.substring(0, dot);
        for (int i = 0; i < DISALLOWED_PACKAGES.length; i++) {
            if (packageName.startsWith(DISALLOWED_PACKAGES[i])) {
                return false;
            }
        }
        return true;
    }


    Class<?> loadClass(String name, boolean resolve, ClassLoader loader)
    {
        try {
            if (loader == null) {
                return Class.forName(name, resolve, null);
            } else {
                Class<?> clazz = loader.loadClass(name);
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
}
