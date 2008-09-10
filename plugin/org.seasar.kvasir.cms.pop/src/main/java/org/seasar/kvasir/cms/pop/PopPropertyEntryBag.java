package org.seasar.kvasir.cms.pop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.seasar.kvasir.cms.pop.extension.PopElement;


public class PopPropertyEntryBag
{
    private Map<String, PopPropertyEntry> map_ = new LinkedHashMap<String, PopPropertyEntry>();


    public PopPropertyEntryBag(PopPropertyEntry[] entries)
    {
        this(entries, null, null, null);
    }


    public PopPropertyEntryBag(PopPropertyEntry[] entries, Pop pop,
        PopContext context, String variant)
    {
        for (int i = 0; i < entries.length; i++) {
            map_.put(entries[i].getId(), entries[i]);
        }
        if (pop != null && context != null && variant != null) {
            PopPropertyMetaData[] metaDatas = pop.getPropertyMetaDatas();
            for (int i = 0; i < metaDatas.length; i++) {
                putPropertyUnlessContained(map_, pop, context, metaDatas[i]
                    .getId(), variant);
            }
            putPropertyUnlessContained(map_, pop, context,
                PopElement.PROP_TITLE, variant);
            putPropertyUnlessContained(map_, pop, context,
                PopElement.PROP_BODY, variant);
            putPropertyUnlessContained(map_, pop, context,
                PopElement.PROP_BODY_TYPE, variant);
        }
    }


    public void putProperty(String id, String value)
    {
        if (value != null) {
            map_.put(id, new PopPropertyEntry(id, value));
        } else {
            map_.remove(id);
        }
    }


    void putPropertyUnlessContained(Map<String, PopPropertyEntry> map, Pop pop,
        PopContext context, String id, String variant)
    {
        if (!map.containsKey(id)) {
            map.put(id, new PopPropertyEntry(id, pop.getProperty(context, map,
                id, variant)));
        }
    }


    public String getProperty(String id)
    {
        PopPropertyEntry entry = map_.get(id);
        if (entry != null) {
            return entry.getValue();
        } else {
            return null;
        }
    }


    public Map<String, Object> getPropertyMap()
    {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Iterator<Map.Entry<String, PopPropertyEntry>> itr = map_
            .entrySet().iterator(); itr.hasNext();) {
            Entry<String, PopPropertyEntry> entry = itr.next();
            map.put(entry.getKey(), entry.getValue().getValue());
        }
        return map;
    }
}
