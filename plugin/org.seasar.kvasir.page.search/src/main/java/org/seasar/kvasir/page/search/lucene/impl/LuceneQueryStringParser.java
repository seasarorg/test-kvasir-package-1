package org.seasar.kvasir.page.search.lucene.impl;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.seasar.kvasir.page.search.ParseException;
import org.seasar.kvasir.page.search.QueryStringParser;


/**
 * LuceneのQueryParserのアダプタクラスです。
 * <p>{@link LuceneSearchEngine}クラス専用です。</p>
 *
 * @author YOKOTA Takehiko
 */
public class LuceneQueryStringParser implements QueryStringParser
{
    private LuceneSearchSystem system_;


    /*
     * QueryStringParser
     */

    public Object parse(String queryString, String option)
    throws ParseException
    {
        Analyzer analyzer = system_.newAnalyzer();
        try {
            return QueryParser.parse(queryString, "body", analyzer);
        } catch (org.apache.lucene.queryParser.ParseException ex) {
            throw new ParseException(ex);
        }
    }


    /*
     * for framework
     */

    public void setSearchSystem(LuceneSearchSystem system)
    {
        system_ = system;
    }
}
