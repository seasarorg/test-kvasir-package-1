package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.ability.RoleAbilityAlfr;
import org.seasar.kvasir.page.impl.RoleImpl;


/**
 *
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class RolePageType extends AbstractPageType
{
    public static final String ID = "role";


    public String getId()
    {
        return ID;
    }


    public Class<? extends Page> getInterface()
    {
        return Role.class;
    }


    public Page wrapPage(Page page)
    {
        RoleAbilityAlfr alfr = (RoleAbilityAlfr)getPlugin().getPageAbilityAlfr(
            RoleAbilityAlfr.class);

        return new RoleImpl(alfr, page);
    }


    public PageMold newPageMold()
    {
        return new RoleMold();
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
