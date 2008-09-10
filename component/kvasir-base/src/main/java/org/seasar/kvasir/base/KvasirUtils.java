package org.seasar.kvasir.base;

import org.seasar.kvasir.util.el.EvaluationException;
import org.seasar.kvasir.util.el.TextTemplateEvaluator;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceUtils;


/**
 * @author YOKOTA Takehiko
 */
public class KvasirUtils
{
    private KvasirUtils()
    {
    }


    public static void filterResource(Resource source, Resource destination,
        String encoding, TextTemplateEvaluator evaluator,
        VariableResolver resolver)
    {
        String text = ResourceUtils.readString(source, encoding, false, null);
        if (text == null) {
            return;
        }
        try {
            text = evaluator.evaluateAsString(text, resolver);
        } catch (EvaluationException ex) {
            throw new RuntimeException("Can't filter file: " + source, ex);
        }
        ResourceUtils.writeString(destination, text, encoding, false);
    }


    public static boolean start(Object obj)
    {
        if (obj instanceof Lifecycle) {
            return ((Lifecycle)obj).start();
        } else {
            return true;
        }
    }


    public static boolean start(Object[] objs)
    {
        return start(objs, false);
    }


    public static boolean start(Object[] objs, boolean force)
    {
        if (objs == null) {
            return true;
        }
        boolean succeed = true;
        for (int i = 0; i < objs.length; i++) {
            if (!start(objs[i])) {
                if (!force) {
                    for (int j = 0; j < i; j++) {
                        try {
                            stop(objs[j]);
                        } catch (Throwable t) {
                            ;
                        }
                    }
                    return false;
                } else {
                    succeed = false;
                }
            }
        }
        return succeed;
    }


    public static void stop(Object obj)
    {
        if (obj instanceof Lifecycle) {
            ((Lifecycle)obj).stop();
        }
    }


    public static void stop(Object[] objs)
    {
        if (objs == null) {
            return;
        }
        RuntimeException ex = null;
        for (int i = 0; i < objs.length; i++) {
            try {
                stop(objs[i]);
            } catch (Throwable t) {
                if (ex == null) {
                    if (t instanceof RuntimeException) {
                        ex = (RuntimeException)t;
                    } else {
                        ex = new RuntimeException(t);
                    }
                }
            }
        }
        if (ex != null) {
            throw ex;
        }
    }
}
