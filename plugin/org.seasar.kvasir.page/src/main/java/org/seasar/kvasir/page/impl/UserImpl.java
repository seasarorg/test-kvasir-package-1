package org.seasar.kvasir.page.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageWrapper;
import org.seasar.kvasir.page.ability.GroupAbilityAlfr;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.RoleAbilityAlfr;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.PropertyUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class UserImpl extends PageWrapper
    implements User
{
    public static final String PROP_PASSWORD = User.TYPE + ".password";

    public static final String PROP_MAILADDRESS = User.TYPE + ".mailAddress";

    private PropertyAbility prop_;

    private Role adminRole_;

    private Role anonymousRole_;

    private GroupAbilityAlfr groupAlfr_;

    private RoleAbilityAlfr roleAlfr_;


    public UserImpl(Page page, Role adminRole, Role anonymousRole,
        GroupAbilityAlfr groupAlfr, RoleAbilityAlfr roleAlfr)
    {
        super(page);
        prop_ = (PropertyAbility)page.getAbility(PropertyAbility.class);
        adminRole_ = adminRole;
        anonymousRole_ = anonymousRole;
        groupAlfr_ = groupAlfr;
        roleAlfr_ = roleAlfr;
    }


    public String getPassword()
    {
        return PropertyUtils.valueOf(prop_.getProperty(PROP_PASSWORD), "");
    }


    public void setPassword(String password)
    {
        prop_.setProperty(PROP_PASSWORD, password);
    }


    public String[] getMailAddresses()
    {
        return PropertyUtils.toLines(prop_.getProperty(PROP_MAILADDRESS));
    }


    public void setMailAddresses(String[] mailAddresses)
    {
        prop_.setProperty(PROP_MAILADDRESS, PropertyUtils
            .toString(mailAddresses));
    }


    public boolean isAdministrator()
    {
        return ((getId() == Page.ID_ADMINISTRATOR_USER) || roleAlfr_
            .isUserInRole(adminRole_, this));
    }


    public boolean isAnonymous()
    {
        return ((getId() == Page.ID_ANONYMOUS_USER) || roleAlfr_.isUserInRole(
            anonymousRole_, this));
    }


    public Group[] getGroups()
    {
        return groupAlfr_.getGroups(this);
    }


    public Role[] getRoles()
    {
        return roleAlfr_.getRoles(this);
    }
}
