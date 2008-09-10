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
public class RoleMold extends PageMold
{
    public RoleMold()
    {
        super.setType(Role.TYPE);
    }


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
