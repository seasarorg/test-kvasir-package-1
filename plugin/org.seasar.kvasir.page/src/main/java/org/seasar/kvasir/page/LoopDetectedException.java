package org.seasar.kvasir.page;


/**
 * @author YOKOTA Takehiko
 */
public class LoopDetectedException extends Exception
{
    private static final long serialVersionUID = -7599101635235263400L;


    public LoopDetectedException()
    {
        super();
    }


    /**
     * @param message
     */
    public LoopDetectedException(String message)
    {
        super(message);
    }


    /**
     * @param cause
     */
    public LoopDetectedException(Throwable cause)
    {
        super(cause);
    }


    /**
     * @param message
     * @param cause
     */
    public LoopDetectedException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
