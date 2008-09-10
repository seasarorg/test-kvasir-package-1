package org.seasar.kvasir.webapp.servlet;

import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.util.collection.PropertyHandler;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class KvasirServletConfig
    implements ServletConfig
{
    private Kvasir kvasir_;

    private ServletConfig config_;

    private String servletName_;

    private PropertyHandler prop_;


    public KvasirServletConfig(ServletConfig config, String servletName)
    {
        this(config, servletName, null);
    }


    public KvasirServletConfig(ServletConfig config, String servletName,
        PropertyHandler prop)
    {
        if (servletName == null) {
            servletName = config.getServletName();
        }

        kvasir_ = Asgard.getKvasir();
        config_ = config;
        servletName_ = servletName;
        prop_ = prop;
    }


    /*
     * RequestProcessorConfig
     */

    public String getInitParameter(String name)
    {
        String value = kvasir_.getProperty(servletName_ + "$" + name);
        if ((value == null) && (prop_ != null)) {
            value = prop_.getProperty(name);
        }
        return value;
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
        return config_.getServletContext();
    }


    public String getServletName()
    {
        return servletName_;
    }
}
