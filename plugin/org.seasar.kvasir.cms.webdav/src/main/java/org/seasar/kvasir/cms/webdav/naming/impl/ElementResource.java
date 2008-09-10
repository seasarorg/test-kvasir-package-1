package org.seasar.kvasir.cms.webdav.naming.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.naming.resources.Resource;
import org.seasar.kvasir.cms.webdav.naming.Element;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;


public class ElementResource<T> extends Resource
{
    private Element<T> element_;


    public ElementResource(Element<T> element)
    {
        element_ = element;
    }


    @Override
    public InputStream streamContent()
        throws IOException
    {
        return element_.getContent();
    }


    @Override
    public byte[] getContent()
    {
        try {
            return IOUtils.readBytes(streamContent());
        } catch (IOException ex) {
            throw new IORuntimeException("Can't get content: " + element_, ex);
        }
    }
}
