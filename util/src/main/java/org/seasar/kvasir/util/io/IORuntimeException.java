package org.seasar.kvasir.util.io;


/**
 * @author YOKOTA Takehiko
 */
public class IORuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 1315919245767957625L;


    public IORuntimeException()
    {
        super();
    }


    /**
     * @param message
     */
    public IORuntimeException(String message)
    {
        super(message);
    }


    /**
     * @param cause
     */
    public IORuntimeException(Throwable cause)
    {
        super(cause);
    }


    /**
     * @param message
     * @param cause
     */
    public IORuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
