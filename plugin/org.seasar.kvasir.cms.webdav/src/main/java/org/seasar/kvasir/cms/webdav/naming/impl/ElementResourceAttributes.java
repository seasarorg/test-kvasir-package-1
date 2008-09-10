package org.seasar.kvasir.cms.webdav.naming.impl;

import java.util.Date;

import org.apache.naming.resources.ResourceAttributes;
import org.seasar.kvasir.cms.webdav.naming.Element;


public class ElementResourceAttributes<T> extends ResourceAttributes
{
    private static final long serialVersionUID = -5010249518182478159L;

    private Element<T> element_;


    public ElementResourceAttributes(Element<T> element)
    {
        element_ = element;
    }


    public boolean isCollection()
    {
        return element_.isCollection();
    }


    public long getContentLength()
    {
        return element_.getContentLength();
    }


    public long getCreation()
    {
        return element_.getCreation();
    }


    public Date getCreationDate()
    {
        return new Date(getCreation());
    }


    public long getLastModified()
    {
        return element_.getLastModified();
    }


    public Date getLastModifiedDate()
    {
        return new Date(getLastModified());
    }


    public String getName()
    {
        return element_.getName();
    }


    public String getResourceType()
    {
        if (isCollection()) {
            return COLLECTION_TYPE;
        } else {
            return "";
        }
    }
}
