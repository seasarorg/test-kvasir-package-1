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


@SuppressWarnings("unchecked")
public class ThreadLocalServletContext
    implements ServletContext
{
    private ServletContext originalServletContext_;

    private ThreadLocal<ServletContext> servletContexts_ = new ThreadLocal<ServletContext>();


    public ThreadLocalServletContext(ServletContext originalServletContext)
    {
        originalServletContext_ = originalServletContext;
    }


    public ServletContext getOriginalServletContext()
    {
        return originalServletContext_;
    }


    public ServletContext getContextServletContext()
    {
        return servletContexts_.get();
    }


    public ServletContext findContextServletContext()
    {
        ServletContext servletContext = getContextServletContext();
        if (servletContext == null) {
            servletContext = originalServletContext_;
        }
        return servletContext;
    }


    public void setContextServletContext(ServletContext servletContext)
    {
        servletContexts_.set(servletContext);
    }


    public Object getAttribute(String name)
    {
        return findContextServletContext().getAttribute(name);
    }


    public Enumeration getAttributeNames()
    {
        return findContextServletContext().getAttributeNames();
    }


    public ServletContext getContext(String uripath)
    {
        return findContextServletContext().getContext(uripath);
    }


    public String getInitParameter(String name)
    {
        return findContextServletContext().getInitParameter(name);
    }


    public Enumeration getInitParameterNames()
    {
        return findContextServletContext().getInitParameterNames();
    }


    public int getMajorVersion()
    {
        return findContextServletContext().getMajorVersion();
    }


    public String getMimeType(String file)
    {
        return findContextServletContext().getMimeType(file);
    }


    public int getMinorVersion()
    {
        return findContextServletContext().getMinorVersion();
    }


    public RequestDispatcher getNamedDispatcher(String name)
    {
        return findContextServletContext().getNamedDispatcher(name);
    }


    public String getRealPath(String path)
    {
        return findContextServletContext().getRealPath(path);
    }


    public RequestDispatcher getRequestDispatcher(String path)
    {
        return findContextServletContext().getRequestDispatcher(path);
    }


    public URL getResource(String path)
        throws MalformedURLException
    {
        return findContextServletContext().getResource(path);
    }


    public InputStream getResourceAsStream(String path)
    {
        return findContextServletContext().getResourceAsStream(path);
    }


    public Set getResourcePaths(String path)
    {
        return findContextServletContext().getResourcePaths(path);
    }


    public String getServerInfo()
    {
        return findContextServletContext().getServerInfo();
    }


    @SuppressWarnings("deprecation")
    public Servlet getServlet(String name)
        throws ServletException
    {
        return findContextServletContext().getServlet(name);
    }


    public String getServletContextName()
    {
        return findContextServletContext().getServletContextName();
    }


    @SuppressWarnings("deprecation")
    public Enumeration getServletNames()
    {
        return findContextServletContext().getServletNames();
    }


    @SuppressWarnings("deprecation")
    public Enumeration getServlets()
    {
        return findContextServletContext().getServlets();
    }


    public void log(String msg)
    {
        findContextServletContext().log(msg);
    }


    @SuppressWarnings("deprecation")
    public void log(Exception exception, String msg)
    {
        findContextServletContext().log(exception, msg);
    }


    public void log(String message, Throwable throwable)
    {
        findContextServletContext().log(message, throwable);
    }


    public void removeAttribute(String name)
    {
        findContextServletContext().removeAttribute(name);
    }


    public void setAttribute(String name, Object object)
    {
        findContextServletContext().setAttribute(name, object);
    }
}
