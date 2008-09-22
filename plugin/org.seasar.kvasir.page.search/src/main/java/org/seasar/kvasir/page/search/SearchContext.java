package org.seasar.kvasir.page.search;

/**
 * 検索の状態に関するインタフェースです。
 * <p>一連の検索要求を実行するために用いられます。</p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface SearchContext
{
    /**
     * このコンテキストにセットされているクエリオブジェクトを返します。
     * <p>クエリオブジェクトがセットされていない場合はnullを返します。
     * </p>
     * 
     * @return クエリオブジェクト。
     */
    SearchQuery getQuery();


    /**
     * 検索クエリをコンテキストに設定します。
     * 
     * @param query 検索クエリ。nullを指定してはいけません。
     * @throws ParseException 検索クエリのパースに失敗した場合。
     */
    void setQuery(SearchQuery query)
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


    PositionRecorder getPositionRecorder();
}
