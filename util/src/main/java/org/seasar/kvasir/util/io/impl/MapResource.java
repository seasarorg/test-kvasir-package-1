package org.seasar.kvasir.util.io.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class MapResource
    implements Resource
{
    private MapResource     parent_;
    private String          name_;
    private String          path_;

    private Map             childrenMap_ = new HashMap();
    private byte[]          body_;
    private boolean         directory_;
    private boolean         exists_;
    private long            lastModifiedTime_;

    private volatile int open_;


    /*
     * constructors
     */

    public MapResource()
    {
        parent_ = null;
        name_ = "";
        path_ = "";

        directory_ = true;
        exists_ = true;
        lastModifiedTime_ = System.currentTimeMillis();
    }


    MapResource(MapResource parent, String name)
    {
        parent_ = parent;
        name_ = name;
        path_ = parent.path_ + "/" + name;
    }


    /*
     * static methods
     */

    public static MapResource newInstance()
    {
        return new MapResource();
    }


    public static MapResource newInstance(String path)
    {
        if (path.length() == 0) {
            return newInstance();
        } else {
            return (MapResource)new MapResource().getChildResource(path);
        }
    }


    /*
     * public scope methods
     */

    public String getPath()
    {
        return path_;
    }


    /*
     * Object
     */

    public String toString()
    {
        return path_;
    }


    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        } else if (o.getClass() != getClass()) {
            return false;
        }

        MapResource mr = (MapResource)o;
        return mr.path_.equals(path_);
    }


    public int hashCode()
    {
        return path_.hashCode();
    }


    /*
     * Resource
     */

    public synchronized boolean delete()
    {
        if (parent_ == null) {
            return false;
        } else if (exists_ && directory_) {
            if (getChildrenCount() > 0) {
                return false;
            }
        } else if (open_ > 0) {
            return false;
        }

        body_ = null;
        childrenMap_.clear();
        directory_ = false;
        exists_ = false;
        lastModifiedTime_ = 0L;

        return true;
    }


    private int getChildrenCount()
    {
        int count = 0;
        for (Iterator itr = childrenMap_.values().iterator(); itr.hasNext();) {
            if (((MapResource)itr.next()).exists()) {
                count++;
            }
        }
        return count;
    }


    public boolean exists()
    {
        return exists_;
    }


    public Resource getChildResource(String child)
    {
        if (child.startsWith("/")) {
            child = child.substring(1);
        }
        if (child.endsWith("/")) {
            child = child.substring(0, child.length() - 1);
        }

        MapResource mr = this;
        int idx;
        int pre = 0;
        while ((idx = child.indexOf('/', pre)) >= 0) {
            String name = child.substring(pre, idx);
            Map childrenMap = mr.getChildrenMap();
            synchronized (childrenMap) {
                MapResource cmr = (MapResource)childrenMap.get(name);
                if (cmr == null) {
                    cmr = new MapResource(mr, name);
                    childrenMap.put(name, cmr);
                }
                mr = cmr;
            }
            pre = idx + 1;
        }
        String name = child.substring(pre);
        Map childrenMap = mr.getChildrenMap();
        synchronized (childrenMap) {
            MapResource cmr = (MapResource)childrenMap.get(name);
            if (cmr == null) {
                cmr = new MapResource(mr, name);
                childrenMap.put(name, cmr);
            }
            return cmr;
        }
    }


    Map getChildrenMap()
    {
        return childrenMap_;
    }


    public InputStream getInputStream()
        throws ResourceNotFoundException
    {
        if (!exists_) {
            throw new ResourceNotFoundException("Resource not found: " + path_);
        } else if (directory_) {
            throw new ResourceNotFoundException(
                "Directory cannot be opened: " + path_);
        }
        return new ByteArrayInputStream(body_);
    }


    public long getLastModifiedTime()
    {
        return lastModifiedTime_;
    }


    public String getName()
    {
        return name_;
    }


    public OutputStream getOutputStream()
        throws ResourceNotFoundException
    {
        if (directory_) {
            throw new ResourceNotFoundException(
                "Directory cannot be opened: " + path_);
        }
        return new MapResourceOutputStream();
    }


    public Resource getParentResource()
    {
        return parent_;
    }


    public long getSize()
    {
        if (!exists_ || directory_) {
            return 0L;
        }
        return body_.length;
    }


    public URL getURL()
    {
        throw new UnsupportedOperationException();
    }


    public URL[] getURLs()
    {
        throw new UnsupportedOperationException();
    }


    public boolean isDirectory()
    {
        return directory_;
    }


    public String[] list()
    {
        if (!directory_) {
            return null;
        }
        synchronized (childrenMap_) {
            List list = new ArrayList();
            for (Iterator itr = childrenMap_.values().iterator();
            itr.hasNext();) {
                MapResource child = (MapResource)itr.next();
                if (child.exists()) {
                    list.add(child.getName());
                }
            }
            return (String[])list.toArray(new String[0]);
        }
    }


    public Resource[] listResources()
    {
        if (!directory_) {
            return null;
        }
        synchronized (childrenMap_) {
            List list = new ArrayList();
            for (Iterator itr = childrenMap_.values().iterator();
            itr.hasNext();) {
                MapResource child = (MapResource)itr.next();
                if (child.exists()) {
                    list.add(child);
                }
            }
            return (Resource[])list.toArray(new Resource[0]);
        }
    }


    public boolean mkdir()
    {
        if (parent_ == null) {
            return true;
        } else if (!parent_.exists()) {
            return false;
        } else if (exists_) {
            return directory_;
        }
        directory_ = true;
        exists_ = true;
        lastModifiedTime_ = System.currentTimeMillis();
        return true;
    }


    public boolean mkdirs()
    {
        if (parent_ == null) {
            return true;
        } else if (parent_.mkdirs()) {
            return mkdir();
        } else {
            return false;
        }
    }


    public boolean renameTo(Resource dest)
    {
        throw new UnsupportedOperationException();
    }


    public boolean setLastModifiedTime(long time)
    {
        if (!exists_) {
            return false;
        }
        lastModifiedTime_ = time;
        return true;
    }


    public File toFile()
    {
        throw new UnsupportedOperationException();
    }


    public File[] toFiles()
    {
        throw new UnsupportedOperationException();
    }


    /*
     * inner classes
     */

    private class MapResourceOutputStream extends OutputStream
    {
        private ByteArrayOutputStream baos = new ByteArrayOutputStream();

        private MapResourceOutputStream()
        {
            MapResource.this.open_++;
        }

        public void write(int b)
            throws IOException
        {
            baos.write(b);
        }

        public void close()
            throws IOException
        {
            baos.close();

            synchronized (MapResource.this) {
                MapResource.this.body_ = baos.toByteArray();
                MapResource.this.exists_ = true;
                MapResource.this.lastModifiedTime_ = System.currentTimeMillis();
                MapResource.this.open_--;
            }
        }
    }
}
