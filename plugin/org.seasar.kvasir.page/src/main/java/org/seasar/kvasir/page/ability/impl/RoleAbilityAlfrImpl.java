package org.seasar.kvasir.page.ability.impl;

import java.lang.reflect.Array;
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
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.ability.RoleAbility;
import org.seasar.kvasir.page.ability.RoleAbilityAlfr;
import org.seasar.kvasir.page.dao.CastDao;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


/**
 *
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public class RoleAbilityAlfrImpl extends AbstractPageAbilityAlfr
    implements RoleAbilityAlfr
{
    private CastDao dao_;


    public void setDao(CastDao dao)
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
    public User[] getUsers(Role role)
    {
        return (User[])constructPages(dao_.getUserIdsByRoleId(new Integer(role
            .getId())), User.class);
    }


    @Aspect("j2ee.requiredTx")
    public Group[] getGroups(Role role)
    {
        return (Group[])constructPages(dao_.getGroupIdsByRoleId(new Integer(
            role.getId())), Group.class);
    }


    @Aspect("j2ee.requiredTx")
    public Role[] getRoles(User user)
    {
        return ArrayUtils.downcast(getPageAlfr().getPages(
            dao_.getRoleIdsByUserId(new Integer(user.getId()))), Role.class);
    }


    @Aspect("j2ee.requiredTx")
    public Role[] getRoles(Group group)
    {
        Page[] pages = getPageAlfr().getPages(
            dao_.getRoleIdsByGroupId(new Integer(group.getId())));
        return ArrayUtils.downcast(pages, Role.class);
    }


    @Aspect("j2ee.requiredTx")
    public boolean contains(Role role, User user)
    {
        return dao_.containsUserByRoleId(new Integer(role.getId()),
            new Integer(user.getId()));
    }


    @Aspect("j2ee.requiredTx")
    public boolean contains(Role role, Group group)
    {
        return dao_.containsGroupByRoleId(new Integer(role.getId()),
            new Integer(group.getId()));
    }


    @Aspect("j2ee.requiredTx")
    public boolean isUserInRole(Role role, User user)
    {
        return dao_.isUserInRole(Integer.valueOf(role.getId()), Integer
            .valueOf(user.getId()));
    }


    @Aspect("j2ee.requiredTx")
    public void giveRoleTo(final Role role, final User user)
    {
        getPageAlfr().runWithLocking(new Page[] { role, user },
            new Processable<Object>() {
                public Object process()
                    throws ProcessableRuntimeException
                {
                    dao_.giveRoleToUser(Integer.valueOf(role.getId()), Integer
                        .valueOf(user.getId()));
                    return null;
                }
            });
    }


    @Aspect("j2ee.requiredTx")
    public void giveRoleTo(final Role role, final Group group)
    {
        getPageAlfr().runWithLocking(new Page[] { role, group },
            new Processable<Object>() {
                public Object process()
                    throws ProcessableRuntimeException
                {
                    dao_.giveRoleToGroup(Integer.valueOf(role.getId()), Integer
                        .valueOf(group.getId()));
                    return null;
                }
            });
    }


    @Aspect("j2ee.requiredTx")
    public void depriveRoleFrom(final Role role, final User user)
    {
        getPageAlfr().runWithLocking(new Page[] { role, user },
            new Processable<Object>() {
                public Object process()
                    throws ProcessableRuntimeException
                {
                    dao_.deleteUserByRoleId(Integer.valueOf(role.getId()),
                        Integer.valueOf(user.getId()));
                    return null;
                }
            });
    }


    @Aspect("j2ee.requiredTx")
    public void depriveRoleFrom(final Role role, final Group group)
    {
        getPageAlfr().runWithLocking(new Page[] { role, group },
            new Processable<Object>() {
                public Object process()
                    throws ProcessableRuntimeException
                {
                    dao_.deleteGroupByRoleId(Integer.valueOf(role.getId()),
                        Integer.valueOf(group.getId()));
                    return null;
                }
            });
    }


    @Aspect("j2ee.requiredTx")
    public void depriveRoleFromAllUsers(final Role role)
    {
        role.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                dao_.deleteAllUsersByRoleId(Integer.valueOf(role.getId()));
                return null;
            }
        });
    }


    @Aspect("j2ee.requiredTx")
    public void depriveRoleFromAllGroups(final Role role)
    {
        role.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                dao_.deleteAllGroupsByRoleId(Integer.valueOf(role.getId()));
                return null;
            }
        });
    }


    @Aspect("j2ee.requiredTx")
    public void depriveRoleFromAll(final Role role)
    {
        role.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                dao_.deleteByRoleId(Integer.valueOf(role.getId()));
                return null;
            }
        });
    }


    @Aspect("j2ee.requiredTx")
    public void depriveAllRolesFrom(final User user)
    {
        user.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                dao_.deleteByUserId(Integer.valueOf(user.getId()));
                return null;
            }
        });
    }


    @Aspect("j2ee.requiredTx")
    public void depriveAllRolesFrom(final Group group)
    {
        group.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                dao_.deleteByGroupId(Integer.valueOf(group.getId()));
                return null;
            }
        });
    }


    /*
     * PageAbilityAlfr
     */

    public Class<? extends PageAbility> getAbilityInterfaceClass()
    {
        return RoleAbility.class;
    }


    public String getShortId()
    {
        return SHORTID;
    }


    public PageAbility getAbility(Page page)
    {
        Role role = getAsRole(page);
        if (role != null) {
            return new RoleAbilityImpl(this, role);
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
        Role role = getAsRole(page);
        if (role != null) {
            depriveRoleFromAll(role);
            return;
        }
        Group group = getAsGroup(page);
        if (group != null) {
            depriveAllRolesFrom(group);
            return;
        }
        User user = getAsUser(page);
        if (user != null) {
            depriveAllRolesFrom(user);
            return;
        }
    }


    public String[] getVariants(Page page)
    {
        return new String[] { Page.VARIANT_DEFAULT };
    }


    public Attribute getAttribute(Page page, String name, String variant)
    {
        Role role = getAsRole(page);
        if (role == null) {
            return null;
        }
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return null;
        }

        Page specified = PageUtils.decodeToPage(page, name);
        Group group = getAsGroup(specified);
        if (group != null) {
            if (contains(role, group)) {
                Attribute attr = new Attribute();
                attr.setString(SUBNAME_DEFAULT, name);
                return attr;
            } else {
                return null;
            }
        }
        User user = getAsUser(specified);
        if (contains(role, user)) {
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
        Role role = getAsRole(page);
        if (role == null) {
            return;
        }
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return;
        }

        Page specified = PageUtils.decodeToPage(page, name);
        Group group = getAsGroup(specified);
        if (group != null) {
            giveRoleTo(role, group);
            return;
        }
        User user = getAsUser(specified);
        if (user != null) {
            giveRoleTo(role, user);
            return;
        }
    }


    public void removeAttribute(Page page, String name, String variant)
    {
        Role role = getAsRole(page);
        if (role == null) {
            return;
        }
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return;
        }

        Page specified = PageUtils.decodeToPage(page, name);
        Group group = getAsGroup(specified);
        if (group != null) {
            depriveRoleFrom(role, group);
            return;
        }
        User user = getAsUser(specified);
        if (user != null) {
            depriveRoleFrom(role, user);
            return;
        }
    }


    public void clearAttributes(Page page)
    {
        Role role = getAsRole(page);
        if (role == null) {
            return;
        }
        depriveRoleFromAll(role);
    }


    public boolean containsAttribute(Page page, String name, String variant)
    {
        Role role = getAsRole(page);
        if (role == null) {
            return false;
        }
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return false;
        }

        Page specified = PageUtils.decodeToPage(page, name);
        Group group = getAsGroup(specified);
        if (group != null) {
            return contains(role, group);
        }
        User user = getAsUser(specified);
        if (user != null) {
            return contains(role, user);
        }
        return false;
    }


    public Iterator<String> attributeNames(Page page, String variant,
        AttributeFilter filter)
    {
        Role role = getAsRole(page);
        if (role == null) {
            return new ArrayList<String>().iterator();
        }
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return new ArrayList<String>().iterator();
        }

        User[] users;
        Group[] groups;
        users = role.getUsers();
        groups = role.getGroups();
        List<String> list = new ArrayList<String>(users.length + groups.length);
        for (int i = 0; i < users.length; i++) {
            String path = PageUtils
                .encodePathname(page, users[i].getPathname());
            list.add(path);
        }
        for (int i = 0; i < groups.length; i++) {
            String path = PageUtils.encodePathname(page, groups[i]
                .getPathname());
            list.add(path);
        }

        return list.iterator();
    }


    /*
     * private scope methods
     */

    private Object[] constructPages(Number[] idNumbers,
        Class<? extends Page> pageClass)
    {
        int[] ids = new int[idNumbers.length];
        for (int i = 0; i < idNumbers.length; i++) {
            ids[i] = idNumbers[i].intValue();
        }
        Page[] pages = getPageAlfr().getPages(ids);
        Page[] objs = (Page[])Array.newInstance(pageClass, pages.length);
        for (int i = 0; i < objs.length; i++) {
            objs[i] = pages[i];
        }
        return objs;
    }


    /*
     * private scope methods
     */

    private Role getAsRole(Page page)
    {
        if (page == null || !Role.TYPE.equals(page.getType())) {
            return null;
        }
        Role role;
        if (page instanceof Role) {
            role = (Role)page;
        } else {
            role = (Role)getPagePlugin().getPageType(Role.class).wrapPage(page);
        }
        return role;
    }


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
