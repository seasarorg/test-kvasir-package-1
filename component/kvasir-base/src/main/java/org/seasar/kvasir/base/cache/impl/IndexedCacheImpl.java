package org.seasar.kvasir.base.cache.impl;

import org.seasar.kvasir.base.cache.Cache;
import org.seasar.kvasir.base.cache.CacheStorage;
import org.seasar.kvasir.base.cache.IndexedCache;
import org.seasar.kvasir.base.cache.IndexedCacheKey;
import org.seasar.kvasir.base.cache.IndexedCacheStorage;


public class IndexedCacheImpl<I, K extends IndexedCacheKey<I>, T> extends
    CacheImpl<K, T>
    implements IndexedCache<I, K, T>
{
    private IndexedCacheStorage<I, K, T> indexedCacheStorage_;


    public IndexedCacheImpl()
    {
        setCacheStorage(new IndexedCacheStorageImpl<I, K, T>());
    }


    @Override
    public Cache<K, T> setCacheStorage(CacheStorage<K, T> cacheStorage)
    {
        super.setCacheStorage(cacheStorage);
        indexedCacheStorage_ = (IndexedCacheStorage<I, K, T>)cacheStorage;
        return this;
    }


    public void clear(I index)
    {
        indexedCacheStorage_.clear(index);
    }
}
