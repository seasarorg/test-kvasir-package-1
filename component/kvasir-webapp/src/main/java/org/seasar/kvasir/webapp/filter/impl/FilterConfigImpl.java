package org.seasar.kvasir.webapp.filter.impl;

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.seasar.kvasir.util.collection.PropertyHandler;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class FilterConfigImpl
    implements FilterConfig
{
    private String filterName_;

    private ServletContext servletContext_;

    private PropertyHandler prop_;


    public FilterConfigImpl(String filterName, ServletContext servletContext)
    {
        this(filterName, servletContext, null);
    }


    public FilterConfigImpl(String filterName, ServletContext servletContext,
        PropertyHandler prop)
    {
        servletContext_ = servletContext;
        filterName_ = filterName;
        prop_ = prop;
    }


    /*
     * FilterConfig
     */

    public String getFilterName()
    {
        return filterName_;
    }


    public ServletContext getServletContext()
    {
        return servletContext_;
    }


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
}
