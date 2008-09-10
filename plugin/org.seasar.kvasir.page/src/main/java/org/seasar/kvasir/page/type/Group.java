package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.Page;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface Group extends Page
{
    String TYPE = "group";


    boolean contains(User user);

    User[] getUsers();

    void add(User user);

    void remove(User user);

    void clear();

    Role[] getRoles();
}
