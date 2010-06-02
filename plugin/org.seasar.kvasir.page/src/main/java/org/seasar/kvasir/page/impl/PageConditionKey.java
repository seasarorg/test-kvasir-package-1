package org.seasar.kvasir.page.impl;

import java.util.Arrays;

import org.seasar.kvasir.page.condition.PageCondition;


public class PageConditionKey
{
    private PageCondition cond_;

    private boolean suppressOrders_;

    private String[] columns_;


    public PageConditionKey(PageCondition cond, boolean suppressOrders,
        String[] columns)
    {
        cond_ = cond;
        suppressOrders_ = suppressOrders;
        columns_ = columns;
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(columns_);
        result = prime * result + ((cond_ == null) ? 0 : cond_.hashCode());
        result = prime * result + (suppressOrders_ ? 1231 : 1237);
        return result;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PageConditionKey other = (PageConditionKey)obj;
        if (!Arrays.equals(columns_, other.columns_))
            return false;
        if (cond_ == null) {
            if (other.cond_ != null)
                return false;
        } else if (!cond_.equals(other.cond_))
            return false;
        if (suppressOrders_ != other.suppressOrders_)
            return false;
        return true;
    }
}
