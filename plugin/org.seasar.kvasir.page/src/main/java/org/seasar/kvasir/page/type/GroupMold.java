package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.PageMold;


/**
 * グループを作成するための情報を保持するためのクラスです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 * @see Group
 */
public class GroupMold extends PageMold
{
    /**
     * このクラスのオブジェクトを構築します。
     */
    public GroupMold()
    {
        super.setType(Group.TYPE);
    }


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param name 作成するグループの名前。
     */
    public GroupMold(String name)
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
