package org.seasar.kvasir.util.io.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

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
public class JavaResource
    implements Resource
{
    private String path_;

    private ClassLoader cl_;

    private URL url_;

    private boolean urlConstructed_;

    private String npath_;

    private String name_;


    /*
     * constructors
     */

    public JavaResource(String path)
    {
        init(path, Thread.currentThread().getContextClassLoader());
    }


    public JavaResource(String path, Class clazz)
    {
        if (path.startsWith("/")) {
            path = path.substring(1);
        } else {
            String className = clazz.getName();
            int dot = className.lastIndexOf('.');
            if (dot >= 0) {
                path = className.substring(0, dot).replace('.', '/') + "/"
                    + path;
            }
        }
        init(path, clazz.getClassLoader());
    }


    public JavaResource(String path, ClassLoader cl)
    {
        init(path, cl);
    }


    /*
     * public scope methods
     */

    public String toString()
    {
        URL url = getURL();
        return (url != null) ? url.toExternalForm() : "(URL is null) " + path_;
    }


    public boolean equals(Object o)
    {
        if (o == null) {
            return false;
        } else if (o.getClass() != getClass()) {
            return false;
        }

        JavaResource jr = (JavaResource)o;
        if (jr.cl_ == null) {
            if (cl_ != null) {
                return false;
            }
        } else if (!jr.cl_.equals(cl_)) {
            return false;
        }
        if (jr.path_ == null) {
            if (path_ != null) {
                return false;
            }
        } else if (!jr.path_.equals(path_)) {
            return false;
        }
        return true;
    }


    public int hashCode()
    {
        if (path_ == null) {
            return 0;
        } else {
            return path_.hashCode();
        }
    }


    /*
     * Resource
     */

    public boolean delete()
    {
        throw new UnsupportedOperationException();
    }


    public boolean exists()
    {
        return (getURL() != null);
    }


    public Resource getChildResource(String child)
    {
        if (child.startsWith("/")) {
            child = child.substring(1);
        }
        return new JavaResource(npath_.length() == 0 ? child : npath_ + "/"
            + child, cl_);
    }


    public InputStream getInputStream()
        throws ResourceNotFoundException
    {
        InputStream in = cl_.getResourceAsStream(path_);
        if (in == null) {
            throw new ResourceNotFoundException("Can't find: " + path_);
        }
        return in;
    }


    public long getLastModifiedTime()
    {
        URL url = getURL();
        if (url == null) {
            return 0L;
        }
        URLConnection con = null;
        try {
            con = url.openConnection();
            return con.getLastModified();
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            if (con != null) {
                try {
                    con.getInputStream().close();
                } catch (IOException ex1) {
                    ;
                }
            }
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
        String parentPath;
        int slash = npath_.lastIndexOf('/');
        if (slash < 0) {
            parentPath = "/";
        } else {
            parentPath = npath_.substring(0, slash + 1);
        }
        return new JavaResource(parentPath, cl_);
    }


    public long getSize()
    {
        URL url = getURL();
        if (url == null) {
            return 0L;
        }
        URLConnection con = null;
        try {
            con = url.openConnection();
            return con.getContentLength();
        } catch (IOException ex) {
            throw new IORuntimeException();
        } finally {
            if (con != null) {
                try {
                    con.getInputStream().close();
                } catch (IOException ex1) {
                    ;
                }
            }
        }
    }


    public URL getURL()
    {
        if (!urlConstructed_) {
            url_ = cl_.getResource(path_);
            urlConstructed_ = true;
        }
        return url_;
    }


    public URL[] getURLs()
    {
        return new URL[] { getURL() };
    }


    public boolean isDirectory()
    {
        return path_.endsWith("/");
    }


    public String[] list()
    {
        if (!isDirectory()) {
            return null;
        }
        return new String[0];
    }


    public Resource[] listResources()
    {
        if (!isDirectory()) {
            return null;
        }
        return new Resource[0];
    }


    public boolean mkdir()
    {
        throw new UnsupportedOperationException();
    }


    public boolean mkdirs()
    {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }


    public File[] toFiles()
    {
        throw new UnsupportedOperationException();
    }


    /*
     * private scope methods
     */

    private void init(String path, ClassLoader cl)
    {
        path_ = path;
        cl_ = cl;

        if (path.endsWith("/")) {
            npath_ = path.substring(0, path.length() - 1);
        } else {
            npath_ = path;
        }
        if (npath_.startsWith("/")) {
            npath_ = npath_.substring(1);
        }
        int slash = npath_.lastIndexOf('/');
        if (slash < 0) {
            name_ = npath_;
        } else {
            name_ = npath_.substring(slash + 1);
        }
    }
}
