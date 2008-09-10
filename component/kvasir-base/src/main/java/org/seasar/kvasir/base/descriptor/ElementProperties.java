package org.seasar.kvasir.base.descriptor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.util.collection.PropertyHandler;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class ElementProperties
    implements PropertyHandler
{
    private String id_;

    private Plugin<?> plugin_;

    private PropertyHandler prop_;


    public ElementProperties(String id, Plugin<?> plugin, PropertyHandler prop)
    {
        id_ = id;
        prop_ = prop;
        plugin_ = plugin;
    }


    /*
     * PropertyHandler
     */

    public String getProperty(String name)
    {
        if (!prop_.containsPropertyName(name)) {
            return null;
        }
        String value = plugin_.getProperty(encode(name));
        if (value != null) {
            return value;
        } else {
            return prop_.getProperty(name);
        }
    }


    public void setProperty(String name, String value)
    {
        throw new UnsupportedOperationException();
    }


    public void removeProperty(String name)
    {
        throw new UnsupportedOperationException();
    }


    public void clearProperties()
    {
        throw new UnsupportedOperationException();
    }


    public int size()
    {
        return prop_.size();
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


    /*
     * private scope methods
     */

    private String encode(String name)
    {
        if (name == null) {
            return null;
        }
        return "extension.element(" + id_ + ")." + name;
    }
}
