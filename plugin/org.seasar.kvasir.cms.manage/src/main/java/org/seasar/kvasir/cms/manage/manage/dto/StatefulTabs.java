package org.seasar.kvasir.cms.manage.manage.dto;

public class StatefulTabs
{
    private StatefulTab[] elements_;

    private int colspan_;


    public StatefulTabs(StatefulTab[] elements)
    {
        elements_ = elements;
        colspan_ = elements.length * 4;
    }


    public StatefulTab[] getElements()
    {
        return elements_;
    }


    public int getElementsCount()
    {
        return elements_.length;
    }


    public int getColspan()
    {
        return colspan_;
    }
}
