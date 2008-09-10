package org.seasar.kvasir.cms.webdav.naming;

import javax.naming.NamingException;


public class InvalidPathException extends NamingException
{
    private static final long serialVersionUID = -7407038204520713239L;


    public InvalidPathException()
    {
        super();
    }


    public InvalidPathException(String msg)
    {
        super(msg);
    }


    public InvalidPathException(Throwable t)
    {
        super();
        initCause(t);
    }


    public InvalidPathException(String message, Throwable t)
    {
        super(message);
        initCause(t);
    }
}
