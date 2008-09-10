package org.seasar.kvasir.cms.manage.manage.web;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.gard.PageGard;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class NewApplicationPage extends MainPanePage
{
    /*
     * for framework
     */

    private PagePlugin pagePlugin_;

    /*
     * for presentation tier
     */

    private String name_ = "";

    private String applicationDescription_;

    private String applicationName_;

    private String description_;

    private String gardId_;

    private String label_;

    private String installPathname_;


    /*
     * public scope methods
     */

    public Object do_execute()
    {
        PageGard pageGard = null;
        if (gardId_ != null) {
            pageGard = pagePlugin_.getPageGard(gardId_);
        }
        if (pageGard == null) {
            setNotes(new Notes().add(new Note(
                "app.error.newApplication.pageGardNotFound", gardId_)));
            return getRedirection("/list-page.do" + getPathname());
        }

        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            if (installPageGard(pageGard)) {
                setNotes(new Notes().add(new Note(
                    "app.note.newApplication.succeed")));
                updateMenu();
                return getRedirection("/list-page.do" + getPathname());
            }
        }

        return render(pageGard);
    }


    private Object render(PageGard pageGard)
    {
        Plugin<?> plugin = pageGard.getPlugin();
        String displayName = pageGard.getDisplayName();
        if (displayName == null) {
            displayName = pageGard.getId();
        }
        applicationName_ = plugin.resolveString(displayName, getLocale());
        applicationDescription_ = plugin.resolveString(pageGard
            .getDescription(), getLocale());
        installPathname_ = getInstallDestinationPathname(pageGard);
        if (installPathname_ != null && installPathname_.length() == 0) {
            installPathname_ = getResource("app.label.root");
        }

        return "/new-application.html";
    }


    boolean installPageGard(PageGard pageGard)
    {
        PageAlfr pageAlfr = pagePlugin_.getPageAlfr();
        String pathname = getInstallDestinationPathname(pageGard);
        int heimId = getCurrentHeimId();
        Page page;
        String name;
        if (pathname != null) {
            if (pathname.length() == 0) {
                page = pageAlfr.getRootPage(heimId);
                name = null;
            } else {
                page = pageAlfr.getPage(heimId, PageUtils
                    .getParentPathname(pathname));
                name = PageUtils.getName(pathname);
            }
        } else {
            page = getPage();
            name = name_;
        }

        return installPageGard(pageGard, page, name);
    }


    boolean installPageGard(PageGard pageGard, Page page, String name)
    {
        if (page == null) {
            setNotes(new Notes().add(new Note(
                "app.error.parentPageDoesNotExist")));
            return false;
        }

        if (!page.isNode()) {
            setNotes(new Notes().add(new Note("app.error.parentPageIsNotNode",
                page.getPathname())));
            return false;
        }

        if (name != null && name.length() == 0) {
            setNotes(new Notes().add(new Note("app.error.nameIsEmpty")));
            return false;
        }

        try {
            if (name == null) {
                pagePlugin_.install(page, pageGard.getFullId());
            } else {
                Page installed = pagePlugin_.install(page, name, pageGard
                    .getFullId());

                PropertyAbility prop = installed
                    .getAbility(PropertyAbility.class);
                if (label_ != null && label_.trim().length() > 0) {
                    prop.setProperty(PropertyAbility.PROP_LABEL, label_);
                }
                if (description_ != null) {
                    prop.setProperty(PropertyAbility.PROP_DESCRIPTION,
                        description_);
                }
            }
            return true;
        } catch (DuplicatePageException ex) {
            setNotes(new Notes().add(new Note("app.error.pageAlreadyExists",
                (name != null ? name : page.getPathname()))));
            return false;
        }
    }


    /*
     * for framework
     */

    String getInstallDestinationPathname(PageGard pageGard)
    {
        String pathname = null;
        if (pageGard.isSingleton()) {
            pathname = pageGard.getDefaultPathname();
            if (pathname == null) {
                pathname = Page.PATHNAME_PLUGINS + "/" + pageGard.getId();
            }
        }

        return pathname;
    }


    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    /*
     * for presentation tier
     */

    public String getName()
    {
        return name_;
    }


    public void setName(String name)
    {
        name_ = name.trim();
    }


    public String getApplicationDescription()
    {
        return applicationDescription_;
    }


    public String getApplicationName()
    {
        return applicationName_;
    }


    public void setDescription(String description)
    {
        description_ = description;
    }


    public String getGardId()
    {
        return gardId_;
    }


    public void setGardId(String gardId)
    {
        gardId_ = gardId;
    }


    public void setLabel(String label)
    {
        label_ = label;
    }


    public String getInstallPathname()
    {
        return installPathname_;
    }
}
