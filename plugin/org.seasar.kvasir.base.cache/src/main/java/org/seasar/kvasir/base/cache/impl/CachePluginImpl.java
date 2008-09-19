package org.seasar.kvasir.base.cache.impl;

import org.seasar.kvasir.base.cache.Cache;
import org.seasar.kvasir.base.cache.CachePlugin;
import org.seasar.kvasir.base.cache.CacheStorage;
import org.seasar.kvasir.base.cache.IndexedCache;
import org.seasar.kvasir.base.cache.IndexedCacheKey;
import org.seasar.kvasir.base.cache.IndexedCacheStorage;
import org.seasar.kvasir.base.cache.ManagedCache;
import org.seasar.kvasir.base.cache.RefreshingStrategy;
import org.seasar.kvasir.base.cache.setting.CachePluginSettings;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.util.ArrayUtils;


public class CachePluginImpl extends AbstractPlugin<CachePluginSettings>
    implements CachePlugin
{
    private ManagedCache[] caches_ = new ManagedCache[0];


    @Override
    public Class<CachePluginSettings> getSettingsClass()
    {
        return CachePluginSettings.class;
    }


    protected boolean doStart()
    {
        return true;
    }


    protected void doStop()
    {
    }


    public <K, T> RefreshingStrategy<K, T> newRefreshingStrategyForVolatileProvider(
        String id, Cache<K, T> cache)
    {
        if (getKvasir().isStandalone()) {
            return new NonRefreshingStrategy<K, T>();
        } else {
            return new AgeRefreshingStrategy<K, T>(getSettings().getMaxAge(id));
        }
    }


    public <K, T> Cache<K, T> newCache(String id, Class<K> keyClass,
        Class<T> valueClass, boolean register)
    {
        Cache<K, T> cache = new CacheImpl<K, T>();
        if (register) {
            register(id, cache);
        }
        return cache.setCacheStorage(newCacheStorage(id, keyClass, valueClass))
            .setRefreshingStrategy(
                newRefreshingStrategyForVolatileProvider(id, cache));
    }


    public <I, K extends IndexedCacheKey<I>, T> IndexedCache<I, K, T> newIndexedCache(
        String id, Class<I> indexClass, Class<K> keyClass, Class<T> valueClass,
        boolean register)
    {
        IndexedCache<I, K, T> cache = new IndexedCacheImpl<I, K, T>();
        if (register) {
            register(id, cache);
        }
        cache.setCacheStorage(
            newIndexedCacheStorage(id, indexClass, keyClass, valueClass))
            .setRefreshingStrategy(
                newRefreshingStrategyForVolatileProvider(id, cache));
        return cache;
    }


    public <K, T> CacheStorage<K, T> newCacheStorage(String id,
        Class<K> keyClass, Class<T> valueClass)
    {
        return new LRUMapCacheStorage<K, T>(getSettings().getMaxSize(id));
    }


    public <I, K extends IndexedCacheKey<I>, T> IndexedCacheStorage<I, K, T> newIndexedCacheStorage(
        String id, Class<I> indexClass, Class<K> keyClass, Class<T> valueClass)
    {
        return new IndexedCacheStorageImpl<I, K, T>();
    }


    public synchronized void register(String id, ManagedCache cache)
    {
        caches_ = ArrayUtils.add(caches_, cache);
    }


    public ManagedCache[] getCaches()
    {
        return caches_;
    }


    public void refreshCaches()
    {
        for (int i = 0; i < caches_.length; i++) {
            caches_[i].refresh();
        }
    }


    public void pingCaches()
    {
        for (int i = 0; i < caches_.length; i++) {
            caches_[i].ping();
        }
    }
}
