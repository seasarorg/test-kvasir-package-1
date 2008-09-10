package org.seasar.kvasir.cms;

import java.util.Locale;

import org.seasar.kvasir.page.Page;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageRequest
{
    /**
     * コンテキストパスを返します。
     *
     * @return コンテキストパス。
     */
    String getContextPath();


    /**
     * 現在のリクエストで指定されているロケールを返します。
     * <p>指定されていない場合はデフォルトのロケールを返します。</p>
     *
     * @return 現在のリクエストで指定されているロケール。
     */
    Locale getLocale();


    /**
     * 現在処理中のページプロセッサまたはページフィルタを活性化させる元となったディスパッチに関する情報を返します。
     * <p>例えば/path/to/pageにリクエストがあり、対応するページテンプレートから/path/to/include
     * という部分ページがインクルードされ、
     * その結果現在のページプロセッサまたはページフィルタが活性化して処理中になった場合、
     * 処理中のページプロセッサまたはページフィルタからこのメソッドを呼び出すと、
     * このメソッドが返すディスパッチ情報は/path/to/includeに関するものとなります。
     * </p>
     *
     * @return ディスパッチ情報。
     */
    PageDispatch getMy();


    PageDispatch setMy(PageDispatch my);


    /**
     * リクエストディスパッチに関する情報を返します。
     * <p>例えば/path/to/pageにリクエストがあり、対応するページテンプレートから/path/to/include
     * という部分ページがインクルードされた場合、
     * このメソッドが返すディスパッチ情報は/path/to/pageに関するものとなります。
     * </p>
     *
     * @return リクエストディスパッチ情報。
     */
    PageDispatch getThat();


    /**
     * サイトルートページを返します。
     * <p>ユーティリティメソッドです。</p>
     *
     * @return サイトルートページ。
     */
    Page getRootPage();
}
