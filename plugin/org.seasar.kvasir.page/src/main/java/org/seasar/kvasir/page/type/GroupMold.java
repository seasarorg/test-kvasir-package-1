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
public class GroupMold extends PageMold
{
    public GroupMold()
    {
        super.setType(Group.TYPE);
    }


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
