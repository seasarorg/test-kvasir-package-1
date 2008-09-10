package org.seasar.kvasir.cms.webdav.naming.impl;

import javax.naming.NamingException;

import org.seasar.kvasir.cms.webdav.naming.Element;
import org.seasar.kvasir.cms.webdav.naming.ElementFactory;


abstract public class AbstractElementFactory<T>
    implements ElementFactory<T>
{
    protected ElementDirContext<T> dirContext_;


    public void setDirContext(ElementDirContext<T> dirContext)
    {
        dirContext_ = dirContext;
    }


    protected Element<T> getElement(String path)
        throws NamingException
    {
        return dirContext_.newElement(path);
    }


    protected int getHeimId()
    {
        return dirContext_.getHeimId();
    }


    protected String getParentPath(String path)
    {
        return dirContext_.getParentPath(path);
    }


    protected String getName(String path)
    {
        return dirContext_.getName(path);
    }
}
