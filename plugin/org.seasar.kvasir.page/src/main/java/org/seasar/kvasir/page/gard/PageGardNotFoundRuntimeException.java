package org.seasar.kvasir.page.gard;

import org.seasar.kvasir.base.Version;


/**
 * @author YOKOTA Takehiko
 */
public class PageGardNotFoundRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 4572372160573438614L;


    private String      id_;
    private Version     version_;


    public PageGardNotFoundRuntimeException()
    {
        super();
    }


    /**
     * @param message
     */
    public PageGardNotFoundRuntimeException(String message)
    {
        super(message);
    }


    /**
     * @param cause
     */
    public PageGardNotFoundRuntimeException(Throwable cause)
    {
        super(cause);
    }


    /**
     * @param message
     * @param cause
     */
    public PageGardNotFoundRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }


    /*
     * public scope methods
     */

    @Override
    public String toString()
    {
        return getMessage() + " [id=" + id_ + ", version=" + version_ + "]";
    }


    public String getId()
    {
        return id_;
    }


    public PageGardNotFoundRuntimeException setId(String id)
    {
        id_ = id;
        return this;
    }


    public Version getVersion()
    {
        return version_;
    }


    public PageGardNotFoundRuntimeException setVersion(Version version)
    {
        version_ = version;
        return this;
    }
}
