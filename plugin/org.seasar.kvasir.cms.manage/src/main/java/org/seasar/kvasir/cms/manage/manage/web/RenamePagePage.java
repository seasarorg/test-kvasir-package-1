package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.LoopDetectedException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageNotFoundRuntimeException;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;


public class RenamePagePage extends MainPanePage
{
    private String[] names_;


    public Object do_execute()
    {
        if (names_ == null || names_.length == 0) {
            NamePair[] namePairs = getNamePairs();
            if (namePairs.length > 0) {
                if (processRename(namePairs)) {
                    setNotes(new Notes().add(new Note(
                        "app.note.renamePage.succeed")));
                    updateMenu();
                }
            } else {
                setNotes(new Notes().add(new Note(
                    "app.note.childPages.namesIsEmpty")));
            }
            return getRedirection("/list-page.do" + getPathname());
        }

        return render();
    }


    @SuppressWarnings("unchecked")
    private NamePair[] getNamePairs()
    {
        List<NamePair> list = new ArrayList<NamePair>();
        Map<String, String[]> param = getYmirRequest().getParameterMap();
        for (Iterator<Map.Entry<String, String[]>> itr = param.entrySet()
            .iterator(); itr.hasNext();) {
            Map.Entry<String, String[]> entry = itr.next();
            String key = entry.getKey();
            if (!key.startsWith(":")) {
                continue;
            }
            key = key.substring(1);
            list.add(new NamePair(key, ((String[])entry.getValue())[0]));
        }
        return (NamePair[])list.toArray(new NamePair[0]);
    }


    private boolean processRename(NamePair[] namePairs)
    {
        Page page = getPage();
        if (page == null) {
            setNotes(new Notes().add(new Note("app.error.pageNotFound",
                getPathname())));
            return false;
        } else if (!canParent(page)) {
            setNotes(new Notes().add(new Note("app.error.parentPageIsNotNode",
                getPathname())));
            return false;
        }

        try {
            for (int i = 0; i < namePairs.length; i++) {
                if (namePairs[i].current.equals(namePairs[i].newer)) {
                    continue;
                }
                Page child = page.getChild(namePairs[i].current);
                if (child != null) {
                    try {
                        child.moveTo(page, namePairs[i].newer);
                    } catch (DuplicatePageException ex) {
                        setNotes(new Notes().add(new Note(
                            "app.error.pageAlreadyExists", namePairs[i].newer)));
                        updateMenu();
                        return false;
                    } catch (LoopDetectedException ex) {
                        throw new RuntimeException("Can't happen!", ex);
                    }
                }
            }
            return true;
        } catch (PageNotFoundRuntimeException ex) {
            setNotes(new Notes().add(new Note("app.error.pageNotFound",
                getPathname())));
            return false;
        }
    }


    private Object render()
    {
        enableLocationBar(true);

        return "/rename-page.html";
    }


    /*
     * inner classes
     */

    private static class NamePair
    {
        public String current;

        public String newer;


        public NamePair(String current, String newer)
        {
            this.current = current;
            this.newer = newer;
        }
    }


    /*
     * for framework
     */

    /*
     * for presentation tier
     */

    public String[] getNames()
    {
        return names_;
    }


    public void setNames(String[] names)
    {
        names_ = names;
    }
}
