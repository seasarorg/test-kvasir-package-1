package org.seasar.kvasir.util.collection;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;

/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class VariantPropertyHandler
    implements PropertyHandler
{
    private I18NPropertyHandler     handler_;
    private String                  variant_;


    public VariantPropertyHandler(I18NPropertyHandler handler,
        String variant)
    {
        handler_ = handler;
        variant_ = variant;
    }


    public String getProperty(String name)
    {
        return handler_.getProperty(name, variant_);
    }


    public void setProperty(String name, String value)
    {
        handler_.setProperty(name, variant_, value);
    }


    public void removeProperty(String name)
    {
        handler_.removeProperty(name, variant_);
    }


    public void clearProperties()
    {
        handler_.clearProperties(variant_);
    }


    public int size()
    {
        return handler_.size(variant_);
    }


    public boolean containsPropertyName(String name)
    {
        return handler_.containsPropertyName(name, variant_);
    }


    public Enumeration propertyNames()
    {
        return handler_.propertyNames(variant_);
    }


    public void load(Reader in)
        throws IOException
    {
        handler_.load(variant_, in);
    }


    public void store(Writer out)
        throws IOException
    {
        handler_.store(variant_, out);
    }
}
