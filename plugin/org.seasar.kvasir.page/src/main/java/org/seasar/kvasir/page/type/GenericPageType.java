package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;


/**
 *
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class GenericPageType extends AbstractPageType
{
    public static final String ID = "page";


    public String getId()
    {
        return ID;
    }


    public Class<? extends Page> getInterface()
    {
        return Page.class;
    }


    public Page wrapPage(Page page)
    {
        return page;
    }


    public PageMold newPageMold()
    {
        return new PageMold();
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
