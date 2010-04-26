package org.seasar.kvasir.cms.toolbox.toolbox.dto;

import java.text.DateFormat;
import java.util.Locale;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.search.SearchResult;
import org.seasar.kvasir.cms.util.PresentationUtils;


/**
 * 検索結果を画面に表示するためのクラスです。
 * 
 * @author YOKOTA Takehiko
 */
public class SearchResultDto
{
    private SearchResult searchResult_;

    private String iconURL_;

    private String size_;

    private String modifyDate_;

    private String title_;

    private String url_;

    private float score_;

    private String summary_;


    public SearchResultDto(SearchResult searchResult, Locale locale)
    {
        searchResult_ = searchResult;
        Page page = searchResult_.getPage();
        iconURL_ = PresentationUtils.getIconURL(page, "");
        size_ = ((searchResult.getSize() + 500) / 1000) + "k";
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL, locale);
        modifyDate_ = df.format(page.getModifyDate());
        title_ = searchResult.getTitle();
        url_ = searchResult.getURL();
        score_ = searchResult.getScore();
        summary_ = searchResult.getSummary();
    }


    public Page getPage()
    {
        return searchResult_.getPage();
    }


    public String getIconURL()
    {
        return iconURL_;
    }


    public void setIconURL(String iconURL)
    {
        iconURL_ = iconURL;
    }


    public String getTitle()
    {
        return title_;
    }


    public void setTitle(String title)
    {
        title_ = title;
    }


    public String getURL()
    {
        return url_;
    }


    public void setURL(String url)
    {
        url_ = url;
    }


    public float getScore()
    {
        return score_;
    }


    public void setScore(float score)
    {
        score_ = score;
    }


    public String getSummary()
    {
        return summary_;
    }


    public void setSummary(String summary)
    {
        summary_ = summary;
    }


    public String getSize()
    {
        return size_;
    }


    public void setSize(String size)
    {
        size_ = size;
    }


    public String getModifyDate()
    {
        return modifyDate_;
    }


    public void setModifyDate(String modifyDate)
    {
        modifyDate_ = modifyDate;
    }
}
