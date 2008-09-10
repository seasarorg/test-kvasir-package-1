package org.seasar.kvasir.base.cache.impl;

import java.util.HashMap;
import java.util.Map;

import org.seasar.kvasir.base.cache.CachedEntry;
import org.seasar.kvasir.base.cache.ObjectProvider;
import org.seasar.kvasir.base.cache.impl.CachedEntryImpl;


public class TemplateProvider
    implements ObjectProvider<String, Template>
{
    private Map<String, CachedEntry<String, Template>> map_ = new HashMap<String, CachedEntry<String, Template>>();


    public void add(String key, Template template)
    {
        map_.put(key, newEntry(key, template));
    }


    public CachedEntry<String, Template> get(String key)
    {
        return map_.get(key);
    }


    public CachedEntry<String, Template> refresh(
        CachedEntry<String, Template> entry)
    {
        return new CachedEntryImpl<String, Template>(entry.getKey(), entry
            .getSequenceNumber() + 1, entry.getCached());
    }


    public boolean isModified(CachedEntry<String, Template> entry)
    {
        return true;
    }


    public CachedEntry<String, Template> newEntry(String key, Template template)
    {
        return new CachedEntryImpl<String, Template>(key, 1, template);
    }
}
