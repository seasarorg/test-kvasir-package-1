package org.seasar.kvasir.util.io.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class FileResource
    implements Resource
{
    private File file_;


    /*
     * constructors
     */

    public FileResource(File file)
    {
        file_ = file;
    }


    public FileResource(String pathname)
    {
        this(new File(pathname));
    }


    /*
     * public scope methods
     */

    public String toString()
    {
        return file_.toString();
    }


    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        } else if (o.getClass() != getClass()) {
            return false;
        }

        FileResource fr = (FileResource)o;
        return fr.file_.equals(file_);
    }


    public int hashCode()
    {
        return file_.hashCode();
    }


    /*
     * Resource
     */

    public boolean delete()
    {
        return file_.delete();
    }


    public boolean exists()
    {
        return file_.exists();
    }


    public Resource getChildResource(String child)
    {
        return new FileResource(new File(file_, child));
    }


    public InputStream getInputStream()
        throws ResourceNotFoundException
    {
        try {
            return new FileInputStream(file_);
        } catch (FileNotFoundException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }


    public long getLastModifiedTime()
    {
        return file_.lastModified();
    }


    public String getName()
    {
        return file_.getName();
    }


    public OutputStream getOutputStream()
        throws ResourceNotFoundException
    {
        try {
            return new FileOutputStream(file_);
        } catch (FileNotFoundException ex) {
            throw new ResourceNotFoundException(ex);
        }
    }


    public Resource getParentResource()
    {
        return new FileResource(file_.getParentFile());
    }


    public long getSize()
    {
        return file_.length();
    }


    public URL getURL()
    {
        try {
            return file_.toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new IORuntimeException(ex);
        }
    }


    public URL[] getURLs()
    {
        return new URL[] { getURL() };
    }


    public boolean isDirectory()
    {
        return file_.isDirectory();
    }


    public String[] list()
    {
        return file_.list();
    }


    public Resource[] listResources()
    {
        String[] names = list();
        if (names == null) {
            return null;
        } else {
            Resource[] resources = new Resource[names.length];
            for (int i = 0; i < names.length; i++) {
                resources[i] = new FileResource(new File(file_, names[i]));
            }
            return resources;
        }
    }


    public boolean mkdir()
    {
        return file_.mkdir();
    }


    public boolean mkdirs()
    {
        return file_.mkdirs();
    }


    public boolean renameTo(Resource dest)
    {
        File file = dest.toFile();
        if (file != null) {
            return file_.renameTo(file);
        } else {
            return false;
        }
    }


    public boolean setLastModifiedTime(long time)
    {
        return file_.setLastModified(time);
    }


    public File toFile()
    {
        return file_;
    }


    public File[] toFiles()
    {
        return new File[] { toFile() };
    }
}
