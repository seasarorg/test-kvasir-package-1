package org.seasar.kvasir.page.ability.impl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.AbstractPageAbilityAlfr;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.AttributeFilter;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.PropertyAbilityAlfr;
import org.seasar.kvasir.util.LocaleUtils;
import org.seasar.kvasir.util.collection.EnumerationIterator;
import org.seasar.kvasir.util.collection.PropertyHandler;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PropertyAbilityAlfrImpl extends AbstractPageAbilityAlfr
    implements PropertyAbilityAlfr
{
    private PropertyCache cache_;


    public void setCache(PropertyCache cache)
    {
        cache_ = cache;
    }


    /*
     * AbstractPageAbilityAlfr
     */

    @Override
    protected boolean doStart()
    {
        return true;
    }


    @Override
    protected void doStop()
    {
        cache_ = null;
    }


    /*
     * PropertyAbilityAlfr
     */

    public String getProperty(Page page, String name)
    {
        return getProperty(page, name, Page.VARIANT_DEFAULT);
    }


    public String getProperty(Page page, String name, Locale locale)
    {
        String[] variants = LocaleUtils.getSuffixes(locale);
        for (int i = 0; i < variants.length; i++) {
            String value = getProperty(page, name, variants[i]);
            if (value != null) {
                return value;
            }
        }

        return getProperty(page, name);
    }


    public String findProperty(Page page, String name, Locale locale)
    {
        Page p = findPageHoldingProperty(page, name, locale);
        if (p != null) {
            return getProperty(p, name, locale);
        } else {
            return null;
        }
    }


    public Page findPageHoldingProperty(Page page, String name, Locale locale)
    {
        Page p = page;
        while (true) {
            if (getProperty(p, name, locale) != null) {
                return p;
            }
            if (p.isRoot()) {
                return null;
            }
            p = p.getParent().getLord();
        }
    }


    public boolean containsPropertyName(Page page, String name, Locale locale)
    {
        String[] variants = LocaleUtils.getSuffixes(locale);
        for (int i = 0; i < variants.length; i++) {
            if (containsPropertyName(page, name, variants[i])) {
                return true;
            }
        }

        return containsPropertyName(page, name);
    }


    public synchronized String getProperty(Page page, String name,
        String variant)
    {
        PropertyHandler handler = cache_.get(newPropertyKey(page, variant));
        if (handler != null) {
            return handler.getProperty(name);
        } else {
            return null;
        }
    }


    PropertyKey newPropertyKey(Page page, String variant)
    {
        return new PropertyKey(page.getDto().getId(), variant);
    }


    public void setProperty(Page page, String name, String value)
    {
        setProperty(page, name, Page.VARIANT_DEFAULT, value);
    }


    public synchronized void setProperty(final Page page, final String name,
        final String variant, final String value)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                cache_.setProperty(newPropertyKey(page, variant), name, value);
                return null;
            }
        });
    }


    public void setProperties(final Page page, final PropertyHandler prop)
    {
        setProperties(page, Page.VARIANT_DEFAULT, prop);
    }


    public synchronized void setProperties(final Page page,
        final String variant, final PropertyHandler prop)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                cache_.setProperties(newPropertyKey(page, variant), prop);
                return null;
            }
        });
    }


    public void removeProperty(final Page page, final String name)
    {
        removeProperty(page, name, Page.VARIANT_DEFAULT);
    }


    public synchronized void removeProperty(final Page page, final String name,
        final String variant)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                cache_.removeProperty(newPropertyKey(page, variant), name);
                return null;
            }
        });
    }


    public void clearProperties(final Page page)
    {
        clearProperties(page, Page.VARIANT_DEFAULT);
    }


    public synchronized void clearProperties(final Page page,
        final String variant)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                cache_.clearProperties(newPropertyKey(page, variant));
                return null;
            }
        });
    }


    public synchronized void clearAllProperties(final Page page)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                cache_.clearAllProperties(page.getDto().getId());
                return null;
            }
        });
    }


    public boolean containsPropertyName(Page page, String name)
    {
        return containsPropertyName(page, name, Page.VARIANT_DEFAULT);
    }


    public synchronized boolean containsPropertyName(Page page, String name,
        String variant)
    {
        PropertyHandler handler = cache_.get(newPropertyKey(page, variant));
        if (handler != null) {
            return handler.containsPropertyName(name);
        } else {
            return false;
        }
    }


    public Enumeration<String> propertyNames(Page page)
    {
        return propertyNames(page, Page.VARIANT_DEFAULT);
    }


    @SuppressWarnings("unchecked")
    public synchronized Enumeration<String> propertyNames(Page page,
        String variant)
    {
        PropertyHandler handler = cache_.get(newPropertyKey(page, variant));
        if (handler != null) {
            return handler.propertyNames();
        } else {
            List<String> list = Collections.emptyList();
            return Collections.enumeration(list);
        }
    }


    /*
     * PageAbilityAlfr
     */

    public Class<? extends PageAbility> getAbilityInterfaceClass()
    {
        return PropertyAbility.class;
    }


    public String getShortId()
    {
        return SHORTID;
    }


    public PageAbility getAbility(Page page)
    {
        return new PropertyAbilityImpl(this, page);
    }


    public void create(Page page)
    {
        // 特に何もしない。
    }


    public void delete(final Page page)
    {
        cache_.clearAllProperties(page.getDto().getId());
    }


    public synchronized String[] getVariants(Page page)
    {
        return cache_.getVariants(page.getDto().getId());
    }


    public Attribute getAttribute(Page page, String name, String variant)
    {
        String value = getProperty(page, name, variant);
        if (value == null) {
            return null;
        }

        Attribute attr = new Attribute();
        attr.setString(SUBNAME_DEFAULT, value);
        return attr;
    }


    public void setAttribute(Page page, String name, String variant,
        Attribute attr)
    {
        String value = attr.getString(SUBNAME_DEFAULT);
        if (value == null) {
            return;
        }
        setProperty(page, name, variant, value);
    }


    public void removeAttribute(Page page, String name, String variant)
    {
        removeProperty(page, name, variant);
    }


    public void clearAttributes(final Page page)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                delete(page);
                return null;
            }
        });
    }


    public boolean containsAttribute(Page page, String name, String variant)
    {
        return containsPropertyName(page, name, variant);
    }


    @SuppressWarnings("unchecked")
    public Iterator<String> attributeNames(Page page, String variant,
        AttributeFilter filter)
    {
        return (Iterator<String>)new EnumerationIterator(propertyNames(page,
            variant));
    }
}
