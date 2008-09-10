package org.seasar.kvasir.cms.pop;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.el.VariableResolver;


public interface PopContext
    extends VariableResolver
{
    String VARNAME_CONTAINERPAGE = "containerPage";

    String VARNAME_LOCALE = "locale";

    String VARNAME_REQUEST = "request";

    String VARNAME_RESPONSE = "response";

    String VARNAME_SESSION = "session";

    String VARNAME_APPLICATION = "application";

    String VARNAME_PAGEREQUEST = "pageRequest";

    String VARNAME_MY = "my";

    String VARNAME_THAT = "that";

    String VARNAME_PLUGIN = "plugin";

    String VARNAME_POP = "pop";

    String VARNAME_TRANSIENT = "transient";


    /**
     * 処理中のPOPをコンテンツやテンプレート等の中に含むPageオブジェクトを返します。
     * <p>処理中のPOPがどのページにも属さない場合はnullを返します。</p>
     *
     * @return Pageオブジェクト。
     */
    Page getContainerPage();


    /**
     * 現在のロケールを返します。
     *
     * @return ロケール。nullを返すことはありません。
     */
    Locale getLocale();


    /**
     * POPを処理するトリガとなったHTTPリクエストのHttpServletRequestオブジェクトを返します。
     * <p>POPを処理するトリガがHTTPリクエストではない場合はnullを返します。
     * </p>
     *
     * @return HttpServletRequestオブジェクト。
     */
    HttpServletRequest getRequest();


    /**
     * POPを処理するトリガとなったHTTPリクエストのHttpServletResponseオブジェクトを返します。
     * <p>POPを処理するトリガがHTTPリクエストではない場合はnullを返します。
     * </p>
     *
     * @return HttpServletResponseオブジェクト。
     */
    HttpServletResponse getResponse();


    /**
     * POPを処理するトリガとなったHTTPリクエストに関連するHttpSessionオブジェクトを返します。
     * <p>セッションが開始されていない場合はnullを返します。
     * </p>
     * <p>POPを処理するトリガがHTTPリクエストではない場合はnullを返します。
     * </p>
     *
     * @return HttpSessionオブジェクト。
     */
    HttpSession getSession();


    /**
     * POPを処理するトリガとなったHTTPリクエストに関連するServletContextオブジェクトを返します。
     * <p>POPを処理するトリガがHTTPリクエストではない場合はnullを返します。
     * </p>
     *
     * @return ServletContextオブジェクト。
     */
    ServletContext getApplication();


    /**
     * POPを処理するトリガとなったHTTPリクエストに関する情報を持つPageRequestオブジェクトを返します。
     * <p>POPを処理するトリガがHTTPリクエストではない場合はnullを返します。
     * </p>
     *
     * @return PageRequestオブジェクト。
     */
    PageRequest getPageRequest();


    /**
     * POPを処理するトリガとなったディスパッチに関する情報を持つPageDispatchオブジェクトを返します。
     * <p>POPを処理するトリガがディスパッチではない場合はnullを返します。
     * </p>
     *
     * @return PageDispatchオブジェクト。
     */
    PageDispatch getMy();


    /**
     * POPを処理するトリガとなったディスパッチに対応するPageオブジェクトを返します。
     * <p>POPを処理するトリガがディスパッチではない場合や、
     * ディスパッチに対応するページが存在しない場合はnullを返します。
     * </p>
     *
     * @return Pageオブジェクト。
     */
    Page getMyPage();


    /**
     * HTTPリクエストのディスパッチに関する情報を持つPageDispatchオブジェクトを返します。
     * <p>POPを処理するトリガがHTTPリクエストではない場合はnullを返します。
     * </p>
     *
     * @return PageDispatchオブジェクト。
     */
    PageDispatch getThat();


    /**
     * POPを処理するトリガとなったHTTPリクエストに対応するPageオブジェクトを返します。
     * <p>POPを処理するトリガがHTTPリクエストではない場合や、
     * リクエストに対応するページが存在しない場合はnullを返します。
     * </p>
     *
     * @return Pageオブジェクト。
     */
    Page getThatPage();


    /**
     * 処理中のPOPを提供しているプラグインを返します。
     *
     * @return プラグイン。
     */
    Plugin<?> getPlugin();


    /**
     * 処理中のPopオブジェクトを返します。
     *
     * @return Popオブジェクト。
     */
    Pop getPop();


    /**
     * 処理対象のPOPを設定します。
     * <p>POPのレンダリングを行なう場合は、
     * まずこのメソッドを用いて処理対象のPOPをコンテキストにセットする必要があります。
     * レンダリング処理の終了後は速やかに以前のPOPをセットし直して下さい。
     * </p>
     *
     * @param pop レンダリング処理を開始するのPOP。
     * @return 以前にセットされていたPOP。
     */
    Pop setPop(Pop pop);


    /**
     * 現在のコンテキストがプレビューモードかどうかを返します。
     * <p>レンダリング時にシステムの状態を変更するタイプのPopの場合、
     * このフラグがtrueの場合はシステムの状態を変更しないようにする必要があります。
     * </p>
     *
     * @return プレビューモードかどうか。
     */
    boolean isInPreviewMode();


    /**
     * 現在のコンテキストがプレビューモードかどうかを設定します。
     * <p>POPのプレビューを行なう場合は、
     * まずこのメソッドを用いてプレビューモードに移行する必要があります。
     * プレビュー処理の終了後は速やかに以前のモードにセットし直して下さい。
     * </p>
     *
     * @param inPreviewMode プレビューモードかどうか。
     * @return 以前のモードがプレビューモードかどうか。
     */
    boolean setInPreviewMode(boolean inPreviewMode);
}
