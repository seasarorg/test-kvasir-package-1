package org.seasar.kvasir.webapp.servlet;

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.seasar.kvasir.util.collection.PropertyHandler;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ServletConfigImpl
    implements ServletConfig
{
    private String servletName_;

    private ServletContext servletContext_;

    private PropertyHandler prop_;


    public ServletConfigImpl(String servletName, ServletContext servletContext)
    {
        this(servletName, servletContext, null);
    }


    public ServletConfigImpl(String servletName, ServletContext servletContext,
        PropertyHandler prop)
    {
        servletName_ = servletName;
        servletContext_ = servletContext;
        prop_ = prop;
    }


    /*
     * RequestProcessorConfig
     */

    public String getInitParameter(String name)
    {
        if (prop_ != null) {
            return prop_.getProperty(name);
        } else {
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    public Enumeration getInitParameterNames()
    {
        if (prop_ != null) {
            return prop_.propertyNames();
        } else {
            return new Vector().elements();
        }
    }


    public ServletContext getServletContext()
    {
        return servletContext_;
    }


    public String getServletName()
    {
        return servletName_;
    }
}
