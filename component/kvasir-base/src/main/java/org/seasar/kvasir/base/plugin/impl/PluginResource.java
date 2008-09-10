package org.seasar.kvasir.base.plugin.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


/**
 * プラグインが持つファイルを参照するためのクラスです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class PluginResource
    implements Resource
{
    private Resource baseResource_;

    private Resource subResource_;


    /*
     * constructors
     */

    public PluginResource(Resource dir)
    {
        this(dir, null);
    }


    public PluginResource(Resource baseResource, Resource subResource)
    {
        baseResource_ = baseResource;
        subResource_ = subResource;
    }


    public PluginResource(PluginResource parent, String child)
    {
        Resource baseResource = parent.getBaseResource()
            .getChildResource(child);
        Resource subResource = parent.getSubResource();
        if (subResource != null) {
            subResource = subResource.getChildResource(child);
        }

        baseResource_ = baseResource;
        subResource_ = subResource;
    }


    /*
     * public scope methods
     */

    @Override
    public String toString()
    {
        return "baseResource=[" + baseResource_ + "], subResource=["
            + subResource_ + "]";
    }


    @Override
    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        } else if (o.getClass() != getClass()) {
            return false;
        }

        PluginResource pr = (PluginResource)o;
        if (pr.baseResource_ == null) {
            if (baseResource_ != null) {
                return false;
            }
        } else if (!pr.baseResource_.equals(baseResource_)) {
            return false;
        }
        if (pr.subResource_ == null) {
            if (subResource_ != null) {
                return false;
            }
        } else if (!pr.subResource_.equals(subResource_)) {
            return false;
        }

        return true;
    }


    @Override
    public int hashCode()
    {
        if (baseResource_ == null) {
            if (subResource_ == null) {
                return super.hashCode();
            } else {
                return subResource_.hashCode();
            }
        } else {
            if (subResource_ == null) {
                return baseResource_.hashCode();
            } else {
                return baseResource_.hashCode() + subResource_.hashCode();
            }
        }
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
        return false;
    }


    public boolean exists()
    {
        if (baseResource_.exists()) {
            return true;
        } else {
            return existsSubResource();
        }
    }


    public Resource getChildResource(String child)
    {
        Resource subChildResource = (subResource_ != null) ? subResource_
            .getChildResource(child) : null;

        return new PluginResource(baseResource_.getChildResource(child),
            subChildResource);
    }


    public InputStream getInputStream()
        throws ResourceNotFoundException
    {
        try {
            return new FileInputStream(toFile());
        } catch (FileNotFoundException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }


    public long getLastModifiedTime()
    {
        return toFile().lastModified();
    }


    public String getName()
    {
        return subResource_ != null ? subResource_.getName() : baseResource_
            .getName();
    }


    public OutputStream getOutputStream()
        throws ResourceNotFoundException
    {
        throw new UnsupportedOperationException();
    }


    public Resource getParentResource()
    {
        Resource subParentResource = (subResource_ != null) ? subResource_
            .getParentResource() : null;

        return new PluginResource(baseResource_.getParentResource(),
            subParentResource);
    }


    public long getSize()
    {
        return existsSubResource() ? subResource_.getSize() : baseResource_
            .getSize();
    }


    public URL getURL()
    {
        return (existsSubResource() || !baseResource_.exists()) ? subResource_
            .getURL() : baseResource_.getURL();
    }


    public URL[] getURLs()
    {
        List<URL> list = new ArrayList<URL>();
        getURLs(list, subResource_);
        getURLs(list, baseResource_);
        return list.toArray(new URL[0]);
    }


    public boolean isDirectory()
    {
        return existsSubResource() ? subResource_.isDirectory() : baseResource_
            .isDirectory();
    }


    public String[] list()
    {
        if (!isDirectory()) {
            return null;
        }

        Set<String> set = new LinkedHashSet<String>();
        if (subResource_ != null) {
            String[] names = subResource_.list();
            if (names != null) {
                for (int i = 0; i < names.length; i++) {
                    set.add(names[i]);
                }
            }
        }
        String[] names = baseResource_.list();
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                set.add(names[i]);
            }
        }
        return set.toArray(new String[0]);
    }


    public Resource[] listResources()
    {
        String[] names = list();
        if (names == null) {
            return null;
        } else {
            PluginResource[] pfiles = new PluginResource[names.length];
            for (int i = 0; i < names.length; i++) {
                pfiles[i] = new PluginResource(this, names[i]);
            }
            return pfiles;
        }
    }


    public boolean mkdir()
    {
        return false;
    }


    public boolean mkdirs()
    {
        return false;
    }


    public boolean renameTo(Resource dest)
    {
        return false;
    }


    public boolean setLastModifiedTime(long time)
    {
        return false;
    }


    public File toFile()
    {
        return (existsSubResource() || !baseResource_.exists()) ? subResource_
            .toFile() : baseResource_.toFile();
    }


    public File[] toFiles()
    {
        List<File> list = new ArrayList<File>();
        if (existsSubResource()) {
            toFiles(list, subResource_);
        }
        toFiles(list, baseResource_);
        return list.toArray(new File[0]);
    }


    /*
     * private scope methods
     */

    private boolean existsSubResource()
    {
        return ((subResource_ != null) && subResource_.exists());
    }


    private void getURLs(List<URL> list, Resource resource)
    {
        if (resource == null) {
            return;
        }
        if (resource instanceof PluginResource) {
            PluginResource pr = (PluginResource)resource;
            getURLs(list, pr.getSubResource());
            getURLs(list, pr.getBaseResource());
        } else {
            URL url = resource.getURL();
            if (url != null) {
                list.add(url);
            }
        }
    }


    private void toFiles(List<File> list, Resource resource)
    {
        if (resource == null) {
            return;
        }
        if (resource instanceof PluginResource) {
            PluginResource pr = (PluginResource)resource;
            toFiles(list, pr.getSubResource());
            toFiles(list, pr.getBaseResource());
        } else {
            File file = resource.toFile();
            if (file != null) {
                list.add(file);
            }
        }
    }
}
