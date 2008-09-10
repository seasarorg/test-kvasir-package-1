package org.seasar.kvasir.cms.webdav.naming.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.naming.NamingException;

import org.seasar.kvasir.cms.webdav.naming.Element;
import org.seasar.kvasir.cms.webdav.naming.ElementFactory;
import org.seasar.kvasir.cms.webdav.naming.ResourceInfo;


public class ElementImpl<T>
    implements Element<T>
{
    private ElementFactory<T> elementFactory_;

    private int heimId_;

    private String path_;

    private T element_;

    private String attribute_;

    private boolean isFile_;

    private boolean isCollection_;

    private boolean exists_;

    private boolean canRead_;

    private boolean canCreate_;

    private boolean canModify_;

    private boolean canMove_;

    private boolean canDelete_;

    private ResourceInfo resourceInfo_ = NullResourceInfo.INSTANCE;


    public ElementImpl(ElementFactory<T> elementFactory, int heimId, String path)
    {
        elementFactory_ = elementFactory;
        heimId_ = heimId;
        path_ = path;
    }


    public ElementFactory<T> getElementFactory()
    {
        return elementFactory_;
    }


    public String getAttribute()
    {
        return attribute_;
    }


    public ElementImpl<T> setAttribute(String attribute)
    {
        attribute_ = attribute;
        return this;
    }


    public T getElement()
    {
        return element_;
    }


    public ElementImpl<T> setElement(T element)
    {
        element_ = element;
        return this;
    }


    public boolean isCollection()
    {
        return isCollection_;
    }


    public ElementImpl<T> setCollection()
    {
        isCollection_ = true;
        return this;
    }


    public boolean isFile()
    {
        return isFile_;
    }


    public ElementImpl<T> setFile()
    {
        isFile_ = true;
        return this;
    }


    public boolean exists()
    {
        return exists_;
    }


    public ElementImpl<T> setExists()
    {
        exists_ = true;
        return this;
    }


    public boolean canCreate()
    {
        return canCreate_;
    }


    public ElementImpl<T> setCanCreate()
    {
        canCreate_ = true;
        return this;
    }


    public boolean canDelete()
    {
        return canDelete_;
    }


    public ElementImpl<T> setCanDelete()
    {
        canDelete_ = true;
        return this;
    }


    public boolean canModify()
    {
        return canModify_;
    }


    public ElementImpl<T> setCanModify()
    {
        canModify_ = true;
        return this;
    }


    public boolean canMove()
    {
        return canMove_;
    }


    public ElementImpl<T> setCanMove()
    {
        canMove_ = true;
        return this;
    }


    public boolean canRead()
    {
        return canRead_;
    }


    public ElementImpl<T> setCanRead()
    {
        canRead_ = true;
        return this;
    }


    public int getHeimId()
    {
        return heimId_;
    }


    public String getPath()
    {
        return path_;
    }


    public String getName()
    {
        return getName(path_);
    }


    protected String getName(String path)
    {
        int slash = path.lastIndexOf("/");
        if (slash >= 0) {
            return path.substring(slash + 1);
        } else {
            return "";
        }
    }


    public String getParentPath()
    {
        return getParentPath(path_);
    }


    protected String getParentPath(String path)
    {
        int slash = path.lastIndexOf("/");
        if (slash >= 0) {
            return path.substring(0, slash);
        } else {
            return null;
        }
    }


    public ResourceInfo getResourceInfo()
    {
        return resourceInfo_;
    }


    public void setResourceInfo(ResourceInfo resourceInfo)
    {
        resourceInfo_ = resourceInfo;
    }


    public long getContentLength()
    {
        return resourceInfo_.getSize();
    }


    public long getCreation()
    {
        return resourceInfo_.getCreationTime();
    }


    public long getLastModified()
    {
        return resourceInfo_.getLastModifiedTime();
    }


    public InputStream getContent()
        throws IOException
    {
        return resourceInfo_.getInputStream();
    }


    public void create(String encoding, InputStream in)
        throws NamingException
    {
        elementFactory_.create(this, encoding, in);
    }


    public void delete()
        throws NamingException
    {
        elementFactory_.delete(this);
    }


    public void move(Element<T> destination)
        throws NamingException
    {
        elementFactory_.move(this, destination);
    }


    public void setContent(String encoding, InputStream in)
        throws NamingException
    {
        elementFactory_.setContent(this, encoding, in);
    }
}
