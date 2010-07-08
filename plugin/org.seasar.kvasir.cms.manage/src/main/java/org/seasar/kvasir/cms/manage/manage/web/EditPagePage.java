package org.seasar.kvasir.cms.manage.manage.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletContext;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;
import net.skirnir.freyja.render.html.OptionTag;

import org.seasar.cms.ymir.Request;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.cms.manage.PageService;
import org.seasar.kvasir.cms.manage.tab.impl.PageTab;
import org.seasar.kvasir.cms.publish.PublishPlugin;
import org.seasar.kvasir.cms.publish.setting.RepositoryElement;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.GenericPageType;
import org.seasar.kvasir.page.type.PageType;
import org.seasar.kvasir.page.type.User;


public class EditPagePage extends MainPanePage
{
    private OptionTag[] ownerUserCandidates_;

    @Binding(bindingType = BindingType.MUST)
    protected ServletContext servletContext;

    @Binding(bindingType = BindingType.MUST)
    protected PublishPlugin publishPlugin_;

    private List<RepositoryElement> repositories_;

    private String repositoryId_;


    /*
     * set by framework
     */

    /*
     * for presentation tier
     */

    public void setRepositoryId(String repositoryId)
    {
        repositoryId_ = repositoryId;
    }


    /*
     * public scope methods
     */

    @SuppressWarnings("unchecked")
    public Object do_execute()
    {
        Page page = getPage();
        PageType pageType = getPagePlugin().getPageType(page.getType());

        if (Request.METHOD_POST.equalsIgnoreCase(getYmirRequest().getMethod())) {
            Notes notes = getPageService().updatePage(page,
                getYmirRequest().getParameterMap());
            if (notes == null) {
                page.touch();
                setNotes(new Notes().add(new Note("app.note.editPage.succeed")));
                updateMenu();
                return getRedirection("/edit-page.do" + getPathname());
            } else {
                setNotes(notes);
            }
        }

        return render(pageType);
    }


    public Object do_publish()
    {
        if (Request.METHOD_POST.equalsIgnoreCase(getYmirRequest().getMethod())) {
            if (repositoryId_ == null || repositoryId_.length() == 0) {
                setNotes(new Notes().add(new Note(
                    "app.note.editPage.publish.required.repositoryId")));
            } else {
                publishPlugin_.publish(getPage(), true, repositoryId_, true);

                setNotes(new Notes().add(new Note(
                    "app.note.editPage.publish.succeed")));
            }
        }
        return getRedirection("/edit-page.do" + getPathname());
    }


    /*
     * private scope methods
     */

    private String render(PageType pageType)
    {
        prepareOwnerUser();
        enableTab(PageTab.NAME_EDIT);
        enableLocationBar(true);
        String path = "/" + pageType.getId() + "/edit-page.html";
        URL url;
        try {
            url = servletContext.getResource(path);
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Logic error", ex);
        }
        if (url == null) {
            path = "/" + GenericPageType.ID + "/edit-page.html";
        }

        repositories_ = publishPlugin_.getRepositories();

        return path;
    }


    void prepareOwnerUser()
    {
        List<OptionTag> ownerUserCandidateList = new ArrayList<OptionTag>();

        User ownerUser = getPage().getOwnerUser();
        int ownerUserId = PageService.USERID_UNKNOWN;
        if (ownerUser == null) {
            ownerUserCandidateList.add(new OptionTag(String
                .valueOf(PageService.USERID_UNKNOWN),
                getResource("app.label.unknown")).setSelected(true));
        } else {
            ownerUserId = ownerUser.getId();
        }

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

        for (int i = 0; i < users.length; i++) {
            StringBuilder sb = new StringBuilder();
            String label = users[i].getAbility(PropertyAbility.class)
                .getProperty(PropertyAbility.PROP_LABEL, getLocale());
            if (heimId != PathId.HEIM_MIDGARD
                && users[i].getHeimId() == PathId.HEIM_MIDGARD) {
                sb.append('/');
            }
            sb.append(users[i].getPathname()).append(" (");
            if (label != null) {
                sb.append(label);
            }
            sb.append(')');
            ownerUserCandidateList.add(new OptionTag(String.valueOf(users[i]
                .getId()), sb.toString())
                .setSelected(users[i].getId() == ownerUserId));
        }
        ownerUserCandidates_ = ownerUserCandidateList.toArray(new OptionTag[0]);
    }


    /*
     * for framework / presentation tier
     */

    public OptionTag[] getOwnerUserCandidates()
    {
        return ownerUserCandidates_;
    }


    public List<RepositoryElement> getRepositories()
    {
        return repositories_;
    }
}
