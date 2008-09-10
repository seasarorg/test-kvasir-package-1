package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.cms.manage.manage.dto.PermissionRow;
import org.seasar.kvasir.cms.manage.manage.dto.RoleRow;
import org.seasar.kvasir.cms.manage.tab.impl.PageTab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.Permission;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PrivilegeLevel;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.Role;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class PermissionPage extends MainPanePage
{
    public static final String DO_GRANT = "grant";

    public static final String DO_REVOKE = "revoke";

    public static final String DO_NEVER = "never";

    private static final int DOVALUE_GRANT = 0;

    private static final int DOVALUE_REVOKE = 1;

    private static final int DOVALUE_NEVER = 2;

    public static final String PREFIX_PRIVILEGE = "privilege:";

    /*
     * set by framework
     */

    private boolean recursive_;

    private Integer roleId_;

    private String privilege_;

    private String do_;

    /*
     * for presentation tier
     */

    private PermissionRow[] permissions_;

    private RoleRow[] roles_;


    /*
     * inner fields
     */

    /*
     * public scope methods
     */

    /*
     * ACTION : list
     */

    public String do_list()
    {
        enableTab(PageTab.NAME_PERMISSION);
        enableLocationBar(true);
        prepareRoles();
        preparePermissions();
        return "/permission.list.html";
    }


    private void prepareRoles()
    {
        Page page = getPage();
        Locale locale = getLocale();

        int heimId = getCurrentHeimId();
        Role[] roles = (Role[])getPageAlfr().getPages(heimId,
            new PageCondition().setType(Role.TYPE));
        if (heimId != PathId.HEIM_MIDGARD) {
            List<Role> roleList = new ArrayList<Role>();

            roleList.addAll(Arrays.asList((Role[])getPageAlfr().getPage(
                Page.ID_ROLES).getChildren(
                new PageCondition().setType(Role.TYPE))));
            roleList.addAll(Arrays.asList(roles));
            roles = roleList.toArray(new Role[0]);
        }
        roles_ = new RoleRow[roles.length];
        for (int i = 0; i < roles.length; i++) {
            roles_[i] = new RoleRow(roles[i], page, locale);
        }
        Arrays.sort(roles_, new Comparator<RoleRow>() {
            public int compare(RoleRow o1, RoleRow o2)
            {
                return o1.getPathname().compareTo(o2.getPathname());
            }
        });
    }


    private void preparePermissions()
    {
        PermissionAbility perm = getPage().getAbility(PermissionAbility.class);
        Permission[] ps = perm.getPermissions();
        Map<Role, Permission> map = new HashMap<Role, Permission>();
        for (int i = 0; i < ps.length; i++) {
            map.put(ps[i].getRole(), ps[i]);
        }
        permissions_ = new PermissionRow[roles_.length];
        for (int i = 0; i < roles_.length; i++) {
            Permission p = map.get(roles_[i].getRole());
            Privilege priv = (p != null) ? p.getPrivilege()
                : Privilege.ACCESS_NONE;
            permissions_[i] = new PermissionRow(roles_[i].getRole(), priv, this);
        }
        Arrays.sort(permissions_, new Comparator<PermissionRow>() {
            public int compare(PermissionRow o1, PermissionRow o2)
            {
                return o1.getRolePathname().compareTo(o2.getRolePathname());
            }
        });
    }


    /*
     * ACTION : update
     */

    @SuppressWarnings("unchecked")
    public Object do_update()
    {
        if (!"POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            return null;
        }

        // roleId, privilege, doパターンか？
        boolean updated = false;
        if (roleId_ != null && privilege_ != null && do_ != null) {
            Role role = (Role)getPageAlfr().getPage(Role.class,
                roleId_.intValue());
            Privilege priv = Privilege.getPrivilege(privilege_);
            if (role != null && priv != null) {
                update(getPage(), role, priv, getDoValue(do_), recursive_);
                updated = true;
            }
        } else {
            // privilege:ROLEID=PRIVILEGEパターン。
            Map<String, String[]> map = getYmirRequest().getParameterMap();
            for (Iterator<Map.Entry<String, String[]>> itr = map.entrySet()
                .iterator(); itr.hasNext();) {
                Map.Entry<String, String[]> entry = itr.next();
                String key = entry.getKey();
                if (!key.startsWith(PREFIX_PRIVILEGE)) {
                    continue;
                }
                int roleId;
                try {
                    roleId = Integer.parseInt(key.substring(PREFIX_PRIVILEGE
                        .length()));
                } catch (NumberFormatException ex) {
                    continue;
                }
                Role role = (Role)getPageAlfr().getPage(Role.class, roleId);
                if (role == null) {
                    continue;
                }
                String[] values = (String[])entry.getValue();
                for (int i = 0; i < values.length; i++) {
                    Privilege priv = Privilege.getPrivilege(values[i]);
                    if (priv == null) {
                        continue;
                    }
                    update(getPage(), role, priv, DOVALUE_GRANT, recursive_);
                    updated = true;
                }
            }
        }
        if (updated) {
            setNotes(new Notes().add(new Note("app.note.permission.succeed")));
        }

        return getRedirection("/permission.list.do" + getPathname());
    }


    private void update(Page page, Role role, Privilege priv, int doValue,
        boolean recursive)
    {
        PermissionAbility perm = page.getAbility(PermissionAbility.class);
        if (doValue == DOVALUE_GRANT) {
            perm.grantPrivilege(role, priv);
        } else if (doValue == DOVALUE_REVOKE) {
            perm.revokePrivilege(role, priv.getType());
        } else if (doValue == DOVALUE_NEVER) {
            priv = Privilege.getPrivilege(priv.getType(), PrivilegeLevel.NEVER);
            perm.grantPrivilege(role, priv);
        } else {
            throw new IllegalArgumentException("Unknown doValue: " + doValue);
        }
        if (recursive) {
            Page[] children = page.getChildren();
            for (int i = 0; i < children.length; i++) {
                update(children[i], role, priv, doValue, recursive);
            }
        }
    }


    private int getDoValue(String doString)
    {
        int doValue = DOVALUE_GRANT;
        if (DO_REVOKE.equals(doString)) {
            doValue = DOVALUE_REVOKE;
        } else if (DO_NEVER.equals(doString)) {
            doValue = DOVALUE_NEVER;
        }
        return doValue;
    }


    /*
     * for framework / presentation tier
     */

    /*
     * for framework
     */

    public void setDo(String doString)
    {
        do_ = doString;
    }


    public void setPrivilege(String privilege)
    {
        privilege_ = privilege;
    }


    public void setRecursive(boolean recursive)
    {
        recursive_ = recursive;
    }


    public void setRoleId(Integer roleId)
    {
        roleId_ = roleId;
    }


    /*
     * for presentation tier
     */

    public PermissionRow[] getPermissions()
    {
        return permissions_;
    }


    public RoleRow[] getRoles()
    {
        return roles_;
    }
}
