package org.seasar.kvasir.base;

public interface Enabler<T>
{
    boolean isEnabled(T target);
}
