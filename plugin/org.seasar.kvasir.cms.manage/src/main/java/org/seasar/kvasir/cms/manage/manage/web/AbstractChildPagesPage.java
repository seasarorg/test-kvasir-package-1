package org.seasar.kvasir.cms.manage.manage.web;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;

import org.seasar.kvasir.page.Page;


abstract public class AbstractChildPagesPage extends MainPanePage
{
    /*
     * set by framework
     */

    private String[] names_;


    /*
     * for presentation tier
     */

    abstract protected void processPage(Page page);


    /*
     * public scope methods
     */

    public final Object do_execute()
    {
        if (names_ == null || names_.length == 0) {
            setNotes(new Notes().add(new Note(
                "app.note.childPages.namesIsEmpty")));
        } else {
            Page page = getPage();
            if (canParent(page) && names_ != null) {
                for (int i = 0; i < names_.length; i++) {
                    Page target = page.getChild(names_[i]);
                    if (target != null) {
                        processPage(target);
                    }
                }
            }
            postProcess();
        }
        return getRedirection("/list-page.do" + getPathname());
    }


    /*
     * protected scope methods
     */

    protected void postProcess()
    {
    }


    /*
     * for framework / presentation tier
     */

    public final void setNames(String[] names)
    {
        names_ = names;
    }
}
