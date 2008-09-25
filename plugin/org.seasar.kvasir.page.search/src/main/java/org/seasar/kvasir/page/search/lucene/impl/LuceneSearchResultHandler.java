package org.seasar.kvasir.page.search.lucene.impl;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.Hits;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.search.SearchResult;
import org.seasar.kvasir.page.search.SearchResultHandler;
import org.seasar.kvasir.page.search.impl.SearchResultImpl;
import org.seasar.kvasir.page.search.lucene.DocumentCreator;


class LuceneSearchResultHandler
    implements SearchResultHandler
{
    private Hits hits_;

    private int idx_;

    private PageAlfr pageAlfr_;

    private KvasirLog log_ = KvasirLogFactory
        .getLog(LuceneSearchResultHandler.class);


    LuceneSearchResultHandler(Hits hits, PageAlfr pageAlfr)
    {
        hits_ = hits;
        idx_ = 0;
        pageAlfr_ = pageAlfr;
    }


    public int getLength()
    {
        return hits_.length();
    }


    public boolean hasNext()
    {
        return idx_ < getLength();
    }


    public SearchResult next()
        throws IOException
    {
        Document doc = hits_.doc(idx_);
        float score = hits_.score(idx_);
        idx_++;

        SearchResultImpl result = new SearchResultImpl();
        do {
            if (log_.isDebugEnabled()) {
                log_.debug("id=" + doc.get("id"));
            }
            String id = doc.get("id");
            if (id == null) {
                break;
            }
            Page page = pageAlfr_.getPage(Integer.parseInt(id));
            if (page == null) {
                break;
            }

            String pathname = page.getPathname();
            if (log_.isDebugEnabled()) {
                log_.debug("pathname=" + pathname);
            }

            result.setPage(page);
            long size = 0;
            try {
                size = Long.parseLong(doc.get(DocumentCreator.FIELD_SIZE));
            } catch (Throwable t) {
                ;
            }
            result.setSize(size);
            String label = doc.get(DocumentCreator.FIELD_LABEL);
            if (label != null) {
                result.setTitle(label);
            }
            result.setURL(pathname);
            result.setScore(score);
            String summary = doc.get(DocumentCreator.FIELD_SUMMARY);
            if (summary != null) {
                result.setSummary(summary);
            }
            String variant = doc.get(DocumentCreator.FIELD_VARIANT);
            if (variant != null) {
                result.setVariant(variant);
            }
        } while (false);

        return result;
    }


    public void skip(int count)
        throws IOException
    {
        idx_ += count;
        if (idx_ > getLength()) {
            idx_ = getLength();
        }
    }


    public void close()
    {
    }
}
