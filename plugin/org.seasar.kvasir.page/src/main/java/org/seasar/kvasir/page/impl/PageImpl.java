package org.seasar.kvasir.page.impl;

import static org.seasar.kvasir.page.ability.PropertyAbility.PROP_PAGEGARD_ID;
import static org.seasar.kvasir.page.ability.PropertyAbility.PROP_PAGEGARD_VERSION;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.annotation.ForTest;
import org.seasar.kvasir.page.CollisionDetectedRuntimeException;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.LoopDetectedException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PageEvent;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.UpdatedPageEvent;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.dao.PageDto;
import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 *  * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageImpl
    implements Page
{
    private PagePlugin plugin_;

    private PageAlfr pageAlfr_;

    private Map<Object, PageAbility> abilityMap_ = new HashMap<Object, PageAbility>();

    private PageDto dto_;

    private boolean optimisticLockEnabled_;


    public PageImpl(PagePlugin plugin, PageAlfr pageAlfr, PageDto dto)
    {
        plugin_ = plugin;
        pageAlfr_ = pageAlfr;
        dto_ = dto;
    }


    @ForTest
    PageImpl()
    {
    }


    @Override
    public int hashCode()
    {
        return dto_.getId().intValue();
    }


    @Override
    public boolean equals(Object o)
    {
        if ((o == null) || !(o instanceof Page)) {
            return false;
        }
        return (getId() == ((Page)o).getId());
    }


    @Override
    public String toString()
    {
        return "{id=" + dto_.getId() + ", heimId=" + dto_.getHeimId()
            + ", pathname=" + dto_.getPathname() + "}";
    }


    /*
     * public scope methods
     */

    public int getId()
    {
        return dto_.getId();
    }


    public String getPathname()
    {
        return dto_.getPathname();
    }


    public String getType()
    {
        return dto_.getType();
    }


    public boolean isTypeOf(String type)
    {
        if (type == null) {
            return false;
        } else {
            return type.equals(getType());
        }
    }


    public void setType(String type)
    {
        PageDto dto = newDto();
        dto.setType(type);
        update(dto);
    }


    public String getLocalPathname()
    {
        return dto_.getPathname().substring(getLordPathname().length());
    }


    public String getLordPathname()
    {
        return getLord().getPathname();
    }


    public Page getLord()
    {
        return pageAlfr_.getPage(dto_.getLordId().intValue());
    }


    public Page getLordPage()
    {
        return getLord();
    }


    public Page[] getLords()
    {
        LinkedList<Page> lordList = new LinkedList<Page>();
        Page p = this;
        while (true) {
            Page lordPage = p.getLord();
            lordList.addFirst(lordPage);
            if (lordPage.isRoot()) {
                break;
            }
            p = lordPage.getParent();
        }
        return lordList.toArray(new Page[0]);
    }


    public String getParentPathname()
    {
        return dto_.getParentPathname();
    }


    public Page getParent()
    {
        String parentPathname = getParentPathname();
        if (parentPathname == null) {
            return null;
        } else {
            return pageAlfr_.getPage(getHeimId(), parentPathname);
        }
    }


    public Page getRoot()
    {
        return pageAlfr_.getRootPage(getHeimId());
    }


    public String getName()
    {
        return dto_.getName();
    }


    public int getOrderNumber()
    {
        return dto_.getOrderNumber().intValue();
    }


    public void setOrderNumber(int orderNumber)
    {
        PageDto dto = newDto();
        dto.setOrderNumber(orderNumber);
        update(dto);
    }


    void update(final PageDto dto)
    {
        if (pageAlfr_.updatePage(PageImpl.this, dto) == 0) {
            throw new CollisionDetectedRuntimeException(dto_.getId());
        }
    }


    PageDto newDto()
    {
        PageDto dto = new PageDto();
        if (optimisticLockEnabled_) {
            dto.setVersion(dto_.getVersion());
        }
        return dto;
    }


    public Date getCreateDate()
    {
        return dto_.getCreateDate();
    }


    public String getCreateDateString()
    {
        return PageUtils.formatDate(getCreateDate());
    }


    public void setCreateDate(Date createDate)
    {
        PageDto dto = newDto();
        dto.setCreateDate(createDate);
        update(dto);
    }


    public Date getModifyDate()
    {
        return dto_.getModifyDate();
    }


    public String getModifyDateString()
    {
        return PageUtils.formatDate(getModifyDate());
    }


    public void setModifyDate(Date modifyDate)
    {
        PageDto dto = newDto();
        dto.setModifyDate(modifyDate);
        update(dto);
    }


    public Date getRevealDate()
    {
        return dto_.getRevealDate();
    }


    public String getRevealDateString()
    {
        return PageUtils.formatDate(getRevealDate());
    }


    public void setRevealDate(Date revealDate)
    {
        PageDto dto = newDto();
        dto.setRevealDate(revealDate);
        update(dto);
    }


    public Date getConcealDate()
    {
        return dto_.getConcealDate();
    }


    public String getConcealDateString()
    {
        return PageUtils.formatDate(getConcealDate());
    }


    public void setConcealDate(Date concealDate)
    {
        PageDto dto = newDto();
        dto.setConcealDate(concealDate);
        update(dto);
    }


    public User getOwnerUser()
    {
        return pageAlfr_.getPage(User.class, dto_.getOwnerUserId().intValue());
    }


    public void setOwnerUser(User ownerUser)
    {
        PageDto dto = newDto();
        dto.setOwnerUserId(ownerUser != null ? Integer.valueOf(ownerUser
            .getId()) : (Integer)null);
        update(dto);
    }


    public boolean isConcealed()
    {
        return isConcealedWhen(new Date());
    }


    public boolean isConcealedWhen(Date date)
    {
        return date.before(dto_.getRevealDate())
            || date.after(dto_.getConcealDate());
    }


    public boolean isNode()
    {
        return dto_.getNode().booleanValue();
    }


    public void setNode(boolean node)
    {
        if (isRoot()) {
            // ルートページについては変更できない。
            if (node) {
                return;
            } else {
                throw new IllegalArgumentException(
                    "Can't change node property of root page");
            }
        }

        PageDto dto = newDto();
        dto.setNode(Boolean.valueOf(node));
        update(dto);
    }


    public boolean isAsFile()
    {
        return dto_.getAsFile().booleanValue();
    }


    public void setAsFile(boolean asFile)
    {
        PageDto dto = newDto();
        dto.setAsFile(Boolean.valueOf(asFile));
        update(dto);
    }


    public boolean isListing()
    {
        return dto_.getListing().booleanValue();
    }


    public void setListing(boolean listing)
    {
        PageDto dto = newDto();
        dto.setListing(Boolean.valueOf(listing));
        update(dto);
    }


    public int getHeimId()
    {
        return dto_.getHeimId();
    }


    public Page getNearestNode()
    {
        if (isNode()) {
            return this;
        } else {
            return getParent();
        }
    }


    public boolean isRoot()
    {
        return (dto_.getPathname().length() == 0);
    }


    public boolean isLord()
    {
        return equals(getLord());
    }


    public boolean isAsLord()
    {
        return isLord();
    }


    public void setAsLord(boolean set)
    {
        pageAlfr_.setAsLord(this, set);
    }


    public void moveTo(Page toParent, String toName)
        throws DuplicatePageException, LoopDetectedException
    {
        pageAlfr_.moveTo(this, toParent, toName);
    }


    public void touch()
    {
        touch(true);
    }


    public void touch(final boolean updateModifyDate)
    {
        runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                if (updateModifyDate) {
                    setModifyDate(new Date());
                }

                // リスナに通知する。
                notifyPageListeners(new UpdatedPageEvent(PageImpl.this));

                return null;
            }
        });
    }


    public Page getChild(String name)
    {
        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        return pageAlfr_.getPage(getHeimId(), dto_.getPathname() + "/" + name);
    }


    @SuppressWarnings("unchecked")
    public <P extends Page> P getChild(Class<P> clazz, String name)
    {
        Page child = getChild(name);
        if ((child != null) && !clazz.isAssignableFrom(child.getClass())) {
            child = null;
        }
        return (P)child;
    }


    public Page[] getChildren()
    {
        return pageAlfr_.getChildPages(this);
    }


    public String[] getChildNames()
    {
        return pageAlfr_.getChildPageNames(this);
    }


    public int getChildrenCount()
    {
        return pageAlfr_.getChildPagesCount(this);
    }


    public Page[] getChildren(PageCondition cond)
    {
        return pageAlfr_.getChildPages(this, cond);
    }


    public String[] getChildNames(PageCondition cond)
    {
        return pageAlfr_.getChildPageNames(this, cond);
    }


    public int getChildrenCount(PageCondition cond)
    {
        return pageAlfr_.getChildPagesCount(this, cond);
    }


    public Page createChild(PageMold mold)
        throws DuplicatePageException
    {
        // PageAlfrがページのライフサイクルを全て管理できるようにこうしている。
        return pageAlfr_.createChildPage(this, mold);
    }


    public void delete()
    {
        // PageAlfrがページのライフサイクルを全て管理できるようにこうしている。
        pageAlfr_.deletePage(this);
    }


    public Object getAbility(Object key)
    {
        PageAbility ability = null;
        if (abilityMap_.containsKey(key)) {
            ability = abilityMap_.get(key);
        } else {
            PageAbilityAlfr alfr = plugin_.getPageAbilityAlfr(key);
            if (alfr != null) {
                ability = alfr.getAbility(this);
            }
            abilityMap_.put(key, ability);
        }
        return ability;
    }


    @SuppressWarnings("unchecked")
    public <P extends PageAbility> P getAbility(Class<P> key)
    {
        return (P)getAbility((Object)key);
    }


    public String getNonExistentChildName(String name)
    {
        if (getChild(name) == null) {
            return name;
        }
        int suffix = 2;
        String head;
        String tail;
        int dot = name.indexOf('.');
        if (dot < 0) {
            head = name;
            tail = "";
        } else {
            head = name.substring(0, dot);
            tail = name.substring(dot);
        }
        String actualName;
        do {
            actualName = head + String.valueOf(suffix++) + tail;
        } while (getChild(actualName) != null);
        return actualName;
    }


    public String getGardId()
    {
        if (isRoot()) {
            return GARDID_ROOT;
        } else {
            return getAbility(PropertyAbility.class).getProperty(
                PROP_PAGEGARD_ID);
        }
    }


    public String[] getGardIds()
    {
        LinkedList<String> gardIdList = new LinkedList<String>();
        Page p = this;
        while (true) {
            Page lordPage = p.getLord();
            if (lordPage.isRoot()) {
                break;
            }
            String gardId = lordPage.getGardId();
            if (gardId != null) {
                gardIdList.addFirst(gardId);
            }
            p = lordPage.getParent();
        }
        return gardIdList.toArray(new String[0]);
    }


    public Page[] getGardRoots()
    {
        LinkedList<Page> gardList = new LinkedList<Page>();
        Page p = this;
        while (true) {
            Page lordPage = p.getLord();
            if (lordPage.isRoot()) {
                gardList.addFirst(lordPage);
                break;
            }
            String gardId = lordPage.getGardId();
            if (gardId != null) {
                gardList.addFirst(lordPage);
            }
            p = lordPage.getParent();
        }
        return gardList.toArray(new Page[0]);
    }


    public Version getGardVersion()
    {
        PropertyAbility prop = getAbility(PropertyAbility.class);
        String versionString = prop.getProperty(PROP_PAGEGARD_VERSION);
        if (versionString == null) {
            return null;
        }
        return new Version(versionString);
    }


    public Page getNearestGardRoot()
    {
        Page p = this;
        while (true) {
            Page lordPage = p.getLord();
            if (lordPage.isRoot()) {
                break;
            }
            String gardId = lordPage.getGardId();
            if (gardId != null) {
                return lordPage;
            }
            p = lordPage.getParent();
        }
        return pageAlfr_.getRootPage(dto_.getHeimId());
    }


    public boolean isOptimisticLockEnabled()
    {
        return optimisticLockEnabled_;
    }


    public boolean setOptimisticLockEnabled(boolean optimisticLockEnabled)
    {
        boolean old = optimisticLockEnabled_;
        optimisticLockEnabled_ = optimisticLockEnabled;
        return old;
    }


    void notifyPageListeners(PageEvent pageEvent)
    {
        plugin_.notifyPageListeners(pageEvent);
    }


    public void refresh()
    {
        pageAlfr_.refreshPage(this);
    }


    public PageAlfr getAlfr()
    {
        return pageAlfr_;
    }


    public PageDto getDto()
    {
        return dto_;
    }


    public void setDto(PageDto dto)
    {
        dto_ = dto;
    }


    public <R> R runWithLocking(Processable<R> processable)
    {
        return pageAlfr_.runWithLocking(new Page[] { this }, processable);
    }
}
