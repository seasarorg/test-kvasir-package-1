package org.seasar.kvasir.page.search.lucene.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.search.RawSearchResults;
import org.seasar.kvasir.page.search.SearchQuery;
import org.seasar.kvasir.page.search.SearchResult;
import org.seasar.kvasir.page.search.SearchResultFactory;
import org.seasar.kvasir.page.search.impl.SearchResultImpl;
import org.seasar.kvasir.page.search.lucene.DocumentCreator;
import org.seasar.kvasir.util.io.IORuntimeException;


public class LuceneSearchResultFactory
    implements SearchResultFactory
{
    private PageAlfr pageAlfr_;

    private KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    /*
     * SearchResultFactory
     */

    public SearchResult[] getSearchResults(SearchQuery query, Object rawResult)
    {
        RawSearchResults rsr = new RawSearchResults(rawResult);
        List<SearchResult> list = new ArrayList<SearchResult>(((Hits)rawResult)
            .length());
        SearchResult sr;
        while ((sr = next(query, rsr)) != null) {
            list.add(sr);
        }

        return list.toArray(new SearchResult[0]);
    }


    public SearchResult next(SearchQuery query, RawSearchResults result)
    {
        Boolean eor = (Boolean)result.getAttribute("eor");
        if (eor != null && eor.booleanValue()) {
            return null;
        }

        Hits hits = (Hits)result.getRawResult();
        try {
            int[] length = (int[])result.getAttribute("length");
            if (length == null) {
                length = new int[] { hits.length() };
                result.setAttribute("length", length);
                if (log_.isDebugEnabled()) {
                    log_.debug("HIT count: " + length[0]);
                }
            }

            int[] idx = (int[])result.getAttribute("lastIndex");
            if (idx == null) {
                idx = new int[] { 0 };
                result.setAttribute("lastIndex", idx);
            } else {
                idx[0]++;
            }

            for (; idx[0] < length[0]; idx[0]++) {
                Document doc = hits.doc(idx[0]);
                if (log_.isDebugEnabled()) {
                    log_.debug("[" + idx[0] + "] id=" + doc.get("id"));
                }
                String id = doc.get("id");
                if (id == null) {
                    continue;
                }
                Page page = pageAlfr_.getPage(Integer.parseInt(id));
                if (page == null) {
                    continue;
                }
                String pathname = page.getPathname();
                if (log_.isDebugEnabled()) {
                    log_.debug("[" + idx[0] + "] pathname=" + pathname);
                }

                SearchResultImpl sr = new SearchResultImpl();
                sr.setPage(page);
                long size = 0;
                try {
                    size = Long.parseLong(doc.get(DocumentCreator.FIELD_SIZE));
                } catch (Throwable t) {
                    ;
                }
                sr.setSize(size);
                String label = doc.get(DocumentCreator.FIELD_LABEL);
                if (label != null) {
                    sr.setTitle(label);
                }
                sr.setURL(pathname);
                sr.setScore(hits.score(idx[0]));
                String summary = doc.get(DocumentCreator.FIELD_SUMMARY);
                if (summary != null) {
                    sr.setSummary(summary);
                }
                String variant = doc.get(DocumentCreator.FIELD_VARIANT);
                if (variant != null) {
                    sr.setVariant(variant);
                }

                return sr;
            }

            result.setAttribute("eor", Boolean.TRUE);
            return null;
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
    }


    /*
     * for framework
     */

    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pageAlfr_ = pagePlugin.getPageAlfr();
    }
}
