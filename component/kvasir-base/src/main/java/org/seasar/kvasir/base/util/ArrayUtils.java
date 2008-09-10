package org.seasar.kvasir.base.util;

import java.lang.reflect.Array;


public class ArrayUtils
{
    private ArrayUtils()
    {
    }


    public static <T> void reverse(T[] objs)
    {
        if (objs != null) {
            int count = objs.length / 2;
            for (int l = 0, r = objs.length - 1; l < count; l++, r--) {
                T tmp = objs[l];
                objs[l] = objs[r];
                objs[r] = tmp;
            }
        }
    }


    @SuppressWarnings("unchecked")
    public static <T> T[] add(T[] array, T obj)
    {
        if (array == null) {
            throw new IllegalArgumentException("Array must not be null");
        }
        T[] newArray = (T[])Array.newInstance(array.getClass()
            .getComponentType(), array.length + 1);
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = obj;
        return newArray;
    }


    @SuppressWarnings("unchecked")
    public static <T, S extends T> S[] downcast(T[] array, Class<S> clazz)
    {
        if (array == null) {
            return null;
        }
        S[] newArray = (S[])Array.newInstance(clazz, array.length);
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }
}
