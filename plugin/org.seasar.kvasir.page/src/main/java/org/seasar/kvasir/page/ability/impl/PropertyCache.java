package org.seasar.kvasir.page.ability.impl;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.base.cache.Cache;
import org.seasar.kvasir.base.cache.CacheListener;
import org.seasar.kvasir.base.cache.CachePlugin;
import org.seasar.kvasir.base.cache.CacheStorage;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.IndexedCache;
import org.seasar.kvasir.base.cache.ObjectProvider;
import org.seasar.kvasir.base.cache.RefreshingStrategy;
import org.seasar.kvasir.base.cache.impl.CachedEntryImpl;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.util.collection.PropertyHandler;


public class PropertyCache
    implements IndexedCache<Integer, PropertyKey, PropertyHandler>
{
    public static final String ID = PagePlugin.ID + ".property";

    private static final int LOADED_PROVIDER = 1;

    private static final int LOADED_CACHEPLUGIN = 2;

    private static final int LOADED_ALL = LOADED_PROVIDER | LOADED_CACHEPLUGIN;

    private int loaded_;

    private PropertyProvider provider_;

    private IndexedCache<Integer, PropertyKey, PropertyHandler> cache_;

    private CacheStorage<Integer, String[]> variantStorage_;

    private CachePlugin cachePlugin_;


    public void setCachePlugin(CachePlugin cachePlugin)
    {
        cachePlugin_ = cachePlugin;

        if ((loaded_ |= LOADED_CACHEPLUGIN) == LOADED_ALL) {
            initialize();
        }
    }


    public void setProvider(PropertyProvider provider)
    {
        provider_ = provider;

        if ((loaded_ |= LOADED_PROVIDER) == LOADED_ALL) {
            initialize();
        }
    }


    void initialize()
    {
        cache_ = cachePlugin_.newIndexedCache(ID, Integer.class,
            PropertyKey.class, PropertyHandler.class, false);
        cache_.setObjectProvider(provider_);
        variantStorage_ = cachePlugin_.newCacheStorage(ID, Integer.class,
            String[].class);
        cachePlugin_.register(ID, this);
    }


    public void setProperty(PropertyKey key, String name, String value)
    {
        PropertyHandler handler = get(key);
        if (handler != null) {
            handler.setProperty(name, value);
        } else {
            // nullがキャッシュされている場合があるのでその場合はそれを除去する。
            remove(key);
        }
        variantStorage_.remove(key.getPageId());

        provider_.setProperty(key, name, value);
    }


    public void setProperties(PropertyKey key, PropertyHandler prop)
    {
        remove(key);
        variantStorage_.remove(key.getPageId());

        provider_.setProperties(key, prop);
    }


    public void removeProperty(PropertyKey key, String name)
    {
        PropertyHandler handler = get(key);
        if (handler != null) {
            handler.removeProperty(name);
            variantStorage_.remove(key.getPageId());
        }

        provider_.removeProperty(key, name);
    }


    public void clearProperties(PropertyKey key)
    {
        remove(key);
        variantStorage_.remove(key.getPageId());

        provider_.clearProperties(key);
    }


    public void clearAllProperties(Integer id)
    {
        clear(id);
        variantStorage_.remove(id);

        provider_.clearAllProperties(id);
    }


    public String[] getVariants(Integer id)
    {
        CachedEntry<Integer, String[]> entry = variantStorage_.get(id);
        if (entry == null) {
            entry = new CachedEntryImpl<Integer, String[]>(id, 1, provider_
                .getVariants(id));
            variantStorage_.register(entry);
        }
        return entry.getCached();
    }


    public void clear(Integer index)
    {
        cache_.clear(index);
    }


    public void addListener(CacheListener<PropertyKey, PropertyHandler> listener)
    {
        cache_.addListener(listener);
    }


    public void clear()
    {
        cache_.clear();
    }


    public PropertyHandler get(PropertyKey key)
    {
        return cache_.get(key);
    }


    public CacheStorage<PropertyKey, PropertyHandler> getCacheStorage()
    {
        return cache_.getCacheStorage();
    }


    public CachedEntry<PropertyKey, PropertyHandler> getEntry(PropertyKey key)
    {
        return cache_.getEntry(key);
    }


    public CachedEntry<PropertyKey, PropertyHandler> getEntry(PropertyKey key,
        boolean registerIfNotExists)
    {
        return cache_.getEntry(key, registerIfNotExists);
    }


    public ObjectProvider<PropertyKey, PropertyHandler> getObjectProvider()
    {
        return cache_.getObjectProvider();
    }


    public RefreshingStrategy<PropertyKey, PropertyHandler> getRefreshingStrategy()
    {
        return cache_.getRefreshingStrategy();
    }


    public void ping()
    {
        cache_.ping();
    }


    public void ping(PropertyKey key)
    {
        cache_.ping(key);
    }


    public void refresh()
    {
        cache_.refresh();
    }


    public void refresh(PropertyKey key)
    {
        cache_.refresh(key);
    }


    public void register(PropertyKey key, PropertyHandler object)
    {
        cache_.register(key, object);
    }


    public void remove(PropertyKey key)
    {
        cache_.remove(key);
    }


    @Binding(bindingType = BindingType.NONE)
    public Cache<PropertyKey, PropertyHandler> setCacheStorage(
        CacheStorage<PropertyKey, PropertyHandler> cacheStorage)
    {
        throw new UnsupportedOperationException();
    }


    @Binding(bindingType = BindingType.NONE)
    public Cache<PropertyKey, PropertyHandler> setObjectProvider(
        ObjectProvider<PropertyKey, PropertyHandler> objectProvider)
    {
        throw new UnsupportedOperationException();
    }


    @Binding(bindingType = BindingType.NONE)
    public Cache<PropertyKey, PropertyHandler> setRefreshingStrategy(
        RefreshingStrategy<PropertyKey, PropertyHandler> refreshingStrategy)
    {
        throw new UnsupportedOperationException();
    }


    public long getTotalSize()
    {
        return cache_.getTotalSize();
    }


    public long getUsedSize()
    {
        return cache_.getUsedSize();
    }
}
