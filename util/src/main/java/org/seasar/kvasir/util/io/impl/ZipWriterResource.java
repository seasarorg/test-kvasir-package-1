package org.seasar.kvasir.util.io.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


/**
 * ZIP形式のファイルを書き出すためのResource実装です。
 * <p><b>注意：</b>
 * <p>このクラスは指定されたZIP出力ストリームをクローズしません。
 * ZIP出力ストリームの作成元の責任でZIP出力ストリームをクローズして下さい。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class ZipWriterResource
    implements Resource
{
    private ZipOutputStream zos_;
    private SortedMap entryMap_;
    private Set createdSet_;
    private boolean createEmptyDirectory_;

    private String name_;
    private String path_;
    private String spath_;
    private boolean exists_;
    private boolean directory_;


    public ZipWriterResource(ZipOutputStream zos)
    {
        this(zos, true);
    }


    public ZipWriterResource(ZipOutputStream zos, boolean createEmptyDirectory)
    {
        zos_ = zos;
        entryMap_ = new TreeMap();
        createEmptyDirectory_ = createEmptyDirectory;
        if (!createEmptyDirectory) {
            createdSet_ = new HashSet();
            createdSet_.add("/");
        }

        name_ = "";
        path_ = "";
        spath_ = "";
        exists_ = true;
        directory_ = true;
    }


    ZipWriterResource(ZipOutputStream zos, String path, SortedMap entryMap,
        Set createdSet, boolean createEmptyDirectory)
    {
        zos_ = zos;
        entryMap_ = entryMap;
        createdSet_ = createdSet;
        createEmptyDirectory_ = createEmptyDirectory;

        int slash = path.lastIndexOf('/');
        name_ = (slash >= 0) ? path.substring(slash + 1) : path;
        path_ = path;
        spath_ = path_ + "/";

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

        ZipWriterResource zwr = (ZipWriterResource)o;
        return zwr.path_.equals(path_);
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
        if (!exists_) {
            return true;
        } else if (!directory_) {
            return false;
        } else if (createEmptyDirectory_) {
            return false;
        } else if (path_.length() == 0) {
            return false;
        } else if (list().length > 0) {
            return false;
        }
        entryMap_.remove(spath_);
        exists_ = false;
        directory_ = false;
        return true;
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
        return new ZipWriterResource(zos_, spath_ + child,
            entryMap_, createdSet_, createEmptyDirectory_);
    }


    public InputStream getInputStream()
        throws ResourceNotFoundException
    {
        if (!exists_) {
            throw new ResourceNotFoundException("Resource not found: " + path_);
        }
        throw new UnsupportedOperationException();
    }


    public long getLastModifiedTime()
    {
        return 0L;
    }


    public String getName()
    {
        return name_;
    }


    public OutputStream getOutputStream()
        throws ResourceNotFoundException
    {
        if (exists_) {
            if (directory_) {
                throw new IllegalStateException(
                    "Can't open directory: " + spath_);
            } else {
                throw new IllegalStateException("Already opened: " + path_);
            }
        }

        
        ZipWriterResource parent = (ZipWriterResource)getParentResource();
        if (!parent.exists()) {
            throw new ResourceNotFoundException("Parent does not exist: "
                + path_);
        }

        if (!createEmptyDirectory_) {
            while (parent != null) {
                String entryName = parent.getPath() + "/";
                if (!createdSet_.contains(entryName)) {
                    try {
                        zos_.putNextEntry(new ZipEntry(entryName));
                        zos_.closeEntry();
                    } catch (IOException ex) {
                        throw new IORuntimeException(ex);
                    }
                    createdSet_.add(entryName);
                }
                parent = (ZipWriterResource)parent.getParentResource();
            }
        }

        ZipEntry zipEntry = new ZipEntry(path_);
        entryMap_.put(path_, zipEntry);
        exists_ = true;
        directory_ = false;
        try {
            zos_.putNextEntry(zipEntry);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
        return new ZipOutputStream0();
    }


    public Resource getParentResource()
    {
        if (path_.length() == 0) {
            return null;
        } else {
            int slash = path_.lastIndexOf('/');
            if (slash >= 0) {
                return new ZipWriterResource(zos_, path_.substring(0, slash),
                    entryMap_, createdSet_, createEmptyDirectory_);
            } else {
                return new ZipWriterResource(zos_, "",
                    entryMap_, createdSet_, createEmptyDirectory_);
            }
        }
    }


    public long getSize()
    {
        return 0L;
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
        if (exists_) {
            if (directory_) {
                return true;
            } else {
                return false;
            }
        } else if (!getParentResource().exists()) {
            return false;
        }
        ZipEntry zipEntry = new ZipEntry(spath_);
        if (createEmptyDirectory_) {
            try {
                zos_.putNextEntry(zipEntry);
            } catch (IOException ex) {
                return false;
            } finally {
                try {
                    zos_.closeEntry();
                } catch (IOException ex2) {
                    return false;
                }
            }
        }
        entryMap_.put(spath_, zipEntry);
        exists_ = true;
        directory_ = true;
        return true;
    }


    public boolean mkdirs()
    {
        Resource parent = getParentResource();
        if (parent == null) {
            return true;
        }
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                return false;
            }
        }
        return mkdir();
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
                list.add(new ZipWriterResource(zos_, path,
                    entryMap_, createdSet_, createEmptyDirectory_));
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

    String getPath()
    {
        return path_;
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


    /*
     * inner classes
     */

    private class ZipOutputStream0 extends OutputStream
    {
        public void write(int b)
            throws IOException
        {
            ZipWriterResource.this.zos_.write(b);
        }

        public void close()
            throws IOException
        {
            zos_.closeEntry();
        }
    }
}
