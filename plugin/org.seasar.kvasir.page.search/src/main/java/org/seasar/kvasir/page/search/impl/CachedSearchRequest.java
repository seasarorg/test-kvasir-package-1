package org.seasar.kvasir.page.search.impl;

import java.util.HashMap;
import java.util.Map;

import org.seasar.kvasir.page.search.ParseException;
import org.seasar.kvasir.page.search.SearchQuery;
import org.seasar.kvasir.page.search.SearchRequest;
import org.seasar.kvasir.page.search.SearchResult;


/**
 * <code>net.skirnir.kvasir.search.CachedSearchEngine</code>
 * のためのSearchResultの実装クラスです。
 *
 * @author YOKOTA Takehiko
 */
public class CachedSearchRequest
    implements SearchRequest
{
    private CachedSearchSystem  system_;

    private SearchQuery         query_;

    private SearchResult[]      results_;

    private Map<String, Object> attribute_ = new HashMap<String, Object>();


    public CachedSearchRequest(CachedSearchSystem system)
    {
        system_ = system;
    }


    /*
     * SearchRequest
     */

    public SearchResult[] search(SearchQuery query)
        throws ParseException
    {
        return search(query, SearchQuery.OFFSET_FIRST, SearchQuery.LENGTH_ALL);
    }


    public SearchResult[] search(SearchQuery query, int offset, int length)
        throws ParseException
    {
        if (query_ == null || !query_.equals(query)) {
            results_ = system_.search(query);
            query_ = query;
            query_.freeze();
        }

        int len = results_.length - offset;
        if (len <= 0) {
            return new SearchResult[0];
        }

        if (length != SearchQuery.LENGTH_ALL) {
            if (len > length) {
                len = length;
            }
        }

        SearchResult[] results = new SearchResult[len];
        for (int i = 0; i < len; i++) {
            results[i] = (SearchResult)results_[offset + i].clone();
        }
        return results;
    }


    public int getResultsCount(SearchQuery query)
        throws ParseException
    {
        if (query_ == null || !query_.equals(query)) {
            results_ = system_.search(query);
            query_ = query;
            query_.freeze();
        }

        return results_.length;
    }


    public Object getAttribute(String name)
    {
        return attribute_.get(name);
    }


    public void setAttribute(String name, Object value)
    {
        attribute_.put(name, value);
    }


    public void removeAttribute(String name)
    {
        attribute_.remove(name);
    }
}
