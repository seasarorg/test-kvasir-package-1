package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.Page;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface Role
    extends Page
{
    String TYPE = "role";


    User[] getUsers();


    Group[] getGroups();


    boolean contains(User user);


    boolean contains(Group group);


    /**
     * 指定されたユーザがこのロールを持つかどうかを返します。
     * <p>ユーザがロールを間接的に持つ場合（ユーザが所属しているグループにロールが割り当てられている場合など）
     * でもtrueを返します。
     * </p>
     * <p>実際にユーザがロールを持っていない場合でも、
     * ユーザが管理者ロールを持っていればtrueを返します。
     * </p>
     * 
     * @param user ユーザ。
     * nullを指定してはいけません。
     * @return ユーザがロールを持つかどうか。
     */
    boolean isUserInRole(User user);


    void giveRoleTo(User user);


    void giveRoleTo(Group group);


    void depriveRoleFrom(User user);


    void depriveRoleFrom(Group group);


    void depriveRoleFromAllUsers();


    void depriveRoleFromAllGroups();
}
