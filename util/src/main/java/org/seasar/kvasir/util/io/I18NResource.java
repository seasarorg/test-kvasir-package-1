package org.seasar.kvasir.util.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;


/**
 * @author YOKOTA Takehiko
 */
public interface I18NResource
{
    String VARIANT_DEFAULT = "";


    InputStream getInputStream(String variant)
        throws ResourceNotFoundException;


    InputStream getInputStream(Locale locale)
        throws ResourceNotFoundException;


    boolean exists(String variant);


    boolean exists(Locale locale);


    long getLastModifiedTime(String variant);


    long getLastModifiedTime(Locale locale);


    String[] getVariants();


    OutputStream getOutputStream(String variant)
        throws ResourceNotFoundException;


    Resource getParent();
}
