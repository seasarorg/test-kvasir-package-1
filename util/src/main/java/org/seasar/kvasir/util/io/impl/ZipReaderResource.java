package org.seasar.kvasir.util.io.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


/**
 * ZIP形式で圧縮されているファイルを読み込むためのResource実装です。
 * <p><b>注意：</b>
 * <p>このクラスは指定されたZIPファイルをクローズしません。
 * ZIPファイルの作成元の責任でZIPファイルをクローズして下さい。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class ZipReaderResource
    implements Resource
{
    private ZipFile file_;
    private String name_;
    private String path_;
    private String spath_;

    private SortedMap entryMap_;
    private boolean exists_;
    private boolean directory_;


    public ZipReaderResource(ZipFile file)
    {
        file_ = file;
        name_ = "";
        path_ = "";
        spath_ = "";

        entryMap_ = new TreeMap();
        for (Enumeration enm = file_.entries(); enm.hasMoreElements();) {
            ZipEntry entry = (ZipEntry)enm.nextElement();
            entryMap_.put(entry.getName(), entry);
        }
        exists_ = true;
        directory_ = true;
    }


    ZipReaderResource(ZipFile file, String path, SortedMap entryMap)
    {
        file_ = file;
        int slash = path.lastIndexOf('/');
        name_ = (slash >= 0) ? path.substring(slash + 1) : path;
        path_ = path;
        spath_ = path_ + "/";
        entryMap_ = entryMap;

        if (path_.length() == 0) {
            exists_ = true;
            directory_ = true;
        } else {
            if (entryMap.containsKey(path)) {
                exists_ = true;
                directory_ = false;
            } else if (entryMap.containsKey(spath_)) {
                exists_ = true;
                directory_ = true;
            } else {
                exists_ = false;
                directory_ = false;
            }
        }
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

        ZipReaderResource zrr = (ZipReaderResource)o;
        return zrr.path_.equals(path_);
    }


    public int hashCode()
    {
        return path_.hashCode();
    }


    /*
     * Resource
     */

    public boolean delete()
    {
        return false;
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
        return new ZipReaderResource(file_, spath_ + child, entryMap_);
    }


    public InputStream getInputStream()
        throws ResourceNotFoundException
    {
        if (!exists_) {
            throw new ResourceNotFoundException("Resource not found: " + this);
        } else if (directory_) {
            throw new IORuntimeException("Can't get input stream of directory");
        }
        ZipEntry entry = (ZipEntry)entryMap_.get(path_);
        try {
            return file_.getInputStream(entry);
        } catch (IOException ex) {
            throw new IORuntimeException("Can't get input stream: " + this, ex);
        }
    }


    public long getLastModifiedTime()
    {
        if (!exists_) {
            return 0L;
        } else if (path_.length() == 0) {
            return 0L;
        } else {
            long time = getEntry().getTime();
            if (time == -1) {
                time = 0L;
            }
            return time;
        }
    }


    public String getName()
    {
        return name_;
    }


    public OutputStream getOutputStream()
        throws ResourceNotFoundException
    {
        throw new UnsupportedOperationException();
    }


    public Resource getParentResource()
    {
        if (path_.length() == 0) {
            return null;
        } else {
            int slash = path_.lastIndexOf('/');
            if (slash >= 0) {
                return new ZipReaderResource(file_,
                    path_.substring(0, slash), entryMap_);
            } else {
                return new ZipReaderResource(file_, "", entryMap_);
            }
        }
    }


    public long getSize()
    {
        if (!exists_) {
            return 0L;
        } else if (path_.length() == 0) {
            return 0L;
        } else {
            long size = getEntry().getSize();
            if (size == -1) {
                size = 0L;
            }
            return size;
        }
    }


    public URL getURL()
    {
        return null;
    }


    public URL[] getURLs()
    {
        return new URL[0];
    }


    public boolean isDirectory()
    {
        return directory_;
    }


    public boolean mkdir()
    {
        throw new UnsupportedOperationException();
    }


    public boolean mkdirs()
    {
        throw new UnsupportedOperationException();
    }


    public String[] list()
    {
        if (!exists_ || !directory_) {
            return null;
        }

        Map map = getDescendantEntryMap();
        List list = new ArrayList();
        int pathOffset = (path_.length() == 0) ? 0 : path_.length() + 1;
        for (Iterator itr = map.keySet().iterator(); itr.hasNext();) {
            String path = (String)itr.next();
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            String subPath = path.substring(pathOffset);
            if (subPath.indexOf('/') < 0) {
                list.add(subPath);
            }
        }
        return (String[])list.toArray(new String[0]);
    }


    public Resource[] listResources()
    {
        if (!exists_ || !directory_) {
            return null;
        }

        Map map = getDescendantEntryMap();
        List list = new ArrayList();
        int pathOffset = (path_.length() == 0) ? 0 : path_.length() + 1;
        for (Iterator itr = map.keySet().iterator(); itr.hasNext();) {
            String path = (String)itr.next();
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            String subPath = path.substring(pathOffset);
            if (subPath.indexOf('/') < 0) {
                list.add(new ZipReaderResource(file_, path, entryMap_));
            }
        }
        return (Resource[])list.toArray(new Resource[0]);
    }


    public boolean renameTo(Resource dest)
    {
        throw new UnsupportedOperationException();
    }


    public boolean setLastModifiedTime(long time)
    {
        throw new UnsupportedOperationException();
    }


    public File toFile()
    {
        return null;
    }


    public File[] toFiles()
    {
        return new File[0];
    }


    /*
     * package scope methods
     */

    ZipEntry getEntry()
    {
        ZipEntry entry;
        if (directory_) {
            entry = (ZipEntry)entryMap_.get(spath_);
        } else {
            entry = (ZipEntry)entryMap_.get(path_);
        }
        return entry;
    }


    Map getDescendantEntryMap()
    {
        if (path_.length() == 0) {
            return entryMap_;
        } else {
            return entryMap_.subMap(
                path_ + '/' + (char)0,  path_ + (char)('/' + 1));
        }
    }
}
