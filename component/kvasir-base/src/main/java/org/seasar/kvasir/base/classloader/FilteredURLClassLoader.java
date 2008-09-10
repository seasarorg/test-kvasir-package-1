package org.seasar.kvasir.base.classloader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class FilteredURLClassLoader extends ClassLoader
{
    private FilteredURL[] urls_;

    private ClassLoader filtered_;

    private ClassLoader parent_;


    /*
     * constructors
     */

    public FilteredURLClassLoader(FilteredURL[] urls, ClassLoader filtered,
        ClassLoader parent)
    {
        super(filtered);

        urls_ = urls;
        filtered_ = filtered;
        parent_ = parent;
    }


    /*
     * Object
     */

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("F{ ");
        for (int i = 0; i < urls_.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(urls_[i]);
        }
        sb.append(" }(filtered=").append(filtered_).append(", parent=").append(
            parent_).append(")");
        return sb.toString();
    }


    /*
     * ClassLoader
     */

    public URL getResource(String name)
    {
        for (int i = 0; i < urls_.length; i++) {
            URL url = urls_[i].getResource(name);
            if (url != null) {
                return url;
            }
        }
        if (parent_ != null) {
            return parent_.getResource(name);
        }
        return null;
    }


    public Enumeration<URL> getResources(String name)
        throws IOException
    {
        Set<URL> resourceSet = new LinkedHashSet<URL>();
        for (int i = 0; i < urls_.length; i++) {
            for (Enumeration<URL> enm = urls_[i].getResources(name); enm
                .hasMoreElements();) {
                resourceSet.add(enm.nextElement());
            }
        }
        if (parent_ != null) {
            for (Enumeration<URL> enm = parent_.getResources(name); enm
                .hasMoreElements();) {
                resourceSet.add(enm.nextElement());
            }
        }
        return Collections.enumeration(resourceSet);
    }


    public synchronized Class<?> loadClass(String name)
        throws ClassNotFoundException
    {
        for (int i = 0; i < urls_.length; i++) {
            if (!urls_[i].isClassMatched(name)) {
                continue;
            }
            return filtered_.loadClass(name);
        }
        if (parent_ != null) {
            return parent_.loadClass(name);
        }
        throw new ClassNotFoundException("Class doesn't exist: " + name);
    }


    /*
     * inner classes
     */

    public static class FilteredURL
    {
        private static final String CLASS_SUFFIX = ".class";

        private URL url_;

        private FilteredClassLoader classLoaderForCheck_;


        public FilteredURL(URL url, String[] classPatterns,
            String[] resourcePatterns)
        {
            url_ = url;
            classLoaderForCheck_ = new FilteredClassLoader(new URLClassLoader(
                new URL[] { url }, null), classPatterns, resourcePatterns);
        }


        public URL getResource(String name)
        {
            return classLoaderForCheck_.getResource(name);
        }


        public Enumeration<URL> getResources(String name)
            throws IOException
        {
            return classLoaderForCheck_.getResources(name);
        }


        public boolean isClassMatched(String name)
        {
            return classLoaderForCheck_.isClassMatched(name);
        }


        public String toString()
        {
            StringBuffer sb = new StringBuffer();
            sb.append("[ ");
            sb.append(url_);
            sb.append(" (");
            for (int i = 0; i < classLoaderForCheck_.getClassPatternStrings().length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(classLoaderForCheck_.getClassPatternStrings()[i]);
            }
            sb.append("), (");
            for (int i = 0; i < classLoaderForCheck_.getResourcePatternStrings().length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(classLoaderForCheck_.getResourcePatternStrings()[i]);
            }
            sb.append(")]");
            return sb.toString();
        }


        public URL getURL()
        {
            return url_;
        }
    }
}
