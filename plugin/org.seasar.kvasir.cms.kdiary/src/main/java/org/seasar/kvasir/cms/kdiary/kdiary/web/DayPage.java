package org.seasar.kvasir.cms.kdiary.kdiary.web;

import java.sql.Timestamp;

import org.seasar.cms.ymir.extension.constraint.Required;
import org.seasar.kvasir.cms.kdiary.kdiary.KdiaryDate;
import org.seasar.kvasir.cms.kdiary.kdiary.dao.Comment;
import org.seasar.kvasir.page.Page;


public class DayPage extends DayPageBase
{
    @Override
    public void _render()
    {
        super._render();

        if (pageExists()) {
            entry_ = toEntry(getPage());
        } else {
            String path = getYmirRequest().getPath();
            message_ = getLocalizedMessage("message.articleNotFound",
                new KdiaryDate(path.substring(9, 15) + path.substring(16, 18))
                    .format(getJavaDateFormat()));
        }
    }


    @Override
    public void _get()
    {
    }


    @Required( { "name", "body" })
    public String _post_comment()
    {
        if (pageExists()) {
            commentDao_.insert(new Comment(body_, name_, new Timestamp(System
                .currentTimeMillis()), mail_, getPage().getId()));
        }

        return "redirect:" + getYmirRequest().getPath();
    }


    boolean pageExists()
    {
        Page page = getPage();
        return (page != null && !page.isConcealed());
    }
}
