package org.seasar.kvasir.cms.manage.manage.web;

import org.seasar.cms.ymir.extension.annotation.In;
import org.seasar.cms.ymir.extension.annotation.Out;
import org.seasar.cms.ymir.scope.impl.SessionScope;
import org.seasar.kvasir.cms.manage.dto.Clipboard;
import org.seasar.kvasir.cms.manage.manage.dto.ClipboardImpl;
import org.seasar.kvasir.page.Page;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class ClipboardPage extends MainPanePage
{
    /*
     * for presentation tier
     */

    private Clipboard clipboard_;

    private String[] names_;


    /*
     * clipboard.do
     */

    public Object do_execute()
    {
        return render();
    }


    private Object render()
    {
        return "/clipboard.html";
    }


    /*
     * clipboard.memory.do
     */

    public Object do_memory()
    {
        do {
            if (names_ == null || names_.length == 0) {
                setNotes(new Notes().add(new Note(
                    "app.note.childPages.namesIsEmpty")));
                break;
            }

            Page page = getPage();
            if (page == null) {
                setNotes(new Notes().add(new Note("app.error.pageNotFound",
                    getPathname())));
                break;
            }

            Clipboard clipboard = getClipboard();
            for (int i = 0; i < names_.length; i++) {
                Page child = page.getChild(names_[i]);
                if (child != null) {
                    clipboard.addEntry(new Clipboard.PageEntry(child));
                }
            }

            setNotes(new Notes().add(new Note(
                "app.note.clipboard.addedToClipboard")));
        } while (false);

        return getRedirection("/list-page.do" + getPathname());
    }


    /*
     * clipboard.delete.do
     */

    public Object do_delete()
    {
        do {
            if (names_ == null || names_.length == 0) {
                setNotes(new Notes().add(new Note(
                    "app.error.clipboard.namesIsEmpty")));
                break;
            }

            Clipboard clipboard = getClipboard();
            for (int i = 0; i < names_.length; i++) {
                clipboard.removeEntry(names_[i]);
            }

            setNotes(new Notes().add(new Note(
                "app.note.clipboard.deletedFromClipboard")));
        } while (false);

        return do_execute();
    }


    /*
     * clipboard.delete.do
     */

    public Object do_clear()
    {
        do {
            getClipboard().clearEntries();

            setNotes(new Notes().add(new Note(
                "app.note.clipboard.clearedClipboard")));
        } while (false);

        return do_execute();
    }


    /*
     * for framework
     */

    /*
     * for presentation tier
     */

    @Out(SessionScope.class)
    public Clipboard getClipboard()
    {
        if (clipboard_ == null) {
            clipboard_ = new ClipboardImpl();
        }
        return clipboard_;
    }


    @In(SessionScope.class)
    public void setClipboard(Clipboard clipboard)
    {
        clipboard_ = clipboard;
    }


    public void setNames(String[] names)
    {
        names_ = names;
    }
}
