package org.seasar.kvasir.util.el;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class EvaluationException extends Exception
{
    private static final long serialVersionUID = 7582297841325387298L;


    public EvaluationException()
    {
        super();
    }


    public EvaluationException(String message)
    {
        super(message);
    }


    public EvaluationException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public EvaluationException(Throwable cause)
    {
        super(cause);
    }
}
