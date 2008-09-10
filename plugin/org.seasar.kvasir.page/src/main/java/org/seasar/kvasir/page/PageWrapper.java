package org.seasar.kvasir.page;

import java.util.Date;

import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.dao.PageDto;
import org.seasar.kvasir.page.type.User;


/**
 *
 * <p><b>同期化：</b>
 * このクラスのサブクラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageWrapper
    implements Page
{
    protected Page page_;


    public PageWrapper(Page page)
    {
        page_ = page;
    }


    public Page getPage()
    {
        return page_;
    }


    @Override
    public int hashCode()
    {
        return page_.hashCode();
    }


    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof Page)) {
            return false;
        }
        Page page = (Page)o;
        return (getId() == page.getId());
    }


    @Override
    public String toString()
    {
        return page_.toString();
    }


    public Page createChild(PageMold mold)
        throws DuplicatePageException
    {
        return page_.createChild(mold);
    }


    public void delete()
    {
        page_.delete();
    }


    public Object getAbility(Object key)
    {
        return page_.getAbility(key);
    }


    public <P extends PageAbility> P getAbility(Class<P> key)
    {
        return page_.getAbility(key);
    }


    public String getName()
    {
        return page_.getName();
    }


    public Page getChild(String name)
    {
        return page_.getChild(name);
    }


    public <P extends Page> P getChild(Class<P> clazz, String name)
    {
        return page_.getChild(clazz, name);
    }


    public String[] getChildNames()
    {
        return page_.getChildNames();
    }


    public String[] getChildNames(PageCondition cond)
    {
        return page_.getChildNames(cond);
    }


    public Page[] getChildren()
    {
        return page_.getChildren();
    }


    public Page[] getChildren(PageCondition cond)
    {
        return page_.getChildren(cond);
    }


    public int getChildrenCount()
    {
        return page_.getChildrenCount();
    }


    public int getChildrenCount(PageCondition cond)
    {
        return page_.getChildrenCount(cond);
    }


    public Date getCreateDate()
    {
        return page_.getCreateDate();
    }


    public String getCreateDateString()
    {
        return page_.getCreateDateString();
    }


    public Page getNearestNode()
    {
        return page_.getNearestNode();
    }


    public int getId()
    {
        return page_.getId();
    }


    public String getLocalPathname()
    {
        return page_.getLocalPathname();
    }


    public Page getLord()
    {
        return page_.getLord();
    }


    public Page[] getLords()
    {
        return page_.getLords();
    }


    public String getLordPathname()
    {
        return page_.getLordPathname();
    }


    public Date getModifyDate()
    {
        return page_.getModifyDate();
    }


    public String getModifyDateString()
    {
        return page_.getModifyDateString();
    }


    public String getNonExistentChildName(String name)
    {
        return page_.getNonExistentChildName(name);
    }


    public int getOrderNumber()
    {
        return page_.getOrderNumber();
    }


    public User getOwnerUser()
    {
        return page_.getOwnerUser();
    }


    public Page getParent()
    {
        return page_.getParent();
    }


    public String getParentPathname()
    {
        return page_.getParentPathname();
    }


    public Page getRoot()
    {
        return page_.getRoot();
    }


    public String getPathname()
    {
        return page_.getPathname();
    }


    public String getType()
    {
        return page_.getType();
    }


    public void setType(String type)
    {
        page_.setType(type);
    }


    public boolean isConcealed()
    {
        return page_.isConcealed();
    }


    public boolean isNode()
    {
        return page_.isNode();
    }


    public int getHeimId()
    {
        return page_.getHeimId();
    }


    public boolean isRoot()
    {
        return page_.isRoot();
    }


    public void moveTo(Page page, String name)
        throws DuplicatePageException, LoopDetectedException
    {
        page_.moveTo(page, name);
    }


    public boolean isLord()
    {
        return page_.isLord();
    }


    public void setAsLord(boolean set)
    {
        page_.setAsLord(set);
    }


    public void setCreateDate(Date createDate)
    {
        page_.setCreateDate(createDate);
    }


    public void setModifyDate(Date modifyDate)
    {
        page_.setModifyDate(modifyDate);
    }


    public void setNode(boolean node)
    {
        page_.setNode(node);
    }


    public boolean isAsFile()
    {
        return page_.isAsFile();
    }


    public void setAsFile(boolean asFile)
    {
        page_.setAsFile(asFile);
    }


    public boolean isListing()
    {
        return page_.isListing();
    }


    public void setListing(boolean listing)
    {
        page_.setListing(listing);
    }


    public void setOrderNumber(int orderNumber)
    {
        page_.setOrderNumber(orderNumber);
    }


    public void setOwnerUser(User ownerUser)
    {
        page_.setOwnerUser(ownerUser);
    }


    public void touch()
    {
        page_.touch();
    }


    public void touch(boolean updateModifyDate)
    {
        page_.touch(updateModifyDate);
    }


    public String getGardId()
    {
        return page_.getGardId();
    }


    public String[] getGardIds()
    {
        return page_.getGardIds();
    }


    public Page[] getGardRoots()
    {
        return page_.getGardRoots();
    }


    public Version getGardVersion()
    {
        return page_.getGardVersion();
    }


    public Page getNearestGardRoot()
    {
        return page_.getNearestGardRoot();
    }


    public boolean isOptimisticLockEnabled()
    {
        return page_.isOptimisticLockEnabled();
    }


    public boolean setOptimisticLockEnabled(boolean optimisticLockEnabled)
    {
        return page_.setOptimisticLockEnabled(optimisticLockEnabled);
    }


    public void refresh()
    {
        page_.refresh();
    }


    public PageAlfr getAlfr()
    {
        return page_.getAlfr();
    }


    public PageDto getDto()
    {
        return page_.getDto();
    }


    public void setDto(PageDto dto)
    {
        page_.setDto(dto);
    }


    public <R> R runWithLocking(Processable<R> processable)
    {
        return page_.runWithLocking(processable);
    }


    public Date getConcealDate()
    {
        return page_.getConcealDate();
    }


    public String getConcealDateString()
    {
        return page_.getConcealDateString();
    }


    public Date getRevealDate()
    {
        return page_.getRevealDate();
    }


    public String getRevealDateString()
    {
        return page_.getRevealDateString();
    }


    public boolean isConcealedWhen(Date date)
    {
        return page_.isConcealedWhen(date);
    }


    public void setConcealDate(Date concealDate)
    {
        page_.setConcealDate(concealDate);
    }


    public void setRevealDate(Date revealDate)
    {
        page_.setRevealDate(revealDate);
    }
}
