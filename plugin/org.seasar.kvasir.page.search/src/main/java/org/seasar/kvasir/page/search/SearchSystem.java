package org.seasar.kvasir.page.search;

import org.seasar.kvasir.page.Page;


/**
 * 検索システムを表すインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface SearchSystem
{
    String PROP_INDEXED = "_framework.search.indexed";

    String getId();

    SearchResultFactory getSearchResultFactory();

    QueryStringParser getQueryStringParser();

    SearchRequest newSearchRequest();

    SearchResult[] search(SearchQuery query)
        throws ParseException;

    void addToIndex(Page[] pages);

    void removeFromIndex(Page[] pages);

    void removeFromIndex(int[] pageIds);

    void clearIndex();
}
