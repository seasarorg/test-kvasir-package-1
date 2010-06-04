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


    QueryStringParser getQueryStringParser();


    SearchContext newContext();


    /**
     * 検索結果を返します。
     * <p><code>context</code>に指定されたクエリにマッチする検索結果を返します。
     * </p>
     * <p>マッチする全ての結果を返します。</p>
     * <p>このメソッドを呼び出す前に{@link SearchContext#setQuery(SearchQuery)}を用いて
     * コンテキストに検索クエリをセットしておく必要があります。
     * </p>
     *
     * @param context 検索コンテキスト。
     * @return 検索結果。
     */
    SearchResult[] search(SearchContext context);


    /**
     * 検索結果を返します。
     * <p><code>context</code>に指定されたクエリにマッチする検索結果を返します。
     * </p>
     * <p><code>offset</code>で指定した要素から
     * <code>length</code>で指定した個数分だけ返します。
     * （返される結果の個数は<code>length</code>より小さいこともあります。）
     * <p>このメソッドを呼び出す前に{@link SearchContext#setQuery(SearchQuery)}を用いて
     * コンテキストに検索クエリをセットしておく必要があります。
     * </p>
     *
     * @param context 検索コンテキスト。
     * @param offset 何番目の結果から返すか。0オリジンです。
     * @param length 最大何個の結果を返すか。
     * 個数の上限を指定したくない場合は{@link SearchQuery#LENGTH_ALL}
     * を指定して下さい。
     * @return 検索結果。
     */
    SearchResult[] search(SearchContext context, int offset, int length);


    /**
     * 検索結果の総数を返します。
     * <p><code>context</code>に指定されたクエリにマッチする検索結果の総数を返します。
     * </p>
     * <p>このメソッドが返す総数は検索システムが返す「生の」検索結果の総数です。
     * {@link #search(SearchContext)}が返す検索結果は
     * {@link SearchResultHandler}や{@link SearchResultFilter}によってフィルタされるため、
     * 通常{@link #search(SearchContext)}で取得できる全ての検索結果の個数は
     * このメソッドが返す総数と同じか少なくなります。
     * </p>
     * <p>このメソッドを呼び出す前に{@link SearchContext#setQuery(SearchQuery)}を用いて
     * コンテキストに検索クエリをセットしておく必要があります。
     * </p>
     *
     * @param context 検索コンテキスト。
     * @return 検索結果の総数。
     */
    int getResultsCount(SearchContext context);


    void addToIndex(Page[] pages);


    void removeFromIndex(Page[] pages);


    void removeFromIndex(int[] pageIds);


    void clearIndex();
}
