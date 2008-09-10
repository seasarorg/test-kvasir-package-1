package org.seasar.kvasir.page.ability.template.impl;

import org.seasar.kvasir.base.cache.CacheStorage;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.impl.AbstractCacheListener;
import org.seasar.kvasir.base.cache.impl.CachedEntryImpl;
import org.seasar.kvasir.base.cache.impl.IndexedCacheImpl;
import org.seasar.kvasir.base.cache.impl.LRUMapCacheStorage;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.template.Template;
import org.seasar.kvasir.page.ability.template.TemplateAbilityPlugin;
import org.seasar.kvasir.page.ability.template.dao.TemplateDto;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceUtils;


public class TemplateCache extends
    IndexedCacheImpl<Integer, TemplateKey, Template>
{
    private TemplateAbilityPlugin plugin_;

    private TemplateProvider provider_;

    private CacheStorage<Integer, TemplateDto> dtoStorage_ = new LRUMapCacheStorage<Integer, TemplateDto>();

    private CacheStorage<Integer, String[]> variantStorage_ = new LRUMapCacheStorage<Integer, String[]>();


    public void setPlugin(TemplateAbilityPlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setProvider(TemplateProvider provider)
    {
        provider_ = provider;
        setObjectProvider(provider_);
        addListener(new AbstractCacheListener<TemplateKey, Template>() {
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
    }


    @Override
    public void refresh()
    {
        super.refresh();
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
}
