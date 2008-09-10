package org.seasar.kvasir.cms.zpt.impl;

import java.util.HashMap;
import java.util.Map;

import org.seasar.kvasir.base.util.collection.LRUMap;
import org.seasar.kvasir.cms.util.CmsUtils;

import net.skirnir.freyja.Element;
import net.skirnir.freyja.Macro;
import net.skirnir.freyja.TemplateSet;


public class KvasirCachedTemplateSet
    implements TemplateSet
{
    private static final int CACHE_SIZE = 128;

    private TemplateSet templateSet_;

    private Map<Integer, Map<String, CacheEntry>> cache_ = new HashMap<Integer, Map<String, CacheEntry>>();


    public KvasirCachedTemplateSet(TemplateSet templateSet)
    {
        templateSet_ = templateSet;
    }


    public Long getSerialNumber(String templateName)
    {
        return templateSet_.getSerialNumber(templateName);
    }


    public Entry getEntry(String templateName)
    {
        return getCacheEntry(templateName, null);
    }


    public boolean hasEntry(String templateName)
    {
        return templateSet_.hasEntry(templateName);
    }


    public Element[] getElements(String templateName)
    {
        Entry entry = getEntry(templateName);
        if (entry == null) {
            return null;
        } else {
            return entry.getElements();
        }
    }


    public Element[] getElements(String templateName, String macroName)
    {
        if (macroName == null) {
            return getElements(templateName);
        } else {
            Macro macro = getMacro(templateName, macroName);
            if (macro == null) {
                return null;
            } else {
                return new Element[] { macro.getWholeElement() };
            }
        }
    }


    public Macro getMacro(String templateName, String macroName)
    {
        CacheEntry cacheEntry = getCacheEntry(templateName, macroName);
        if (cacheEntry == null) {
            return null;
        } else {
            return cacheEntry.getMacro(macroName);
        }
    }


    public Macro getMacro(Entry entry, String macroName)
    {
        return templateSet_.getMacro(entry, macroName);
    }


    public String getCanonicalName(String baseTemplateName, String templateName)
    {
        return templateSet_.getCanonicalName(baseTemplateName, templateName);
    }


    /*
     * private scope methods
     */

    private synchronized CacheEntry getCacheEntry(String templateName,
        String macroName)
    {
        Long serialNumber = templateSet_.getSerialNumber(templateName);
        if (serialNumber == null) {
            return null;
        }

        Integer heimId = CmsUtils.getHeimId();
        Map<String, CacheEntry> map = cache_.get(heimId);
        if (map == null) {
            map = new LRUMap<String, CacheEntry>(CACHE_SIZE);
            cache_.put(heimId, map);
        }
        CacheEntry cacheEntry = map.get(templateName);
        if (cacheEntry != null) {
            if (serialNumber.longValue() > cacheEntry.getSerialNumber()) {
                cacheEntry = null;
            }
        }
        if (cacheEntry == null) {
            Element[] elems = templateSet_.getElements(templateName);
            if (elems == null) {
                return null;
            }
            cacheEntry = new CacheEntry(templateName, serialNumber.longValue(),
                elems);
            map.put(templateName, cacheEntry);
        }

        if (macroName != null) {
            if (!cacheEntry.containsMacro(macroName)) {
                cacheEntry.setMacro(macroName, templateSet_.getMacro(
                    cacheEntry, macroName));
            }
        }

        return cacheEntry;
    }


    /*
     * innter classes
     */

    private static class CacheEntry
        implements TemplateSet.Entry
    {
        private String templateName_;

        private long serialNumber_;

        private Element[] elements_;

        private Map<String, Macro> macroMap_ = new HashMap<String, Macro>();


        public CacheEntry(String templateName, long serialNumber,
            Element[] elements)
        {
            templateName_ = templateName;
            serialNumber_ = serialNumber;
            elements_ = elements;
        }


        public String getTemplateName()
        {
            return templateName_;
        }


        public long getSerialNumber()
        {
            return serialNumber_;
        }


        public Element[] getElements()
        {
            return elements_;
        }


        public boolean containsMacro(String macroName)
        {
            return macroMap_.containsKey(macroName);
        }


        public Macro getMacro(String macroName)
        {
            return macroMap_.get(macroName);
        }


        public void setMacro(String macroName, Macro macro)
        {
            macroMap_.put(macroName.intern(), macro);
        }
    }
}
