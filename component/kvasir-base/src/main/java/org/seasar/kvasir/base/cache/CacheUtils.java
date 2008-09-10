package org.seasar.kvasir.base.cache;

import org.seasar.kvasir.base.cache.impl.SynchronizedCacheImpl;


public class CacheUtils
{
    private CacheUtils()
    {
    }


    public <K, T> Cache<K, T> synchronizedCache(Cache<K, T> cache)
    {
        return new SynchronizedCacheImpl<K, T>(cache);
    }
}
