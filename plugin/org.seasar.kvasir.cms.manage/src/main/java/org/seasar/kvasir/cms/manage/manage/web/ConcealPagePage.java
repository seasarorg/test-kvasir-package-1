package org.seasar.kvasir.cms.manage.manage.web;

import java.util.Date;

import org.seasar.kvasir.page.Page;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class ConcealPagePage extends AbstractChildPagesPage
{
    /*
     * set by framework
     */

    private boolean value_;


    /*
     * for presentation tier
     */

    /*
     * AbstractChildPageBean
     */

    protected void processPage(Page page)
    {
        if (value_) {
            page.setRevealDate(Page.DATE_RAGNAROK);
        } else {
            page.setRevealDate(new Date());
        }
    }


    protected void postProcess()
    {
        setNotes(new Notes().add(new Note("app.note.concealPage." + value_
            + ".succeed")));
        updateMenu();
    }


    /*
     * for framework / presentation tier
     */

    public void setValue(boolean value)
    {
        value_ = value;
    }
}
