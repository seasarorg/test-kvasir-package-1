package org.seasar.kvasir.page.condition;

import java.util.Date;

import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.type.User;


public class PageConditionWithHeimId extends PageCondition
{
    private PageCondition pageCondition_;

    private int heimId_;


    public PageConditionWithHeimId(PageCondition pageCondition, int heimId)
    {
        pageCondition_ = pageCondition;
        heimId_ = heimId;
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + heimId_;
        result = prime * result
            + ((pageCondition_ == null) ? 0 : pageCondition_.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        PageConditionWithHeimId other = (PageConditionWithHeimId)obj;
        if (heimId_ != other.heimId_)
            return false;
        if (pageCondition_ == null) {
            if (other.pageCondition_ != null)
                return false;
        } else if (!pageCondition_.equals(other.pageCondition_))
            return false;
        return true;
    }


    @Override
    public PageCondition addOption(Formula option)
    {
        return pageCondition_.addOption(option);
    }


    @Override
    public PageCondition addOrder(Order order)
    {
        return pageCondition_.addOrder(order);
    }


    public Object clone()
    {
        PageConditionWithHeimId cloned = (PageConditionWithHeimId)super.clone();
        cloned.pageCondition_ = (PageCondition)pageCondition_.clone();
        return cloned;
    }


    @Override
    public PageCondition freeze()
    {
        return pageCondition_.freeze();
    }


    @Override
    public int getHeimId()
    {
        return heimId_;
    }


    @Override
    public int getLength()
    {
        return pageCondition_.getLength();
    }


    @Override
    public int getOffset()
    {
        return pageCondition_.getOffset();
    }


    @Override
    public Formula getOption()
    {
        return pageCondition_.getOption();
    }


    @Override
    public Order[] getOrders()
    {
        return pageCondition_.getOrders();
    }


    @Override
    public Privilege getPrivilege()
    {
        return pageCondition_.getPrivilege();
    }


    @Override
    public String getType()
    {
        return pageCondition_.getType();
    }


    @Override
    public User getUser()
    {
        return pageCondition_.getUser();
    }


    @Override
    public boolean isFreezed()
    {
        return pageCondition_.isFreezed();
    }


    @Override
    public boolean isIncludeConcealed()
    {
        return pageCondition_.isIncludeConcealed();
    }


    @Override
    public boolean isOnlyListed()
    {
        return pageCondition_.isOnlyListed();
    }


    @Override
    public Date getCurrentDate()
    {
        return pageCondition_.getCurrentDate();
    }


    @Override
    public boolean isRecursive()
    {
        return pageCondition_.isRecursive();
    }


    @Override
    public PageCondition setIncludeConcealed(boolean includeConcealed)
    {
        return pageCondition_.setIncludeConcealed(includeConcealed);
    }


    @Override
    public PageCondition setOnlyListed(boolean onlyListed)
    {
        return pageCondition_.setOnlyListed(onlyListed);
    }


    @Override
    public PageCondition setCurrentDate(Date currentDate)
    {
        return pageCondition_.setCurrentDate(currentDate);
    }


    @Override
    public PageCondition setLength(int length)
    {
        return pageCondition_.setLength(length);
    }


    @Override
    public PageCondition setOffset(int offset)
    {
        return pageCondition_.setOffset(offset);
    }


    @Override
    public PageCondition setOption(Formula option)
    {
        return pageCondition_.setOption(option);
    }


    @Override
    public PageCondition setOptions(Formula... option)
    {
        return pageCondition_.setOptions(option);
    }


    @Override
    public PageCondition setOrder(Order order)
    {
        return pageCondition_.setOrder(order);
    }


    @Override
    public PageCondition setOrders(Order... orders)
    {
        return pageCondition_.setOrders(orders);
    }


    @Override
    public PageCondition setPrivilege(Privilege privilege)
    {
        return pageCondition_.setPrivilege(privilege);
    }


    @Override
    public PageCondition setRecursive(boolean recursive)
    {
        return pageCondition_.setRecursive(recursive);
    }


    @Override
    public PageCondition setType(String type)
    {
        return pageCondition_.setType(type);
    }


    @Override
    public PageCondition setUser(User user)
    {
        return pageCondition_.setUser(user);
    }


    @Override
    public String toString()
    {
        return pageCondition_.toString();
    }
}
