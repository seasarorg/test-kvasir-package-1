package org.seasar.kvasir.base.container;

public class TooManyRegistrationRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 1002176392679329874L;


    public TooManyRegistrationRuntimeException()
    {
    }


    public TooManyRegistrationRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public TooManyRegistrationRuntimeException(String message)
    {
        super(message);
    }


    public TooManyRegistrationRuntimeException(Throwable cause)
    {
        super(cause);
    }
}
