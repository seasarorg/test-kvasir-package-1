package org.seasar.kvasir.page.search.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.search.SearchResult;


/**
 * @author YOKOTA Takehiko
 */
public class SearchResultImpl
    implements SearchResult
{
    private Page page_ = null;

    private String title_ = "";

    private String url_ = "";

    private float score_ = 0;

    private long size_ = 0;

    private String summary_ = "";

    private String variant_ = Page.VARIANT_DEFAULT;

    private Map<String, String> propMap_ = new HashMap<String, String>();


    /*
     * constructors
     */

    /*
     * public scope methods
     */

    public void setPage(Page page)
    {
        page_ = page;
    }


    public void setTitle(String name)
    {
        title_ = name;
    }


    public void setURL(String url)
    {
        url_ = url;
    }


    public void setScore(float score)
    {
        score_ = score;
    }


    public void setSize(long size)
    {
        size_ = size;
    }


    public void setSummary(String summary)
    {
        summary_ = summary;
    }


    public void setVariant(String variant)
    {
        variant_ = variant;
    }


    public Iterator<String> propertyNameIterator()
    {
        return propMap_.keySet().iterator();
    }


    public void setProperty(String key, String value)
    {
        if (value != null) {
            propMap_.put(key, value);
        } else {
            propMap_.remove(key);
        }
    }


    public void removeProperty(String key)
    {
        propMap_.remove(key);
    }


    /*
     * SearchResult
     */

    public Object clone()
    {
        try {
            SearchResultImpl sr = (SearchResultImpl)super.clone();
            sr.propMap_ = new HashMap<String, String>(propMap_);
            return sr;
        } catch (CloneNotSupportedException ex) {
            throw new RuntimeException(ex);
        }
    }


    public Page getPage()
    {
        return page_;
    }


    public String getTitle()
    {
        return title_;
    }


    public String getURL()
    {
        return url_;
    }


    public float getScore()
    {
        return score_;
    }


    public long getSize()
    {
        return size_;
    }


    public String getSummary()
    {
        return summary_;
    }


    public String getVariant()
    {
        return variant_;
    }


    public String getProperty(String key)
    {
        String value = (String)propMap_.get(key);
        if (value == null) {
            value = "";
        }
        return value;
    }


    /*
     * Object
     */

    public String toString()
    {
        return new StringBuffer().append("page=").append(page_).append(
            ", title=").append(title_).append(", URL=").append(url_).append(
            ", score=").append(score_).append(", summary=").append(summary_)
            .append(", variant=").append(variant_).append(", propertyMap=")
            .append(propMap_).toString();
    }
}
