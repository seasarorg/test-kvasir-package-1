package org.seasar.kvasir.cms.manage.manage.web;

import org.seasar.cms.ymir.Request;
import org.seasar.kvasir.cms.manage.PageService;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.type.PageType;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;

public class NewPagePage extends MainPanePage {
    /*
     * set by framwork
     */

    private String name_;

    private boolean createAndEdit_;

    private String type_;

    /*
     * public scope methods
     */

    public Object do_execute() {
        PageType pageType = getPagePlugin().getPageType(
                (type_ != null && type_.trim().length() > 0) ? type_
                        : Page.TYPE);

        if (Request.METHOD_POST.equals(getYmirRequest().getMethod())) {
            Page created = createPage(pageType);
            if (created != null) {
                created.touch();
                setNotes(new Notes().add(new Note("app.note.newPage.succeed")));
                updateMenu();
                if (createAndEdit_) {
                    return getRedirection("/edit-page.do"
                            + created.getPathname());
                } else {
                    return getRedirection("/list-page.do" + getPathname());
                }
            }
        }

        return "/" + pageType.getId() + "/new-page.html";
    }

    /*
     * private scope methods
     */

    @SuppressWarnings("unchecked")
    private Page createPage(PageType pageType) {
        Page page = getPage();
        if (!canParent(page)) {
            setNotes(new Notes().add(new Note("app.error.parentPageIsNotNode",
                    page.getPathname())));
            return null;
        }

        if (name_ == null || name_.trim().length() == 0) {
            setNotes(new Notes().add(new Note("app.error.nameIsEmpty")));
            return null;
        }

        if (!getPagePlugin().isValidName(name_)) {
            setNotes(new Notes().add(new Note("app.error.nameIsInvalid")));
            return null;
        }

        PageService pageService = getPlugin().getPageService(pageType.getId());
        try {
            return pageService.createPage(getPage(), pageType.newPageMold(),
                    getYmirRequest().getParameterMap());
        } catch (DuplicatePageException ex) {
            setNotes(new Notes().add(new Note("app.error.pageAlreadyExists",
                    name_)));
            return null;
        }
    }

    /*
     * for framework / presentation tier
     */

    public String getName() {
        return name_;
    }

    public void setName(String name) {
        name_ = name;
    }

    public boolean isCreateAndEdit() {
        return createAndEdit_;
    }

    public void setCreateAndEdit(boolean createAndEdit) {
        createAndEdit_ = createAndEdit;
    }

    public String getType() {
        return type_;
    }

    public void setType(String type) {
        type_ = type;
    }
}
