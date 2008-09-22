package org.seasar.kvasir.page.search;

import java.io.IOException;


/**
 * 検索システムから返された生の検索結果を
 * 共通の検索結果情報に変換するためのインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface SearchResultHandler
{
    /**
     * 検索結果の総数を返します。
     * 
     * @return 検索結果の総数。
     */
    int getLength();


    /**
     * 検索結果がまだ存在するかどうかを返します。
     *
     * @return 検索結果がまだ存在するかどうか。
     */
    boolean hasNext();


    /**
     * 検索システムから返された検索結果から1つ結果を取り出して
     * SearchResultオブジェクトを生成して返します。
     * <p>結果を全て取り出してしまった後にこのメソッドを呼び出すと
     * nullを返します。</p>
     *
     * @return SearchResultオブジェクト。
     */
    SearchResult next()
        throws IOException;


    /**
     * 検索結果リソースを開放します。
     * <p>検索結果の取得が終わったらこのメソッドを必ず呼び出して下さい。
     * </p>
     */
    void close();
}
