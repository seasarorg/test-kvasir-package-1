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
public interface GroupAbilityAlfr extends PageAbilityAlfr
{
    String      SHORTID = "group";

    String      PROP_PREFIX_USER = "user.";


    User[] getUsers(Group group);

    void add(Group group, User user);

    void remove(Group group, User user);

    void clear(Group group);

    boolean contains(Group group, User user);

    Group[] getGroups(User user);
}
