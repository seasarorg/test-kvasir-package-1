package org.seasar.kvasir.cms.manage.manage.web;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;

import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.LoopDetectedException;
import org.seasar.kvasir.page.Page;


public class MovePagePage extends AbstractClipboardOperatingPage
{
    protected void processPage(Page page, Notes notes)
    {
        String name = page.getName();
        try {
            page.moveTo(getPage(), name);
        } catch (DuplicatePageException ex) {
            notes.add(new Note("app.error.movePage.nameAlreadyExists",
                name));
        } catch (LoopDetectedException ex) {
            notes.add(new Note("app.error.movePage.loopDetected", page
                .getPathname()));
        }
    }


    protected void postProcess(Notes notes)
    {
        if (notes.size() == 0) {
            notes.add(new Note("app.note.movePage.succeed"));
        }
    }
}
