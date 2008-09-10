package org.seasar.kvasir.page.search;



/**
 * 検索システムから返された生の検索結果から
 * SearchResultオブジェクトを生成するためのファクトリを表すインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface SearchResultFactory
{
    /**
     * 検索システムから返された生の検索結果から
     * SearchResultオブジェクトを生成します。
     * <p>このメソッドは検索クエリのmaxCountで指定された値に関わらず、
     * 生の検索結果を全て取り出した上でSearchResultオブジェクトを生成します。
     * </p>
     *
     * @param query 検索クエリ。
     * @param rawResult 生の検索結果。
     * @return SearchResultオブジェクト。
     */
    SearchResult[] getSearchResults(SearchQuery query, Object rawResult);

    /**
     * 検索システムから返された検索結果から1つ結果を取り出して
     * SearchResultオブジェクトを生成して返します。
     * <p>結果を全て取り出してしまった後にこのメソッドを呼び出すと
     * nullを返します。</p>
     *
     * @param query 検索クエリ。
     * @param result 検索結果。
     * @return SearchResultオブジェクト。
     */
    SearchResult next(SearchQuery query, RawSearchResults result);
}
