package org.seasar.kvasir.page.ability;

import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface RoleAbility extends PageAbility
{
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
