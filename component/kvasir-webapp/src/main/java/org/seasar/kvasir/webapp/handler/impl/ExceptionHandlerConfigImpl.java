package org.seasar.kvasir.webapp.handler.impl;

import java.util.Enumeration;
import java.util.Vector;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.webapp.handler.ExceptionHandlerConfig;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class ExceptionHandlerConfigImpl
    implements ExceptionHandlerConfig
{
    private Kvasir kvasir_;

    private String errorHandlerName_;

    private PropertyHandler prop_;


    public ExceptionHandlerConfigImpl(String errorHandlerName)
    {
        this(errorHandlerName, null);
    }


    public ExceptionHandlerConfigImpl(String errorHandlerName,
        PropertyHandler prop)
    {
        kvasir_ = Asgard.getKvasir();
        errorHandlerName_ = errorHandlerName;
        prop_ = prop;
    }


    /*
     * RequestProcessorConfig
     */

    public String getInitParameter(String name)
    {
        String value = kvasir_.getProperty(errorHandlerName_ + "$" + name);
        if ((value == null) && (prop_ != null)) {
            value = prop_.getProperty(name);
        }
        return value;
    }


    @SuppressWarnings("unchecked")
    public Enumeration<String> getInitParameterNames()
    {
        if (prop_ != null) {
            return prop_.propertyNames();
        } else {
            return new Vector<String>().elements();
        }
    }


    public String getErrorHandlerName()
    {
        return errorHandlerName_;
    }
}
