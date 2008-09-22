package org.seasar.kvasir.page.search.lucene.impl;

import org.apache.lucene.search.Query;
import org.seasar.kvasir.page.search.ParseException;
import org.seasar.kvasir.page.search.QueryStringParser;
import org.seasar.kvasir.page.search.SearchQuery;
import org.seasar.kvasir.page.search.impl.SearchContextImpl;


class LuceneSearchContext extends SearchContextImpl
{
    static final int UNKNOWN = -1;

    private QueryStringParser queryStringParser_;

    private Query luceneQuery_;

    private int resultCount_ = -1;


    LuceneSearchContext(QueryStringParser queryStringParser)
    {
        queryStringParser_ = queryStringParser;
    }


    @Override
    public void setQuery(SearchQuery query)
        throws ParseException
    {
        super.setQuery(query);

        luceneQuery_ = (Query)queryStringParser_.parse(query.getQueryString(),
            query.getOption());
    }


    public Query getLuceneQuery()
    {
        return luceneQuery_;
    }


    public int getResultCount()
    {
        return resultCount_;
    }


    public void setResultCount(int resultCount)
    {
        resultCount_ = resultCount;
    }
}
