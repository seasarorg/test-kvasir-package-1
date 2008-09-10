package org.seasar.kvasir.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;


public class NormalizedRuntimeExceptionTest extends TestCase
{
    public void testSpike()
        throws Exception
    {
        Method method = getClass().getMethod("hoe", new Class[0]);
        try {
            method.invoke(this, new Object[0]);
        } catch (InvocationTargetException ex) {
//            new NormalizedRuntimeException(new RuntimeException(ex))
//                .printStackTrace();
        }
    }


    public void hoe()
    {
        throw new IllegalArgumentException("hoe");
    }
}
