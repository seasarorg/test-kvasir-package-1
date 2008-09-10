package org.seasar.kvasir.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author YOKOTA Takehiko
 */
public class ThrowableUtils
{
    public static String getStackTraceString(Throwable t)
    {
        if (t == null) {
            return null;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }


    public static Throwable getCause(Throwable t)
    {
        if (t == null) {
            return null;
        }

        Throwable cause = t.getCause();
        if (cause == null) {
            try {
                Class clazz = t.getClass();
                Method method = clazz.getMethod("getRootCause", new Class[0]);
                Object obj = method.invoke(t, new Object[0]);
                if (obj instanceof Throwable) {
                    cause = (Throwable)obj;
                }
            } catch (NoSuchMethodException ignore) {
            } catch (IllegalAccessException ignore) {
            } catch (InvocationTargetException ignore) {
            }
        }
        return cause;
    }


    public static Throwable getRootCause(Throwable t)
    {
        if (t == null) {
            return null;
        }

        Throwable cause = getCause(t);
        if (cause == null) {
            return t;
        } else {
            return getRootCause(cause);
        }
    }
}
