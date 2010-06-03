package org.seasar.kvasir.cms;

import java.util.Locale;

import org.seasar.kvasir.page.Page;


/**
 * Pageに対するリクエストに関する情報を表すインタフェースです。
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


    PageDispatch setThat(PageDispatch that);


    /**
     * このリクエストに対応するHeimのルートページを返します。
     *
     * @return ルートページ。
     */
    Page getRootPage();


    /**
     * このリクエストに対応するパス名を返します。
     * <p>ディレクトリへのアクセスであってもパス名の末尾には「/」は付与されません。
     * </p>
     * 
     * @return パス名。
     */
    String getPathname();
}
