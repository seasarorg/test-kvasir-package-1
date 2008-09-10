package org.seasar.kvasir.page.ability;

import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.type.Role;


/**
 * ページに関するユーザの操作権限を表すためのクラスです。
 * <p>Kvasir/Soraでは、ロールを通して間接的にユーザに権限を持たせますが、
 * どのロールがどのようなタイプの権限をどのレベルで持つかをページ毎に設定することができます。
 * このうち「どのロールがどのようなタイプの権限をどのレベルで」を表すのが
 * Permissionです。</p>
 *
 * <p><b>同期化：</b>
 * このクラスは不変クラスです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 * @see org.seasar.kvasir.page.Privilege
 */
public class Permission
{
    private Role        role_;
    private Privilege   priv_;


    /*
     * constructors
     */

    /**
     * このクラスのオブジェクトを構築します。
     *
     * @param role ロール。
     * @param priv 権限値。
     */
    public Permission(Role role, Privilege priv)
    {
        role_ = role;
        priv_ = priv;
    }


    /**
     * このクラスのオブジェクトを構築します。
     *
     * @param role ロール。
     * @param privString 権限を表す文字列。
     * 権限を表す文字列についてはPrivilegeクラスの説明を参照して下さい。
     */
    public Permission(Role role, String privString)
    {
        this(role, Privilege.getPrivilege(privString));
    }


    /**
     * このクラスのオブジェクトを構築します。
     *
     * @param roleId ロールのID。
     * @param type 権限のタイプ。
     * @param level 権限レベル。
     */
    public Permission(int roleId, int type, int level)
    {
        role_ = (Role)PageUtils.getPageAlfr().getPage(Role.class, roleId);
        priv_ = Privilege.getPrivilege(type, level);
    }


    /*
     * public scope methods
     */

    /**
     * ロールを返します。
     *
     * @return ロール。
     */
    public Role getRole()
    {
        return role_;
    }


    /**
     * 権限値を返します。
     *
     * @return 権限値。
     */
    public Privilege getPrivilege()
    {
        return priv_;
    }
}
