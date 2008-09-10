package org.seasar.kvasir.util.collection;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;


public class DelegatingI18NPropertyHandler
    implements I18NPropertyHandler
{
    private I18NPropertyHandler[] handlers_;


    public DelegatingI18NPropertyHandler(I18NPropertyHandler handler1,
        I18NPropertyHandler handler2)
    {
        this(new I18NPropertyHandler[] { handler1, handler2 });
    }


    public DelegatingI18NPropertyHandler(I18NPropertyHandler[] handlers)
        throws IllegalArgumentException
    {
        if (handlers == null) {
            throw new IllegalArgumentException("Specify non-null array.");
        }
        handlers_ = handlers;
    }


    public void clearProperties(String variant)
    {
        if (handlers_.length > 0) {
            handlers_[0].clearProperties(variant);
        }
    }


    public boolean containsPropertyName(String name, String variant)
    {
        for (int i = 0; i < handlers_.length; i++) {
            if (handlers_[i].containsPropertyName(name, variant)) {
                return true;
            }
        }
        return false;
    }


    public boolean containsPropertyName(String name, Locale Locale)
    {
        for (int i = 0; i < handlers_.length; i++) {
            if (handlers_[i].containsPropertyName(name, Locale)) {
                return true;
            }
        }
        return false;
    }


    public String getProperty(String name, String variant)
    {
        for (int i = 0; i < handlers_.length; i++) {
            String value = handlers_[i].getProperty(name, variant);
            if (value != null) {
                return value;
            }
        }
        return null;
    }


    public String getProperty(String name, Locale locale)
    {
        for (int i = 0; i < handlers_.length; i++) {
            String value = handlers_[i].getProperty(name, locale);
            if (value != null) {
                return value;
            }
        }
        return null;
    }


    public String[] getVariants()
    {
        Set variantSet = new LinkedHashSet();
        for (int i = 0; i < handlers_.length; i++) {
            variantSet.addAll(Arrays.asList(handlers_[i].getVariants()));
        }
        return (String[])variantSet.toArray(new String[0]);
    }


    public void load(String variant, Reader in)
        throws IOException
    {
        if (handlers_.length > 0) {
            handlers_[0].load(variant, in);
        } else {
            in.close();
        }
    }


    public Enumeration propertyNames(String variant)
    {
        return Collections.enumeration(propertyNameSet(variant));
    }


    Set propertyNameSet(String variant)
    {
        Set nameSet = new LinkedHashSet();
        for (int i = 0; i < handlers_.length; i++) {
            for (Enumeration enm = handlers_[i].propertyNames(); enm
                .hasMoreElements();) {
                nameSet.add((String)enm.nextElement());
            }
        }
        return nameSet;
    }


    public void removeProperty(String name, String variant)
    {
        if (handlers_.length > 0) {
            handlers_[0].removeProperty(name, variant);
        }
    }


    public void setProperty(String name, String variant, String value)
    {
        if (handlers_.length > 0) {
            handlers_[0].setProperty(name, variant, value);
        }
    }


    public int size(String variant)
    {
        return propertyNameSet(variant).size();
    }


    public void store(String variant, Writer out)
        throws IOException
    {
        if (handlers_.length > 0) {
            handlers_[0].store(variant, out);
        }
    }


    public void clearProperties()
    {
        clearProperties(VARIANT_DEFAULT);
    }


    public boolean containsPropertyName(String name)
    {
        return containsPropertyName(name, VARIANT_DEFAULT);
    }


    public String getProperty(String name)
    {
        return getProperty(name, VARIANT_DEFAULT);
    }


    public void load(Reader in)
        throws IOException
    {
        load(VARIANT_DEFAULT, in);
    }


    public Enumeration propertyNames()
    {
        return propertyNames(VARIANT_DEFAULT);
    }


    public void removeProperty(String name)
    {
        removeProperty(name, VARIANT_DEFAULT);
    }


    public void setProperty(String name, String value)
    {
        setProperty(name, VARIANT_DEFAULT, value);
    }


    public int size()
    {
        return size(VARIANT_DEFAULT);
    }


    public void store(Writer out)
        throws IOException
    {
        store(VARIANT_DEFAULT, out);
    }
}
