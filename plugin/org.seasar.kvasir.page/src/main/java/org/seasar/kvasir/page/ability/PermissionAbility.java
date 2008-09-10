package org.seasar.kvasir.page.ability;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface PermissionAbility extends PageAbility
{
    /**
     * このページに対して、
     * 指定されたユーザが権限を持つかどうかを返します。
     *
     * @param user ユーザ。
     * @param priv 権限値。
     * @return 指定された権限を持つかどうか。
     */
    boolean permits(User user, Privilege priv);

    /**
     * このページに設定されている権限を表すPermissionの配列を返します。
     * <p>権限が設定されていない場合は空の配列を返します。</p>
     *
     * @return Permissionの配列。
     */
    Permission[] getPermissions();

    /**
     * このページに関する権限を設定します。
     * <p>現在設定されている内容は全て削除されます。</p>
     *
     * @param permissions Permissionの配列。
     */
    void setPermissions(Permission[] permissions);

    /**
     * このページに対して設定されている権限を全て削除します。
     */
    void clearPermissions();

    /**
     * このページに関して、
     * 指定されたロールに指定された権限を付与します。
     * <p><code>role</code>
     * が指定された権限タイプに関して既に権限を持っている場合は、
     * レベルの高低に関わらず権限レベルを上書きします。
     * 例えば<code>LEVEL_ACCESS</code>を持っている場合に
     * <code>LEVEL_VIEW</code>
     * を指定された場合、権限レベルは<code>LEVEL_VIEW</code>に設定されます。
     * </p>
     * <p>権限レベルとして<code>LEVEL_NONE</code>を指定した場合は
     * {@link #revokePrivilege(Page,Role,Privilege)}
     * を呼び出すのと同じになります。
     * </p>
     *
     * @param role ロール。
     * @param priv 権限値。
     */
    void grantPrivilege(Role role, Privilege priv);

    /**
     * 指定されたロールから指定された権限を剥奪します。
     * <p><code>role</code>で指定されたロールが
     * <code>privType</code>で指定された権限タイプの権限を持っている場合、
     * その権限を剥奪します。
     * 権限を剥奪すると、権限レベルは<code>LEVEL_NONE</code>と同じになります。
     * </p>
     *
     * @param role ロール。
     * @param privType 権限タイプ。
     */
    void revokePrivilege(Role role, PrivilegeType privType);

    /**
     * このページに関して、
     * 指定されたロールが持っている全ての権限を剥奪します。
     *
     * @param role ロール。
     */
    void revokeAllPrivileges(Role role);
}
