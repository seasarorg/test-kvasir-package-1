package org.seasar.kvasir.cms.manage.manage.web;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageNotFoundRuntimeException;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.condition.Order;
import org.seasar.kvasir.page.condition.PageCondition;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;
import net.skirnir.freyja.render.html.OptionTag;


public class SortPagePage extends MainPanePage
{
    private OptionTag[] current_;

    private Integer[] newer_;

    private String sortBy_;

    private boolean ascend_ = true;


    public void setSortBy(String sortBy)
    {
        sortBy_ = sortBy;
    }


    public void setAscend(boolean ascend)
    {
        ascend_ = ascend;
    }


    public Object do_execute()
    {
        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())
            && newer_ != null) {
            if (processSort()) {
                setNotes(new Notes().add(new Note("app.note.sortPage.succeed")));
                updateMenu();
            }
            return getRedirection("/list-page.do" + getPathname());
        }

        return render();
    }


    private boolean processSort()
    {
        final Page page = getPage();
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
            page.runWithLocking(new Processable<Object>() {
                public Object process()
                    throws ProcessableRuntimeException
                {
                    Page[] children = page.getChildren();
                    Map<Integer, Page> pageMap = new LinkedHashMap<Integer, Page>();
                    for (int i = 0; i < children.length; i++) {
                        pageMap.put(new Integer(children[i].getId()),
                            children[i]);
                    }
                    int orderNumber = 1;
                    for (int i = 0; i < newer_.length; i++) {
                        Integer idKey = newer_[i];
                        Page child = (Page)pageMap.get(idKey);
                        if (child != null) {
                            child.setOrderNumber(orderNumber++);
                            pageMap.remove(idKey);
                        }
                    }
                    for (Iterator<Page> itr = pageMap.values().iterator(); itr
                        .hasNext();) {
                        itr.next().setOrderNumber(orderNumber++);
                    }

                    return null;
                }
            });
        } catch (PageNotFoundRuntimeException ex) {
            setNotes(new Notes().add(new Note("app.error.pageNotFound",
                getPathname())));
            return false;
        }
        return true;
    }


    private Object render()
    {
        enableLocationBar(true);
        prepareChildren();

        return "/sort-page.html";
    }


    private void prepareChildren()
    {
        PageCondition pageCondition = new PageCondition();
        if (sortBy_ != null) {
            pageCondition.setOrder(new Order(sortBy_, ascend_));
        }
        Page[] children = getPage().getChildren(pageCondition);
        OptionTag[] optionTags = new OptionTag[children.length];
        for (int i = 0; i < children.length; i++) {
            optionTags[i] = new OptionTag(String.valueOf(children[i].getId()),
                children[i].getName());
        }
        current_ = optionTags;
    }


    public OptionTag[] getCurrent()
    {
        return current_;
    }


    public void setNewer(Integer[] newer)
    {
        newer_ = newer;
    }
}
