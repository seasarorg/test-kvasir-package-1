package org.seasar.kvasir.page;

import org.seasar.kvasir.page.ability.Privilege;


/**
 * @author YOKOTA Takehiko
 */
public class SecurityRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 772954334255122248L;


    private String      pathname_;
    private Page        page_;
    private Page        user_;
    private Privilege   priv_;


    public SecurityRuntimeException()
    {
        super();
    }


    /**
     * @param message
     */
    public SecurityRuntimeException(String message)
    {
        super(message);
    }


    /**
     * @param cause
     */
    public SecurityRuntimeException(Throwable cause)
    {
        super(cause);
    }


    /**
     * @param message
     * @param cause
     */
    public SecurityRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }


    /*
     * public scope methods
     */

    public Page getPage()
    {
        return page_;
    }


    public SecurityRuntimeException setPage(Page page)
    {
        page_ = page;
        return this;
    }


    public String getPathname()
    {
        return pathname_;
    }


    public SecurityRuntimeException setPathname(String pathname)
    {
        pathname_ = pathname;
        return this;
    }


    public Privilege getPrivilege()
    {
        return priv_;
    }


    public SecurityRuntimeException setPrivilege(Privilege priv)
    {
        priv_ = priv;
        return this;
    }


    public Page getUser()
    {
        return user_;
    }


    public SecurityRuntimeException setUser(Page user)
    {
        user_ = user;
        return this;
    }
}
