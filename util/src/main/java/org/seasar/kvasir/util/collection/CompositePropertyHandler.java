package org.seasar.kvasir.util.collection;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;


public class CompositePropertyHandler
    implements PropertyHandler
{
    private PropertyHandler[] handlers_;


    public CompositePropertyHandler(PropertyHandler[] handlers)
    {
        handlers_ = handlers;
    }


    public void clearProperties()
    {
        throw new UnsupportedOperationException();
    }


    public boolean containsPropertyName(String name)
    {
        for (int i = 0; i < handlers_.length; i++) {
            if (handlers_[i].containsPropertyName(name)) {
                return true;
            }
        }
        return false;
    }


    public String getProperty(String name)
    {
        for (int i = 0; i < handlers_.length; i++) {
            String value = handlers_[i].getProperty(name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }


    public void load(Reader in)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }


    public Enumeration propertyNames()
    {
        return Collections.enumeration(getNameSet());
    }


    Set getNameSet()
    {
        Set nameSet = new LinkedHashSet();
        for (int i = 0; i < handlers_.length; i++) {
            for (Enumeration enm = handlers_[i].propertyNames(); enm
                .hasMoreElements();) {
                nameSet.add(enm.nextElement());
            }
        }
        return nameSet;
    }


    public void removeProperty(String name)
    {
        throw new UnsupportedOperationException();
    }


    public void setProperty(String name, String value)
    {
        throw new UnsupportedOperationException();
    }


    public int size()
    {
        return getNameSet().size();
    }


    public void store(Writer out)
        throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
