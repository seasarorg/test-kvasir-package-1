package org.seasar.kvasir.page.ability.template.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.seasar.framework.container.annotation.tiger.Aspect;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.impl.AbstractObjectProvider;
import org.seasar.kvasir.base.cache.impl.CachedEntryImpl;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.template.Template;
import org.seasar.kvasir.page.ability.template.dao.TemplateDao;
import org.seasar.kvasir.page.ability.template.dao.TemplateDto;
import org.seasar.kvasir.page.ability.template.dao.TemplatesDao;
import org.seasar.kvasir.page.ability.template.dao.TemplatesDto;


public class TemplateProvider extends
    AbstractObjectProvider<TemplateKey, Template>
{
    private TemplateDao templateDao_;

    private TemplatesDao templatesDao_;


    public void setTemplateDao(TemplateDao templateDao)
    {
        templateDao_ = templateDao;
    }


    public void setTemplatesDao(TemplatesDao templatesDao)
    {
        templatesDao_ = templatesDao;
    }


    @Aspect("j2ee.requiredTx")
    public CachedEntry<TemplateKey, Template> get(TemplateKey key)
    {
        TemplatesDto dto = templatesDao_.getObjectByPageIdAndVariant(key
            .getPageId(), key.getVariant());
        Template template;
        if (dto != null) {
            template = new TemplateImpl(dto);
        } else {
            template = null;
        }
        return newEntry(key, template);
    }


    @Aspect("j2ee.requiredTx")
    public boolean isModified(CachedEntry<TemplateKey, Template> entry)
    {
        Date modifyDate = templatesDao_.getModifyDateByPageIdAndVariant(entry
            .getKey().getPageId(), entry.getKey().getVariant());
        Template oldTemplate = entry.getCached();
        if (oldTemplate != null) {
            return (modifyDate == null || modifyDate.after(oldTemplate
                .getModifyDate()));
        } else {
            return (modifyDate != null);
        }
    }


    public CachedEntry<TemplateKey, Template> newEntry(TemplateKey key,
        Template object)
    {
        return new CachedEntryImpl<TemplateKey, Template>(key, 1, object);
    }


    @Aspect("j2ee.requiredTx")
    public void setTemplate(TemplateKey key, String template)
    {
        Integer id = key.getPageId();
        String variant = key.getVariant();
        if (templatesDao_.existsByPageIdAndVariant(id, variant)) {
            TemplatesDto changeSet = new TemplatesDto();
            changeSet.setBody(template);
            templatesDao_.updateByPageIdAndVariant(changeSet, key.getPageId(),
                key.getVariant());
        } else {
            if (!templateDao_.existsByPageId(id)) {
                templateDao_.insert(new TemplateDto(id, "", ""));
            }
            templatesDao_.insert(new TemplatesDto(id, variant, template,
                new Date()));
        }
    }


    @Aspect("j2ee.requiredTx")
    public void removeTemplate(TemplateKey key)
    {
        templatesDao_.deleteByPageIdAndVariant(key.getPageId(), key
            .getVariant());
    }


    @Aspect("j2ee.requiredTx")
    public void clearAllTemplates(Integer pageId)
    {
        templatesDao_.deleteByPageId(pageId);
        templateDao_.deleteByPageId(pageId);
    }


    @Aspect("j2ee.requiredTx")
    public void setType(Integer pageId, String type)
    {
        if (templateDao_.existsByPageId(pageId)) {
            TemplateDto changeSet = new TemplateDto();
            changeSet.setType(type);
            templateDao_.updateByPageId(changeSet, pageId);
        } else {
            templateDao_.insert(new TemplateDto(pageId, type, ""));
        }
    }


    @Aspect("j2ee.requiredTx")
    public TemplateDto getTemplateDto(Integer pageId)
    {
        return templateDao_.getObjectByPageId(pageId);
    }


    @Aspect("j2ee.requiredTx")
    public void setResponseContentType(Integer pageId,
        String responseContentType)
    {
        if (templateDao_.existsByPageId(pageId)) {
            TemplateDto changeSet = new TemplateDto();
            changeSet.setResponseContentType(responseContentType);
            templateDao_.updateByPageId(changeSet, pageId);
        } else {
            templateDao_
                .insert(new TemplateDto(pageId, "", responseContentType));
        }
    }


    @Aspect("j2ee.requiredTx")
    public String[] getVariants(Integer pageId)
    {
        Set<String> variantSet = new TreeSet<String>(Arrays
            .asList(templatesDao_.getVariantsByPageId(pageId)));
        variantSet.add(Page.VARIANT_DEFAULT);
        return variantSet.toArray(new String[0]);
    }
}
