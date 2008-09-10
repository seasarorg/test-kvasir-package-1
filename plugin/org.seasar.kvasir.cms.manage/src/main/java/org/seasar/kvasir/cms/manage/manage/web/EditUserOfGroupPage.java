package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.seasar.kvasir.cms.manage.manage.dto.UserOfGroupRow;
import org.seasar.kvasir.cms.manage.tab.impl.GroupTab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.User;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class EditUserOfGroupPage extends MainPanePage
{
    /*
     * set by framework
     */

    private Integer[] userIds_ = new Integer[0];

    /*
     * for presentation tier
     */

    private UserOfGroupRow[] users_;


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
                "app.error.editUserOfGroup.notGroup", getPathname())));
            return "/error.html";
        }
        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            if (updateUserOfGroup()) {
                return getRedirection("/edit-userOfGroup.do" + getPathname());
            }
        }

        return render();
    }


    /*
     * private scope methods
     */

    private boolean updateUserOfGroup()
    {
        Group group = (Group)getPage();
        User[] users = group.getUsers();
        Set<Integer> userSet = new HashSet<Integer>();
        for (int i = 0; i < users.length; i++) {
            userSet.add(new Integer(users[i].getId()));
        }
        Set<Integer> addUserSet = new HashSet<Integer>();
        for (int i = 0; i < userIds_.length; i++) {
            if (userSet.contains(userIds_[i])) {
                userSet.remove(userIds_[i]);
                continue;
            }
            addUserSet.add(userIds_[i]);
        }

        PageAlfr pageAlfr = getPageAlfr();
        for (Iterator<Integer> itr = addUserSet.iterator(); itr.hasNext();) {
            Integer userId = itr.next();
            User user = pageAlfr.getPage(User.class, userId.intValue());
            if (user == null) {
                continue;
            }
            group.add(user);
        }
        for (Iterator<Integer> itr = userSet.iterator(); itr.hasNext();) {
            Integer userId = itr.next();
            User user = pageAlfr.getPage(User.class, userId.intValue());
            if (user == null) {
                continue;
            }
            group.remove(user);
        }
        setNotes(new Notes().add(new Note("app.note.editUserOfGroup.succeed")));
        return true;
    }


    private String render()
    {
        enableTab(GroupTab.NAME_USEROFGROUP);
        enableLocationBar(true);
        prepareUsers();

        return "/" + Group.TYPE + "/edit-userOfGroup.html";
    }


    private void prepareUsers()
    {
        Group group = (Group)getPage();

        int heimId = getCurrentHeimId();
        User[] users = (User[])getPageAlfr().getPages(heimId,
            new PageCondition().setType(User.TYPE));
        if (heimId != PathId.HEIM_MIDGARD) {
            List<User> userList = new ArrayList<User>();
            userList.addAll(Arrays.asList((User[])getPageAlfr().getPage(
                Page.ID_USERS).getChildren(
                new PageCondition().setType(User.TYPE))));
            userList.addAll(Arrays.asList(users));
            users = userList.toArray(new User[0]);
        }
        List<UserOfGroupRow> list = new ArrayList<UserOfGroupRow>(users.length);
        for (int i = 0; i < users.length; i++) {
            list.add(new UserOfGroupRow(group, users[i]));
        }
        users_ = list.toArray(new UserOfGroupRow[0]);
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


    /*
     * for presentation tier
     */

    public UserOfGroupRow[] getUsers()
    {
        return users_;
    }
}
