package org.seasar.kvasir.cms.webdav.naming.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.seasar.kvasir.cms.webdav.naming.ResourceInfo;


public class NullResourceInfo
    implements ResourceInfo
{
    public static final NullResourceInfo INSTANCE = new NullResourceInfo();


    public InputStream getInputStream()
        throws IOException
    {
        return new ByteArrayInputStream(new byte[0]);
    }


    public long getLastModifiedTime()
    {
        return 0L;
    }


    public long getSize()
    {
        return 0L;
    }


    public long getCreationTime()
    {
        return 0L;
    }
}
