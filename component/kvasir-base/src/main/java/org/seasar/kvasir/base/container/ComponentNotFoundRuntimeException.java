package org.seasar.kvasir.base.container;

/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class ComponentNotFoundRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -1628092363010610826L;


    public ComponentNotFoundRuntimeException()
    {
        super();
    }


    public ComponentNotFoundRuntimeException(String message)
    {
        super(message);
    }


    public ComponentNotFoundRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public ComponentNotFoundRuntimeException(Throwable cause)
    {
        super(cause);
    }
}
