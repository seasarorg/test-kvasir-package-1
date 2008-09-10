package org.seasar.kvasir.page.search;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class ParseException extends Exception
{
    private static final long serialVersionUID = -2904274942389910881L;


    public ParseException()
    {
        super();
    }


    public ParseException(String message)
    {
        super(message);
    }


    public ParseException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public ParseException(Throwable cause)
    {
        super(cause);
    }
}
