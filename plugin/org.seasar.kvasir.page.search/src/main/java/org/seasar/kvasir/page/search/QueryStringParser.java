package org.seasar.kvasir.page.search;



/**
 * クエリ文字列から検索エンジン依存のクエリオブジェクトを構築するためのインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface QueryStringParser
{
    /**
     * 指定されたクエリ文字列から検索エンジン依存のクエリオブジェクトを構築します。
     *
     * @param queryString クエリ文字列。
     * @param option オプション。
     * @return 構築したクエリオブジェクト。
     * @exception ParseExcetion
     * クエリ文字列の形式が不正だった場合。
     */
    Object parse(String queryString, String option)
        throws ParseException;
}
