package org.seasar.kvasir.page;



/**
 * @author YOKOTA Takehiko
 */
public class PageNotFoundRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -3174074258015562309L;

    private String pathname_;

    private int[] ids_ = new int[0];


    public PageNotFoundRuntimeException()
    {
        super();
    }


    /**
     * @param message
     */
    public PageNotFoundRuntimeException(String message)
    {
        super(message);
    }


    /**
     * @param cause
     */
    public PageNotFoundRuntimeException(Throwable cause)
    {
        super(cause);
    }


    /**
     * @param message
     * @param cause
     */
    public PageNotFoundRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }


    /*
     * public scope methods
     */

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (getMessage() != null) {
            sb.append(getMessage());
        } else {
            sb.append("Some pages are not found");
        }
        if (ids_.length > 0) {
            sb.append(": id={");
            String delim = "";
            for (int i = 0; i < ids_.length; i++) {
                sb.append(delim).append(ids_[i]);
                delim = ", ";
            }
        } else if (pathname_ != null) {
            sb.append(": pathname=").append(pathname_);
        }
        return sb.toString();
    }


    public int[] getIds()
    {
        return ids_;
    }


    public PageNotFoundRuntimeException setId(int id)
    {
        ids_ = new int[] { id };
        return this;
    }


    public String getPathname()
    {
        return pathname_;
    }


    public PageNotFoundRuntimeException setPathname(String pathname)
    {
        pathname_ = pathname;
        return this;
    }


    public PageNotFoundRuntimeException addId(int id)
    {
        int[] newIds = new int[ids_.length + 1];
        System.arraycopy(ids_, 0, newIds, 0, ids_.length);
        newIds[ids_.length] = id;
        ids_ = newIds;
        return this;
    }
}
