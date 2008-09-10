package org.seasar.kvasir.page;


/**
 * @author YOKOTA Takehiko
 */
public class DuplicatePageException extends Exception
{
    private static final long serialVersionUID = 8248370156278305560L;


    public DuplicatePageException()
    {
        super();
    }


    /**
     * @param message
     */
    public DuplicatePageException(String message)
    {
        super(message);
    }


    /**
     * @param cause
     */
    public DuplicatePageException(Throwable cause)
    {
        super(cause);
    }


    /**
     * @param message
     * @param cause
     */
    public DuplicatePageException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
