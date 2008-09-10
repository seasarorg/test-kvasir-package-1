package org.seasar.kvasir.cms.ymir;

/**
 * どのパスへのアクセスをどのアプリケーションに処理させるかの設定情報を表すインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface ApplicationMapping
{
    /**
     * 実際にリクエストを処理するアプリケーションオブジェクトを返します。
     * 
     * @return アプリケーションオブジェクト。
     */
    YmirApplication getForwardedApplication();


    /**
     * パスを返します。
     * <p><code>isRegex()</code>がtrueの場合はパスの正規表現を表します。</p>
     * 
     * @return パス。
     */
    String getPath();


    /**
     * パスを正規表現として解釈するかどうかを返します。
     * 
     * @return パスを正規表現として解釈するかどうか。
     */
    boolean isRegex();


    /**
     * 指定されたパス文字列がこのオブジェクトが持つパス情報とマッチするかどうかを返します。
     * 
     * @param path パス文字列。
     * @return マッチするかどうか。
     */
    boolean isMatched(String path);
}
