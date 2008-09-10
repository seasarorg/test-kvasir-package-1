package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.Locale;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;


public class PageRow extends SimplePageRow
{
    private String size_;

    private String modifyDate_;


    public PageRow(Page page, Locale locale, boolean even)
    {
        super(page, locale, even);
        size_ = prepareSize(page, locale);
        modifyDate_ = page.getModifyDateString();
    }


    public String getModifyDate()
    {
        return modifyDate_;
    }


    public String getSize()
    {
        return size_;
    }


    /*
     * private scope methods
     */

    private String prepareSize(Page page, Locale locale)
    {
        Content content = page.getAbility(ContentAbility.class)
            .getLatestContent(locale);
        long size = (content != null ? content.getBodyResource().getSize() : 0);
        if (size > 1024) {
            size = (size + 512) / 1024;
            if (size > 1024) {
                size = (size + 512) / 1024;
                return size + "MB";
            } else {
                return size + "KB";
            }
        } else {
            return size + "Bytes";
        }
    }
}
