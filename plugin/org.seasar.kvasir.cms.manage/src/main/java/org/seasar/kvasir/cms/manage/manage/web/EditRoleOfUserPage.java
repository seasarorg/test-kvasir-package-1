package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.seasar.kvasir.cms.manage.manage.dto.RoleOfUserRow;
import org.seasar.kvasir.cms.manage.tab.impl.UserTab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class EditRoleOfUserPage extends MainPanePage
{
    /*
     * set by framework
     */

    private Integer[] roleIds_ = new Integer[0];

    /*
     * for presentation tier
     */

    private RoleOfUserRow[] roles_;


    /*
     * inner fields
     */

    /*
     * public scope methods
     */

    /*
     * ACTION : execute
     */

    public Object do_execute()
    {
        if (!User.TYPE.equals(getPage().getType())) {
            setNotes(new Notes().add(new Note(
                "app.error.editRoleOfUser.notUser", getPathname())));
            return "/error.html";
        }
        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            if (updateRoleOfUser()) {
                return getRedirection("/edit-roleOfUser.do" + getPathname());
            }
        }

        return render();
    }


    /*
     * private scope methods
     */

    private boolean updateRoleOfUser()
    {
        User user = (User)getPage();
        Role[] roles = user.getRoles();
        Set<Integer> roleSet = new HashSet<Integer>();
        for (int i = 0; i < roles.length; i++) {
            roleSet.add(new Integer(roles[i].getId()));
        }
        Set<Integer> addRoleSet = new HashSet<Integer>();
        for (int i = 0; i < roleIds_.length; i++) {
            if (roleSet.contains(roleIds_[i])) {
                roleSet.remove(roleIds_[i]);
                continue;
            }
            addRoleSet.add(roleIds_[i]);
        }

        PageAlfr pageAlfr = getPageAlfr();
        for (Iterator<Integer> itr = addRoleSet.iterator(); itr.hasNext();) {
            Integer roleId = itr.next();
            Role role = pageAlfr.getPage(Role.class, roleId.intValue());
            if (role == null) {
                continue;
            }
            role.giveRoleTo(user);
        }
        for (Iterator<Integer> itr = roleSet.iterator(); itr.hasNext();) {
            Integer roleId = itr.next();
            if (roleId.intValue() == Page.ID_ALL_ROLE
                || roleId.intValue() == Page.ID_OWNER_ROLE) {
                continue;
            }
            Role role = pageAlfr.getPage(Role.class, roleId.intValue());
            if (role == null) {
                continue;
            }
            role.depriveRoleFrom(user);
        }
        setNotes(new Notes().add(new Note("app.note.editRoleOfUser.succeed")));
        return true;
    }


    private String render()
    {
        enableTab(UserTab.NAME_ROLEOFUSER);
        enableLocationBar(true);
        prepareRoles();

        return "/" + User.TYPE + "/edit-roleOfUser.html";
    }


    private void prepareRoles()
    {
        User user = (User)getPage();

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
        List<RoleOfUserRow> list = new ArrayList<RoleOfUserRow>(roles.length);
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].getId() == Page.ID_ALL_ROLE
                || roles[i].getId() == Page.ID_OWNER_ROLE) {
                continue;
            }
            list.add(new RoleOfUserRow(user, roles[i]));
        }
        roles_ = list.toArray(new RoleOfUserRow[0]);
    }


    /*
     * for framework / presentation tier
     */

    /*
     * for framework
     */

    public void setRoleIds(Integer[] roleIds)
    {
        roleIds_ = roleIds;
    }


    /*
     * for presentation tier
     */

    public RoleOfUserRow[] getRoles()
    {
        return roles_;
    }
}
