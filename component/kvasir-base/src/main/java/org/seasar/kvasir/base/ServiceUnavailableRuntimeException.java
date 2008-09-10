package org.seasar.kvasir.base;

/**
 * @author YOKOTA Takehiko
 */
public class ServiceUnavailableRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -6970755320425137792L;


    public ServiceUnavailableRuntimeException()
    {
        super();
    }


    /**
     * @param message
     */
    public ServiceUnavailableRuntimeException(String message)
    {
        super(message);
    }


    /**
     * @param cause
     */
    public ServiceUnavailableRuntimeException(Throwable cause)
    {
        super(cause);
    }


    /**
     * @param message
     * @param cause
     */
    public ServiceUnavailableRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
