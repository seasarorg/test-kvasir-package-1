package org.seasar.kvasir.page;

public class CollisionDetectedRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    private int id_;


    public CollisionDetectedRuntimeException()
    {
    }


    public CollisionDetectedRuntimeException(int id)
    {
        id_ = id;
    }


    public CollisionDetectedRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public CollisionDetectedRuntimeException(String message)
    {
        super(message);
    }


    public CollisionDetectedRuntimeException(Throwable cause)
    {
        super(cause);
    }
}
