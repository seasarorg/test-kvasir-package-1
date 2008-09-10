package org.seasar.kvasir.page.auth;

import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface AuthSystem
{
    String getId();


    /**
     * 指定された名前に対応するUserオブジェクトを返します。
     * <p>まず<code>heim</code>で指定されたheimを検索し、
     * 見つからない場合は<code>PathId.HEIM_MIDGARD</code>を検索します。
     * 最終的に見つからない場合はnullを返します。
     * </p>
     *
     * @param heim ユーザを検索するHeim。
     * @param name ユーザの名前。
     * @return 見つかったUserオブジェクト。
     */
    User getUser(int heimId, String name);


    /**
     * 認証処理を行ないます。
     * <p>指定された名前とパスワードを用いて認証処理を行ないます。
     * </p>
     *
     * @param heim ユーザを検索するHeim。
     * @param name ユーザの名前。
     * @param password ユーザのパスワード。
     * @return 認証に成功したかどうか。
     */
    boolean authenticate(int heimId, String name, String password);


    /**
     * 指定された名前に対応するユーザのパスワードを変更します。
     *
     * @param heim ユーザが属するheim。
     * @param name ユーザの名前。
     * @param password ユーザのパスワード。
     */
    void changePassword(int heimId, String name, String password);


    /**
     * 指定されたユーザのパスワードを変更します。
     *
     * @param user ユーザ。
     * @param password ユーザのパスワード。
     */
    void changePassword(User user, String password);
}
