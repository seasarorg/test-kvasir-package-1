package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.List;

import org.seasar.cms.ymir.extension.annotation.In;
import org.seasar.cms.ymir.extension.annotation.Out;
import org.seasar.cms.ymir.scope.impl.SessionScope;
import org.seasar.kvasir.cms.manage.dto.Clipboard;
import org.seasar.kvasir.page.Page;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


abstract public class AbstractClipboardOperatingPage extends MainPanePage
{
    /*
     * for framework
     */

    private Clipboard clipboard_;


    abstract protected void processPage(Page page, Notes notes);


    abstract protected void postProcess(Notes notes);


    public Object do_execute()
    {
        Notes notes = new Notes();
        do {
            Page[] pages = popPagesFromClipboard();
            if (pages.length == 0) {
                notes
                    .add(new Note("app.error.clipboardOperating.entryIsEmpty"));
                break;
            }

            Page targetPage = getPage();
            if (targetPage == null) {
                notes.add(new Note("app.error.pageNotFound", getPathname()));
                break;
            } else if (!canParent(targetPage)) {
                notes.add(new Note("app.error.parentPageIsNotNode",
                    getPathname()));
                break;
            }

            for (int i = 0; i < pages.length; i++) {
                processPage(pages[i], notes);
            }
        } while (false);

        postProcess(notes);

        setNotes(notes);
        updateMenu();
        return getRedirection("/list-page.do" + getPathname());
    }


    /*
     * for framework
     */

    protected Page[] popPagesFromClipboard()
    {
        if (clipboard_ == null) {
            return new Page[0];
        }
        Clipboard.Entry[] entries = clipboard_.getEntries();
        List<Page> list = new ArrayList<Page>();
        for (int i = 0; i < entries.length; i++) {
            if (!(entries[i] instanceof Clipboard.PageEntry)) {
                continue;
            }
            String name = entries[i].getName();
            int id = Integer.parseInt(name);
            Page page = getPageAlfr().getPage(id);
            if (page != null) {
                list.add(page);
            }
            clipboard_.removeEntry(name);
        }
        return (Page[])list.toArray(new Page[0]);
    }


    @Out(SessionScope.class)
    public Clipboard getClipboard()
    {
        return clipboard_;
    }


    @In(SessionScope.class)
    public void setClipboard(Clipboard clipboard)
    {
        clipboard_ = clipboard;
    }
}
