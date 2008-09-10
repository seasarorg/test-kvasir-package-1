package org.seasar.kvasir.page.ability.content.impl;

import java.util.Date;

import org.seasar.kvasir.base.cache.CacheStorage;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.impl.CachedEntryImpl;
import org.seasar.kvasir.base.cache.impl.ImmediateRefreshingStrategy;
import org.seasar.kvasir.base.cache.impl.IndexedCacheImpl;
import org.seasar.kvasir.base.cache.impl.LRUMapCacheStorage;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbilityPlugin;
import org.seasar.kvasir.page.ability.content.ContentMold;


public class ContentCache extends
    IndexedCacheImpl<Integer, ContentKey, Content>
{
    private ContentAbilityPlugin plugin_;

    private ContentProvider provider_;

    private CacheStorage<Integer, String[]> variantStorage_ = new LRUMapCacheStorage<Integer, String[]>();


    public void setPlugin(ContentAbilityPlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setProvider(ContentProvider provider)
    {
        provider_ = provider;
        setObjectProvider(provider_);
        setRefreshingStrategy(new ImmediateRefreshingStrategy<ContentKey, Content>());
    }


    @Override
    public void refresh()
    {
        super.refresh();
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
}
