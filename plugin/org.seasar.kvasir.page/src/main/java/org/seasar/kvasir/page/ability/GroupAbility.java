package org.seasar.kvasir.page.ability;

import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface GroupAbility extends PageAbility
{
    User[] getUsers();

    void add(User user);

    void remove(User user);

    void clear();

    boolean contains(User user);
}
