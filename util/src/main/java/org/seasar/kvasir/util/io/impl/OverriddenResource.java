package org.seasar.kvasir.util.io.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


/**
 * 重ねあわされたリソースを表すクラスです。
 * <p>変更に関するメソッドは全て<code>subResource</code>に適用されます。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class OverriddenResource
    implements Resource
{
    private Resource baseResource_;

    private Resource subResource_;


    /*
     * constructors
     */

    public OverriddenResource(Resource baseResource, Resource subResource)
    {
        baseResource_ = baseResource;
        subResource_ = subResource;
    }


    /*
     * public scope methods
     */

    public String toString()
    {
        return "baseResource=[" + baseResource_ + "], subResource=["
            + subResource_ + "]";
    }


    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        } else if (o.getClass() != getClass()) {
            return false;
        }

        OverriddenResource or = (OverriddenResource)o;
        if (!or.baseResource_.equals(baseResource_)) {
            return false;
        }
        if (!or.subResource_.equals(subResource_)) {
            return false;
        }

        return true;
    }


    public int hashCode()
    {
        return baseResource_.hashCode() + subResource_.hashCode();
    }


    public Resource getBaseResource()
    {
        return baseResource_;
    }


    public Resource getSubResource()
    {
        return subResource_;
    }


    /*
     * Resouces
     */

    public boolean delete()
    {
        return subResource_.delete();
    }


    public boolean exists()
    {
        return (subResource_.exists() || baseResource_.exists());
    }


    public Resource getChildResource(String child)
    {
        return new OverriddenResource(baseResource_.getChildResource(child),
            subResource_.getChildResource(child));
    }


    public InputStream getInputStream()
        throws ResourceNotFoundException
    {
        return getExistentResource().getInputStream();
    }


    public long getLastModifiedTime()
    {
        return getExistentResource().getLastModifiedTime();
    }


    public String getName()
    {
        return subResource_.getName();
    }


    public OutputStream getOutputStream()
        throws ResourceNotFoundException
    {
        return subResource_.getOutputStream();
    }


    public Resource getParentResource()
    {
        return new OverriddenResource(baseResource_.getParentResource(),
            subResource_.getParentResource());
    }


    public long getSize()
    {
        return getExistentResource().getSize();
    }


    public URL getURL()
    {
        return getExistentResource().getURL();
    }


    public URL[] getURLs()
    {
        Set set = new LinkedHashSet();
        URL[] urls = subResource_.getURLs();
        for (int i = 0; i < urls.length; i++) {
            set.add(urls[i]);
        }
        urls = baseResource_.getURLs();
        for (int i = 0; i < urls.length; i++) {
            set.add(urls[i]);
        }
        return (URL[])set.toArray(new URL[0]);
    }


    public boolean isDirectory()
    {
        return getExistentResource().isDirectory();
    }


    public String[] list()
    {
        if (!isDirectory()) {
            return null;
        }

        Set set = new LinkedHashSet();
        String[] names = subResource_.list();
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                set.add(names[i]);
            }
        }
        names = baseResource_.list();
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                set.add(names[i]);
            }
        }
        return (String[])set.toArray(new String[0]);
    }


    public Resource[] listResources()
    {
        String[] names = list();
        if (names == null) {
            return null;
        } else {
            Resource[] resources = new Resource[names.length];
            for (int i = 0; i < names.length; i++) {
                resources[i] = getChildResource(names[i]);
            }
            return resources;
        }
    }


    public boolean mkdir()
    {
        return subResource_.mkdir();
    }


    public boolean mkdirs()
    {
        return subResource_.mkdirs();
    }


    public boolean renameTo(Resource dest)
    {
        return getExistentResource().renameTo(dest);
    }


    public boolean setLastModifiedTime(long time)
    {
        return getExistentResource().setLastModifiedTime(time);
    }


    public File toFile()
    {
        return getExistentResource().toFile();
    }


    public File[] toFiles()
    {
        Set set = new LinkedHashSet();
        File[] files = subResource_.toFiles();
        for (int i = 0; i < files.length; i++) {
            set.add(files[i]);
        }
        files = baseResource_.toFiles();
        for (int i = 0; i < files.length; i++) {
            set.add(files[i]);
        }
        return (File[])set.toArray(new File[0]);
    }


    /*
     * private scope methods
     */

    private Resource getExistentResource()
    {
        if (subResource_.exists() || !baseResource_.exists()) {
            return subResource_;
        } else {
            return baseResource_;
        }
    }
}
