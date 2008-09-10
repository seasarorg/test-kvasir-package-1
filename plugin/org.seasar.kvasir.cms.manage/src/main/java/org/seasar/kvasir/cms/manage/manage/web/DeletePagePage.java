package org.seasar.kvasir.cms.manage.manage.web;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;

import org.seasar.kvasir.page.Page;


public class DeletePagePage extends AbstractChildPagesPage
{
    protected void processPage(Page page)
    {
        page.delete();
    }


    protected void postProcess()
    {
        setNotes(new Notes().add(new Note("app.note.deletePage.succeed")));
        updateMenu();
    }
}
