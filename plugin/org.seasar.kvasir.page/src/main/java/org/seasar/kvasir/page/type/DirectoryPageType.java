package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.impl.DirectoryImpl;


/**
 *
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class DirectoryPageType extends AbstractPageType
{
    public static final String ID = "directory";


    public String getId()
    {
        return ID;
    }


    public Class<? extends Page> getInterface()
    {
        return Directory.class;
    }


    public Page wrapPage(Page page)
    {
        return new DirectoryImpl(page);
    }


    public PageMold newPageMold()
    {
        return new DirectoryMold();
    }


    public String convertFieldToPropertyName(String field)
    {
        return null;
    }


    public boolean isNumericField(String field)
    {
        return false;
    }
}
