package org.seasar.kvasir.page.search.impl;

import java.util.HashMap;
import java.util.Map;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.search.ParseException;
import org.seasar.kvasir.page.search.PositionRecorder;
import org.seasar.kvasir.page.search.SearchContext;
import org.seasar.kvasir.page.search.SearchQuery;


public class SearchContextImpl
    implements SearchContext
{
    private Map<String, Object> attribute_ = new HashMap<String, Object>();

    private SearchQuery query_;

    private PositionRecorder recorder_;

    private String[] topPathnames;

    private String[] topPathnames2;


    public SearchQuery getQuery()
    {
        return query_;
    }


    public void setQuery(SearchQuery query)
        throws ParseException
    {
        if (query_ != null) {
            throw new IllegalStateException("Query has been already set");
        }

        query_ = query;

        Page[] topPages = query_.getTopPages();
        int[] topHeimIds = null;
        topPathnames = null;
        topPathnames2 = null;
        if (topPages.length > 0) {
            topHeimIds = new int[topPages.length];
            topPathnames = new String[topPages.length];
            topPathnames2 = new String[topPages.length];
            for (int i = 0; i < topPages.length; i++) {
                String pathname = topPages[i].getPathname();
                topHeimIds[i] = topPages[i].getHeimId();
                topPathnames[i] = pathname;
                topPathnames2[i] = pathname + "/";
            }
        }
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


    public PositionRecorder getPositionRecorder()
    {
        if (recorder_ == null) {
            recorder_ = new PositionRecorderImpl();
        } else {
            recorder_.rewind();
        }
        return recorder_;
    }
}
