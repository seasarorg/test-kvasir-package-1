package org.seasar.kvasir.page.ability.content.impl;

import java.util.Date;
import java.util.Locale;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.AbstractPageAbility;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.ContentAbilityAlfr;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentMold;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ContentAbilityImpl extends AbstractPageAbility
    implements ContentAbility
{
    private ContentAbilityAlfr alfr_;


    public ContentAbilityImpl(ContentAbilityAlfr alfr, Page page)
    {
        super(alfr, page);
        alfr_ = alfr;
    }


    public Date getModifyDate()
    {
        return alfr_.getModifyDate(page_);
    }


    public int getEarliestRevisionNumber(String variant)
    {
        return alfr_.getEarliestRevisionNumber(page_, variant);
    }


    public int getLatestRevisionNumber(String variant)
    {
        return alfr_.getLatestRevisionNumber(page_, variant);
    }


    public Content getLatestContent(String variant)
    {
        return alfr_.getLatestContent(page_, variant);
    }


    public Content getLatestContent(Locale locale)
    {
        return alfr_.getLatestContent(page_, locale);
    }


    public Content getContent(Locale locale, Date date)
    {
        return alfr_.getContent(page_, locale, date);
    }


    public Content getContent(String variant, int revisionNumber)
    {
        return alfr_.getContent(page_, variant, revisionNumber);
    }


    public void setContent(String variant, ContentMold mold)
    {
        alfr_.setContent(page_, variant, mold);
    }


    public void updateContent(String variant, ContentMold mold)
    {
        alfr_.updateContent(page_, variant, mold);
    }


    public void removeContentsBefore(String variant, int revisionNumber)
    {
        alfr_.removeContentsBefore(page_, variant, revisionNumber);
    }


    public void clearContents(String variant)
    {
        alfr_.clearContents(page_, variant);
    }


    public void clearAllContents()
    {
        alfr_.clearAllContents(page_);
    }
}
