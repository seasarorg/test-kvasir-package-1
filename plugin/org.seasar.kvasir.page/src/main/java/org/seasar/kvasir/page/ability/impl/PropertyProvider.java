package org.seasar.kvasir.page.ability.impl;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.seasar.framework.container.annotation.tiger.Aspect;
import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.ObjectProvider;
import org.seasar.kvasir.base.cache.impl.CachedEntryImpl;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.dao.PropertiesDao;
import org.seasar.kvasir.page.dao.PropertiesDto;
import org.seasar.kvasir.page.dao.PropertyDao;
import org.seasar.kvasir.page.dao.PropertyDto;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;


public class PropertyProvider
    implements ObjectProvider<PropertyKey, PropertyHandler>
{
    private PropertyDao propertyDao_;

    private PropertiesDao propertiesDao_;


    public void setPropertyDao(PropertyDao propertyDao)
    {
        propertyDao_ = propertyDao;
    }


    public void setPropertiesDao(PropertiesDao propertiesDao)
    {
        propertiesDao_ = propertiesDao;
    }


    @Aspect("j2ee.requiredTx")
    public CachedEntry<PropertyKey, PropertyHandler> get(PropertyKey key)
    {
        if (Page.VARIANT_DEFAULT.equals(key.getVariant())) {
            return getDefault(key);
        }

        String body = propertiesDao_.getBodyByPageIdAndVariant(key.getPageId(),
            key.getVariant());
        PropertyHandler handler;
        if (body != null) {
            handler = new MapProperties(new TreeMap<String, String>());
            try {
                handler.load(new StringReader(body));
            } catch (IOException ex) {
                throw new RuntimeException("Can't happen!", ex);
            }
        } else {
            handler = null;
        }
        return newEntry(key, handler);
    }


    @Aspect("j2ee.requiredTx")
    CachedEntry<PropertyKey, PropertyHandler> getDefault(PropertyKey key)
    {
        PropertyDto[] dtos = propertyDao_.getDtosByPageId(key.getPageId());
        PropertyHandler handler = new MapProperties(
            new TreeMap<String, String>());
        for (int i = 0; i < dtos.length; i++) {
            handler.setProperty(dtos[i].getName(), dtos[i].getValue());
        }
        return newEntry(key, handler);
    }


    public boolean isModified(CachedEntry<PropertyKey, PropertyHandler> entry)
    {
        return false;
    }


    public CachedEntry<PropertyKey, PropertyHandler> newEntry(PropertyKey key,
        PropertyHandler object)
    {
        return new CachedEntryImpl<PropertyKey, PropertyHandler>(key, 1, object);
    }


    public CachedEntry<PropertyKey, PropertyHandler> refresh(
        CachedEntry<PropertyKey, PropertyHandler> entry)
    {
        return get(entry.getKey());
    }


    @Aspect("j2ee.requiredTx")
    public void setProperty(PropertyKey key, String name, String value)
    {
        if (Page.VARIANT_DEFAULT.equals(key.getVariant())) {
            setDefaultProperty(key.getPageId(), name, value);
        }

        PropertyHandler handler = new MapProperties(
            new TreeMap<String, String>());
        String body = propertiesDao_.getBodyByPageIdAndVariant(key.getPageId(),
            key.getVariant());
        if (body != null) {
            try {
                handler.load(new StringReader(body));
            } catch (IOException ex) {
                throw new RuntimeException("Can't happen!", ex);
            }
        }

        handler.setProperty(name, value);
        StringWriter sw = new StringWriter();
        try {
            handler.store(sw);
        } catch (IOException ex) {
            throw new RuntimeException("Can't happen!", ex);
        }

        if (body != null) {
            PropertiesDto changeSet = new PropertiesDto();
            changeSet.setBody(sw.toString());
            propertiesDao_.updateByPageIdAndVariant(changeSet, key.getPageId(),
                key.getVariant());
        } else {
            propertiesDao_.insert(new PropertiesDto(key.getPageId(), key
                .getVariant(), sw.toString()));
        }
    }


    @Aspect("j2ee.requiredTx")
    void setDefaultProperty(Integer id, String name, String value)
    {
        if (propertyDao_.existsPageIdAndName(id, name)) {
            PropertyDto changeSet = new PropertyDto();
            changeSet.setValue(value);
            propertyDao_.updateByPageIdAndName(changeSet, id, name);
        } else {
            propertyDao_.insert(new PropertyDto(id, name, value));
        }
    }


    @Aspect("j2ee.requiredTx")
    public void setProperties(PropertyKey key, PropertyHandler handler)
    {
        if (Page.VARIANT_DEFAULT.equals(key.getVariant())) {
            setDefaultProperties(key.getPageId(), handler);
        }

        StringWriter sw = new StringWriter();
        try {
            handler.store(sw);
        } catch (IOException ex) {
            throw new RuntimeException("Can't happen!", ex);
        }

        if (propertiesDao_.existsByPageIdAndVariant(key.getPageId(), key
            .getVariant())) {
            PropertiesDto changeSet = new PropertiesDto();
            changeSet.setBody(sw.toString());
            propertiesDao_.updateByPageIdAndVariant(changeSet, key.getPageId(),
                key.getVariant());
        } else {
            propertiesDao_.insert(new PropertiesDto(key.getPageId(), key
                .getVariant(), sw.toString()));
        }
    }


    @SuppressWarnings("unchecked")
    @Aspect("j2ee.requiredTx")
    void setDefaultProperties(Integer pageId, PropertyHandler handler)
    {
        propertyDao_.deleteByPageId(pageId);
        for (Enumeration<String> enm = handler.propertyNames(); enm
            .hasMoreElements();) {
            String name = enm.nextElement();
            propertyDao_.insert(new PropertyDto(pageId, name, handler
                .getProperty(name)));
        }
    }


    @Aspect("j2ee.requiredTx")
    public void removeProperty(PropertyKey key, String name)
    {
        if (Page.VARIANT_DEFAULT.equals(key.getVariant())) {
            removeDefaultProperty(key.getPageId(), name);
        }

        PropertyHandler handler = new MapProperties(
            new TreeMap<String, String>());
        String body = propertiesDao_.getBodyByPageIdAndVariant(key.getPageId(),
            key.getVariant());
        if (body != null) {
            try {
                handler.load(new StringReader(body));
            } catch (IOException ex) {
                throw new RuntimeException("Can't happen!", ex);
            }
        }

        if (!handler.containsPropertyName(name)) {
            // 指定されたプロパティは存在しないので何もしない。
            return;
        }

        handler.removeProperty(name);

        if (handler.size() > 0) {
            StringWriter sw = new StringWriter();
            try {
                handler.store(sw);
            } catch (IOException ex) {
                throw new RuntimeException("Can't happen!", ex);
            }

            PropertiesDto changeSet = new PropertiesDto();
            changeSet.setBody(sw.toString());
            propertiesDao_.updateByPageIdAndVariant(changeSet, key.getPageId(),
                key.getVariant());
        } else {
            propertiesDao_.deleteByPageIdAndVariant(key.getPageId(), key
                .getVariant());
        }
    }


    @Aspect("j2ee.requiredTx")
    void removeDefaultProperty(Integer pageId, String name)
    {
        propertyDao_.deleteByPageIdAndName(pageId, name);
    }


    @Aspect("j2ee.requiredTx")
    public void clearProperties(PropertyKey key)
    {
        if (Page.VARIANT_DEFAULT.equals(key.getVariant())) {
            clearDefaultProperties(key.getPageId());
        }

        propertiesDao_.deleteByPageIdAndVariant(key.getPageId(), key
            .getVariant());
    }


    @Aspect("j2ee.requiredTx")
    void clearDefaultProperties(Integer pageId)
    {
        propertyDao_.deleteByPageId(pageId);
    }


    @Aspect("j2ee.requiredTx")
    public void clearAllProperties(Integer pageId)
    {
        propertyDao_.deleteByPageId(pageId);
        propertiesDao_.deleteByPageId(pageId);
    }


    @Aspect("j2ee.requiredTx")
    public String[] getVariants(Integer pageId)
    {
        Set<String> variantSet = new TreeSet<String>(Arrays
            .asList(propertiesDao_.getVariantsByPageId(pageId)));
        variantSet.add(Page.VARIANT_DEFAULT);
        return variantSet.toArray(new String[0]);
    }
}
