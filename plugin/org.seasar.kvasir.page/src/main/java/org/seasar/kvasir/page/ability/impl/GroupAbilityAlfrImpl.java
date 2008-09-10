package org.seasar.kvasir.page.ability.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.framework.container.annotation.tiger.Aspect;
import org.seasar.kvasir.base.util.ArrayUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.AbstractPageAbilityAlfr;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.AttributeFilter;
import org.seasar.kvasir.page.ability.GroupAbility;
import org.seasar.kvasir.page.ability.GroupAbilityAlfr;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.dao.MemberDao;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.User;


/**
 *
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public class GroupAbilityAlfrImpl extends AbstractPageAbilityAlfr
    implements GroupAbilityAlfr
{
    private MemberDao dao_;


    public void setDao(MemberDao dao)
    {
        dao_ = dao;
    }


    @Override
    protected boolean doStart()
    {
        return true;
    }


    @Override
    protected void doStop()
    {
        dao_ = null;
    }


    /*
     * public scope methods
     */

    @Aspect("j2ee.requiredTx")
    public User[] getUsers(final Group group)
    {
        return ArrayUtils.downcast(getPageAlfr().getPages(
            dao_.getUserIdsByGroupId(group.getDto().getId())), User.class);
    }


    @Aspect("j2ee.requiredTx")
    public void add(final Group group, final User user)
    {
        getPageAlfr().runWithLocking(new Page[] { group, user },
            new Processable<Object>() {
                public Object process()
                    throws ProcessableRuntimeException
                {
                    dao_.addUserByGroupId(new Integer(group.getId()),
                        new Integer(user.getId()));
                    return null;
                }
            });
    }


    @Aspect("j2ee.requiredTx")
    public void remove(final Group group, final User user)
    {
        getPageAlfr().runWithLocking(new Page[] { group, user },
            new Processable<Object>() {
                public Object process()
                    throws ProcessableRuntimeException
                {
                    dao_.removeUserByGroupId(new Integer(group.getId()),
                        new Integer(user.getId()));
                    return null;
                }
            });
    }


    @Aspect("j2ee.requiredTx")
    public void clear(final Group group)
    {
        group.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                dao_.clearUsersByGroupId(new Integer(group.getId()));
                return null;
            }
        });
    }


    @Aspect("j2ee.requiredTx")
    public boolean contains(Group group, User user)
    {
        return dao_.containsUserByGroupId(new Integer(group.getId()),
            new Integer(user.getId()));
    }


    @Aspect("j2ee.requiredTx")
    public Group[] getGroups(User user)
    {
        Number[] idNumbers = dao_.getGroupIdsByUserId(Integer.valueOf(user
            .getId()));
        int[] ids = new int[idNumbers.length];
        for (int i = 0; i < idNumbers.length; i++) {
            ids[i] = idNumbers[i].intValue();
        }
        Page[] pages = getPageAlfr().getPages(ids);
        Group[] groups = new Group[pages.length];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = (Group)pages[i];
        }
        return groups;
    }


    @Aspect("j2ee.requiredTx")
    public void clearFromGroups(final User user)
    {
        user.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                dao_.clearGroupsByUserId(Integer.valueOf(user.getId()));
                return null;
            }
        });
    }


    /*
     * PageAbilityAlfr
     */

    public Class<? extends PageAbility> getAbilityInterfaceClass()
    {
        return GroupAbility.class;
    }


    public String getShortId()
    {
        return SHORTID;
    }


    public PageAbility getAbility(Page page)
    {
        Group group = getAsGroup(page);
        if (group != null) {
            return new GroupAbilityImpl(this, group);
        } else {
            return null;
        }
    }


    public void create(Page page)
    {
        // 特に何もしない。
    }


    public void delete(Page page)
    {
        Group group = getAsGroup(page);
        if (group != null) {
            clear(group);
            return;
        }
        User user = getAsUser(page);
        if (user != null) {
            clearFromGroups(user);
            return;
        }
    }


    public String[] getVariants(Page page)
    {
        return new String[] { Page.VARIANT_DEFAULT };
    }


    public Attribute getAttribute(Page page, String name, String variant)
    {
        Group group = getAsGroup(page);
        if (group == null) {
            return null;
        }
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return null;
        }

        User user = (User)PageUtils.decodeToPage(User.class, page, name);
        if (contains(group, user)) {
            Attribute attr = new Attribute();
            attr.setString(SUBNAME_DEFAULT, name);
            return attr;
        } else {
            return null;
        }
    }


    public void setAttribute(Page page, String name, String variant,
        Attribute attr)
    {
        Group group = getAsGroup(page);
        if (group == null) {
            return;
        }
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return;
        }

        User user = (User)PageUtils.decodeToPage(User.class, page, name);
        add(group, user);
    }


    public void removeAttribute(Page page, String name, String variant)
    {
        Group group = getAsGroup(page);
        if (group == null) {
            return;
        }
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return;
        }

        User user = (User)PageUtils.decodeToPage(User.class, page, name);
        remove(group, user);
    }


    public void clearAttributes(Page page)
    {
        Group group = getAsGroup(page);
        if (group == null) {
            return;
        }
        clear(group);
    }


    public boolean containsAttribute(Page page, String name, String variant)
    {
        Group group = getAsGroup(page);
        if (group == null) {
            return false;
        }
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return false;
        }

        User user = (User)PageUtils.decodeToPage(User.class, page, name);
        return contains(group, user);
    }


    public Iterator<String> attributeNames(Page page, String variant,
        AttributeFilter filter)
    {
        Group group = getAsGroup(page);
        if (group == null) {
            return new ArrayList<String>().iterator();
        }
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return new ArrayList<String>().iterator();
        }

        User[] users = getUsers(group);
        List<String> list = new ArrayList<String>(users.length);
        for (int i = 0; i < users.length; i++) {
            String path = PageUtils
                .encodePathname(page, users[i].getPathname());
            list.add(path);
        }

        return list.iterator();
    }


    /*
     * private scope methods
     */

    private Group getAsGroup(Page page)
    {
        if (page == null || !Group.TYPE.equals(page.getType())) {
            return null;
        }
        Group group;
        if (page instanceof Group) {
            group = (Group)page;
        } else {
            group = (Group)getPagePlugin().getPageType(Group.class).wrapPage(
                page);
        }
        return group;
    }


    private User getAsUser(Page page)
    {
        if (page == null || !User.TYPE.equals(page.getType())) {
            return null;
        }
        User user;
        if (page instanceof User) {
            user = (User)page;
        } else {
            user = (User)getPagePlugin().getPageType(User.class).wrapPage(page);
        }
        return user;
    }
}
