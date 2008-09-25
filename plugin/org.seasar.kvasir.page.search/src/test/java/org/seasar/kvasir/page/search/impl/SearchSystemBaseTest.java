package org.seasar.kvasir.page.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.search.SearchContext;
import org.seasar.kvasir.page.search.SearchResult;
import org.seasar.kvasir.page.search.SearchResultHandler;


public class SearchSystemBaseTest extends TestCase
{
    private SearchSystemBase target_ = new SearchSystemBase() {
        private Set<String> visibleSet_ = new HashSet<String>();
        {
            visibleSet_.add("0");
            visibleSet_.add("2");
            visibleSet_.add("3");
            visibleSet_.add("5");
            visibleSet_.add("6");
            visibleSet_.add("8");
        }


        public void addToIndex(Page[] pages)
        {
        }


        public void clearIndex()
        {
        }


        public int getResultsCount(SearchContext context)
        {
            return 0;
        }


        public SearchContext newContext()
        {
            return null;
        }


        public void removeFromIndex(Page[] pages)
        {
        }


        public void removeFromIndex(int[] pageIds)
        {
        }


        public SearchResult[] search(SearchContext context, int offset,
            int length)
        {
            return new SearchResult[0];
        }


        @Override
        protected boolean isVisible(SearchContext context, SearchResult result,
            Set<Page> pageSet)
        {
            return visibleSet_.contains(result.getTitle());
        }
    };


    public void testCreateSearchResults()
        throws Exception
    {
        SearchContext context = new SearchContextImpl();
        SearchResult[] actual = target_.createSearchResults(context,
            newHandler(), 0, 5);

        int idx = 0;
        assertEquals(5, actual.length);
        assertEquals("0", actual[idx++].getTitle());
        assertEquals("2", actual[idx++].getTitle());
        assertEquals("3", actual[idx++].getTitle());
        assertEquals("5", actual[idx++].getTitle());
        assertEquals("6", actual[idx++].getTitle());

        actual = target_.createSearchResults(context, newHandler(), 5, 5);

        idx = 0;
        assertEquals(1, actual.length);
        assertEquals("8", actual[idx++].getTitle());
    }


    private SearchResultHandler newHandler()
    {
        return new SearchResultHandler() {
            private List<SearchResult> list_ = new ArrayList<SearchResult>();

            private int idx_ = 0;

            {
                SearchResultImpl result = new SearchResultImpl();
                result.setTitle("0");
                list_.add(result);
                result = new SearchResultImpl();
                result.setTitle("1");
                list_.add(result);
                result = new SearchResultImpl();
                result.setTitle("2");
                list_.add(result);
                result = new SearchResultImpl();
                result.setTitle("3");
                list_.add(result);
                result = new SearchResultImpl();
                result.setTitle("4");
                list_.add(result);
                result = new SearchResultImpl();
                result.setTitle("5");
                list_.add(result);
                result = new SearchResultImpl();
                result.setTitle("6");
                list_.add(result);
                result = new SearchResultImpl();
                result.setTitle("7");
                list_.add(result);
                result = new SearchResultImpl();
                result.setTitle("8");
                list_.add(result);
            }


            public void close()
            {
            }


            public int getLength()
            {
                return list_.size();
            }


            public boolean hasNext()
            {
                return idx_ < getLength();
            }


            public SearchResult next()
                throws IOException
            {
                return list_.get(idx_++);
            }


            public void skip(int count)
                throws IOException
            {
                idx_ += count;
            }
        };
    }
}
