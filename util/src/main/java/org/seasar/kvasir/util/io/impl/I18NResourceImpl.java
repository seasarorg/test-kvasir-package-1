package org.seasar.kvasir.util.io.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.seasar.kvasir.util.LocaleUtils;
import org.seasar.kvasir.util.io.I18NResource;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class I18NResourceImpl
    implements I18NResource
{
    private Resource baseResource_;

    private Resource parent_;

    private String baseName_;

    private String suffix_;


    /*
     * constructors
     */

    public I18NResourceImpl(Resource baseResource)
    {
        baseResource_ = baseResource;
        parent_ = baseResource.getParentResource();
        String name = baseResource.getName();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            baseName_ = name.substring(0, dot);
            suffix_ = name.substring(dot);
        } else {
            baseName_ = name;
            suffix_ = "";
        }
    }


    public String toString()
    {
        return baseResource_.toString();
    }


    /*
     * I18NResource
     */

    public InputStream getInputStream(String variant)
        throws ResourceNotFoundException
    {
        return getResource(variant).getInputStream();
    }


    public InputStream getInputStream(Locale locale)
        throws ResourceNotFoundException
    {
        return getResource(locale).getInputStream();
    }


    public boolean exists(String variant)
    {
        return getResource(variant).exists();
    }


    public boolean exists(Locale locale)
    {
        return getResource(locale).exists();
    }


    public long getLastModifiedTime(String variant)
    {
        return getResource(variant).getLastModifiedTime();
    }


    public long getLastModifiedTime(Locale locale)
    {
        return getResource(locale).getLastModifiedTime();
    }


    public String[] getVariants()
    {
        String baseResourceName = baseResource_.getName();
        String bn = baseName_ + "_";
        Set set = new LinkedHashSet();
        Resource[] resources = parent_.listResources();
        for (int i = 0; i < resources.length; i++) {
            String name = resources[i].getName();
            if (name.equals(baseResourceName)) {
                set.add(VARIANT_DEFAULT);
            } else if (name.length() > baseResourceName.length()
                && name.startsWith(bn) && name.endsWith(suffix_)) {
                set.add(name.substring(bn.length(), name.length()
                    - suffix_.length()));
            }
        }
        return (String[])set.toArray(new String[0]);
    }


    public OutputStream getOutputStream(String variant)
        throws ResourceNotFoundException
    {
        return getResource(variant).getOutputStream();
    }


    public Resource getParent()
    {
        return parent_;
    }


    /*
     * package scope methods
     */

    Resource getResource(String variant)
    {
        StringBuffer sb = new StringBuffer().append(baseName_);
        if (variant.length() > 0) {
            sb.append("_").append(variant);
        }
        sb.append(suffix_);
        return parent_.getChildResource(sb.toString());
    }


    Resource getResource(Locale locale)
    {
        Resource resource = LocaleUtils.findResource(parent_, baseName_,
            suffix_, locale);
        if (resource == null) {
            resource = baseResource_;
        }
        return resource;
    }
}
