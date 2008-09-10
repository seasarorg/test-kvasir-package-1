package org.seasar.kvasir.page.auth;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.type.User;


/**
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


    AuthSystem getDefaultAuthSystem();


    User authenticate(int heimId, String name, String password);


    User actAs(User user);


    User getCurrentActor();


    User getCurrentActualActor();


    AuthSystem getAuthSystem(Object key);


    AuthSystem[] getAuthSystems();
}
