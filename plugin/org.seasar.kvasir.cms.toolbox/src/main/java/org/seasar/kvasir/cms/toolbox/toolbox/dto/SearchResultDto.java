package org.seasar.kvasir.cms.toolbox.toolbox.dto;

import java.text.DateFormat;
import java.util.Locale;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.search.SearchResult;
import org.seasar.kvasir.cms.util.PresentationUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class SearchResultDto
{
    private SearchResult searchResult_;
    private String iconURL_;
    private String size_;
    private String modifyDate_;


    public SearchResultDto(SearchResult searchResult, Locale locale)
    {
        searchResult_ = searchResult;
        Page page = searchResult_.getPage();
        iconURL_ = PresentationUtils.getIconURL(page, "");
        size_ = ((searchResult.getSize() + 500) / 1000) + "k";
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, locale);
        modifyDate_ = df.format(page.getModifyDate());
    }


    public String getIconURL()
    {
        return iconURL_;
    }


    public String getTitle()
    {
        return searchResult_.getTitle();
    }


    public String getURL()
    {
        return searchResult_.getURL();
    }


    public float getScore()
    {
        return searchResult_.getScore();
    }


    public String getSummary()
    {
        return searchResult_.getSummary();
    }


    public String getSize()
    {
        return size_;
    }


    public String getModifyDate()
    {
        return modifyDate_;
    }
}
