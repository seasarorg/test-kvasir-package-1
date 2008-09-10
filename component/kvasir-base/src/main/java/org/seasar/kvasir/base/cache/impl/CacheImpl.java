package org.seasar.kvasir.base.cache.impl;

import java.util.Iterator;

import org.seasar.kvasir.base.cache.Cache;
import org.seasar.kvasir.base.cache.CacheListener;
import org.seasar.kvasir.base.cache.CacheStorage;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.ObjectProvider;
import org.seasar.kvasir.base.cache.RefreshingStrategy;
import org.seasar.kvasir.base.util.ArrayUtils;


public class CacheImpl<K, T>
    implements Cache<K, T>
{
    private ObjectProvider<K, T> objectProvider_;

    private CacheStorage<K, T> cacheStorage_ = new LRUMapCacheStorage<K, T>();

    private RefreshingStrategy<K, T> refreshingStrategy_ = new PingRefreshingStrategy<K, T>();

    @SuppressWarnings("unchecked")
    private CacheListener<K, T>[] listeners_ = new CacheListener[0];


    public Cache<K, T> setObjectProvider(ObjectProvider<K, T> objectProvider)
    {
        objectProvider_ = objectProvider;
        return this;
    }


    public Cache<K, T> setCacheStorage(CacheStorage<K, T> cacheStorage)
    {
        cacheStorage_ = cacheStorage;
        for (int i = 0; i < listeners_.length; i++) {
            cacheStorage.addListener(listeners_[i]);
        }
        return this;
    }


    public Cache<K, T> setRefreshingStrategy(
        RefreshingStrategy<K, T> refreshingStrategy)
    {
        refreshingStrategy_ = refreshingStrategy;
        return this;
    }


    public CachedEntry<K, T> getEntry(K key)
    {
        return getEntry(key, true);
    }


    public CachedEntry<K, T> getEntry(K key, boolean registerIfNotExists)
    {
        CachedEntry<K, T> entry = cacheStorage_.get(key);
        if (entry == null) {
            if (registerIfNotExists) {
                entry = objectProvider_.get(key);
                cacheStorage_.register(entry);
            }
        } else {
            entry.age();
            if (refreshingStrategy_.shouldRefreshByUse(entry)) {
                CachedEntry<K, T> refreshed = objectProvider_.refresh(entry);
                if (refreshed != entry) {
                    cacheStorage_.register(refreshed);
                    entry = refreshed;
                }
            }
        }
        return entry;
    }


    public T get(K key)
    {
        return getEntry(key).getCached();
    }


    public void ping()
    {
        for (Iterator<CachedEntry<K, T>> itr = cacheStorage_.getEntryIterator(); itr
            .hasNext();) {
            ping(itr.next());
        }
    }


    public void ping(K key)
    {
        CachedEntry<K, T> entry = getEntry(key);
        if (entry != null) {
            ping(entry);
        }
    }


    void ping(CachedEntry<K, T> entry)
    {
        if (refreshingStrategy_.shouldRefreshByPing(entry)) {
            CachedEntry<K, T> newEntry = objectProvider_.refresh(entry);
            if (newEntry != entry) {
                cacheStorage_.register(newEntry);
            }
        }
    }


    public void refresh()
    {
        for (Iterator<CachedEntry<K, T>> itr = cacheStorage_.getEntryIterator(); itr
            .hasNext();) {
            refresh(itr.next());
        }
    }


    public void refresh(K key)
    {
        CachedEntry<K, T> entry = getEntry(key);
        if (entry != null) {
            refresh(entry);
        }
    }


    void refresh(CachedEntry<K, T> entry)
    {
        CachedEntry<K, T> newEntry = objectProvider_.refresh(entry);
        if (newEntry != entry) {
            cacheStorage_.register(newEntry);
        }
    }


    public void remove(K key)
    {
        cacheStorage_.remove(key);
    }


    public void clear()
    {
        cacheStorage_.clear();
    }


    public void register(K key, T object)
    {
        cacheStorage_.register(objectProvider_.newEntry(key, object));
    }


    public void addListener(CacheListener<K, T> listener)
    {
        listeners_ = ArrayUtils.add(listeners_, listener);
        if (cacheStorage_ != null) {
            cacheStorage_.addListener(listener);
        }
    }


    public CacheStorage<K, T> getCacheStorage()
    {
        return cacheStorage_;
    }


    public ObjectProvider<K, T> getObjectProvider()
    {
        return objectProvider_;
    }


    public RefreshingStrategy<K, T> getRefreshingStrategy()
    {
        return refreshingStrategy_;
    }


    public long getTotalSize()
    {
        return cacheStorage_.getTotalSize();
    }


    public long getUsedSize()
    {
        return cacheStorage_.getUsedSize();
    }
}
