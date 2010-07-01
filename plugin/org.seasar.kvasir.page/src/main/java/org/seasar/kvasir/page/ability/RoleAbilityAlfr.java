package org.seasar.kvasir.page.ability;

import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface RoleAbilityAlfr
    extends PageAbilityAlfr
{
    String SHORTID = "role";


    User[] getUsers(Role role);


    Group[] getGroups(Role role);


    Role[] getRoles(User user);


    Role[] getRoles(Group group);


    boolean contains(Role role, User user);


    boolean contains(Role role, Group group);


    /**
     * 指定されたユーザが指定されたロールを持つかどうかを返します。
     * <p>ユーザがロールを間接的に持つ場合（ユーザが所属しているグループにロールが割り当てられている場合など）
     * でもtrueを返します。
     * </p>
     * <p>このメソッドは実際にユーザがロールを持っているかどうかを判定します。
     * 実際にロールを持っていないのであれば、ユーザが管理者ロールを持っていたとしてもfalseを返します。
     * </p>
     * 
     * @param role ロール。
     * nullを指定してはいけません。
     * @param user ユーザ。
     * nullを指定してはいけません。
     * @return ユーザがロールを持つかどうか。
     */
    boolean isUserInRole(Role role, User user);


    void giveRoleTo(Role role, User user);


    void giveRoleTo(Role role, Group group);


    void depriveRoleFrom(Role role, User user);


    void depriveRoleFrom(Role role, Group group);


    void depriveRoleFromAllUsers(Role role);


    void depriveRoleFromAllGroups(Role role);
}
