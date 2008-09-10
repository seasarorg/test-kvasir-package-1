package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.seasar.kvasir.cms.manage.dto.Clipboard;


public class ClipboardImpl
    implements Clipboard
{
    private Map<String, Entry> entryMap_ = Collections
        .synchronizedMap(new LinkedHashMap<String, Entry>());


    public void addEntry(Entry entry)
    {
        entryMap_.put(entry.getName(), entry);
    }


    public void removeEntry(String name)
    {
        entryMap_.remove(name);
    }


    public void clearEntries()
    {
        entryMap_.clear();
    }


    public Entry[] getEntries()
    {
        synchronized (entryMap_) {
            return (Entry[])entryMap_.values().toArray(new Entry[0]);
        }
    }


    public int getEntriesCount()
    {
        return entryMap_.size();
    }
}
