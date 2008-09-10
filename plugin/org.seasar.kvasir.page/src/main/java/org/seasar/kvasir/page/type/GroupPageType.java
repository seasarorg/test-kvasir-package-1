package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.ability.GroupAbilityAlfr;
import org.seasar.kvasir.page.ability.RoleAbilityAlfr;
import org.seasar.kvasir.page.impl.GroupImpl;


/**
 *
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class GroupPageType extends AbstractPageType
{
    public static final String ID = "group";


    public String getId()
    {
        return ID;
    }


    public Class<? extends Page> getInterface()
    {
        return Group.class;
    }


    public Page wrapPage(Page page)
    {
        GroupAbilityAlfr groupAlfr = (GroupAbilityAlfr)getPlugin()
            .getPageAbilityAlfr(GroupAbilityAlfr.class);
        RoleAbilityAlfr roleAlfr = (RoleAbilityAlfr)getPlugin()
            .getPageAbilityAlfr(RoleAbilityAlfr.class);

        return new GroupImpl(page, groupAlfr, roleAlfr);
    }


    public PageMold newPageMold()
    {
        return new GroupMold();
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
