package org.seasar.kvasir.page.ability.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.framework.container.annotation.tiger.Aspect;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.AbstractPageAbilityAlfr;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.AttributeFilter;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.ability.Permission;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.PermissionAbilityAlfr;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PrivilegeLevel;
import org.seasar.kvasir.page.ability.PrivilegeType;
import org.seasar.kvasir.page.dao.PermissionDao;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


/**
 *
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public class PermissionAbilityAlfrImpl extends AbstractPageAbilityAlfr
    implements PermissionAbilityAlfr
{
    private PermissionDao dao_;


    public void setDao(PermissionDao dao)
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
    public boolean permits(Page page, User user, Privilege priv)
    {
        if (user.isAdministrator()) {
            return true;
        }

        return dao_.permits(new Integer(page.getId()),
            new Integer(user.getId()), priv);
    }


    @Aspect("j2ee.requiredTx")
    public Permission[] getPermissions(Page page)
    {
        List<Permission> list = dao_.getPermissionListByPageId(new Integer(page
            .getId()));
        Permission[] perms = new Permission[list.size()];
        int cnt = 0;
        for (Iterator<Permission> itr = list.iterator(); itr.hasNext();) {
            perms[cnt++] = itr.next();
        }
        return perms;
    }


    @Aspect("j2ee.requiredTx")
    public void setPermissions(final Page page, final Permission[] perms)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                dao_.setPermissionsByPageId(new Integer(page.getId()), perms);
                return null;
            }
        });
    }


    @Aspect("j2ee.requiredTx")
    public void clearPermissions(final Page page)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                dao_.clearPermissionsByPageId(new Integer(page.getId()));
                return null;
            }
        });
    }


    @Aspect("j2ee.requiredTx")
    public PrivilegeLevel getPrivilegeLevel(Page page, Role role,
        PrivilegeType privType)
    {
        Number level = dao_.getPrivilegeLevelByPageIdAndRoleIdAndPrivType(
            new Integer(page.getId()), new Integer(role.getId()), new Integer(
                privType.getValue()));
        if (level != null) {
            return PrivilegeLevel.getLevel(level.intValue(),
                PrivilegeLevel.NONE);
        } else {
            return PrivilegeLevel.NONE;
        }
    }


    @Aspect("j2ee.requiredTx")
    public void grantPrivilege(final Page page, final Role role,
        final Privilege priv)
    {
        if (priv.getLevel() == PrivilegeLevel.NONE) {
            revokePrivilege(page, role, priv.getType());
            return;
        }

        getPageAlfr().runWithLocking(new Page[] { page, role },
            new Processable<Object>() {
                public Object process()
                    throws ProcessableRuntimeException
                {
                    dao_.grantPrivilegeByPageId(new Integer(page.getId()),
                        new Integer(role.getId()), priv);
                    return null;
                }
            });
    }


    @Aspect("j2ee.requiredTx")
    public void revokePrivilege(final Page page, final Role role,
        final PrivilegeType privType)
    {
        getPageAlfr().runWithLocking(new Page[] { page, role },
            new Processable<Object>() {
                public Object process()
                    throws ProcessableRuntimeException
                {
                    dao_.revokePrivilegeByPageIdAndRoleIdAndPrivType(
                        new Integer(page.getId()), new Integer(role.getId()),
                        new Integer(privType.getValue()));
                    return null;
                }
            });
    }


    @Aspect("j2ee.requiredTx")
    public void revokeAllPrivileges(final Page page, final Role role)
    {
        getPageAlfr().runWithLocking(new Page[] { page, role },
            new Processable<Object>() {
                public Object process()
                    throws ProcessableRuntimeException
                {
                    dao_.revokePrivilegesByPageIdAndRoleId(new Integer(page
                        .getId()), new Integer(role.getId()));
                    return null;
                }
            });
    }


    @Aspect("j2ee.requiredTx")
    public void revokeAllPrivilegesFrom(final Role role)
    {
        role.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                dao_.revokePrivilegesByRoleId(new Integer(role.getId()));
                return null;
            }
        });
    }


    /*
     * private scope methods
     */

    private Permission getPermission(Page page, String encoded)
    {
        int dot = encoded.indexOf('.');
        if (dot < 0) {
            return null;
        }
        String type = encoded.substring(0, dot);
        String path = encoded.substring(dot + 1);

        Role role = (Role)PageUtils.decodeToPage(Role.class, page, path);
        if (role == null) {
            return null;
        }

        Privilege priv = Privilege.getPrivilege(type);
        if (priv == null) {
            return null;
        }

        return new Permission(role, priv);
    }


    /*
     * PageAbilityAlfr
     */

    public Class<? extends PageAbility> getAbilityInterfaceClass()
    {
        return PermissionAbility.class;
    }


    public String getShortId()
    {
        return SHORTID;
    }


    public PageAbility getAbility(Page page)
    {
        return new PermissionAbilityImpl(this, page);
    }


    public void create(Page page)
    {
        Page parent = page.getParent();
        if (parent == null) {
            return;
        }

        // 親のPermissionをコピーする。
        setPermissions(page, getPermissions(parent));
    }


    public void delete(Page page)
    {
        Role role = getAsRole(page);
        if (role != null) {
            revokeAllPrivilegesFrom(role);
        }
        clearPermissions(page);
    }


    public String[] getVariants(Page page)
    {
        return new String[] { Page.VARIANT_DEFAULT };
    }


    public Attribute getAttribute(Page page, String name, String variant)
    {
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return null;
        }

        Permission perm = getPermission(page, name);
        if (perm == null) {
            return null;
        }
        PrivilegeLevel level = getPrivilegeLevel(page, perm.getRole(), perm
            .getPrivilege().getType());
        if (level == PrivilegeLevel.NONE) {
            return null;
        }

        Attribute attr = new Attribute();
        attr.setString(SUBNAME_DEFAULT, level.getName());
        return attr;
    }


    public void setAttribute(Page page, String name, String variant,
        Attribute attr)
    {
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return;
        }

        Permission perm = getPermission(page, name);
        if (perm == null) {
            return;
        }

        PrivilegeLevel level = PrivilegeLevel.getLevel(attr
            .getString(SUBNAME_DEFAULT), PrivilegeLevel.UNKNOWN);
        if (level == PrivilegeLevel.UNKNOWN) {
            return;
        }
        Privilege priv = Privilege.getPrivilege(perm.getPrivilege().getType(),
            level);
        grantPrivilege(page, perm.getRole(), priv);
    }


    public void removeAttribute(Page page, String variant, String name)
    {
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return;
        }

        Permission perm = getPermission(page, name);
        if (perm == null) {
            return;
        }

        revokePrivilege(page, perm.getRole(), perm.getPrivilege().getType());
    }


    public void clearAttributes(Page page)
    {
        clearPermissions(page);
    }


    public boolean containsAttribute(Page page, String variant, String name)
    {
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return false;
        }

        Permission perm = getPermission(page, name);
        if (perm == null) {
            return false;
        }

        PrivilegeLevel level = getPrivilegeLevel(page, perm.getRole(), perm
            .getPrivilege().getType());
        return (level != PrivilegeLevel.NONE);
    }


    public Iterator<String> attributeNames(Page page, String variant,
        AttributeFilter filter)
    {
        if (!Page.VARIANT_DEFAULT.equals(variant)) {
            return new ArrayList<String>().iterator();
        }

        Permission[] perms = getPermissions(page);
        List<String> list = new ArrayList<String>(perms.length);
        for (int i = 0; i < perms.length; i++) {
            String type = perms[i].getPrivilege().getType().getName();
            String path = PageUtils.encodePathname(page, perms[i].getRole()
                .getPathname());
            list.add(type + "." + path);
        }

        return list.iterator();
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
}
