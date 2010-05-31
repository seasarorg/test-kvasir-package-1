package org.seasar.kvasir.cms;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.setting.CmsPluginSettings;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.type.User;


/**
 * 基本的なCMS機能を提供するプラグインです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface CmsPlugin
    extends Plugin<CmsPluginSettings>
{
    String ID = "org.seasar.kvasir.cms";

    String ID_PATH = ID.replace('.', '/');

    /**
     * PageRequestを
     * requestオブジェクトにバインドするためのキーです。
     */
    String ATTR_PAGEREQUEST = ID + ".pageRequest";

    /**
     * 現在のログインユーザのIDを
     * sessionオブジェクトにバインドするためのキーです。
     */
    String ATTR_USERID = ID + ".userId";

    /**
     * オリジナルのコンテキストパスを
     * requestオブジェクトにバインドするためのキーです。
     */
    String ATTR_CONTEXT_PATH = ID + ".contextPath";

    /**
     * HTTPレスポンスのContent-Typeをrequestオブジェクトにバインドするためのキーです。
     * <p>HttpServletResponse#setContentType()で設定したContent-Typeは
     * 取得できないため、チェインの後段のプロセッサ／フィルタにContent-Typeを知らせるために
     * このキーを使用します。チェインの後段のプロセッサ／フィルタがContent-Typeを
     * 必要とする場合はこのキーでrequestオブジェクトにContent-Typeを
     * バインドするようにして下さい。
     * また、前段のプロセッサ／フィルタが設定したContent-Typeを
     * 後段のプロセッサ／フィルタが知りたい場合はこのキーを利用してrequestオブジェクトから
     * Content-Typeを取得するようにして下さい。
     * </p>
     */
    String ATTR_RESPONSECONTENTTYPE = ID + ".responseContentType";

    /**
     * サイトプレビューモードであるかどうかを
     * sessionオブジェクトにバインドするためのキーです。
     * このキーでsessionオブジェクトに何らかの値がバインドされている場合、
     * サイトプレビューモードと判定されます。
     */
    String ATTR_SITEPREVIEWMODE = ID + ".sitePreviewMode";

    /**
     * Exception発生時にExceptionオブジェクトを格納するためのキーです。
     */
    String ATTR_EXCEPTION = ID + ".exception";

    /**
     * コンテントの下書きをプロパティとして格納するためのキーです。
     */
    String PROP_CONTENTDRAFT = ID + ".contentDraft";


    void login(HttpServletRequest request, User user);


    User getUser(HttpServletRequest request);


    void logout(HttpServletRequest request);


    RequestSnapshot enter(ServletContext context, HttpServletRequest httpRequest);


    void leave(RequestSnapshot snapshot);


    String[] getPageFilterPhases();


    String getPageFilterDefaultPhase();


    String[] getPageProcessorPhases();


    String getPageProcessorDefaultPhase();


    /**
     * システムに登録されているHeimのIDを返します。
     * <p>{@link PathId#HEIM_ALFHEIM}などの、システム用のHeimは返しません。
     * </p>
     *
     * @return Heimのidの配列。
     */
    int[] getHeimIds();


    /**
     * リクエストに対応するHeimのidを返します。
     * <p><code>request</code>がnullの場合は{@link PathId#HEIM_MIDGARD}を返します。
     * また、リクエストURLがシステムに登録されていない場合も{@link PathId#HEIM_MIDGARD}を返します。
     * </p>
     *
     * @param request リクエスト。
     * @return HeimのID。
     */
    int determineHeimId(HttpServletRequest request);


    /**
     * 指定されたサイトURLに対応するHeimのIDを返します。
     * <p>「<code>http://localhost:8080</code>」のようなサイトURLに対応するHeimのIDを返します。
     * </p>
     * <p><code>site</code>がnullの場合は{@link PathId#HEIM_MIDGARD}を返します。
     * またサイトURLがシステムに登録されていない場合も{@link PathId#HEIM_MIDGARD}を返します。
     * </p>
     *
     * @param site サイトURL。「スキーム＋ドメイン（＋ポート番号）」という形式で、
     * 末尾に「<code>/</code>」はついていてもついていなくても構いません。
     * @return HeimのID。
     */
    int determineHeimId(String site);


    /**
     * 指定されたHeimに対応するサイトのURLを返します。
     * <p>対応するサイトが複数ある場合は最初のものを返します。
     * </p>
     * <p>対応するサイトURLが見つからなかった場合はnullを返します。
     * </p>
     *
     * @param heimId HeimのID。
     * @return サイトのURL。
     */
    String getSite(int heimId);


    /**
     * 現在のセッションにおいてサイトプレビューモードを開始します。
     * 
     * @param request リクエスト。
     * nullを指定してはいけません。
     * @return 元々サイトプレビューモードだったかどうか。
     * @see CmsPlugin#ATTR_SITEPREVIEWMODE
     */
    boolean enterInSitePreviewMode(HttpServletRequest request);


    /**
     * 現在のセッションにおいてサイトプレビューモードか開始されているかどうかを返します。
     * 
     * @param request リクエスト。
     * nullを指定してはいけません。
     * @see CmsPlugin#ATTR_SITEPREVIEWMODE
     */
    boolean isInSitePreviewMode(HttpServletRequest request);


    /**
     * 現在のセッションにおいてサイトプレビューモードを終了します。
     * 
     * @param request リクエスト。
     * nullを指定してはいけません。
     * @see CmsPlugin#ATTR_SITEPREVIEWMODE
     */
    void leaveSitePreviewMode(HttpServletRequest request);


    /**
     * 指定されたページについて、指定されたバリアントのコンテントの下書きを返します。
     * 
     * @param page ページ。nullを指定した場合はnullが返されます。
     * @param variant バリアント。nullを指定してはいけません。
     * @return コンテントの下書き。コンテントの下書きが存在しない場合はnullを返します。
     */
    ContentDraft getContentDraft(Page page, String variant);


    /**
     * 指定されたページについて、指定されたバリアントのコンテントの下書きを設定します。
     * 
     * @param page ページ。nullを指定した場合は何もしません。
     * @param variant バリアント。nullを指定してはいけません。
     * @param draft コンテントの下書き。
     * nullを指定すると{@link #removeContentDraft(Page, String)}を呼び出したのと同じになります。
     */
    void setContentDraft(Page page, String variant, ContentDraft draft);


    /**
     * 指定されたページについて、指定されたバリアントのコンテントの下書きを削除します。
     * 
     * @param page ページ。nullを指定した場合は何もしません。
     * @param variant バリアント。nullを指定してはいけません。
     */
    void removeContentDraft(Page page, String variant);
}
