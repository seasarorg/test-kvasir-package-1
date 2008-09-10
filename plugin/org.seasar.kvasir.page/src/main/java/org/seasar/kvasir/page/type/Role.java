package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.Page;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface Role extends Page
{
    String TYPE = "role";


    User[] getUsers();

    Group[] getGroups();

    boolean contains(User user);

    boolean contains(Group group);

    boolean isUserInRole(User user);

    void giveRoleTo(User user);

    void giveRoleTo(Group group);

    void depriveRoleFrom(User user);

    void depriveRoleFrom(Group group);

    void depriveRoleFromAllUsers();

    void depriveRoleFromAllGroups();
}
