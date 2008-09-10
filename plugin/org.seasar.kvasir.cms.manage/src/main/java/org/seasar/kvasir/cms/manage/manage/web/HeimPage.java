package org.seasar.kvasir.cms.manage.manage.web;

import org.seasar.kvasir.cms.pop.PopPlugin;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class HeimPage extends MainPanePage
{
    private PopPlugin popPlugin_;


    public void setPopPlugin(PopPlugin popPlugin)
    {
        popPlugin_ = popPlugin;
    }


    public Object do_execute()
    {
        return render();
    }


    private Object render()
    {
        return "/heim.html";
    }


    public Object do_updatePanes()
    {
        popPlugin_.updatePanes(getCurrentHeimId());

        setNotes(new Notes().add(new Note("app.note.heim.updatePanes.succeed")));

        return getRedirection("/heim.do");
    }
}
