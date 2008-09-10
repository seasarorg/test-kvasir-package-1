package org.seasar.kvasir.page.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageWrapper;
import org.seasar.kvasir.page.ability.RoleAbilityAlfr;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.Role;
import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class RoleImpl extends PageWrapper
    implements Role
{
    private RoleAbilityAlfr     alfr_;


    public RoleImpl(RoleAbilityAlfr alfr, Page page)
    {
        super(page);
        alfr_ = alfr;
    }


    public User[] getUsers()
    {
        return alfr_.getUsers(this);
    }


    public Group[] getGroups()
    {
        return alfr_.getGroups(this);
    }


    public boolean contains(User user)
    {
        return alfr_.contains(this, user);
    }


    public boolean contains(Group group)
    {
        return alfr_.contains(this, group);
    }


    public boolean isUserInRole(User user)
    {
        return alfr_.isUserInRole(this, user);
    }


    public void giveRoleTo(User user)
    {
        alfr_.giveRoleTo(this, user);
    }


    public void giveRoleTo(Group group)
    {
        alfr_.giveRoleTo(this, group);
    }


    public void depriveRoleFrom(User user)
    {
        alfr_.depriveRoleFrom(this, user);
    }


    public void depriveRoleFrom(Group group)
    {
        alfr_.depriveRoleFrom(this, group);
    }


    public void depriveRoleFromAllUsers()
    {
        alfr_.depriveRoleFromAllUsers(this);
    }


    public void depriveRoleFromAllGroups()
    {
        alfr_.depriveRoleFromAllGroups(this);
    }
}
