package org.seasar.kvasir.util.collection;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.seasar.kvasir.util.LocaleUtils;
import org.seasar.kvasir.util.el.EvaluationException;
import org.seasar.kvasir.util.el.TextTemplateEvaluator;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.el.impl.EchoTextTemplateEvaluator;
import org.seasar.kvasir.util.el.impl.SimpleTextTemplateEvaluator;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * 制限事項: ロケール名部分を持たないファイルは必ず配置するようにしてください。
 * "aaa.xproperties"という指定に対しては"aaa.xproperties"ファイルを配置してください。
 * "aaa_en.properties"しか存在しないような場合にはIllegalArgumentExceptionを投げます。
 * 
 * @author YOKOTA Takehiko
 */
public class I18NProperties
    implements I18NPropertyHandler
{
    private final Resource dir_;

    private final String baseName_;

    private final String suffix_;

    private final I18NPropertyHandler parent_;

    private final int minLength_;

    private String[] variants_;

    private final Map propByLocaleMap_ = new HashMap();

    private final Map propByResourceNameMap_ = new HashMap();

    private final Map modifiedByVariantMap_ = Collections
        .synchronizedMap(new HashMap());

    private final Map variableResolvers_ = Collections
        .synchronizedMap(new HashMap());

    private TextTemplateEvaluator evaluator_ = new EchoTextTemplateEvaluator();

    /**
     * trueの場合はプロパティの${...}部分を変数として解釈します。
     */
    private boolean evaluateVariable_;


    /*
     * constructors
     */

    /**
     * このクラスのインスタンスを構築します。
     * <p>構築されるインスタンスは、
     * <code>new I18NProperties(dir, baseName, suffix, null)</code>
     * で構築したインスタンスと同じになります。</p>
     * 
     * @param dir プロパティリソースの親ディレクトリ。
     * nullを指定してはいけません。
     * @param baseName プロパティリソースのベース名。
     * nullを指定してはいけません。
     * @param suffix プロパティリソースのサフィックス。
     * nullを指定してはいけません。
     */
    public I18NProperties(final Resource dir, final String baseName,
        final String suffix)
    {
        this(dir, baseName, suffix, null);
    }


    /**
     * このクラスのインスタンスを構築します。
     * <p>構築されるインスタンスは、
     * ディレクトリ<code>dir</code>にある
     * <code>baseName</code>とロケールのサフィックスと<code>suffix</code>
     * を連結した名前のプロパティリソース群から適切なプロパティを返すような
     * {@link I18NProperties}インスタンスになります。</p>
     * <p><code>parent</code>としてnullでないオブジェクトを指定した場合、
     * プロパティが見つからなかった場合に<code>parent</code>
     * からプロパティを探して返すようになります。</p>
     * 
     * @param dir プロパティリソースの親ディレクトリ。
     * nullを指定してはいけません。
     * @param baseName プロパティリソースのベース名。
     * nullを指定してはいけません。
     * @param suffix プロパティリソースのサフィックス。
     * nullを指定してはいけません。
     * @param parent 親オブジェクト。nullを指定しても構いません。
     */
    public I18NProperties(final Resource dir, final String baseName,
        final String suffix, final I18NPropertyHandler parent)
    {
        dir_ = dir;
        baseName_ = baseName;
        suffix_ = suffix;
        parent_ = parent;
        minLength_ = baseName_.length() + suffix_.length();
    }


    /*
     * public scope methods
     */
    public String toString()
    {
        return "{directory=" + dir_ + ", baseName=" + baseName_ + ", suffix="
            + suffix_ + "}";
    }


    /*
     * I18NPropertyHandler
     */

    public String getProperty(final String name, final String variant)
    {
        return getProperty0(name, variant);
    }


    private String getProperty0(final String name, final String variant)
    {
        final PropertyHandler propertyHandler = getPropertiesByVariant(variant,
            false);
        if (propertyHandler != null) {
            final String property = getPropertyFromPropertyHandler(
                propertyHandler, name);
            if (property != null) {
                return property;
            }
        }
        if (parent_ != null) {
            return parent_.getProperty(name, variant);
        }
        return null;
    }


    public String getProperty(final String name)
    {
        return getProperty(name, VARIANT_DEFAULT);
    }


    public String getProperty(final String name, final Locale locale)
    {
        return getProperty0(name, locale);
    }


    private String getProperty0(final String name, final Locale locale)
    {
        final PropertyHandler propertyHandler = getPropertiesByLocale(locale);
        if (propertyHandler != null) {
            final String property = getPropertyFromPropertyHandler(
                propertyHandler, name);
            if (property != null) {
                return property;
            }
        }
        if (parent_ != null) {
            return parent_.getProperty(name, locale);
        }
        return null;
    }


    public boolean containsPropertyName(final String name, final Locale locale)
    {
        return getPropertiesByLocale(locale).containsPropertyName(name);
    }


    public boolean containsPropertyName(final String name, final String variant)
    {
        final PropertyHandler prop = getPropertiesByVariant(variant, false);
        if (prop != null) {
            return prop.containsPropertyName(name);
        } else {
            return false;
        }
    }


    public boolean containsPropertyName(final String name)
    {
        return containsPropertyName(name, VARIANT_DEFAULT);
    }


    public void setProperty(final String name, final String variant,
        final String value)
    {
        getPropertiesByVariant(variant, true).setProperty(name, value);
        modifiedByVariantMap_.put(variant, Boolean.TRUE);
    }


    public void setProperty(final String name, final String value)
    {
        setProperty(name, VARIANT_DEFAULT, value);
    }


    public void removeProperty(final String name, final String variant)
    {
        final PropertyHandler prop = getPropertiesByVariant(variant, false);
        if (prop != null) {
            prop.removeProperty(name);
            modifiedByVariantMap_.put(variant, Boolean.TRUE);
        }
    }


    public void removeProperty(final String name)
    {
        removeProperty(name, VARIANT_DEFAULT);
    }


    public void clearProperties(final String variant)
    {
        final PropertyHandler prop = getPropertiesByVariant(variant, false);
        if (prop != null) {
            prop.clearProperties();
            modifiedByVariantMap_.put(variant, Boolean.TRUE);
        }
    }


    public void clearProperties()
    {
        clearProperties(VARIANT_DEFAULT);
    }


    public int size(final String variant)
    {
        final PropertyHandler prop = getPropertiesByVariant(variant, false);
        if (prop != null) {
            return prop.size();
        } else {
            return 0;
        }
    }


    public int size()
    {
        return size(VARIANT_DEFAULT);
    }


    public Enumeration propertyNames(final String variant)
    {
        final PropertyHandler prop = getPropertiesByVariant(variant, false);
        if (prop != null) {
            return prop.propertyNames();
        } else {
            return new Vector().elements();
        }
    }


    public Enumeration propertyNames()
    {
        return propertyNames(VARIANT_DEFAULT);
    }


    public synchronized String[] getVariants()
    {
        if (variants_ == null) {
            final String[] names = dir_.list();
            if (names == null) {
                variants_ = new String[0];
            } else {
                final List list = new ArrayList(names.length);
                for (int i = 0; i < names.length; i++) {
                    if (names[i].length() < minLength_
                        || !names[i].startsWith(baseName_)
                        || !names[i].endsWith(suffix_)) {
                        continue;
                    }
                    final String suffix = names[i].substring(
                        baseName_.length(), names[i].length()
                            - suffix_.length());
                    if (suffix.length() > 0) {
                        if (suffix.charAt(0) != '_') {
                            continue;
                        }
                        list.add(suffix.substring(1));
                    } else {
                        list.add(suffix);
                    }
                }
                variants_ = (String[])list.toArray(new String[0]);
            }
        }
        return variants_;
    }


    public void load(final String variant, final Reader in)
        throws IOException
    {
        getPropertiesByVariant(variant, true).load(in);
    }


    public void load(final Reader in)
        throws IOException
    {
        load(VARIANT_DEFAULT, in);
    }


    public void store(final String variant, final Writer out)
        throws IOException
    {
        final PropertyHandler prop = getPropertiesByVariant(variant, false);
        if (prop != null) {
            prop.store(out);
        }
    }


    public void store(final Writer out)
        throws IOException
    {
        store(VARIANT_DEFAULT, out);
    }


    public synchronized void store()
        throws IOException
    {
        for (final Iterator itr = modifiedByVariantMap_.entrySet().iterator(); itr
            .hasNext();) {
            final Map.Entry entry = (Map.Entry)itr.next();
            final String variant = (String)entry.getKey();
            final Boolean modified = (Boolean)entry.getValue();

            if (modified != null && modified.booleanValue()) {
                final String resourceName = getResourceName(variant);
                final PropertyHandler prop = (PropertyHandler)propByResourceNameMap_
                    .get(resourceName);
                if (prop == null) {
                    throw new RuntimeException("LOGIC ERROR");
                }

                final Resource resource = dir_.getChildResource(resourceName);
                if (prop.size() == 0) {
                    // 空のvariantについてはリソースを削除する。
                    resource.delete();
                    // variantsから削除したvariantを取り除くためにこうする。
                    variants_ = null;
                } else {
                    // 空でないvariantについてはリソースとして保存する。
                    OutputStream os = null;
                    Writer out = null;
                    try {
                        os = resource.getOutputStream();
                        out = new OutputStreamWriter(os, "UTF-8");
                        os = null;
                        prop.store(out);
                    } finally {
                        IOUtils.closeQuietly(out);
                        IOUtils.closeQuietly(os);
                    }
                }
            }
        }
    }


    /*
     * private scope methods
     */

    private synchronized PropertyHandler getPropertiesByLocale(
        final Locale locale)
    {
        PropertyHandler prop = (PropertyHandler)propByLocaleMap_.get(locale);
        if (prop == null) {
            final Resource[] resources = LocaleUtils.findResources(dir_,
                baseName_, suffix_, locale);
            final List propList = new ArrayList(resources.length + 1);
            for (int i = 0; i < resources.length; i++) {
                final String resourceName = resources[i].getName();
                PropertyHandler p;
                if (!propByResourceNameMap_.containsKey(resourceName)) {
                    p = newPropertyHandler();
                    try {
                        p.load(new InputStreamReader(resources[i]
                            .getInputStream(), "UTF-8"));
                    } catch (final ResourceNotFoundException ex) {
                        p = null;
                        continue;
                    } catch (final IOException ex) {
                        throw new RuntimeException("Can't read resource: "
                            + resources[i], ex);
                    }
                    propByResourceNameMap_.put(resourceName, p);
                } else {
                    p = (PropertyHandler)propByResourceNameMap_
                        .get(resourceName);
                }
                if (p != null) {
                    propList.add(p);
                }
            }
            prop = newPropertyHandler((PropertyHandler[])propList
                .toArray(new PropertyHandler[0]));
            propByLocaleMap_.put(locale, prop);
        }
        return prop;
    }


    private synchronized PropertyHandler getPropertiesByVariant(
        final String variant, final boolean createIfNotExist)
    {
        final String resourceName = getResourceName(variant);

        if (!propByResourceNameMap_.containsKey(resourceName)) {
            final Resource resource = dir_.getChildResource(resourceName);
            PropertyHandler prop = newPropertyHandler();
            try {
                prop.load(new InputStreamReader(resource.getInputStream(),
                    "UTF-8"));
            } catch (final ResourceNotFoundException ex) {
                prop = null;
            } catch (final IOException ex) {
                throw new RuntimeException("Can't read resource: " + resource,
                    ex);
            }
            if (prop == null && createIfNotExist) {
                prop = newPropertyHandler();

                final String[] variants = getVariants();
                final String[] vs = new String[variants.length + 1];
                System.arraycopy(variants, 0, vs, 0, variants.length);
                vs[variants.length] = variant;
                variants_ = vs;
            }
            propByResourceNameMap_.put(resourceName, prop);
        }
        return (PropertyHandler)propByResourceNameMap_.get(resourceName);
    }


    private String getResourceName(final String variant)
    {
        final StringBuffer sb = new StringBuffer(baseName_.length()
            + variant.length() + 1 + suffix_.length());
        sb.append(baseName_);
        if (variant.length() > 0) {
            sb.append("_");
            sb.append(variant);
        }
        sb.append(suffix_);
        return sb.toString();
    }


    private PropertyHandler newPropertyHandler()
    {
        return newPropertyHandler(null);
    }


    private PropertyHandler newPropertyHandler(final PropertyHandler[] parents)
    {
        return new SynchronizedPropertyHandler(new MapProperties(new TreeMap(),
            parents, true));
    }


    private String getPropertyFromPropertyHandler(final PropertyHandler prop,
        final String name)
    {
        final VariableResolver variableResolver = getVariableResolver(prop);
        return resolveValue(variableResolver, prop.getProperty(name));
    }


    private VariableResolver getVariableResolver(final PropertyHandler prop)
    {
        VariableResolver variableResolver = (VariableResolver)variableResolvers_
            .get(prop);
        if (variableResolver == null) {
            variableResolver = new MyVariableResolver(prop);
            variableResolvers_.put(prop, variableResolver);
        }
        return variableResolver;
    }


    private String resolveValue(final VariableResolver variableResolver,
        final String value)
    {
        try {
            return evaluator_.evaluateAsString(value, variableResolver);
        } catch (final EvaluationException ex) {
            //logger_.warn("Can't evaluate: " + value, ex);
            return value;
        }
    }


    public void setEvaluateVariable(final boolean evaluateVariable)
    {
        evaluateVariable_ = evaluateVariable;
        if (evaluateVariable_) {
            evaluator_ = new SimpleTextTemplateEvaluator();
        } else {
            evaluator_ = new EchoTextTemplateEvaluator();
        }
    }


    private class MyVariableResolver
        implements VariableResolver
    {

        private final PropertyHandler propertyHandler_;


        MyVariableResolver(final PropertyHandler propertyHandler)
        {
            propertyHandler_ = propertyHandler;
        }


        public Object getValue(final Object key)
        {
            final String value = getPropertyFromPropertyHandler(
                propertyHandler_, String.valueOf(key));
            if (value != null) {
                return value;
            }
            return "";
        }

    }

}
