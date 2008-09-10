package org.seasar.kvasir.base;

/**
 * @author YOKOTA Takehiko
 */
public interface Adaptable
{
    Object getAdapter(Object key);


    <T> T getAdapter(Class<T> key);
}
