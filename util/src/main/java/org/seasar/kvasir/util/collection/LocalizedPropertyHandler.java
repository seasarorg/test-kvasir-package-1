package org.seasar.kvasir.util.collection;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Locale;


/**
 * 指定されたI18NPropertyHandler
 * に対して特定のロケールについてアクセスするためのPropertyHandlerです。
 * <p>プロパティの読み出しだけが可能です。
 * 変更操作はサポートしていません。
 * また読み出しメソッドについてもサポートされていないものがあります。
 * </p>
 * <p><b>同期化：</b>
 * このクラスは内包するI18NPropertyHandler
 * がスレッドセーフである場合のみスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class LocalizedPropertyHandler
    implements PropertyHandler
{
    private I18NPropertyHandler handler_;
    private Locale locale_;
    private String prefix_;


    public LocalizedPropertyHandler(I18NPropertyHandler handler, Locale locale)
    {
        this(handler, locale, "");
    }


    public LocalizedPropertyHandler(I18NPropertyHandler handler, Locale locale,
        String prefix)
    {
        handler_ = handler;
        locale_ = locale;
        prefix_ = prefix;
    }


    public String getProperty(String name)
    {
        return handler_.getProperty(constructName(name), locale_);
    }


    private String constructName(String name)
    {
        return prefix_ + name;
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
        return handler_.size();
    }


    public boolean containsPropertyName(String name)
    {
        return handler_.containsPropertyName(constructName(name), locale_);
    }


    public Enumeration propertyNames()
    {
        throw new UnsupportedOperationException();
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
