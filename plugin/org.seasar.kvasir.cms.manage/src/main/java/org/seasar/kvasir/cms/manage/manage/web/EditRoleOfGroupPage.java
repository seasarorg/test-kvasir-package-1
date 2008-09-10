package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.seasar.kvasir.cms.manage.manage.dto.RoleOfGroupRow;
import org.seasar.kvasir.cms.manage.tab.impl.GroupTab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.Role;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class EditRoleOfGroupPage extends MainPanePage
{
    /*
     * set by framework
     */

    private Integer[] roleIds_ = new Integer[0];

    /*
     * for presentation tier
     */

    private RoleOfGroupRow[] roles_;


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
        if (!Group.TYPE.equals(getPage().getType())) {
            setNotes(new Notes().add(new Note(
                "app.error.editRoleOfGroup.notGroup", getPathname())));
            return "/error.html";
        }
        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            if (updateRoleOfGroup()) {
                return getRedirection("/edit-roleOfGroup.do" + getPathname());
            }
        }

        return render();
    }


    /*
     * private scope methods
     */

    private boolean updateRoleOfGroup()
    {
        Group group = (Group)getPage();
        Role[] roles = group.getRoles();
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
            role.giveRoleTo(group);
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
            role.depriveRoleFrom(group);
        }
        setNotes(new Notes().add(new Note("app.note.editRoleOfGroup.succeed")));
        return true;
    }


    private String render()
    {
        enableTab(GroupTab.NAME_ROLEOFGROUP);
        enableLocationBar(true);
        prepareRoles();

        return "/" + Group.TYPE + "/edit-roleOfGroup.html";
    }


    private void prepareRoles()
    {
        Group group = (Group)getPage();

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
        List<RoleOfGroupRow> list = new ArrayList<RoleOfGroupRow>(roles.length);
        for (int i = 0; i < roles.length; i++) {
            if (roles[i].getId() == Page.ID_ALL_ROLE
                || roles[i].getId() == Page.ID_OWNER_ROLE) {
                continue;
            }
            list.add(new RoleOfGroupRow(group, roles[i]));
        }
        roles_ = list.toArray(new RoleOfGroupRow[0]);
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

    public RoleOfGroupRow[] getRoles()
    {
        return roles_;
    }
}
