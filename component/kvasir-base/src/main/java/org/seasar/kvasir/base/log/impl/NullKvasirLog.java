package org.seasar.kvasir.base.log.impl;

import org.seasar.kvasir.base.log.KvasirLog;


/**
 * 何もしないKvasirLogの実装です。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class NullKvasirLog
    implements KvasirLog
{
    public void debug(Object message, Throwable t)
    {
    }


    public void debug(Object message)
    {
    }


    public void error(Object message, Throwable t)
    {
    }


    public void error(Object message)
    {
    }


    public void fatal(Object message, Throwable t)
    {
    }


    public void fatal(Object message)
    {
    }


    public void info(Object message, Throwable t)
    {
    }


    public void info(Object message)
    {
    }


    public boolean isDebugEnabled()
    {
        return false;
    }


    public boolean isErrorEnabled()
    {
        return false;
    }


    public boolean isFatalEnabled()
    {
        return false;
    }


    public boolean isInfoEnabled()
    {
        return false;
    }


    public boolean isTraceEnabled()
    {
        return false;
    }


    public boolean isWarnEnabled()
    {
        return false;
    }


    public void trace(Object message, Throwable t)
    {
    }


    public void trace(Object message)
    {
    }


    public void warn(Object message, Throwable t)
    {
    }


    public void warn(Object message)
    {
    }
}
