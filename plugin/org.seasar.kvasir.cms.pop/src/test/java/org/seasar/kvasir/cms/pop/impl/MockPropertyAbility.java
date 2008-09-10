package org.seasar.kvasir.cms.pop.impl;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.AttributeFilter;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.util.collection.PropertyHandler;


/**
 * @author YOKOTA Takehiko
 */
public class MockPropertyAbility
    implements PropertyAbility
{
    public String getProperty(String name)
    {
        return null;
    }


    public String getProperty(String name, Locale locale)
    {
        if (name.equals("pop.full.text-0.title")) {
            return "Title_" + locale.toString();
        }
        if (name.equals("pop.full.text-0.body")) {
            return "Body_" + locale.toString();
        }
        return null;
    }


    public String findProperty(String name, Locale locale)
    {
        return null;
    }


    public Page findPageHoldingProperty(String name, Locale locale)
    {
        return null;
    }


    public boolean containsPropertyName(String name, Locale locale)
    {
        return false;
    }


    public String getProperty(String name, String variant)
    {
        return null;
    }


    public void setProperty(String name, String value)
    {
    }


    public void setProperty(String name, String variant, String value)
    {
    }


    public void setProperties(String variant, PropertyHandler prop)
    {
    }


    public void setProperties(PropertyHandler prop)
    {
    }


    public void removeProperty(String name)
    {
    }


    public void removeProperty(String name, String variant)
    {
    }


    public void clearProperties()
    {
    }


    public void clearProperties(String variant)
    {
    }


    public void clearAllProperties()
    {
    }


    public boolean containsPropertyName(String name)
    {
        return false;
    }


    public boolean containsPropertyName(String name, String variant)
    {
        return false;
    }


    public Enumeration<String> propertyNames()
    {
        return null;
    }


    public Enumeration<String> propertyNames(String variant)
    {
        return null;
    }


    public void delete()
    {
    }


    public String[] getVariants()
    {
        return null;
    }


    public Attribute getAttribute(String name, String variant)
    {
        return null;
    }


    public void setAttribute(String name, String variant, Attribute value)
    {
    }


    public void removeAttribute(String name, String variant)
    {
    }


    public void clearAttributes()
    {
    }


    public boolean containsAttribute(String name, String variant)
    {
        return false;
    }


    public Iterator<String> attributeNames(String variant)
    {
        return null;
    }


    public Iterator<String> attributeNames(String variant,
        AttributeFilter filter)
    {
        return null;
    }

}
