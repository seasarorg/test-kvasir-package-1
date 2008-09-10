package org.seasar.kvasir.cms.manage.manage.web;

import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.LoopDetectedException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PagePlugin;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class CopyPagePage extends AbstractClipboardOperatingPage
{
    /*
     * for framework
     */

    private PagePlugin pagePlugin_;


    protected void processPage(Page page, Notes notes)
    {
        String name = page.getName();
        try {
            pagePlugin_.copy(getPage(), name, page, true);
        } catch (DuplicatePageException ex) {
            throw new RuntimeException("Can't happen!", ex);
        } catch (LoopDetectedException ex) {
            notes.add(new Note("app.error.copyPage.loopDetected", page
                .getPathname()));
        }
    }


    protected void postProcess(Notes notes)
    {
        if (notes.size() == 0) {
            notes.add(new Note("app.note.copyPage.succeed"));
        }
    }


    /*
     * for framework
     */

    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }
}
