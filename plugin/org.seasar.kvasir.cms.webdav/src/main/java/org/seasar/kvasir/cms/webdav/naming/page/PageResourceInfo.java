package org.seasar.kvasir.cms.webdav.naming.page;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.seasar.kvasir.cms.webdav.naming.ResourceInfo;
import org.seasar.kvasir.page.Page;


public class PageResourceInfo
    implements ResourceInfo
{
    private Page page_;


    public PageResourceInfo(Page page)
    {
        page_ = page;
    }


    public long getSize()
    {
        return 0L;
    }


    public long getLastModifiedTime()
    {
        return page_.getModifyDate().getTime();
    }


    public InputStream getInputStream()
        throws IOException
    {
        return new ByteArrayInputStream(new byte[0]);
    }


    public long getCreationTime()
    {
        return page_.getCreateDate().getTime();
    }
}
