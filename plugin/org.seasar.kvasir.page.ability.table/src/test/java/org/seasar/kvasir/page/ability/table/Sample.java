package org.seasar.kvasir.page.ability.table;

import org.seasar.dao.annotation.tiger.Id;
import org.seasar.kvasir.page.ability.table.annotation.PageId;


public class Sample
{
    private int id_;

    private int pageId_;

    private String value_;


    @Id
    public int getId()
    {
        return id_;
    }


    public void setId(int id)
    {
        id_ = id;
    }


    @PageId
    public int getPageId()
    {
        return pageId_;
    }


    public void setPageId(int pageId)
    {
        pageId_ = pageId;
    }


    public String getValue()
    {
        return value_;
    }


    public void setValue(String value)
    {
        value_ = value;
    }
}
