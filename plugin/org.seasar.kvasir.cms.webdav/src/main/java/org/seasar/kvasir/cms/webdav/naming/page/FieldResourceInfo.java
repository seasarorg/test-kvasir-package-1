package org.seasar.kvasir.cms.webdav.naming.page;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.TreeMap;

import org.seasar.kvasir.cms.webdav.naming.ResourceInfo;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.io.IORuntimeException;


public class FieldResourceInfo
    implements ResourceInfo
{
    private Page page_;

    private byte[] body_;


    public FieldResourceInfo(Page page)
    {
        page_ = page;

        PropertyHandler handler = new MapProperties(
            new TreeMap<String, String>());
        handler
            .setProperty(Page.FIELD_ASFILE, String.valueOf(page_.isAsFile()));
        handler.setProperty(Page.FIELD_CONCEALDATE, page_
            .getConcealDateString());
        handler.setProperty(Page.FIELD_CREATEDATE, page_.getCreateDateString());
        handler.setProperty(Page.FIELD_LISTING, String.valueOf(page_
            .isListing()));
        handler.setProperty(Page.FIELD_LORD, String.valueOf(page_.isLord()));
        handler.setProperty(Page.FIELD_MODIFYDATE, page_.getModifyDateString());
        handler.setProperty(Page.FIELD_NODE, String.valueOf(page_.isNode()));
        handler.setProperty(Page.FIELD_ORDERNUMBER, String.valueOf(page_
            .getOrderNumber()));
        handler.setProperty(Page.FIELD_REVEALDATE, page_.getRevealDateString());

        StringWriter sw = new StringWriter();
        try {
            handler.store(sw);
        } catch (IOException ex) {
            throw new IORuntimeException("Can't happen!", ex);
        }
        try {
            body_ = sw.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new IORuntimeException("Can't happen!", ex);
        }
    }


    public long getSize()
    {
        return body_.length;
    }


    public long getLastModifiedTime()
    {
        return page_.getModifyDate().getTime();
    }


    public InputStream getInputStream()
        throws IOException
    {
        return new ByteArrayInputStream(body_);
    }


    public long getCreationTime()
    {
        return page_.getCreateDate().getTime();
    }
}
