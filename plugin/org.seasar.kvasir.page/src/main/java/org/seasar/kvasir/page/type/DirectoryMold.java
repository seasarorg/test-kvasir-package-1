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
public class DirectoryMold extends PageMold
{
    public DirectoryMold()
    {
        super.setType(Directory.TYPE);
        setNode(true);
    }


    public DirectoryMold(String name)
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
