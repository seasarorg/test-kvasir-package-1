package org.seasar.kvasir.page.ability.content.mock;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.ContentMold;
import org.seasar.kvasir.page.ability.mock.MockPageAbility;


public class MockContentAbility extends MockPageAbility
    implements ContentAbility
{
    private Date modifyDate_;

    private Map<Locale, Content> contentByLocaleMap_ = new HashMap<Locale, Content>();


    public MockContentAbility(Page page)
    {
        super(page);
    }


    public Date getModifyDate()
    {
        return modifyDate_;
    }


    public void setModifyDate(Date modifyDate)
    {
        modifyDate_ = modifyDate;
    }


    public int getEarliestRevisionNumber(String variant)
    {
        return 0;
    }


    public int getLatestRevisionNumber(String variant)
    {
        return 0;
    }


    public Content getLatestContent(String variant)
    {
        return null;
    }


    public Content getLatestContent(Locale locale)
    {
        return contentByLocaleMap_.get(locale);
    }


    public MockContentAbility setLatestContent(Locale locale, Content content)
    {
        contentByLocaleMap_.put(locale, content);
        return this;
    }


    public Content getContent(Locale locale, Date date)
    {
        return null;
    }


    public Content getContent(String variant, int revisionNumber)
    {
        return null;
    }


    public void setContent(String variant, ContentMold mold)
    {
    }


    public void updateContent(String variant, ContentMold mold)
    {
    }


    public void removeContentsBefore(String variant, int revisionNumber)
    {
    }


    public void clearContents(String variant)
    {
    }


    public void clearAllContents()
    {
    }
}
