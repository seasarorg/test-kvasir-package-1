package org.seasar.kvasir.page.ability.template.impl;

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
import org.seasar.kvasir.base.cache.impl.AbstractCacheListener;
import org.seasar.kvasir.base.cache.impl.CachedEntryImpl;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.template.Template;
import org.seasar.kvasir.page.ability.template.TemplateAbilityPlugin;
import org.seasar.kvasir.page.ability.template.dao.TemplateDto;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceUtils;


public class TemplateCache
    implements IndexedCache<Integer, TemplateKey, Template>
{
    private static final int LOADED_PROVIDER = 1;

    private static final int LOADED_CACHEPLUGIN = 2;

    private static final int LOADED_ALL = LOADED_PROVIDER | LOADED_CACHEPLUGIN;

    private int loaded_;

    private TemplateAbilityPlugin plugin_;

    private TemplateProvider provider_;

    private IndexedCache<Integer, TemplateKey, Template> cache_;

    private CacheStorage<Integer, TemplateDto> dtoStorage_;

    private CacheStorage<Integer, String[]> variantStorage_;

    private CachePlugin cachePlugin_;


    public void setCachePlugin(CachePlugin cachePlugin)
    {
        cachePlugin_ = cachePlugin;

        if ((loaded_ |= LOADED_CACHEPLUGIN) == LOADED_ALL) {
            initialize();
        }
    }


    @Binding(bindingType = BindingType.MUST)
    public void setPlugin(TemplateAbilityPlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setProvider(TemplateProvider provider)
    {
        provider_ = provider;

        if ((loaded_ |= LOADED_PROVIDER) == LOADED_ALL) {
            initialize();
        }
    }


    void initialize()
    {
        cache_ = cachePlugin_.newIndexedCache(TemplateAbilityPlugin.ID,
            Integer.class, TemplateKey.class, Template.class, false);
        cache_.setObjectProvider(provider_);
        cache_.addListener(new AbstractCacheListener<TemplateKey, Template>() {
            @Override
            public void notifyRegistered(
                CachedEntry<TemplateKey, Template> entry)
            {
                Resource dir = plugin_
                    .getConfigurationDirectory()
                    .getChildResource(
                        "templates"
                            + PageUtils.getFilePath(entry.getKey().getPageId()));
                String variant = entry.getKey().getVariant();
                StringBuilder sb = new StringBuilder();
                sb.append("body");
                if (variant.length() > 0) {
                    sb.append("_").append(variant);
                }
                Resource resource = dir.getChildResource(sb.toString());
                Template template = entry.getCached();
                if (template != null) {
                    dir.mkdirs();
                    ResourceUtils.writeString(resource, template.getBody());
                    template.setBodyResource(resource);
                } else {
                    resource.delete();
                }
            }
        });
        cachePlugin_.register(TemplateAbilityPlugin.ID, this);

        dtoStorage_ = cachePlugin_.newCacheStorage(TemplateAbilityPlugin.ID,
            Integer.class, TemplateDto.class);
        variantStorage_ = cachePlugin_.newCacheStorage(
            TemplateAbilityPlugin.ID, Integer.class, String[].class);
    }


    public void refresh()
    {
        cache_.refresh();
        dtoStorage_.clear();
        variantStorage_.clear();
    }


    public void setTempate(TemplateKey key, String template)
    {
        // テンプレートを持っていない時にTemplateProvider#setTemplate()すると
        // dtoもnull→非nullに変わるのでdtoキャッシュもクリアする必要がある。
        dtoStorage_.remove(key.getPageId());

        remove(key);
        variantStorage_.remove(key.getPageId());
        provider_.setTemplate(key, template);
    }


    public void removeTemplate(TemplateKey key)
    {
        remove(key);
        variantStorage_.remove(key.getPageId());
        provider_.removeTemplate(key);
    }


    public void clearAllTemplates(Integer pageId)
    {
        clear(pageId);
        dtoStorage_.remove(pageId);
        variantStorage_.remove(pageId);
        provider_.clearAllTemplates(pageId);
    }


    public String getType(Integer pageId)
    {
        TemplateDto dto = getDtoEntry(pageId).getCached();
        if (dto != null) {
            return dto.getType();
        } else {
            return "";
        }
    }


    CachedEntry<Integer, TemplateDto> getDtoEntry(Integer pageId)
    {
        CachedEntry<Integer, TemplateDto> entry = dtoStorage_.get(pageId);
        if (entry == null) {
            entry = new CachedEntryImpl<Integer, TemplateDto>(pageId, 1,
                provider_.getTemplateDto(pageId));
            dtoStorage_.register(entry);
        }
        return entry;
    }


    public void setType(Integer pageId, String type)
    {
        dtoStorage_.remove(pageId);
        provider_.setType(pageId, type);
    }


    public String getResponseContentType(Integer pageId)
    {
        TemplateDto dto = getDtoEntry(pageId).getCached();
        if (dto != null) {
            return dto.getResponseContentType();
        } else {
            return "";
        }
    }


    public void setResponseContentType(Integer pageId,
        String responseContentType)
    {
        dtoStorage_.remove(pageId);
        provider_.setResponseContentType(pageId, responseContentType);
    }


    public String[] getVariants(Integer pageId)
    {
        CachedEntry<Integer, String[]> entry = variantStorage_.get(pageId);
        if (entry == null) {
            entry = new CachedEntryImpl<Integer, String[]>(pageId, 1, provider_
                .getVariants(pageId));
            variantStorage_.register(entry);
        }
        return entry.getCached();
    }


    public boolean hasAnyTemplates(Integer pageId)
    {
        return (getDtoEntry(pageId).getCached() != null);
    }


    public void clear(Integer index)
    {
        cache_.clear(index);
    }


    public void addListener(CacheListener<TemplateKey, Template> listener)
    {
        cache_.addListener(listener);
    }


    public void clear()
    {
        cache_.clear();
    }


    public Template get(TemplateKey key)
    {
        return cache_.get(key);
    }


    public CacheStorage<TemplateKey, Template> getCacheStorage()
    {
        return cache_.getCacheStorage();
    }


    public CachedEntry<TemplateKey, Template> getEntry(TemplateKey key)
    {
        return cache_.getEntry(key);
    }


    public CachedEntry<TemplateKey, Template> getEntry(TemplateKey key,
        boolean registerIfNotExists)
    {
        return cache_.getEntry(key, registerIfNotExists);
    }


    public ObjectProvider<TemplateKey, Template> getObjectProvider()
    {
        return cache_.getObjectProvider();
    }


    public RefreshingStrategy<TemplateKey, Template> getRefreshingStrategy()
    {
        return cache_.getRefreshingStrategy();
    }


    public void ping()
    {
        cache_.ping();
    }


    public void ping(TemplateKey key)
    {
        cache_.ping(key);
    }


    public void refresh(TemplateKey key)
    {
        cache_.refresh(key);
    }


    public void register(TemplateKey key, Template object)
    {
        cache_.register(key, object);
    }


    public void remove(TemplateKey key)
    {
        cache_.remove(key);
    }


    @Binding(bindingType = BindingType.NONE)
    public Cache<TemplateKey, Template> setCacheStorage(
        CacheStorage<TemplateKey, Template> cacheStorage)
    {
        throw new UnsupportedOperationException();
    }


    @Binding(bindingType = BindingType.NONE)
    public Cache<TemplateKey, Template> setObjectProvider(
        ObjectProvider<TemplateKey, Template> objectProvider)
    {
        throw new UnsupportedOperationException();
    }


    @Binding(bindingType = BindingType.NONE)
    public Cache<TemplateKey, Template> setRefreshingStrategy(
        RefreshingStrategy<TemplateKey, Template> refreshingStrategy)
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
