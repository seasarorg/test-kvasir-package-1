package org.seasar.kvasir.page.ability.impl;

import java.util.Enumeration;
import java.util.Locale;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.AbstractPageAbility;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.PropertyAbilityAlfr;
import org.seasar.kvasir.util.collection.PropertyHandler;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PropertyAbilityImpl extends AbstractPageAbility
    implements PropertyAbility
{
    private PropertyAbilityAlfr alfr_;


    public PropertyAbilityImpl(PropertyAbilityAlfr alfr, Page page)
    {
        super(alfr, page);

        alfr_ = alfr;
    }


    public String getProperty(String name)
    {
        return alfr_.getProperty(page_, name);
    }


    public String getProperty(String name, Locale locale)
    {
        return alfr_.getProperty(page_, name, locale);
    }


    public String findProperty(String name, Locale locale)
    {
        return alfr_.findProperty(page_, name, locale);
    }


    public Page findPageHoldingProperty(String name, Locale locale)
    {
        return alfr_.findPageHoldingProperty(page_, name, locale);
    }


    public boolean containsPropertyName(String name, Locale locale)
    {
        return alfr_.containsPropertyName(page_, name, locale);
    }


    public String getProperty(String name, String variant)
    {
        return alfr_.getProperty(page_, name, variant);
    }


    public void setProperty(String name, String value)
    {
        alfr_.setProperty(page_, name, value);
    }


    public void setProperty(String name, String variant, String value)
    {
        alfr_.setProperty(page_, name, variant, value);
    }


    public void setProperties(String variant, PropertyHandler prop)
    {
        alfr_.setProperties(page_, variant, prop);
    }


    public void setProperties(PropertyHandler prop)
    {
        alfr_.setProperties(page_, prop);
    }


    public void removeProperty(String name)
    {
        alfr_.removeProperty(page_, name);
    }


    public void removeProperty(String name, String variant)
    {
        alfr_.removeProperty(page_, name, variant);
    }


    public void clearProperties()
    {
        alfr_.clearProperties(page_);
    }


    public void clearProperties(String variant)
    {
        alfr_.clearProperties(page_, variant);
    }


    public void clearAllProperties()
    {
        alfr_.clearAllProperties(page_);
    }


    public boolean containsPropertyName(String name)
    {
        return alfr_.containsPropertyName(page_, name);
    }


    public boolean containsPropertyName(String name, String variant)
    {
        return alfr_.containsPropertyName(page_, name, variant);
    }


    public Enumeration<String> propertyNames()
    {
        return alfr_.propertyNames(page_);
    }


    public Enumeration<String> propertyNames(String variant)
    {
        return alfr_.propertyNames(page_, variant);
    }
}
