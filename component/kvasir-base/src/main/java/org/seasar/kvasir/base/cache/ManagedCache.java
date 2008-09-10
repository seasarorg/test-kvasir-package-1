package org.seasar.kvasir.base.cache;

public interface ManagedCache
{
    void refresh();


    void ping();


    long getTotalSize();


    long getUsedSize();
}
