package org.seasar.kvasir.page.mock;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.LoopDetectedException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ability.GroupAbility;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.RoleAbility;
import org.seasar.kvasir.page.ability.mock.MockGroupAbility;
import org.seasar.kvasir.page.ability.mock.MockPermissionAbility;
import org.seasar.kvasir.page.ability.mock.MockPropertyAbility;
import org.seasar.kvasir.page.ability.mock.MockRoleAbility;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.dao.PageDto;
import org.seasar.kvasir.page.type.User;


public class MockPage
    implements Page
{
    private int id_;

    private int heimId_;

    private String pathname_;

    private PageDto dto_;

    private boolean root_;

    private boolean lord_;

    private boolean node_;

    private boolean asFile_;

    private boolean listing_ = true;

    private boolean concealed_;

    private User ownerUser_;

    private Page parent_;

    private int orderNumber_;

    private MockPageAlfr pageAlfr_;

    private Map<Object, PageAbility> abilityMap_ = new HashMap<Object, PageAbility>();

    private Date modifyDate_;

    private Date createDate_;

    private String type_ = Page.TYPE;


    public MockPage(int id, String pathname)
    {
        this(id, PathId.HEIM_MIDGARD, pathname);
    }


    public MockPage(int id, int heimId, String pathname)
    {
        id_ = id;
        heimId_ = heimId;
        pathname_ = pathname;

        registerAbility(PropertyAbility.class, new MockPropertyAbility(this));
        registerAbility(PermissionAbility.class,
            new MockPermissionAbility(this));
        registerAbility(RoleAbility.class, new MockRoleAbility(this));
        registerAbility(GroupAbility.class, new MockGroupAbility(this));
    }


    @Override
    public String toString()
    {
        return "{id=" + id_ + ", heimId=" + heimId_ + ", pathname=" + pathname_
            + "}";
    }


    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + id_;
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
        final MockPage other = (MockPage)obj;
        if (id_ != other.id_)
            return false;
        return true;
    }


    public MockPageAlfr getPageAlfr()
    {
        return pageAlfr_;
    }


    public MockPage setPageAlfr(MockPageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
        return this;
    }


    public int getId()
    {
        return id_;
    }


    public String getPathname()
    {
        return pathname_;
    }


    public String getType()
    {
        return type_;
    }


    public void setType(String type)
    {
        type_ = type;
    }


    public String getLordPathname()
    {
        return getLord().getPathname();
    }


    public String getLocalPathname()
    {
        return pathname_.substring(getLordPathname().length());
    }


    public Page getLord()
    {
        if (isLord()) {
            return this;
        } else {
            return getParent().getLord();
        }
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


    public String getNonExistentChildName(String name)
    {
        return null;
    }


    public String getParentPathname()
    {
        if (pathname_.length() == 0) {
            return null;
        } else {
            return pathname_.substring(0, pathname_.lastIndexOf('/'));
        }
    }


    public Page getParent()
    {
        return parent_;
    }


    public MockPage setParent(Page parent)
    {
        parent_ = parent;
        return this;
    }


    public Page getRoot()
    {
        return pageAlfr_.getRootPage(getHeimId());
    }


    public String getName()
    {
        if (pathname_.length() == 0) {
            return "";
        } else {
            return pathname_.substring(pathname_.lastIndexOf('/') + 1);
        }
    }


    public int getOrderNumber()
    {
        return orderNumber_;
    }


    public void setOrderNumber(int orderNumber)
    {
        orderNumber_ = orderNumber;
    }


    public Date getCreateDate()
    {
        return createDate_;
    }


    public void setCreateDate(Date createDate)
    {
        createDate_ = createDate;
    }


    public String getCreateDateString()
    {
        return PageUtils.formatDate(getCreateDate());
    }


    public Date getModifyDate()
    {
        return modifyDate_;
    }


    public void setModifyDate(Date modifyDate)
    {
        modifyDate_ = modifyDate;
    }


    public String getModifyDateString()
    {
        return PageUtils.formatDate(getModifyDate());
    }


    public User getOwnerUser()
    {
        return ownerUser_;
    }


    public void setOwnerUser(User ownerUser)
    {
        ownerUser_ = ownerUser;
    }


    public boolean isConcealed()
    {
        return concealed_;
    }


    public MockPage setConcealed(boolean concealed)
    {
        concealed_ = concealed;
        return this;
    }


    public boolean isNode()
    {
        return node_;
    }


    public void setNode(boolean node)
    {
        node_ = node;
    }


    public boolean isAsFile()
    {
        return asFile_;
    }


    public void setAsFile(boolean asFile)
    {
        asFile_ = asFile;
    }


    public boolean isListing()
    {
        return listing_;
    }


    public void setListing(boolean listing)
    {
        listing_ = listing;
    }


    public int getHeimId()
    {
        return heimId_;
    }


    public MockPage setHeimId(int heimId)
    {
        heimId_ = heimId;
        return this;
    }


    public boolean isDeleted()
    {
        return false;
    }


    public Page getNearestNode()
    {
        return null;
    }


    public boolean isRoot()
    {
        return root_;
    }


    public MockPage setRoot(boolean root)
    {
        root_ = root;
        setAsLord(true);
        return this;
    }


    public boolean isLord()
    {
        return lord_;
    }


    public void setAsLord(boolean lord)
    {
        lord_ = lord;
    }


    public void moveTo(Page toParent, String toName)
        throws DuplicatePageException, LoopDetectedException
    {
    }


    public void touch()
    {
    }


    public void touch(boolean updateModifyDate)
    {
    }


    public Page getChild(String name)
    {
        if (pageAlfr_ == null) {
            return null;
        }

        if (!name.startsWith("/")) {
            name = "/" + name;
        }
        return pageAlfr_.getPage(heimId_, pathname_ + name);
    }


    public Page[] getChildren()
    {
        if (pageAlfr_ == null) {
            return new Page[0];
        }

        return pageAlfr_.getChildPages(heimId_, pathname_);
    }


    public Page[] getChildren(PageCondition cond)
    {
        return null;
    }


    public String[] getChildNames()
    {
        if (pageAlfr_ == null) {
            return new String[0];
        }

        return pageAlfr_.getChildPageNames(heimId_, pathname_);
    }


    public String[] getChildNames(PageCondition cond)
    {
        return null;
    }


    public int getChildrenCount()
    {
        return 0;
    }


    public int getChildrenCount(PageCondition cond)
    {
        return 0;
    }


    public Page createChild(PageMold mold)
        throws DuplicatePageException
    {
        return null;
    }


    public void delete()
    {
    }


    public void notifyChangeAncestorPathname(String fromPathname,
        String toPathname)
    {
    }


    public void notifyChangeAncestorLord(int oldLordId, int newLordId)
    {
    }


    public <P extends Page> P getChild(Class<P> clazz, String name)
    {
        return null;
    }


    @SuppressWarnings("unchecked")
    public <P extends PageAbility> P getAbility(Class<P> key)
    {
        return (P)abilityMap_.get(key);
    }


    public <P extends PageAbility> void registerAbility(Class<P> key, P ability)
    {
        abilityMap_.put(key, ability);
    }


    public String getGardId()
    {
        return null;
    }


    public String[] getGardIds()
    {
        return null;
    }


    public Page[] getGardRoots()
    {
        return null;
    }


    public Version getGardVersion()
    {
        return null;
    }


    public Page getNearestGardRoot()
    {
        return null;
    }


    public boolean isOptimisticLockEnabled()
    {
        return false;
    }


    public boolean setOptimisticLockEnabled(boolean optimisticLockEnabled)
    {
        return false;
    }


    public void refresh()
    {
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
        return null;
    }


    public Date getConcealDate()
    {
        return null;
    }


    public String getConcealDateString()
    {
        return null;
    }


    public Date getRevealDate()
    {
        return null;
    }


    public String getRevealDateString()
    {
        return null;
    }


    public boolean isConcealedWhen(Date date)
    {
        return false;
    }


    public void setConcealDate(Date concealDate)
    {
    }


    public void setRevealDate(Date revealDate)
    {
    }


    public Object getAbility(Object key)
    {
        return abilityMap_.get(key);
    }


    public PageAlfr getAlfr()
    {
        return pageAlfr_;
    }
}
