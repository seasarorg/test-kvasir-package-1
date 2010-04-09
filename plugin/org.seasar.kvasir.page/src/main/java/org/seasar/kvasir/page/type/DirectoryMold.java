package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.PageMold;


/**
 * ディレクトリを作成するための情報を保持するためのクラスです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 * @see Directory
 */
public class DirectoryMold extends PageMold
{
    /**
     * このクラスのオブジェクトを構築します。
     */
    public DirectoryMold()
    {
        super.setType(Directory.TYPE);
        setNode(true);
    }


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param name 作成するディレクトリの名前。
     */
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
