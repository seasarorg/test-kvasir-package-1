package org.seasar.kvasir.cms.kdiary.kdiary.web;

import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.COMMENTBODY_SHORTLENGTH;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.LIMIT_INFINITY;

import org.seasar.kvasir.cms.kdiary.kdiary.Globals;
import org.seasar.kvasir.cms.kdiary.kdiary.dto.EntryDto;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.condition.Order;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.util.PropertyUtils;


public class _RootPage extends _RootPageBase
{
    @Override
    public void _get()
    {
    }


    public void _render()
    {
        super._render();

        entries_ = getRecentEntries(getProperty("latestLimit", 5));
    }


    EntryDto[] getRecentEntries(int length)
    {
        int commentLimit = PropertyUtils.valueOf(
            getProperty(Globals.PROP_COMMENTLIMIT), LIMIT_INFINITY);

        Page[] pages = getArticleDirectory().getChildren(
            new PageCondition().setIncludeConcealed(false).setRecursive(true)
                .setType(Page.TYPE).setOrder(
                    new Order(PageCondition.FIELD_PATHNAME, false))
                .setLength(length));
        EntryDto[] entries = new EntryDto[pages.length];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = toEntry(pages[i], commentLimit,
                COMMENTBODY_SHORTLENGTH);
        }
        return entries;
    }
}
