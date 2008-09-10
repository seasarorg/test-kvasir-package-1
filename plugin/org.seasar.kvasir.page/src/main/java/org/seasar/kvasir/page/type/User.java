package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.Page;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface User extends Page
{
    String TYPE = "user";


    String getPassword();

    void setPassword(String password);

    String[] getMailAddresses();

    void setMailAddresses(String[] mailAddresses);

    boolean isAdministrator();

    boolean isAnonymous();

    Group[] getGroups();

    Role[] getRoles();
}
