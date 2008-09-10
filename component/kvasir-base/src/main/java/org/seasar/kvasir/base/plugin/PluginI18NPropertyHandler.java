package org.seasar.kvasir.base.plugin;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Locale;

import org.seasar.kvasir.util.collection.I18NPropertyHandler;


/**
 * プラグインが持つプロパティに
 * I18NPropertyHandlerインタフェース経由でアクセスするためのアダプタクラスです。
 * <p>プロパティの読み出しだけが可能です。
 * 変更操作はサポートしていません。
 * また読み出しメソッドについてもサポートされていないものがあります。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class PluginI18NPropertyHandler
    implements I18NPropertyHandler
{
    private Plugin<?> plugin_;


    public PluginI18NPropertyHandler(Plugin<?> plugin)
    {
        plugin_ = plugin;
    }


    public String getProperty(String name, String variant)
    {
        throw new UnsupportedOperationException();
    }


    public String getProperty(String name, Locale locale)
    {
        return plugin_.getProperty(name, locale);
    }


    public void setProperty(String name, String variant, String value)
    {
        throw new UnsupportedOperationException();
    }


    public void removeProperty(String name, String variant)
    {
        throw new UnsupportedOperationException();
    }


    public void clearProperties(String variant)
    {
        throw new UnsupportedOperationException();
    }


    public int size(String variant)
    {
        throw new UnsupportedOperationException();
    }


    public boolean containsPropertyName(String name, String variant)
    {
        throw new UnsupportedOperationException();
    }


    public boolean containsPropertyName(String name, Locale Locale)
    {
        throw new UnsupportedOperationException();
    }


    public Enumeration<String> propertyNames(String variant)
    {
        throw new UnsupportedOperationException();
    }


    public String[] getVariants()
    {
        throw new UnsupportedOperationException();
    }


    public void load(String variant, Reader in)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }


    public void store(String variant, Writer out)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }


    public String getProperty(String name)
    {
        return plugin_.getProperty(name);
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
        throw new UnsupportedOperationException();
    }


    public boolean containsPropertyName(String name)
    {
        throw new UnsupportedOperationException();
    }


    public Enumeration<String> propertyNames()
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
