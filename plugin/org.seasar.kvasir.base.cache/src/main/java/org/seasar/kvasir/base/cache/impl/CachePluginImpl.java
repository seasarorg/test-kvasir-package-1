package org.seasar.kvasir.base.cache.impl;

import org.seasar.kvasir.base.cache.Cache;
import org.seasar.kvasir.base.cache.CachePlugin;
import org.seasar.kvasir.base.cache.IndexedCache;
import org.seasar.kvasir.base.cache.IndexedCacheKey;
import org.seasar.kvasir.base.cache.ManagedCache;
import org.seasar.kvasir.base.cache.RefreshingStrategy;
import org.seasar.kvasir.base.cache.setting.CachePluginSettings;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.plugin.SettingsEvent;
import org.seasar.kvasir.base.plugin.impl.AbstractSettingsListener;
import org.seasar.kvasir.base.util.ArrayUtils;


public class CachePluginImpl extends AbstractPlugin<CachePluginSettings>
    implements CachePlugin
{
    private int maxAge_;

    private ManagedCache[] caches_ = new ManagedCache[0];


    @Override
    public Class<CachePluginSettings> getSettingsClass()
    {
        return CachePluginSettings.class;
    }


    protected boolean doStart()
    {
        addSettingsListener(new AbstractSettingsListener<CachePluginSettings>() {
            @Override
            public void notifyUpdated(SettingsEvent<CachePluginSettings> event)
            {
                maxAge_ = event.getNewSettings().getMaxAge();
            }
        });

        return true;
    }


    protected void doStop()
    {
    }


    public <K, T> RefreshingStrategy<K, T> newRefreshingStrategyForVolatileProvider(
        Cache<K, T> cache)
    {
        if (getKvasir().isStandalone()) {
            return new NonRefreshingStrategy<K, T>();
        } else {
            return new AgeRefreshingStrategy<K, T>(maxAge_);
        }
    }


    public <K, T> Cache<K, T> newCache(Class<K> keyClass, Class<T> valueClass)
    {
        Cache<K, T> cache = new CacheImpl<K, T>();
        return cache
            .setRefreshingStrategy(newRefreshingStrategyForVolatileProvider(cache));
    }


    public <I, K extends IndexedCacheKey<I>, T> IndexedCache<I, K, T> newIndexedCache(
        Class<I> indexClass, Class<K> keyClass, Class<T> valueClass)
    {
        IndexedCache<I, K, T> cache = new IndexedCacheImpl<I, K, T>();
        cache
            .setRefreshingStrategy(newRefreshingStrategyForVolatileProvider(cache));
        return cache;
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
