package org.seasar.kvasir.webapp.servlet;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
@SuppressWarnings("unchecked")
public class ServletContextWrapper
    implements ServletContext
{
    private ServletContext sc_;


    public ServletContextWrapper(ServletContext sc)
    {
        sc_ = sc;
    }


    public ServletContext getServletContext()
    {
        return sc_;
    }


    /*
     * ServletContext
     */

    public Object getAttribute(String arg0)
    {
        return sc_.getAttribute(arg0);
    }


    public Enumeration getAttributeNames()
    {
        return sc_.getAttributeNames();
    }


    public ServletContext getContext(String arg0)
    {
        return sc_.getContext(arg0);
    }


    public String getInitParameter(String arg0)
    {
        return sc_.getInitParameter(arg0);
    }


    public Enumeration getInitParameterNames()
    {
        return sc_.getInitParameterNames();
    }


    public int getMajorVersion()
    {
        return sc_.getMajorVersion();
    }


    public String getMimeType(String arg0)
    {
        return sc_.getMimeType(arg0);
    }


    public int getMinorVersion()
    {
        return sc_.getMinorVersion();
    }


    public RequestDispatcher getNamedDispatcher(String arg0)
    {
        return sc_.getNamedDispatcher(arg0);
    }


    public String getRealPath(String arg0)
    {
        return sc_.getRealPath(arg0);
    }


    public RequestDispatcher getRequestDispatcher(String arg0)
    {
        return sc_.getRequestDispatcher(arg0);
    }


    public URL getResource(String arg0)
        throws MalformedURLException
    {
        return sc_.getResource(arg0);
    }


    public InputStream getResourceAsStream(String arg0)
    {
        return sc_.getResourceAsStream(arg0);
    }


    public Set getResourcePaths(String arg0)
    {
        return sc_.getResourcePaths(arg0);
    }


    public String getServerInfo()
    {
        return sc_.getServerInfo();
    }


    /**
     * @deprecated
     */
    @Deprecated
    public Servlet getServlet(String arg0)
        throws ServletException
    {
        return sc_.getServlet(arg0);
    }


    public String getServletContextName()
    {
        return sc_.getServletContextName();
    }


    /**
     * @deprecated
     */
    @Deprecated
    public Enumeration getServletNames()
    {
        return sc_.getServletNames();
    }


    /**
     * @deprecated
     */
    @Deprecated
    public Enumeration getServlets()
    {
        return sc_.getServlets();
    }


    /**
     * @deprecated
     */
    @Deprecated
    public void log(Exception arg0, String arg1)
    {
        sc_.log(arg0, arg1);
    }


    public void log(String arg0)
    {
        sc_.log(arg0);
    }


    public void log(String arg0, Throwable arg1)
    {
        sc_.log(arg0, arg1);
    }


    public void removeAttribute(String arg0)
    {
        sc_.removeAttribute(arg0);
    }


    public void setAttribute(String arg0, Object arg1)
    {
        sc_.setAttribute(arg0, arg1);
    }
}
