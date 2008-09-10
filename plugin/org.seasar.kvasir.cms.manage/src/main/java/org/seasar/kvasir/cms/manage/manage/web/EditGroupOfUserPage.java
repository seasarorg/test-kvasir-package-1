package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.seasar.kvasir.cms.manage.manage.dto.GroupOfUserRow;
import org.seasar.kvasir.cms.manage.tab.impl.UserTab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.User;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class EditGroupOfUserPage extends MainPanePage
{
    /*
     * set by framework
     */

    private Integer[] groupIds_ = new Integer[0];

    /*
     * for presentation tier
     */

    private GroupOfUserRow[] groups_;


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
                "app.error.editGroupOfUser.notUser", getPathname())));
            return "/error.html";
        }
        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            if (updateGroupOfUser()) {
                return getRedirection("/edit-groupOfUser.do" + getPathname());
            }
        }

        return render();
    }


    /*
     * private scope methods
     */

    private boolean updateGroupOfUser()
    {
        User user = (User)getPage();

        Group[] groups = user.getGroups();
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

        PageAlfr pageAlfr = getPageAlfr();
        for (Iterator<Integer> itr = addGroupSet.iterator(); itr.hasNext();) {
            Integer groupId = itr.next();
            Group group = pageAlfr.getPage(Group.class, groupId.intValue());
            if (group == null) {
                continue;
            }
            group.add(user);
        }
        for (Iterator<Integer> itr = groupSet.iterator(); itr.hasNext();) {
            Integer groupId = itr.next();
            if (groupId.intValue() == Page.ID_ALL_GROUP) {
                continue;
            }
            Group group = pageAlfr.getPage(Group.class, groupId.intValue());
            if (group == null) {
                continue;
            }
            group.remove(user);
        }
        setNotes(new Notes().add(new Note("app.note.editGroupOfUser.succeed")));
        return true;
    }


    private String render()
    {
        enableTab(UserTab.NAME_GROUPOFUSER);
        enableLocationBar(true);
        prepareGroups();

        return "/" + User.TYPE + "/edit-groupOfUser.html";
    }


    private void prepareGroups()
    {
        User user = (User)getPage();

        int heimId = getCurrentHeimId();
        Group[] groups = (Group[])getPageAlfr().getPages(heimId,
            new PageCondition().setType(Group.TYPE));
        if (heimId != PathId.HEIM_MIDGARD) {
            List<Group> groupList = new ArrayList<Group>();
            groupList.addAll(Arrays.asList((Group[])getPageAlfr().getPage(
                Page.ID_GROUPS).getChildren(
                new PageCondition().setType(Group.TYPE))));
            groupList.addAll(Arrays.asList(groups));
            groups = groupList.toArray(new Group[0]);
        }
        List<GroupOfUserRow> list = new ArrayList<GroupOfUserRow>(groups.length);
        for (int i = 0; i < groups.length; i++) {
            if (groups[i].getId() == Page.ID_ALL_GROUP) {
                continue;
            }
            list.add(new GroupOfUserRow(user, groups[i]));
        }
        groups_ = list.toArray(new GroupOfUserRow[0]);
    }


    /*
     * for framework / presentation tier
     */

    /*
     * for framework
     */

    public void setGroupIds(Integer[] groupIds)
    {
        groupIds_ = groupIds;
    }


    /*
     * for presentation tier
     */

    public GroupOfUserRow[] getGroups()
    {
        return groups_;
    }
}
