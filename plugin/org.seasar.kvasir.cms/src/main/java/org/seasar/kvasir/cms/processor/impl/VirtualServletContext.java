package org.seasar.kvasir.cms.processor.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.cms.processor.LocalPathResolver;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.webapp.servlet.ServletContextWrapper;
import org.seasar.kvasir.webapp.util.ServletUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
@SuppressWarnings("unchecked")
public class VirtualServletContext extends ServletContextWrapper
{
    private Kvasir kvasir_;

    private PageAlfr pageAlfr_;

    private Map attr_ = new HashMap();

    private String id_;

    private ServletContext context_;

    private PropertyHandler prop_;

    private LocalPathResolver resolver_;

    private int lordId_;

    private boolean activated_;


    public VirtualServletContext(String id, PropertyHandler prop,
        LocalPathResolver resolver, int lordId, Kvasir kvasir, PageAlfr pageAlfr)
    {
        super(ServletUtils.getOriginalServletContext());

        kvasir_ = kvasir;
        pageAlfr_ = pageAlfr;
        id_ = id;
        context_ = ServletUtils.getOriginalServletContext();
        prop_ = prop;
        resolver_ = resolver;
        lordId_ = lordId;

        activated_ = true;
    }


    /*
     * public scope methods
     */

    public boolean isActivated()
    {
        return activated_;
    }


    public void setActivated(boolean activated)
    {
        activated_ = activated;
    }


    public synchronized Object getAttribute(String arg0)
    {
        return attr_.get(arg0);
    }


    public synchronized Enumeration getAttributeNames()
    {
        return Collections.enumeration(attr_.keySet());
    }


    public ServletContext getContext(String arg0)
    {
        return null;
    }


    public String getInitParameter(String name)
    {
        String value = kvasir_.getProperty(id_ + "$" + name);
        if (value == null && prop_ != null) {
            value = prop_.getProperty(name);
        }
        return value;
    }


    public Enumeration getInitParameterNames()
    {
        if (prop_ != null) {
            return prop_.propertyNames();
        } else {
            return new Vector().elements();
        }
    }


    public int getMajorVersion()
    {
        return context_.getMajorVersion();
    }


    public String getMimeType(String arg0)
    {
        return context_.getMimeType(arg0);
    }


    public int getMinorVersion()
    {
        return context_.getMinorVersion();
    }


    public RequestDispatcher getNamedDispatcher(String name)
    {
        // XXX 今のところ未実装。
        // nameをPageProcessorのIDと見なしてPageProcessorに処理を
        // ディスパッチしても良いが、PageProcessorはチェインで処理している
        // ため単純にディスパッチするのではまずい気がする。
        return null;
    }


    public String getRealPath(String path)
    {
        return resolver_.getRealPath(path);
    }


    public RequestDispatcher getRequestDispatcher(String path)
    {
        Page lordPage = pageAlfr_.getPage(lordId_);
        if (lordPage == null) {
            return null;
        }
        String realPath;
        if (path.startsWith("!")) {
            realPath = path.substring(1/*= "!".length() */);
        } else {
            realPath = lordPage.getPathname() + path;
        }
        return new VirtualRequestDispatcher(path, context_
            .getRequestDispatcher(realPath));
    }


    public URL getResource(String path)
        throws MalformedURLException
    {
        String filePath = getRealPath(path);
        if (filePath != null) {
            return new File(filePath).toURI().toURL();
        } else {
            return null;
        }
    }


    public InputStream getResourceAsStream(String path)
    {
        String filePath = getRealPath(path);
        if (filePath == null) {
            return null;
        } else {
            try {
                return new FileInputStream(new File(filePath));
            } catch (FileNotFoundException ex) {
                return null;
            }
        }
    }


    public Set getResourcePaths(String path)
    {
        return resolver_.getResourcePaths(path);
    }


    public String getServerInfo()
    {
        return context_.getServerInfo();
    }


    /**
     * @deprecated
     */
    public Servlet getServlet(String arg0)
        throws ServletException
    {
        return context_.getServlet(arg0);
    }


    public String getServletContextName()
    {
        return id_;
    }


    /**
     * @deprecated
     */
    public Enumeration getServletNames()
    {
        return context_.getServletNames();
    }


    /**
     * @deprecated
     */
    public Enumeration getServlets()
    {
        return context_.getServlets();
    }


    /**
     * @deprecated
     */
    public void log(Exception arg0, String arg1)
    {
    }


    public void log(String arg0)
    {
        // XXX 適切なログに吐こう。
    }


    public void log(String arg0, Throwable arg1)
    {
        // XXX 適切なログに吐こう。
    }


    public synchronized void removeAttribute(String arg0)
    {
        attr_.remove(arg0);
    }


    public synchronized void setAttribute(String arg0, Object arg1)
    {
        attr_.put(arg0.intern(), arg1);
    }
}
