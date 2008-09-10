package org.seasar.kvasir.system.log;

import org.apache.commons.logging.Log;
import org.seasar.kvasir.base.log.KvasirLog;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class KvasirLogImpl
    implements KvasirLog
{
    private KvasirLogFactoryImpl factory_;

    private Log log_;


    /*
     * constructors
     */

    public KvasirLogImpl(KvasirLogFactoryImpl factory, Log log)
    {
        factory_ = factory;
        log_ = log;
    }


    /*
     * public scope methods
     */

    public void debug(Object message)
    {
        log_.debug(message);
    }


    public void debug(Object message, Throwable t)
    {
        log_.debug(message, t);
    }


    @Override
    public boolean equals(Object obj)
    {
        return log_.equals(obj);
    }


    public void error(Object message)
    {
        log_.error(message);
        factory_.incrementErrorCount();
    }


    public void error(Object message, Throwable t)
    {
        log_.error(message, t);
        factory_.incrementErrorCount();
    }


    public void fatal(Object message)
    {
        log_.fatal(message);
        factory_.incrementFatalCount();
    }


    public void fatal(Object message, Throwable t)
    {
        log_.fatal(message, t);
        factory_.incrementFatalCount();
    }


    @Override
    public int hashCode()
    {
        return log_.hashCode();
    }


    public void info(Object message)
    {
        log_.info(message);
    }


    public void info(Object message, Throwable t)
    {
        log_.info(message, t);
    }


    public boolean isDebugEnabled()
    {
        return log_.isDebugEnabled();
    }


    public boolean isErrorEnabled()
    {
        return log_.isErrorEnabled();
    }


    public boolean isFatalEnabled()
    {
        return log_.isFatalEnabled();
    }


    public boolean isInfoEnabled()
    {
        return log_.isInfoEnabled();
    }


    public boolean isTraceEnabled()
    {
        return log_.isTraceEnabled();
    }


    public boolean isWarnEnabled()
    {
        return log_.isWarnEnabled();
    }


    @Override
    public String toString()
    {
        return log_.toString();
    }


    public void trace(Object message)
    {
        log_.trace(message);
    }


    public void trace(Object message, Throwable t)
    {
        log_.trace(message, t);
    }


    public void warn(Object message)
    {
        log_.warn(message);
        factory_.incrementWarnCount();
    }


    public void warn(Object message, Throwable t)
    {
        log_.warn(message, t);
        factory_.incrementWarnCount();
    }
}
