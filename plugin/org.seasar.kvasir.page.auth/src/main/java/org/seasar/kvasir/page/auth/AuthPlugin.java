package org.seasar.kvasir.page.auth;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.type.User;


/**
 * ユーザ認証機能を提供するプラグインです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface AuthPlugin
    extends Plugin<EmptySettings>
{
    String ID = "org.seasar.kvasir.page.auth";

    String ID_PATH = ID.replace('.', '/');

    String DEFAULT_AUTHSYSTEMID = ID + ".defaultAuthSystem";

    /**
     * 認証に使用する認証システムのIDを表すプロパティのキーです。
     */
    String PROP_AUTHSYSTEM = "authSystem";


    /**
     * 通常Kvasir/Soraで使用されるデフォルトの認証システムを返します。
     * 
     * @return デフォルトの認証システム。
     * @see #DEFAULT_AUTHSYSTEMID
     */
    AuthSystem getDefaultAuthSystem();


    /**
     * 指定されたユーザ名とパスワードについて認証を行ないます。
     * 
     * @param heimId ユーザが登録されているHeimのID。
     * @param name ユーザ名。
     * @param password パスワード。
     * @return 認証が成功した場合、対応する{@link User}オブジェクト。
     * 認証が失敗した場合はnull。
     */
    User authenticate(int heimId, String name, String password);


    /**
     * 現在のリクエストの実行ユーザを指定します。
     * 
     * @param user 実行ユーザ。nullを指定すると実行ユーザは未指定となります。
     * @return このメソッドが呼び出された時点での実行ユーザ。
     * 実行ユーザが未指定の場合はnullが返されます。
     */
    User actAs(User user);


    /**
     * 現在のリクエストの実行ユーザを返します。
     * 
     * @return 現在のリクエストの実行ユーザ。
     * 実行ユーザが未指定の場合はnullが返されます。
     */
    User getCurrentActor();


    /**
     * 現在のリクエストの実行ユーザを返します。
     * 
     * @return 現在のリクエストの実行ユーザ。
     * 実行ユーザが未指定の場合は匿名ユーザを表わすUserオブジェクトが返されます。
     * @see Page#ID_ANONYMOUS_USER
     */
    User getCurrentActualActor();


    /**
     * 登録されている認証システムのうち、指定されたキーに関連付けられているものを返します。
     * 
     * @param key キー。
     * @return 登録されている認証システムのうち、指定されたキーに関連付けられているもの。
     * キーに関連付けされている認証システムが存在しない場合はnullが返されます。
     */
    AuthSystem getAuthSystem(Object key);


    /**
     * 登録されている全ての認証システムを返します。
     * 
     * @return 登録されている全ての認証システム。
     * nullが返されることはありません。
     */
    AuthSystem[] getAuthSystems();
}
