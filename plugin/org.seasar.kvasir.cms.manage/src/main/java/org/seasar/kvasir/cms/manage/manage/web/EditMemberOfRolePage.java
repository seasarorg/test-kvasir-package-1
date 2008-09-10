package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.seasar.kvasir.cms.manage.manage.dto.GroupOfRoleRow;
import org.seasar.kvasir.cms.manage.manage.dto.UserOfRoleRow;
import org.seasar.kvasir.cms.manage.tab.impl.RoleTab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class EditMemberOfRolePage extends MainPanePage
{
    public static final int ID_DUMMY = 0;

    /*
     * set by framework
     */

    private Integer[] userIds_;

    private Integer[] groupIds_;

    /*
     * for presentation tier
     */

    private UserOfRoleRow[] users_;

    private GroupOfRoleRow[] groups_;


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
        if (!Role.TYPE.equals(getPage().getType())) {
            setNotes(new Notes().add(new Note(
                "app.error.editMemberOfRole.notGroup", getPathname())));
            return "/error.html";
        }
        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            if (updateMemberOfRole()) {
                return getRedirection("/edit-memberOfRole.do" + getPathname());
            }
        }

        return render();
    }


    /*
     * private scope methods
     */

    private boolean updateMemberOfRole()
    {
        Role role = (Role)getPage();
        PageAlfr pageAlfr = getPageAlfr();
        if (userIds_ != null) {
            User[] users = role.getUsers();
            Set<Integer> userSet = new HashSet<Integer>();
            for (int i = 0; i < users.length; i++) {
                userSet.add(new Integer(users[i].getId()));
            }
            Set<Integer> addUserSet = new HashSet<Integer>();
            for (int i = 0; i < userIds_.length; i++) {
                if (userIds_[i].intValue() == ID_DUMMY) {
                    continue;
                }
                if (userSet.contains(userIds_[i])) {
                    userSet.remove(userIds_[i]);
                    continue;
                }
                addUserSet.add(userIds_[i]);
            }

            for (Iterator<Integer> itr = addUserSet.iterator(); itr.hasNext();) {
                Integer userId = itr.next();
                User user = pageAlfr.getPage(User.class, userId.intValue());
                if (user == null) {
                    continue;
                }
                role.giveRoleTo(user);
            }
            for (Iterator<Integer> itr = userSet.iterator(); itr.hasNext();) {
                Integer userId = itr.next();
                User user = pageAlfr.getPage(User.class, userId.intValue());
                if (user == null) {
                    continue;
                }
                role.depriveRoleFrom(user);
            }
        }
        if (groupIds_ != null) {
            Group[] groups = role.getGroups();
            Set<Integer> groupSet = new HashSet<Integer>();
            for (int i = 0; i < groups.length; i++) {
                groupSet.add(new Integer(groups[i].getId()));
            }
            Set<Integer> addGroupSet = new HashSet<Integer>();
            for (int i = 0; i < groupIds_.length; i++) {
                if (groupSet.contains(groupIds_[i])) {
                    groupSet.remove(groupIds_[i]);
                    continue;
                }
                addGroupSet.add(groupIds_[i]);
            }

            for (Iterator<Integer> itr = addGroupSet.iterator(); itr.hasNext();) {
                Integer groupId = itr.next();
                Group group = pageAlfr.getPage(Group.class, groupId.intValue());
                if (group == null) {
                    continue;
                }
                role.giveRoleTo(group);
            }
            for (Iterator<Integer> itr = groupSet.iterator(); itr.hasNext();) {
                Integer groupId = itr.next();
                Group group = pageAlfr.getPage(Group.class, groupId.intValue());
                if (group == null) {
                    continue;
                }
                role.depriveRoleFrom(group);
            }
        }
        setNotes(new Notes().add(new Note("app.note.editMemberOfRole.succeed")));
        return true;
    }


    private String render()
    {
        enableTab(RoleTab.NAME_MEMBEROFROLE);
        enableLocationBar(true);
        prepareMembers();

        return "/" + Role.TYPE + "/edit-memberOfRole.html";
    }


    private void prepareMembers()
    {
        Role role = (Role)getPage();

        int heimId = getCurrentHeimId();
        User[] users = (User[])getPageAlfr().getPages(heimId,
            new PageCondition().setType(User.TYPE));
        Group[] groups = (Group[])getPageAlfr().getPages(getCurrentHeimId(),
            new PageCondition().setType(Group.TYPE));
        if (heimId != PathId.HEIM_MIDGARD) {
            List<User> userList = new ArrayList<User>();
            userList.addAll(Arrays.asList((User[])getPageAlfr().getPage(
                Page.ID_USERS).getChildren(
                new PageCondition().setType(User.TYPE))));
            userList.addAll(Arrays.asList(users));
            users = userList.toArray(new User[0]);

            List<Group> groupList = new ArrayList<Group>();
            groupList.addAll(Arrays.asList((Group[])getPageAlfr().getPage(
                Page.ID_GROUPS).getChildren(
                new PageCondition().setType(Group.TYPE))));
            groupList.addAll(Arrays.asList(groups));
            groups = groupList.toArray(new Group[0]);
        }

        List<UserOfRoleRow> userOfRoleRowList = new ArrayList<UserOfRoleRow>(
            users.length);
        for (int i = 0; i < users.length; i++) {
            userOfRoleRowList.add(new UserOfRoleRow(role, users[i]));
        }
        users_ = userOfRoleRowList.toArray(new UserOfRoleRow[0]);

        List<GroupOfRoleRow> groupOfRoleRowlist = new ArrayList<GroupOfRoleRow>(
            groups.length);
        for (int i = 0; i < groups.length; i++) {
            //            if (groups[i].getId() == Page.ID_ALL_GROUP) {
            //                continue;
            //            }
            groupOfRoleRowlist.add(new GroupOfRoleRow(role, groups[i]));
        }
        groups_ = groupOfRoleRowlist.toArray(new GroupOfRoleRow[0]);
    }


    /*
     * for framework / presentation tier
     */

    /*
     * for framework
     */

    public void setUserIds(Integer[] userIds)
    {
        userIds_ = userIds;
    }


    public void setGroupIds(Integer[] groupIds)
    {
        groupIds_ = groupIds;
    }


    /*
     * for presentation tier
     */

    public UserOfRoleRow[] getUsers()
    {
        return users_;
    }


    public GroupOfRoleRow[] getGroups()
    {
        return groups_;
    }
}
