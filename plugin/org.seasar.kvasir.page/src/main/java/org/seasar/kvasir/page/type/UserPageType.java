package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.ability.GroupAbilityAlfr;
import org.seasar.kvasir.page.ability.RoleAbilityAlfr;
import org.seasar.kvasir.page.impl.UserImpl;


/**
 *
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class UserPageType extends AbstractPageType
{
    public static final String ID = "user";

    public static final String FIELD_PASSWORD = "password";

    public static final String FIELD_MAILADDRESS = "mailAddress";


    public String getId()
    {
        return ID;
    }


    public Class<? extends Page> getInterface()
    {
        return User.class;
    }


    public Page wrapPage(Page page)
    {
        PageAlfr pageAlfr = getPlugin().getPageAlfr();
        Role adminRole = (Role)pageAlfr.getPage(Page.ID_ADMINISTRATOR_ROLE);
        Role anonymousRole = (Role)pageAlfr.getPage(Page.ID_ANONYMOUS_ROLE);
        GroupAbilityAlfr groupAlfr = (GroupAbilityAlfr)getPlugin()
            .getPageAbilityAlfr(GroupAbilityAlfr.class);
        RoleAbilityAlfr roleAlfr = (RoleAbilityAlfr)getPlugin()
            .getPageAbilityAlfr(RoleAbilityAlfr.class);
        return new UserImpl(page, adminRole, anonymousRole, groupAlfr, roleAlfr);
    }


    public PageMold newPageMold()
    {
        return new UserMold();
    }


    public String convertFieldToPropertyName(String field)
    {
        String lfield = field.toLowerCase();
        if (lfield.equals(FIELD_PASSWORD)) {
            return UserImpl.PROP_PASSWORD;
        } else if (lfield.equals(FIELD_MAILADDRESS)) {
            return UserImpl.PROP_MAILADDRESS;
        } else {
            return null;
        }
    }


    public boolean isNumericField(String field)
    {
        return false;
    }


    @Override
    public void processAfterCreated(Page page, PageMold mold)
    {
        if (!(page instanceof User) || !(mold instanceof UserMold)) {
            throw new IllegalArgumentException("PageType mismatch: page="
                + page + ", mold=" + mold);
        }

        super.processAfterCreated(page, mold);

        User user = (User)page;
        UserMold userMold = (UserMold)mold;

        String password = userMold.getPassword();
        if (password != null) {
            user.setPassword(password);
        }
        String[] mailAddresses = userMold.getMailAddresses();
        if (mailAddresses != null) {
            user.setMailAddresses(mailAddresses);
        }
    }
}
