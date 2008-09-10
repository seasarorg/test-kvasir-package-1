package org.seasar.kvasir.page.search.lucene.impl;

import java.util.StringTokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.seasar.kvasir.page.search.ParseException;
import org.seasar.kvasir.page.search.QueryStringParser;
import org.seasar.kvasir.page.search.lucene.DocumentCreator;
import org.seasar.kvasir.util.StringUtils;


/**
 * シンプルなクエリ文字列のパーサクラスです。
 * <p>{@link LuceneSearchSystem}クラス専用です。</p>
 *
 * @author YOKOTA Takehiko
 */
public class LuceneSimpleQueryStringParser
    implements QueryStringParser
{
    private LuceneSearchSystem system_;


    /*
     * QueryStringParser
     */

    public Object parse(String queryString, String option)
        throws ParseException
    {
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(queryString, " ");
        String op = "";
        while (st.hasMoreTokens()) {
            String tkn = st.nextToken();
            sb.append(op);
            sb.append(StringUtils.quoteString(tkn, '"'));
            op = " && ";
        }

        Analyzer analyzer = system_.newAnalyzer();
        try {
            return MultiFieldQueryParser.parse(sb.toString(), new String[] {
                DocumentCreator.FIELD_BODY, DocumentCreator.FIELD_DESCRIPTION,
                DocumentCreator.FIELD_LABEL }, analyzer);
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
