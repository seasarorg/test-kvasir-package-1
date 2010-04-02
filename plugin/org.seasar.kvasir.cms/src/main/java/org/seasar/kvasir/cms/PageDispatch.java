package org.seasar.kvasir.cms;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.Page;


/**
 * Pageに対するディスパッチ（内部的なリクエスト）に関する情報を表すインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageDispatch
{
    /**
     * 処理対象のパスを返します。
     *
     * @return 処理対象のパス。
     */
    String getPathname();


    /**
     * 処理対象のページを返します。
     * <p>処理対象のパスにマッチするページが存在しない場合はnullを返します。</p>
     *
     * @return 処理対象のページ。
     */
    Page getPage();


    /**
     * 処理対象のページが属しているgardのうち、
     * 現在処理中のページプロセッサまたはページフィルタに対応付けられているgardのルートページを返します。
     * <p>現在処理中のページプロセッサまたはページフィルタがgardに対応付けられていない場合は
     * nullを返します。
     * </p>
     *
     * @return gardのルートページ。
     */
    Page getGardRootPage();


    /**
     * 処理対象のページが属しているgardのうち、
     * 現在処理中のページプロセッサまたはページフィルタに対応付けられているgardを定義しているプラグインを返します。
     * <p>現在処理中のページプロセッサまたはページフィルタがgardに対応付けられていない場合は
     * nullを返します。
     * </p>
     *
     * @return gardを定義しているプラグイン。
     */
    Plugin<?> getPlugin();


    /**
     * 処理対象のページが属しているgardのうち、
     * 現在処理中のページプロセッサまたはページフィルタに対応付けられているgardのIDを返します。
     * <p>現在処理中のページプロセッサまたはページフィルタがgardに対応付けられていない場合は
     * nullを返します。
     *
     * @return gardのID。
     */
    String getGardId();


    /**
     * 処理対象のページが属しているgardのうち、
     * 現在処理中のページプロセッサまたはページフィルタに対応付けられているgardからの
     * 相対パス名を返します。
     * <p>現在処理中のページプロセッサまたはページフィルタが
     * gardに対応付けられていない場合は絶対パス名を返します。
     * </p>
     *
     * @return gardからの相対パス名。
     */
    String getLocalPathname();


    /**
     * 処理対象のパスに最も近いページを返します。
     * <p>処理対象のパスにマッチするページがあればそれを返します。
     * なければ階層を1つ上がったパスにマッチするページを探し、あればそのページを返します。
     * 以下再帰的に処理を繰り返し、マッチするものが何もなければルートページを返します。
     * </p>
     *
     * @return 処理対象のパスに最も近いページ。
     */
    Page getNearestPage();


    /**
     * 処理対象のページが属している全てのgardルートページの配列を返します。
     * <p>サイトルートページに近い順に返します。</p>
     * <p>サイトルートページがgardであるかどうかに関わらず、最初の要素として必ずサイトルートページが返ります。</p>
     *
     * @return 処理対象のページが属している全てのgardルートページの配列。
     */
    Page[] getGardRootPages();


    /**
     * 処理対象のページが属している全てのgardのIDの配列を返します。
     * <p>サイトルートページに近い順に返します。</p>
     * <p>gardであったとしてもルートページは含みません。</p>
     *
     * @return 処理対象のページが属している全てのgardのIDの配列。
     */
    String[] getGardIds();


    /*
     * for framework
     */

    void setGardRootPage(Page gardRootPage);
}
