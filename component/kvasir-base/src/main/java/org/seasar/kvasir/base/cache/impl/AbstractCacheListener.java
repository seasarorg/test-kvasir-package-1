package org.seasar.kvasir.base.cache.impl;

import org.seasar.kvasir.base.cache.CacheListener;
import org.seasar.kvasir.base.cache.CachedEntry;


abstract public class AbstractCacheListener<K, T>
    implements CacheListener<K, T>
{
    public void notifyRegistered(CachedEntry<K, T> entry)
    {
    }
}
