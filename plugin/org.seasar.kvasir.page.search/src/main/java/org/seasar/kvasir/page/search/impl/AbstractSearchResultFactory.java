package org.seasar.kvasir.page.search.impl;

import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.page.search.RawSearchResults;
import org.seasar.kvasir.page.search.SearchQuery;
import org.seasar.kvasir.page.search.SearchResult;
import org.seasar.kvasir.page.search.SearchResultFactory;


abstract public class AbstractSearchResultFactory
    implements SearchResultFactory
{
    protected KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    /*
     * abstract methods
     */

    abstract public SearchResult[] getSearchResults(SearchQuery query,
        Object rawResult);


    /*
     * SearchResultFactory
     */

    public SearchResult next(SearchQuery query, RawSearchResults result)
    {
        Boolean eor = (Boolean)result.getAttribute("eor");
        if (eor != null && eor.booleanValue()) {
            return null;
        }

        SearchResult[] srs
            = (SearchResult[])result.getAttribute("searchResults");
        int[] idx;
        if (srs == null) {
            srs = getSearchResults(query, result.getRawResult());
            result.setAttribute("searchResults", srs);
            idx = new int[]{ 0 };
            result.setAttribute("lastIndex", idx);
        } else {
            idx = (int[])result.getAttribute("lastIndex");
            idx[0]++;
        }

        if (idx[0] < srs.length) {
            return srs[idx[0]];
        } else {
            result.setAttribute("eor", Boolean.TRUE);
            return null;
        }
    }
}
