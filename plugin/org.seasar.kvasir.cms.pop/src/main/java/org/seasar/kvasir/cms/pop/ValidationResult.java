package org.seasar.kvasir.cms.pop;

import java.util.HashMap;
import java.util.Map;


public class ValidationResult
{
    public static final String ERROR_ASIS = "error.asIs";

    private Map<String, Entry> entryMap_ = new HashMap<String, Entry>();


    public Entry[] getEntries()
    {
        return entryMap_.values().toArray(new Entry[0]);
    }


    public Entry getEntry(String id)
    {
        return entryMap_.get(id);
    }


    public void addEntry(Entry entry)
    {
        entryMap_.put(entry.getId(), entry);
    }


    public static class Entry
    {
        private String id_;

        private String messageKey_;

        private String[] args_;


        public Entry(String id, String key)
        {
            this(id, key, new String[0]);
        }


        public Entry(String id, String key, String arg0)
        {
            this(id, key, new String[] { arg0 });
        }


        public Entry(String id, String key, String[] args)
        {
            id_ = id;
            messageKey_ = key;
            args_ = args;
        }


        public String getId()
        {
            return id_;
        }


        public String getMessageKey()
        {
            return messageKey_;
        }


        public String[] getArgs()
        {
            return args_;
        }
    }
}
