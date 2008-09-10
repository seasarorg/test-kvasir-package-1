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
public interface RoleAbilityAlfr extends PageAbilityAlfr
{
    String      SHORTID = "role";

    User[] getUsers(Role role);

    Group[] getGroups(Role role);

    Role[] getRoles(User user);

    Role[] getRoles(Group group);

    boolean contains(Role role, User user);

    boolean contains(Role role, Group group);

    boolean isUserInRole(Role role, User user);

    void giveRoleTo(Role role, User user);

    void giveRoleTo(Role role, Group group);

    void depriveRoleFrom(Role role, User user);

    void depriveRoleFrom(Role role, Group group);

    void depriveRoleFromAllUsers(Role role);

    void depriveRoleFromAllGroups(Role role);
}
