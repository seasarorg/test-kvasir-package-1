package org.seasar.kvasir.base.cache.impl;

import org.seasar.kvasir.base.cache.Cache;
import org.seasar.kvasir.base.cache.CacheListener;
import org.seasar.kvasir.base.cache.CacheStorage;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.ObjectProvider;
import org.seasar.kvasir.base.cache.RefreshingStrategy;


public class SynchronizedCacheImpl<K, T>
    implements Cache<K, T>
{
    private Cache<K, T> delegated_;


    public SynchronizedCacheImpl(Cache<K, T> delegated)
    {
        delegated_ = delegated;
    }


    public synchronized T get(K key)
    {
        return delegated_.get(key);
    }


    public synchronized CachedEntry<K, T> getEntry(K key)
    {
        return delegated_.getEntry(key);
    }


    public synchronized CachedEntry<K, T> getEntry(K key,
        boolean registerIfNotExists)
    {
        return delegated_.getEntry(key, registerIfNotExists);
    }


    public synchronized void ping()
    {
        delegated_.ping();
    }


    public synchronized void ping(K key)
    {
        delegated_.ping(key);
    }


    public synchronized void refresh()
    {
        delegated_.refresh();
    }


    public synchronized void refresh(K key)
    {
        delegated_.refresh(key);
    }


    public synchronized Cache<K, T> setCacheStorage(
        CacheStorage<K, T> cacheStorage)
    {
        return delegated_.setCacheStorage(cacheStorage);
    }


    public synchronized Cache<K, T> setObjectProvider(
        ObjectProvider<K, T> objectProvider)
    {
        return delegated_.setObjectProvider(objectProvider);
    }


    public synchronized Cache<K, T> setRefreshingStrategy(
        RefreshingStrategy<K, T> refreshingStrategy)
    {
        return delegated_.setRefreshingStrategy(refreshingStrategy);
    }


    public synchronized void remove(K key)
    {
        delegated_.remove(key);
    }


    public synchronized void clear()
    {
        delegated_.clear();
    }


    public synchronized void register(K key, T object)
    {
        delegated_.register(key, object);
    }


    public synchronized void addListener(CacheListener<K, T> listener)
    {
        delegated_.addListener(listener);
    }


    public CacheStorage<K, T> getCacheStorage()
    {
        return delegated_.getCacheStorage();
    }


    public ObjectProvider<K, T> getObjectProvider()
    {
        return delegated_.getObjectProvider();
    }


    public RefreshingStrategy<K, T> getRefreshingStrategy()
    {
        return delegated_.getRefreshingStrategy();
    }


    public long getTotalSize()
    {
        return delegated_.getTotalSize();
    }


    public long getUsedSize()
    {
        return delegated_.getUsedSize();
    }
}
