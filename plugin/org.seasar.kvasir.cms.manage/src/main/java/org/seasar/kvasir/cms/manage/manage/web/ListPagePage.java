package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.List;

import org.seasar.cms.ymir.extension.annotation.In;
import org.seasar.cms.ymir.scope.impl.SessionScope;
import org.seasar.kvasir.cms.manage.dto.Clipboard;
import org.seasar.kvasir.cms.manage.manage.dto.PageIndicator;
import org.seasar.kvasir.cms.manage.manage.dto.PageRow;
import org.seasar.kvasir.cms.manage.menu.NewItemMenuEntry;
import org.seasar.kvasir.cms.manage.tab.impl.ParentPageTab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.condition.Order;
import org.seasar.kvasir.page.condition.PageCondition;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;
import net.skirnir.freyja.render.html.OptgroupTag;
import net.skirnir.freyja.render.html.OptionTag;


public class ListPagePage extends MainPanePage
{
    /*
     * set by framework
     */

    private int start_ = 0;

    private String sortBy_ = null;

    private boolean ascend_ = true;

    private Clipboard clipboard_;

    /*
     * for presentation tier
     */

    private boolean changedAscend_;

    private boolean clipboardNotEmpty_;

    private int unit_ = 20;

    private OptgroupTag[] newItemMenuEntries_;

    private PageIndicator pageIndicator_;

    private PageRow[] pages_;


    /*
     * inner fields
     */

    /*
     * public scope methods
     */

    public String do_execute()
    {
        Page page = getPage();
        if (page == null) {
            setNotes(new Notes().add(new Note("app.error.pageNotFound",
                getPathname())));
            return "/error.html";
        }

        enableTab(ParentPageTab.NAME_LIST);
        enableLocationBar(true);

        changedAscend_ = ascend_ ? false : true;
        newItemMenuEntries_ = prepareNewItemMenuEntries();
        pageIndicator_ = new PageIndicator(page.getChildrenCount(), unit_,
            start_).setAscend(ascend_).setSortBy(sortBy_);
        pages_ = preparePageRows();
        clipboardNotEmpty_ = (clipboard_ != null && clipboard_
            .getEntriesCount() > 0);

        return "/list-page.html";
    }


    /*
     * private scpoe methods
     */

    private OptgroupTag[] prepareNewItemMenuEntries()
    {
        Page page = getPage();

        String[] categories = getPlugin().getCategoriesOfNewItemMenu();
        OptgroupTag[] optgroupTags = new OptgroupTag[categories.length];
        for (int i = 0; i < categories.length; i++) {
            NewItemMenuEntry[] entries = getPlugin().getNewItemMenuEntries(
                categories[i]);
            List<OptionTag> list = new ArrayList<OptionTag>(entries.length);
            for (int j = 0; j < entries.length; j++) {
                if (!entries[j].isDisplayed(page)) {
                    continue;
                }
                String value = entries[j].getPath() + getPathname()
                    + entries[j].getParameter();
                String content = entries[j].getDisplayName(getLocale());
                list.add(new OptionTag(value, content));
            }
            optgroupTags[i] = new OptgroupTag(getPlugin().getProperty(
                "new-item-menu-entry.category." + categories[i]
                    + ".display-name", getLocale()), (OptionTag[])list
                .toArray(new OptionTag[0]));
        }
        return optgroupTags;
    }


    private PageRow[] preparePageRows()
    {
        Page page = getPage();

        PageCondition pc = new PageCondition().setOffset(start_).setLength(
            unit_);
        String sortBy;
        if (sortBy_ == null || sortBy_.length() == 0) {
            sortBy = "id";
        } else {
            sortBy = sortBy_;
        }
        if (!sortBy.equals("id") || !ascend_) {
            pc.setOrder(new Order(sortBy, ascend_));
        }
        Page[] pages = page.getChildren(pc);
        PageRow[] rows = new PageRow[pages.length];
        for (int i = 0; i < pages.length; i++) {
            rows[i] = new PageRow(pages[i], getPageRequest().getLocale(),
                (i % 2 == 0));
        }
        return rows;
    }


    /*
     * for framework
     */

    public void setStart(int start)
    {
        start_ = start;
    }


    public void setSortBy(String sortBy)
    {
        sortBy_ = sortBy;
    }


    public void setAscend(boolean ascend)
    {
        ascend_ = ascend;
    }


    @In(SessionScope.class)
    public void setClipboard(Clipboard clipboard)
    {
        clipboard_ = clipboard;
    }


    /*
     * for presentation tier
     */

    public int getStart()
    {
        return start_;
    }


    public String getSortBy()
    {
        return sortBy_;
    }


    public boolean isAscend()
    {
        return ascend_;
    }


    public boolean isChangedAscend()
    {
        return changedAscend_;
    }


    public OptgroupTag[] getNewItemMenuEntries()
    {
        return newItemMenuEntries_;
    }


    public PageIndicator getPageIndicator()
    {
        return pageIndicator_;
    }


    public PageRow[] getPages()
    {
        return pages_;
    }


    public boolean isClipboardNotEmpty()
    {
        return clipboardNotEmpty_;
    }
}
