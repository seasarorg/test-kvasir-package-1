package org.seasar.kvasir.base.plugin.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Locale;

import org.seasar.kvasir.util.collection.I18NProperties;
import org.seasar.kvasir.util.collection.I18NPropertyHandler;
import org.seasar.kvasir.util.collection.I18NPropertyReader;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.io.IORuntimeException;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class PluginProperties
    implements PropertyHandler, I18NPropertyReader
{
    private I18NProperties customProp_;

    private I18NPropertyHandler prop_;


    public PluginProperties(String id, I18NProperties customProp,
        I18NPropertyHandler prop)
    {
        customProp_ = customProp;
        prop_ = prop;
    }


    /*
     * public scope methods
     */

    public String getProperty(String name, String variant)
    {
        if (customProp_ != null) {
            String value = customProp_.getProperty(name, variant);
            if (value != null) {
                return value;
            }
        }
        return prop_.getProperty(name, variant);
    }


    public String getProperty(String name, Locale locale)
    {
        if (customProp_ != null) {
            String value = customProp_.getProperty(name, locale);
            if (value != null) {
                return value;
            }
        }
        return prop_.getProperty(name, locale);
    }


    public void store()
    {
        if (customProp_ != null) {
            try {
                customProp_.store();
            } catch (IOException ex) {
                throw new IORuntimeException("Can't store plugin properties: "
                    + customProp_);
            }
        }
    }


    /*
     * PropertyHandler
     */

    public String getProperty(String name)
    {
        if (customProp_ != null) {
            String value = customProp_.getProperty(name);
            if (value != null) {
                return value;
            }
        }
        return prop_.getProperty(name);
    }


    public void setProperty(String name, String value)
    {
        if (customProp_ != null) {
            customProp_.setProperty(name, value);
        }
    }


    public void removeProperty(String name)
    {
        if (customProp_ != null) {
            customProp_.removeProperty(name);
        }
    }


    public void clearProperties()
    {
        for (Enumeration<String> enm = propertyNames(); enm.hasMoreElements();) {
            removeProperty((String)enm.nextElement());
        }
    }


    public int size()
    {
        return prop_.size(I18NPropertyHandler.VARIANT_DEFAULT);
    }


    public boolean containsPropertyName(String name)
    {
        return prop_.containsPropertyName(name);
    }


    @SuppressWarnings("unchecked")
    public Enumeration<String> propertyNames()
    {
        return prop_.propertyNames();
    }


    public void load(Reader in)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }


    public void store(Writer out)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
