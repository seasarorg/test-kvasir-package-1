package org.seasar.kvasir.util.collection;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Locale;


/**
 * @author YOKOTA Takehiko
 */
public interface I18NPropertyHandler
    extends PropertyHandler, I18NPropertyReader
{
    String VARIANT_DEFAULT = "";


    String getProperty(String name, String variant);


    String getProperty(String name, Locale locale);


    void setProperty(String name, String variant, String value);


    void removeProperty(String name, String variant);


    void clearProperties(String variant);


    int size(String variant);


    boolean containsPropertyName(String name, String variant);


    boolean containsPropertyName(String name, Locale Locale);


    Enumeration propertyNames(String variant);


    String[] getVariants();


    void load(String variant, Reader in)
        throws IOException;


    void store(String variant, Writer out)
        throws IOException;
}
