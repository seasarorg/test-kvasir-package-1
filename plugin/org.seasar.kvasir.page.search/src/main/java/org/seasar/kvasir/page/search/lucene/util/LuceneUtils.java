package org.seasar.kvasir.page.search.lucene.util;

import java.io.IOException;

import org.apache.lucene.search.Searcher;


public class LuceneUtils
{
    private LuceneUtils()
    {
    }


    public static void close(Searcher searcher)
    {
        if (searcher != null) {
            try {
                searcher.close();
            } catch (IOException ignore) {
            }
        }
    }
}
