package org.seasar.kvasir.page.ability.impl;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.base.cache.Cache;
import org.seasar.kvasir.base.cache.CacheStorage;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.ObjectProvider;
import org.seasar.kvasir.base.cache.impl.CachedEntryImpl;
import org.seasar.kvasir.base.cache.impl.IndexedCacheImpl;
import org.seasar.kvasir.base.cache.impl.LRUMapCacheStorage;
import org.seasar.kvasir.util.collection.PropertyHandler;


public class PropertyCache extends
    IndexedCacheImpl<Integer, PropertyKey, PropertyHandler>
{
    private PropertyProvider provider_;

    private CacheStorage<Integer, String[]> variantStorage_ = new LRUMapCacheStorage<Integer, String[]>();


    @Override
    @Binding(bindingType = BindingType.NONE)
    public Cache<PropertyKey, PropertyHandler> setObjectProvider(
        ObjectProvider<PropertyKey, PropertyHandler> objectProvider)
    {
        return super.setObjectProvider(objectProvider);
    }


    public void setProvider(PropertyProvider provider)
    {
        provider_ = provider;
        setObjectProvider(provider_);
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
}
