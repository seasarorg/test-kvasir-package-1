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
public class ConsoleKvasirLog
    implements KvasirLog
{
    public void debug(Object message, Throwable t)
    {
        System.out.println("[DEBUG] " + message);
        if (t != null) {
            t.printStackTrace();
        }
    }


    public void debug(Object message)
    {
        System.out.println("[DEBUG] " + message);
    }


    public void error(Object message, Throwable t)
    {
        System.out.println("[ERROR] " + message);
        if (t != null) {
            t.printStackTrace();
        }
    }


    public void error(Object message)
    {
        System.out.println("[ERROR] " + message);
    }


    public void fatal(Object message, Throwable t)
    {
        System.out.println("[FATAL] " + message);
        if (t != null) {
            t.printStackTrace();
        }
    }


    public void fatal(Object message)
    {
        System.out.println("[FATAL] " + message);
    }


    public void info(Object message, Throwable t)
    {
        System.out.println("[INFO] " + message);
        if (t != null) {
            t.printStackTrace();
        }
    }


    public void info(Object message)
    {
        System.out.println("[INFO] " + message);
    }


    public boolean isDebugEnabled()
    {
        return true;
    }


    public boolean isErrorEnabled()
    {
        return true;
    }


    public boolean isFatalEnabled()
    {
        return true;
    }


    public boolean isInfoEnabled()
    {
        return true;
    }


    public boolean isTraceEnabled()
    {
        return true;
    }


    public boolean isWarnEnabled()
    {
        return true;
    }


    public void trace(Object message, Throwable t)
    {
        System.out.println("[TRACE] " + message);
        if (t != null) {
            t.printStackTrace();
        }
    }


    public void trace(Object message)
    {
        System.out.println("[TRACE] " + message);
    }


    public void warn(Object message, Throwable t)
    {
        System.out.println("[WARN] " + message);
        if (t != null) {
            t.printStackTrace();
        }
    }


    public void warn(Object message)
    {
        System.out.println("[WARN] " + message);
    }
}
