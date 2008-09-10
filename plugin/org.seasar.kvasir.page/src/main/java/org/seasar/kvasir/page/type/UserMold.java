package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.PageMold;


/**
 *
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


    public UserMold()
    {
        super.setType(User.TYPE);
    }


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


    public String getPassword()
    {
        return password_;
    }


    public UserMold setPassword(String password)
    {
        password_ = password;

        return this;
    }


    public String[] getMailAddresses()
    {
        return mailAddresses_;
    }


    public UserMold setMailAddresses(String[] mailAddresses)
    {
        mailAddresses_ = mailAddresses;

        return this;
    }
}
