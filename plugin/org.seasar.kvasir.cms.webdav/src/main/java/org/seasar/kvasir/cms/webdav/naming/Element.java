package org.seasar.kvasir.cms.webdav.naming;

import java.io.IOException;
import java.io.InputStream;

import javax.naming.NamingException;


public interface Element<T>
{
    String getAttribute();


    boolean canCreate();


    boolean canDelete();


    boolean canModify();


    boolean canMove();


    boolean canRead();


    T getElement();


    boolean exists();


    boolean isCollection();


    boolean isFile();


    int getHeimId();


    String getPath();


    String getName();


    String getParentPath();


    long getContentLength();


    long getCreation();


    long getLastModified();


    InputStream getContent()
        throws IOException;


    void delete()
        throws NamingException;


    void setContent(String encoding, InputStream in)
        throws NamingException;


    public void move(Element<T> destination)
        throws NamingException;


    void create(String encoding, InputStream in)
        throws NamingException;
}
