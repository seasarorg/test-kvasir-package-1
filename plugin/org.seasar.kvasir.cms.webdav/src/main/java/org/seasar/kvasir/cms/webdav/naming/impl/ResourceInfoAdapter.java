package org.seasar.kvasir.cms.webdav.naming.impl;

import java.io.IOException;
import java.io.InputStream;

import org.seasar.kvasir.cms.webdav.naming.ResourceInfo;
import org.seasar.kvasir.util.io.Resource;


public class ResourceInfoAdapter
    implements ResourceInfo
{
    private Resource resource_;


    public ResourceInfoAdapter(Resource resource)
    {
        resource_ = resource;
    }


    public long getSize()
    {
        return resource_.getSize();
    }


    public InputStream getInputStream()
        throws IOException
    {
        return resource_.getInputStream();
    }


    public long getLastModifiedTime()
    {
        return resource_.getLastModifiedTime();
    }


    public long getCreationTime()
    {
        return 0L;
    }
}
