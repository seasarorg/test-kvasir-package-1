package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.PageMold;


/**
 * ロールを作成するための情報を保持するためのクラスです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class RoleMold extends PageMold
{
    /**
     * このクラスのオブジェクトを構築します。
     */
    public RoleMold()
    {
        super.setType(Role.TYPE);
    }


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param name 作成するロールの名前。
     */
    public RoleMold(String name)
    {
        this();
        setName(name);
    }


    @Override
    public PageMold setType(String type)
    {
        throw new UnsupportedOperationException();
    }
}
