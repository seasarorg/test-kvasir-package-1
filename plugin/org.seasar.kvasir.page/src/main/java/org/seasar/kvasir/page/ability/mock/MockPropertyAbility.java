package org.seasar.kvasir.page.ability.mock;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.util.LocaleUtils;
import org.seasar.kvasir.util.collection.PropertyHandler;


/**
 * TODO かなり作りかけ…。
 *
 * @author YOKOTA Takehiko
 */
public class MockPropertyAbility extends MockPageAbility
    implements PropertyAbility
{
    private Map<String, Map<String, String>> propertyMap_ = new HashMap<String, Map<String, String>>();


    public MockPropertyAbility(Page page)
    {
        super(page);
    }


    public void clearProperties()
    {
        clearProperties(Page.VARIANT_DEFAULT);
    }


    public void clearProperties(String variant)
    {
        Map<String, String> map = propertyMap_.get(variant);
        if (map != null) {
            map.clear();
        }
    }


    public void clearAllProperties()
    {
        propertyMap_.clear();
    }


    public boolean containsPropertyName(String name, Locale locale)
    {
        return false;
    }


    public boolean containsPropertyName(String name)
    {
        return false;
    }


    public boolean containsPropertyName(String name, String variant)
    {
        return false;
    }


    public Page findPageHoldingProperty(String name, Locale locale)
    {
        return null;
    }


    public String findProperty(String name, Locale locale)
    {
        return null;
    }


    public String getProperty(String name)
    {
        return getProperty(name, Page.VARIANT_DEFAULT);
    }


    public String getProperty(String name, Locale locale)
    {
        String[] suffixes = LocaleUtils.getSuffixes(locale, true);
        for (int i = 0; i < suffixes.length; i++) {
            String value = getProperty(name, suffixes[i]);
            if (value != null) {
                return value;
            }
        }
        return null;
    }


    public String getProperty(String name, String variant)
    {
        Map<String, String> map = propertyMap_.get(variant);
        if (map == null) {
            return null;
        } else {
            return map.get(name);
        }
    }


    public Enumeration<String> propertyNames()
    {
        return null;
    }


    public Enumeration<String> propertyNames(String variant)
    {
        return null;
    }


    public void removeProperty(String name)
    {
        removeAttribute(name, Page.VARIANT_DEFAULT);
    }


    public void removeProperty(String name, String variant)
    {
        Map<String, String> map = propertyMap_.get(variant);
        if (map != null) {
            map.remove(name);
        }
    }


    public void setProperties(String variant, PropertyHandler prop)
    {
    }


    public void setProperties(PropertyHandler prop)
    {
    }


    public void setProperty(String name, String value)
    {
        setProperty(name, Page.VARIANT_DEFAULT, value);
    }


    public void setProperty(String name, String variant, String value)
    {
        Map<String, String> map = propertyMap_.get(variant);
        if (map == null) {
            map = new HashMap<String, String>();
            propertyMap_.put(variant, map);
        }
        map.put(name, value);
    }
}
