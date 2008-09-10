package org.seasar.kvasir.util.io;

import java.io.IOException;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class ResourceNotFoundException extends IOException
{
    private static final long serialVersionUID = -1142977234128207378L;


    public ResourceNotFoundException()
    {
        super();
    }


    public ResourceNotFoundException(String s)
    {
        super(s);
    }


    public ResourceNotFoundException(Throwable t)
    {
        super();
        initCause(t);
    }


    public ResourceNotFoundException(String s, Throwable t)
    {
        super(s);
        initCause(t);
    }
}
