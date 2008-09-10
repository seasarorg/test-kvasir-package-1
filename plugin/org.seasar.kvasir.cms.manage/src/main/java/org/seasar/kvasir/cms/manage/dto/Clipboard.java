package org.seasar.kvasir.cms.manage.dto;

import org.seasar.kvasir.page.Page;


public interface Clipboard
{
    void addEntry(Entry entry);


    void removeEntry(String name);


    void clearEntries();


    Entry[] getEntries();


    int getEntriesCount();


    public static class Entry
    {
        private String type_;

        private String name_;

        private Object detail_;


        public Entry(String type, String name, Object detail)
        {
            type_ = type;
            name_ = name;
            detail_ = detail;
        }


        public String getType()
        {
            return type_;
        }


        public String getName()
        {
            return name_;
        }


        public Object getDetail()
        {
            return detail_;
        }
    }

    public static class PageEntry extends Entry
    {
        public PageEntry(Page page)
        {
            super("page", String.valueOf(page.getId()), page.getPathname());
        }
    }
}
