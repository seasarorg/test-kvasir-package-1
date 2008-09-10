package org.seasar.kvasir.base.cache;

public interface CacheListener<K, T>
{
    void notifyRegistered(CachedEntry<K, T> entry);
}
