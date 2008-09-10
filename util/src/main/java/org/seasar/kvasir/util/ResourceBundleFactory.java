package org.seasar.kvasir.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.seasar.kvasir.util.io.Resource;


/**
 * @author YOKOTA Takehiko
 */
public class ResourceBundleFactory
{
    private ResourceBundleFactory()
    {
    }


    public static ResourceBundle getBundle(Resource dir, String baseName,
        Locale locale)
    {
        Resource [] resources = LocaleUtils.findResources(
            dir, baseName, ".properties", locale);
        ResourceBundle parent = null;
        ResourceBundle bundle = null;
        for (int i = resources.length - 1; i >= 0; i--) {
            try {
                bundle = new PropertyResourceBundleInner(
                    resources[i].getInputStream(), parent);
            } catch (IOException ex) {
                throw new MissingResourceException(
                    ex.getMessage(), baseName, "");
            }
            parent = bundle;
        }
        if (bundle == null) {
            throw new MissingResourceException(
                "Can't find resource: " + dir + "/" + baseName,
                baseName, "");
        }
        return bundle;
    }


    /*
     * inner classes
     */

    public static class PropertyResourceBundleInner
        extends PropertyResourceBundle
    {
        public PropertyResourceBundleInner(InputStream in,
            ResourceBundle parent)
                throws IOException
        {
            super(in);
            if (parent != null) {
                setParent(parent);
            }
        }
    }
}
