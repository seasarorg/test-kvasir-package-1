package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.PageMold;


/**
 * ユーザを作成するための情報を保持するためのクラスです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class UserMold extends PageMold
{
    private String password_;

    private String[] mailAddresses_;


    /**
     * このクラスのオブジェクトを構築します。
     */
    public UserMold()
    {
        super.setType(User.TYPE);
    }


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param name 作成するユーザの名前。
     */
    public UserMold(String name)
    {
        this();
        setName(name);
    }


    @Override
    public PageMold setType(String type)
    {
        throw new UnsupportedOperationException();
    }


    /**
     * 生パスワードを返します。
     * 
     * @return 生パスワード。
     */
    public String getPassword()
    {
        return password_;
    }


    /**
     * 生パスワードを設定します。
     * 
     * @param password 生パスワード。
     * @return このオブジェクト。
     */
    public UserMold setPassword(String password)
    {
        password_ = password;

        return this;
    }


    /**
     * メールアドレスの配列を返します。
     * 
     * @return メールアドレスの配列。
     * 値が設定されていない場合等にnullが返されることがあります。
     */
    public String[] getMailAddresses()
    {
        return mailAddresses_;
    }


    /**
     * メールアドレスを設定します。
     * 
     * @param mailAddresses メールアドレスの配列。
     * @return このオブジェクト。
     */
    public UserMold setMailAddresses(String[] mailAddresses)
    {
        mailAddresses_ = mailAddresses;

        return this;
    }
}
