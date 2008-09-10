package org.seasar.kvasir.base.cache.impl;

import org.seasar.kvasir.base.cache.CacheListener;
import org.seasar.kvasir.base.cache.CacheStorage;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.util.ArrayUtils;


abstract public class AbstractCacheStorage<K, T>
    implements CacheStorage<K, T>
{
    @SuppressWarnings("unchecked")
    protected CacheListener<K, T>[] listeners_ = new CacheListener[0];


    public void addListener(CacheListener<K, T> listener)
    {
        listeners_ = ArrayUtils.add(listeners_, listener);
    }


    protected final void notifyRegistered(CachedEntry<K, T> entry)
    {
        for (int i = 0; i < listeners_.length; i++) {
            listeners_[i].notifyRegistered(entry);
        }
    }
}
