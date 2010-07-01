package org.seasar.kvasir.cms.ymir.web;

import java.text.MessageFormat;
import java.util.Locale;

import org.seasar.cms.ymir.Request;
import org.seasar.cms.ymir.extension.annotation.In;
import org.seasar.cms.ymir.extension.annotation.Out;
import org.seasar.cms.ymir.scope.impl.SessionScope;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.type.User;

import net.skirnir.freyja.render.Notes;


/**
 * YmirのPageクラスのためのユーティリティメソッドを提供するための基底クラスです。
 * <p>YmirのPageクラスは必ずしもこのクラスのサブクラスである必要はありませんが、
 * このクラスを基底クラスとすることによって、アプリケーションの作成を容易にすることができます。
 * </p>
 * <p>Ymirのコード自動生成機能を有効にしているプロジェクトでこのクラスを
 * Pageクラスの基底クラスとして使用するには、
 * プロジェクトの<code>src/main/resources/app.XXXX.properties</code>
 * に<code>extension.sourceCreator.superclass=org.seasar.kvasir.cms.ymir.web.PageBase</code>
 * という行を追加して下さい。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageBase
{
    /**
     * リクエストの処理を続行することを示す定数です。
     * アクションメソッドの返り値として利用することができます。
     */
    protected static final String PASSTHROUGH = "passthrough:";

    private Plugin<?> plugin_;

    private PagePlugin pagePlugin_;

    private AuthPlugin authPlugin_;

    private PageAlfr pageAlfr_;

    private Request ymirRequest_;

    private PageRequest pageRequest_;

    private Notes notes_;

    private Notes notesForRedirection_;


    @Binding(bindingType = BindingType.MUST)
    public void setPlugin(Plugin<?> plugin)
    {
        plugin_ = plugin;
    }


    @Binding(bindingType = BindingType.MUST)
    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    @Binding(bindingType = BindingType.MUST)
    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    @Binding(bindingType = BindingType.MUST)
    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    @Binding(bindingType = BindingType.MUST)
    public void setYmirRequest(Request ymirRequest)
    {
        ymirRequest_ = ymirRequest;
    }


    @Binding(bindingType = BindingType.MUST)
    public void setPageRequest(PageRequest pageRequest)
    {
        pageRequest_ = pageRequest;
    }


    /**
     * フォワード先のテンプレートで利用するためのNotesを設定します。
     * <p>このメソッドで指定されたNotesオブジェクトは、
     * フォワード先のテンプレートから<code>self/notes</code>
     * というTALES式で参照することができます。
     * </p>
     * <p>このメソッドで指定されたNotesオブジェクトはリダイレクト先には渡されません。
     * リダイレクト先のPageオブジェクトまたはリダイレクト先のテンプレートに
     * Notesオブジェクトを渡したい場合はこのメソッドの代わりに
     * {@link #setNotesForRedirection(Notes)}を使用して下さい。
     * </p>
     * @see #setNotesForRedirection(Notes)
     * @see #getNotes()
     *
     * @param notes フォワード先のテンプレートで利用するためのNotesオブジェクト。
     */
    @In(name = ".ymir.notesForRedirection", scopeClass = SessionScope.class)
    public void setNotes(Notes notes)
    {
        notes_ = notes;
        notesForRedirection_ = null;
    }


    /**
     * リダイレクト先のPageオブジェクトまたはテンプレートで利用するための
     * Notesを設定します。
     * <p>このメソッドで指定されたNotesオブジェクトは、
     * リダイレクト先のPageオブジェクトから
     * {@link #getNotes()}とすることで参照することができます。
     * また、リダイレクト先のテンプレートから<code>self/notes</code>
     * というTALES式で参照することができます。
     * </p>
     * <p>このメソッドで指定されたNotesオブジェクトはフォワード先のテンプレートからは参照できません。
     * フォワード先のテンプレートにNotesオブジェクトを渡したい場合はこのメソッドの代わりに
     * {@link #setNotes(Notes)}を使用して下さい。
     * </p>
     * @see #setNotes(Notes)
     * @see #getNotes()
     *
     * @param notes リダイレクト先のPageオブジェクトまたはテンプレートで利用するためのNotesオブジェクト。
     */
    public void setNotesForRedirection(Notes notesForRedirection)
    {
        notesForRedirection_ = notesForRedirection;
    }


    @Out(name = ".ymir.notesForRedirection", scopeClass = SessionScope.class)
    public Notes getNotesForRedirection()
    {
        return notesForRedirection_;
    }


    /**
     * Notesを返します。
     * <p>このメソッドはテンプレート中でPageオブジェクトが持つNotesを参照するために
     * フレームワークによって呼び出されます。
     * </p>
     * @see #setNotes(Notes)
     * @see #setNotesForRedirection(Notes)
     *
     * @return Notesオブジェクト。設定されていない場合はnullを返します。
     */
    public Notes getNotes()
    {
        return notes_;
    }


    /**
     * Ymirアプリケーションを定義しているプラグインを返します。
     *
     * @return Pluginインスタンス。nullを返すことはありません。
     */
    protected Plugin<?> getPlugin()
    {
        return plugin_;
    }


    /**
     * Pageプラグインのインスタンスを返します。
     *
     * @return Pageプラグインのインスタンス。nullを返すことはありません。
     */
    protected PagePlugin getPagePlugin()
    {
        return pagePlugin_;
    }


    /**
     * Authプラグインのインスタンスを返します。
     *
     * @return Authプラグインのインスタンス。nullを返すことはありません。
     */
    protected AuthPlugin getAuthPlugin()
    {
        return authPlugin_;
    }


    /**
     * PageAlfrを返します。
     *
     * @return PageAlfr。nullを返すことはありません。
     */
    protected PageAlfr getPageAlfr()
    {
        return pageAlfr_;
    }


    /**
     * 処理中のリクエストに関する情報を持つYmirのRequestオブジェクトを返します。
     * <p>YmirのRequestオブジェクトが持つ情報は、
     * アプリケーションインスタンスのgard rootを
     * あたかもコンテキストルートのように扱った場合の情報になっていることに注意して下さい。
     * 例えばリクエストURLがKvasir/Soraのコンテキスト相対で<code>/app/path/to/page</code>である場合、
     * <code>/app</code>というパスにインストールされたYmirアプリケーションインスタンスにおいては
     * Request#getPath()が<code>/app/path/to/page</code>ではなく
     * <code>/paht/to/page</code>を返します。
     * </p>
     *
     * @return YmirのRequestオブジェクト。nullを返すことはありません。
     */
    protected Request getYmirRequest()
    {
        return ymirRequest_;
    }


    /**
     * 処理中のリクエストに関する情報を持つPageRequestオブジェクトを返します。
     * <p>Ymirアプリケーションは自分の外のページ（Kvasir/Soraの）のことを知りません。
     * そのため、例えばKvasir/Soraのサイトトップにリダイレクトするようなことができません。
     * YmirのRequestオブジェクトの代わりにPageRequestオブジェクトが持つ情報を使うことで、
     * 自分のアプリケーション外のパスにリダイレクトしたりすることができます。
     * </p>
     *
     * @return PageRequestオブジェクト。nullを返すことはありません。
     */
    protected PageRequest getPageRequest()
    {
        return pageRequest_;
    }


    /**
     * 処理中のリクエストが結び付けられているheimのIDを返します。
     *
     * @return 処理中のリクエストが結び付けられているheimのID。
     */
    protected int getHeimId()
    {
        return pageRequest_.getRootPage().getHeimId();
    }


    /**
     * リクエストされたページに再度リクエストするためのURLを返します。
     * <p>返されるURLはKvasir/Soraのコンテキスト相対のパスです。
     * </p>
     * <p>処理後に自分自身にリダイレクトしたい場合などに利用することができます。
     * </p>
     *
     * @return リクエストされたページに再度リクエストするためのURL。
     */
    protected String getURL()
    {
        return toURL(getPathname());
    }


    /**
     * リクエストされたパス名を返します。
     *
     * @return リクエストされたパス名。nullを返すことはありません。
     */
    protected String getPathname()
    {
        return pageRequest_.getMy().getPathname();
    }


    /**
     * リクエストされたKvasir/SoraのPageオブジェクトを返します。
     * <p>このメソッドが返すのは、リクエストされたパス名に対応するPageオブジェクトです。
     * リクエストされたパス名に対応するPageが存在しない場合はnullを返します。
     * </p>
     *
     * @return リクエストされたKvasir/SoraのPageオブジェクト。
     */
    protected Page getPage()
    {
        return pageRequest_.getMy().getPage();
    }


    /**
     * リクエストのロケールを返します。
     *
     * @return リクエストのロケール。nullを返すことはありません。
     */
    protected Locale getLocale()
    {
        return pageRequest_.getLocale();
    }


    /**
     * 現在のログインユーザを返します。
     * <p>ログインしていない場合は匿名ユーザを表すUserオブジェクトを返します。
     * </p>
     *
     * @return Userオブジェクト。nullを返すことはありません。
     */
    protected User getCurrentActor()
    {
        return authPlugin_.getCurrentActor();
    }


    /**
     * アプリケーションインスタンスのgard rootのパス名を返します。
     *
     * @return アプリケーションインスタンスのgard rootのパス名。
     * nullを返すことはありません。
     */
    protected String getGardRootPathname()
    {
        return getGardRootPage().getPathname();
    }


    /**
     * アプリケーションインスタンスのgard rootであるKvasir/SoraのPageオブジェクトを返します。
     *
     * @return アプリケーションインスタンスのgard rootであるKvasir/SoraのPageオブジェクト。
     * nullを返すことはありません。
     */
    protected Page getGardRootPage()
    {
        return pageRequest_.getMy().getGardRootPage();
    }


    /**
     * Ymirアプリケーションを定義しているプラグインのプロパティを返します。
     * <p>指定されたキーに対応するプロパティが存在しない場合はnullを返します。
     * </p>
     *
     * @param key プロパティのキー。
     * @return プロパティの値。
     */
    protected String getPluginProperty(String key)
    {
        return plugin_.getProperty(key);
    }


    /**
     * Ymirアプリケーションを定義しているプラグインのプロパティを返します。
     * <p>指定されたキーに対応するプロパティが存在しない場合は
     * defaultValueで指定された値を返します。
     * </p>
     *
     * @param key プロパティのキー。
     * @return プロパティの値。
     */
    protected String getPluginProperty(String key, String defaultValue)
    {
        String value = getPluginProperty(key);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }


    /**
     * 指定されたキーに対応する地域化されたメッセージを返します。
     * <p>メッセージはリクエストのロケールに従ってプラグインのプロパティから取り出されます。
     * </p>
     * <p>キーに対応するメッセージが見つからなかった場合は「<code>!</code>key<code>!</code>」
     * という文字列を返します。
     * </p>
     *
     * @param key メッセージのキー。
     * @return 地域化されたメッセージ。
     */
    protected String getLocalizedMessage(String key)
    {
        String message = plugin_.getProperty(key, getLocale());
        if (message == null) {
            message = "!" + key + "!";
        }
        return message;
    }


    /**
     * 指定されたキーに対応する地域化されたメッセージを返します。
     * <p>このメソッドは<code>getLocalizedMessage(key, new Object[] { arg1 })</code>と同じです。
     * </p>
     *
     * @param key メッセージのキー。
     * @param arg1 フォーマット要素を置き換えるための引数。
     * @return 地域化されたメッセージ。
     */
    protected String getLocalizedMessage(String key, Object arg1)
    {
        return getLocalizedMessage(key, new Object[] { arg1 });
    }


    /**
     * 指定されたキーに対応する地域化されたメッセージを返します。
     * <p>このメソッドは<code>getLocalizedMessage(key, new Object[] { arg1, arg2 })</code>と同じです。
     * </p>
     *
     * @param key メッセージのキー。
     * @param arg1 フォーマット要素を置き換えるための引数。
     * @param arg2 フォーマット要素を置き換えるための引数。
     * @return 地域化されたメッセージ。
     */
    protected String getLocalizedMessage(String key, Object arg1, Object arg2)
    {
        return getLocalizedMessage(key, new Object[] { arg1, arg2 });
    }


    /**
     * 指定されたキーに対応する地域化されたメッセージを返します。
     * <p>まずメッセージのフォーマットパターンがプラグインのプロパティから取り出され、
     * 取り出されたパターンを{@link MessageFormat#format(java.lang.String, java.lang.Object[])}
     * メソッドによってフォーマットしたものをメッセージとして返します。
     * </p>
     * <p>キーに対応するメッセージが見つからなかった場合は「<code>!</code>key<code>!</code>」
     * という文字列を返します。
     * </p>
     *
     * @param key メッセージのキー。
     * @param args フォーマット要素を置き換えるための引数。nullを指定してはいけません。
     * @return 地域化されたメッセージ。
     */
    protected String getLocalizedMessage(String key, Object[] args)
    {
        return MessageFormat.format(getLocalizedMessage(key), args);
    }


    /**
     * Ymirアプリケーションインスタンスのプロパティを返します。
     * <p>プロパティはYmirアプリケーションインスタンスの
     * gard rootであるKvasir/SoraのPageオブジェクトのプロパティとして保持されています。
     * </p>
     * <p>指定されたキーに対応するプロパティが存在しない場合はnullを返します。
     * </p>
     *
     * @param key プロパティのキー。
     * @return プロパティの値。
     */
    protected String getInstanceProperty(String key)
    {
        return getGardRootPage().getAbility(PropertyAbility.class).getProperty(
            key);
    }


    /**
     * Ymirアプリケーションインスタンスのプロパティを返します。
     * <p>プロパティはYmirアプリケーションインスタンスの
     * gard rootであるKvasir/SoraのPageオブジェクトのプロパティとして保持されています。
     * </p>
     * <p>指定されたキーに対応するプロパティが存在しない場合は
     * defaultValueで指定された値を返します。
     * </p>
     *
     * @param key プロパティのキー。
     * @return プロパティの値。
     */
    protected String getInstanceProperty(String key, String defaultValue)
    {
        String value = getInstanceProperty(key);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }


    /**
     * 指定したパス名のページにアクセスするためのURLを返します。
     * <p>返されるURLはKvasir/Soraのコンテキスト相対のパスです。
     * </p>
     * <p>パス名としてはlord相対表記（<code>@/path/to/page</code>
     * や<code>%/path/to/page</code>）のパス名を指定することもできます。
     * </p>
     *
     * @param pathname パス名。
     * @return 指定したパス名のページにアクセスするためのURL。
     * パス名としてnullが指定された場合はnullを返します。
     */
    protected String toURL(String pathname)
    {
        if (pathname == null) {
            return null;
        } else if (pathname.length() == 0 || pathname.startsWith("/")) {
            return pageRequest_.getContextPath() + pathname;
        } else if (pathname.startsWith("@") || pathname.startsWith("%")) {
            return pageRequest_.getContextPath()
                + pageRequest_.getMy().getNearestPage().getLordPathname()
                + pathname.substring(1/*= "@".length */);
        } else {
            return pathname;
        }
    }
}
