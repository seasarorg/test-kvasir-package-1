package org.seasar.kvasir.base;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.impl.FileResource;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class KvasirProperties
    implements PropertyHandler
{
    private PropertyHandler defaultConfig_;

    private PropertyHandler customConfig_;

    private PropertyHandler tempConfig_;

    private URL customConfigResource_;


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param defaultConfigResourcePath デフォルト設定リソースのパス。
     * 読み込み専用です。nullを指定することはできません。
     * @param customConfigResourcePath カスタム設定リソースのパス。
     * {@link #store()}を呼び出すとこの設定リソースに設定が保存されます。
     * カスタム設定リソース中で定義されている設定は、
     * デフォルト設定リソース中で定義されている設定よりも優先されます。
     * nullを指定することもできますが、
     * その場合は設定は保存されません。。
     * @param tempConfig 一時な設定を保持しているPropertyHandlerオブジェクト。
     * nullを指定することもできます。
     * この設定はどの設定よりも優先されます。
     * @param classLoader 設定リソースを読み込むためのクラスローダ。
     * nullを指定してはいけません。
     */
    @SuppressWarnings("unchecked")
    public KvasirProperties(String defaultConfigResourcePath,
        String customConfigResourcePath, PropertyHandler tempConfig,
        ClassLoader classLoader)
    {
        URL defaultConfigResource = classLoader
            .getResource(defaultConfigResourcePath);
        defaultConfig_ = loadProperties(defaultConfigResource,
            new HashMap<String, String>());

        URL customConfigResource = (customConfigResourcePath != null) ? classLoader
            .getResource(customConfigResourcePath)
            : null;
        customConfig_ = loadProperties(customConfigResource,
            new TreeMap<String, String>());
        customConfigResource_ = customConfigResource;

        if (tempConfig != null) {
            tempConfig_ = new MapProperties(new HashMap<String, String>());
            synchronized (tempConfig) {
                for (Enumeration<String> enm = tempConfig.propertyNames(); enm
                    .hasMoreElements();) {
                    String name = (String)enm.nextElement();
                    tempConfig_.setProperty(name, tempConfig.getProperty(name));
                }
            }
        }
    }


    /*
     * public scope methods
     */

    public synchronized void store()
    {
        if (customConfigResource_ == null) {
            return;
        }

        Resource storeResource = new FileResource(ClassUtils
            .getFileOfResource(customConfigResource_));
        OutputStream os = null;
        Writer out = null;
        try {
            os = storeResource.getOutputStream();
            out = new OutputStreamWriter(os, "UTF-8");
            os = null;
            store(out);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(os);
        }
    }


    /*
     * PropertyHandler
     */

    public synchronized void clearProperties()
    {
        customConfig_.clearProperties();
    }


    public boolean containsPropertyName(String name)
    {
        return defaultConfig_.containsPropertyName(name);
    }


    public synchronized String getProperty(String name)
    {
        if ((tempConfig_ != null) && tempConfig_.containsPropertyName(name)) {
            return tempConfig_.getProperty(name);
        } else if (customConfig_.containsPropertyName(name)) {
            return customConfig_.getProperty(name);
        } else {
            return defaultConfig_.getProperty(name);
        }
    }


    public synchronized void load(Reader in)
        throws IOException
    {
        customConfig_.load(in);
    }


    @SuppressWarnings("unchecked")
    public Enumeration<String> propertyNames()
    {
        return defaultConfig_.propertyNames();
    }


    public synchronized void removeProperty(String name)
    {
        customConfig_.removeProperty(name);
    }


    public synchronized void setProperty(String name, String value)
    {
        customConfig_.setProperty(name, value);
    }


    public int size()
    {
        return defaultConfig_.size();
    }


    public synchronized void store(Writer out)
        throws IOException
    {
        customConfig_.store(out);
    }


    /*
     * private scope methods
     */

    private MapProperties loadProperties(URL resource,
        Map<String, String> baseMap)
    {
        MapProperties prop = new MapProperties(baseMap);
        if (resource != null) {
            try {
                prop.load(resource.openStream(), "UTF-8");
            } catch (IOException ex) {
                throw new IORuntimeException("Can't read resource: " + resource);
            }
        }
        return prop;
    }
}
