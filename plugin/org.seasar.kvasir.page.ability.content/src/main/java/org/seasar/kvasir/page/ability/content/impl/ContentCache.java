package org.seasar.kvasir.page.ability.content.impl;

import java.util.Date;

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
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbilityPlugin;
import org.seasar.kvasir.page.ability.content.ContentMold;


public class ContentCache
    implements IndexedCache<Integer, ContentKey, Content>
{
    private static final int LOADED_PROVIDER = 1;

    private static final int LOADED_CACHEPLUGIN = 2;

    private static final int LOADED_ALL = LOADED_PROVIDER | LOADED_CACHEPLUGIN;

    private int loaded_;

    private ContentProvider provider_;

    private CacheStorage<Integer, String[]> variantStorage_;

    private IndexedCache<Integer, ContentKey, Content> cache_;

    private CachePlugin cachePlugin_;


    @Binding(bindingType = BindingType.MUST)
    public void setCachePlugin(CachePlugin cachePlugin)
    {
        cachePlugin_ = cachePlugin;

        if ((loaded_ |= LOADED_CACHEPLUGIN) == LOADED_ALL) {
            initialize();
        }
    }


    @Binding(bindingType = BindingType.MUST)
    public void setPlugin(ContentAbilityPlugin plugin)
    {
    }


    public void setProvider(ContentProvider provider)
    {
        provider_ = provider;

        if ((loaded_ |= LOADED_PROVIDER) == LOADED_ALL) {
            initialize();
        }
    }


    void initialize()
    {
        cache_ = cachePlugin_.newIndexedCache(ContentAbilityPlugin.ID,
            Integer.class, ContentKey.class, Content.class, false);
        cache_.setObjectProvider(provider_);
        // XXX デフォルトのRefreshingStrategyを使用するようにコメントアウトした。
        // 問題があれば考えよう。
        //        setRefreshingStrategy(new ImmediateRefreshingStrategy<ContentKey, Content>());
        cachePlugin_.register(ContentAbilityPlugin.ID, this);

        variantStorage_ = cachePlugin_.newCacheStorage(ContentAbilityPlugin.ID,
            Integer.class, String[].class);
    }


    public void refresh()
    {
        cache_.refresh();
        variantStorage_.clear();
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


    public void removeContentsBefore(Integer pageId, String variant,
        Integer revisionNumber)
    {
        provider_.removeContentsBefore(pageId, variant, revisionNumber);
    }


    public void clearContents(Integer pageId, String variant)
    {
        provider_.clearContents(pageId, variant);
        clear(pageId);
        variantStorage_.remove(pageId);
    }


    public void clearAllContents(Integer pageId)
    {
        provider_.clearAllContents(pageId);
        clear(pageId);
        variantStorage_.remove(pageId);
    }


    public Content getContent(Integer pageId, String variant,
        Integer revisionNumber)
    {
        return provider_.getContent(pageId, variant, revisionNumber);
    }


    public int getEarliestRevisionNumber(Integer pageId, String variant)
    {
        return provider_.getEarliestRevisionNumber(pageId, variant);
    }


    public int getLatestRevisionNumber(Integer pageId, String variant)
    {
        return provider_.getLatestRevisionNumber(pageId, variant);
    }


    public Date getModifyDate(Integer pageId)
    {
        return provider_.getModifyDate(pageId);
    }


    public int getRevisionNumber(Integer pageId, String variant, Date date)
    {
        return provider_.getRevisionNumber(pageId, variant, date);
    }


    public void updateContent(Integer pageId, String variant, ContentMold mold,
        boolean overwrite)
    {
        provider_.updateContent(pageId, variant, mold, overwrite);
        clear(pageId);
        variantStorage_.remove(pageId);
    }


    public boolean hasAnyContents(Integer pageId)
    {
        return provider_.hasAnyContents(pageId);
    }


    public void clear(Integer index)
    {
        cache_.clear(index);
    }


    public void addListener(CacheListener<ContentKey, Content> listener)
    {
        cache_.addListener(listener);
    }


    public void clear()
    {
        cache_.clear();
    }


    public Content get(ContentKey key)
    {
        return cache_.get(key);
    }


    public CacheStorage<ContentKey, Content> getCacheStorage()
    {
        return cache_.getCacheStorage();
    }


    public CachedEntry<ContentKey, Content> getEntry(ContentKey key)
    {
        return cache_.getEntry(key);
    }


    public CachedEntry<ContentKey, Content> getEntry(ContentKey key,
        boolean registerIfNotExists)
    {
        return cache_.getEntry(key, registerIfNotExists);
    }


    public ObjectProvider<ContentKey, Content> getObjectProvider()
    {
        return cache_.getObjectProvider();
    }


    public RefreshingStrategy<ContentKey, Content> getRefreshingStrategy()
    {
        return cache_.getRefreshingStrategy();
    }


    public void ping()
    {
        cache_.ping();
    }


    public void ping(ContentKey key)
    {
        cache_.ping(key);
    }


    public void refresh(ContentKey key)
    {
        cache_.refresh(key);
    }


    public void register(ContentKey key, Content object)
    {
        cache_.register(key, object);
    }


    public void remove(ContentKey key)
    {
        cache_.remove(key);
    }


    @Binding(bindingType = BindingType.NONE)
    public Cache<ContentKey, Content> setCacheStorage(
        CacheStorage<ContentKey, Content> cacheStorage)
    {
        throw new UnsupportedOperationException();
    }


    @Binding(bindingType = BindingType.NONE)
    public Cache<ContentKey, Content> setObjectProvider(
        ObjectProvider<ContentKey, Content> objectProvider)
    {
        throw new UnsupportedOperationException();
    }


    @Binding(bindingType = BindingType.NONE)
    public Cache<ContentKey, Content> setRefreshingStrategy(
        RefreshingStrategy<ContentKey, Content> refreshingStrategy)
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
