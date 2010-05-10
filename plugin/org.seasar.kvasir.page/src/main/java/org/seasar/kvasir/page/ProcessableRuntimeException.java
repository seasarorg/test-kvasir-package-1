package org.seasar.kvasir.page;

public class ProcessableRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -3139587458464841720L;


    public ProcessableRuntimeException(Throwable cause)
    {
        super(cause);
    }


    public ProcessableRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
