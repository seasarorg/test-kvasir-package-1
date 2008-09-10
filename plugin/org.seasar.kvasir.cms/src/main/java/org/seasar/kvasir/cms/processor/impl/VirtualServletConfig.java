package org.seasar.kvasir.cms.processor.impl;

import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
@SuppressWarnings("unchecked")
class VirtualServletConfig
    implements ServletConfig
{
    private String servletName_;

    private ServletContext context_;


    public VirtualServletConfig(String servletName, ServletContext context)
    {
        servletName_ = servletName;
        context_ = context;
    }


    /*
     * RequestProcessorConfig
     */

    public String getInitParameter(String name)
    {
        return context_.getInitParameter(name);
    }


    public Enumeration getInitParameterNames()
    {
        return context_.getInitParameterNames();
    }


    public ServletContext getServletContext()
    {
        return context_;
    }


    public String getServletName()
    {
        return servletName_;
    }
}
