package org.seasar.kvasir.base.webapp.impl;

import java.io.IOException;
import java.io.InputStream;

import org.seasar.kvasir.base.webapp.Content;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class ResourceContent implements Content
{
    private Resource resource_;
    private String contentType_;


    /*
     * constructors
     */

    public ResourceContent(Resource resource)
    {
        this(resource, null);
    }


    public ResourceContent(Resource resource, String contentType)
    {
        resource_ = resource;
        contentType_ = contentType;
    }


    /*
     * Content
     */

    public String getContentType()
    {
        return contentType_;
    }


    public long getLastModifiedTime()
    {
        return resource_.getLastModifiedTime();
    }


    public InputStream getInputStream()
        throws IOException
    {
        return resource_.getInputStream();
    }
}