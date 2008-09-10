package org.seasar.kvasir.base.log;

/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface KvasirLog
{
    void debug(Object message);


    void debug(Object message, Throwable t);


    void error(Object message);


    void error(Object message, Throwable t);


    void fatal(Object message);


    void fatal(Object message, Throwable t);


    void info(Object message);


    void info(Object message, Throwable t);


    boolean isDebugEnabled();


    boolean isErrorEnabled();


    boolean isFatalEnabled();


    boolean isInfoEnabled();


    boolean isTraceEnabled();


    boolean isWarnEnabled();


    String toString();


    void trace(Object message);


    void trace(Object message, Throwable t);


    void warn(Object message);


    void warn(Object message, Throwable t);
}
