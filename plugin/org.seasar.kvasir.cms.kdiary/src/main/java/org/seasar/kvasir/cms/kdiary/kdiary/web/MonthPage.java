package org.seasar.kvasir.cms.kdiary.kdiary.web;

import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.COMMENTBODY_SHORTLENGTH;
import static org.seasar.kvasir.cms.kdiary.kdiary.Globals.LIMIT_INFINITY;

import org.seasar.kvasir.cms.kdiary.kdiary.Globals;
import org.seasar.kvasir.cms.kdiary.kdiary.dto.EntryDto;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.condition.Order;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.util.PropertyUtils;


public class MonthPage extends MonthPageBase
{
    @Override
    public void _render()
    {
        super._render();

        Page monthDir = getPage();
        if (monthDir == null) {
            entries_ = new EntryDto[0];
        } else {
            entries_ = getMonthEntries(monthDir);
        }
    }


    EntryDto[] getMonthEntries(Page monthDir)
    {
        int commentLimit = PropertyUtils.valueOf(
            getProperty(Globals.PROP_COMMENTLIMIT), LIMIT_INFINITY);

        Page[] pages = monthDir.getChildren(new PageCondition()
            .setIncludeConcealed(false).setOrder(
                new Order(PageCondition.FIELD_NAME)));
        EntryDto[] entries = new EntryDto[pages.length];
        for (int i = 0; i < entries.length; i++) {
            entries[i] = toEntry(pages[i], commentLimit,
                COMMENTBODY_SHORTLENGTH);
        }
        return entries;
    }
}
