package org.seasar.kvasir.page.search;



/**
 * 検索要求を表すインタフェースです。
 * <p>一連の検索要求を実行するために用いられます。</p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface SearchRequest
{
    /**
     * 指定されたクエリにマッチする検索結果を返します。
     * <p>マッチする全ての結果を返します。</p>
     *
     * @param query 検索クエリ。
     * @return 検索結果。
     * @exception ParseExceptoin
     * 検索クエリのパースエラーが発生した場合。
     */
    SearchResult[] search(SearchQuery query)
        throws ParseException;

    /**
     * 指定されたクエリにマッチする検索結果を返します。
     * <p><code>offset</code>で指定した要素から
     * <code>length</code>で指定した個数分だけ返します。
     * （返される結果の個数は<code>length</code>より小さいこともあります。）
     *
     * @param query 検索クエリ。
     * @param offset 何番目の結果から返すか。0オリジンです。
     * @param length 最大何個の結果を返すか。
     * 個数の上限を指定したくない場合は<code>LENGTH_ALL</code>
     * を指定して下さい。
     * @return 検索結果。
     * @exception ParseExceptoin
     * 検索クエリのパースエラーが発生した場合。
     */
    SearchResult[] search(SearchQuery query, int offset, int length)
        throws ParseException;

    /**
     * 指定されたクエリにマッチする検索結果の個数を返します。
     *
     * @param query 検索クエリ。
     * @return 検索結果の個数。
     * @exception ParseExceptoin
     * 検索クエリのパースエラーが発生した場合。
     */
    int getResultsCount(SearchQuery query)
        throws ParseException;

    /**
     * 指定された名前を持つ属性の値を返します。
     * <p>対応する属性が存在しない場合はnullを返します。</p>
     * <p>フレームワークが必要に応じて属性を設定したり参照したりしますので、
     * アプリケーション開発者がこのメソッドを使う場合は注意して下さい。</p>
     *
     * @param name 名前。
     * @return 属性値。
     */
    Object getAttribute(String name);

    /**
     * 指定された名前を持つ属性に指定された値を設定します。
     * <p>対応する属性が既に存在する場合は上書きされます。</p>
     * <p>フレームワークが必要に応じて属性を設定したり参照したりしますので、
     * アプリケーション開発者がこのメソッドを使う場合は注意して下さい。</p>
     *
     * @param name 名前。
     * @param value 値。
     */
    void setAttribute(String name, Object value);

    /**
     * 指定された名前を持つ属性を削除します。
     * <p>対応する属性が存在しない場合は何もしません。</p>
     * <p>フレームワークが必要に応じて属性を設定したり参照したりしますので、
     * アプリケーション開発者がこのメソッドを使う場合は注意して下さい。</p>
     *
     * @param name 名前。
     */
    void removeAttribute(String name);
}
