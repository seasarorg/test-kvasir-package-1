package org.seasar.kvasir.util.collection;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class SynchronizedPropertyHandler
    implements PropertyHandler
{
    private PropertyHandler     prop_;


    public SynchronizedPropertyHandler(PropertyHandler prop)
    {
        prop_ = prop;
    }


    public synchronized void clearProperties()
    {
        prop_.clearProperties();
    }
    public synchronized boolean containsPropertyName(String name)
    {
        return prop_.containsPropertyName(name);
    }
    public synchronized String getProperty(String name)
    {
        return prop_.getProperty(name);
    }
    public synchronized void load(Reader in)
        throws IOException
    {
        prop_.load(in);
    }
    public synchronized Enumeration propertyNames()
    {
        return prop_.propertyNames();
    }
    public synchronized void removeProperty(String name)
    {
        prop_.removeProperty(name);
    }
    public synchronized void setProperty(String name, String value)
    {
        prop_.setProperty(name, value);
    }
    public synchronized int size()
    {
        return prop_.size();
    }
    public synchronized void store(Writer out)
        throws IOException
    {
        prop_.store(out);
    }
}
