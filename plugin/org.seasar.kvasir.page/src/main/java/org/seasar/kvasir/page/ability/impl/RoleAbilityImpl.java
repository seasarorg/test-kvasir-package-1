package org.seasar.kvasir.page.ability.impl;

import org.seasar.kvasir.page.ability.AbstractPageAbility;
import org.seasar.kvasir.page.ability.RoleAbility;
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
public class RoleAbilityImpl extends AbstractPageAbility
    implements RoleAbility
{
    private RoleAbilityAlfr     alfr_;
    private Role                role_;


    public RoleAbilityImpl(RoleAbilityAlfr alfr, Role role)
    {
        super(alfr, role);
        alfr_ = alfr;
        role_ = role;
    }


    public User[] getUsers()
    {
        return alfr_.getUsers(role_);
    }


    public Group[] getGroups()
    {
        return alfr_.getGroups(role_);
    }


    public boolean contains(User user)
    {
        return alfr_.contains(role_, user);
    }


    public boolean contains(Group group)
    {
        return alfr_.contains(role_, group);
    }


    public boolean isUserInRole(User user)
    {
        return alfr_.isUserInRole(role_, user);
    }


    public void giveRoleTo(User user)
    {
        alfr_.giveRoleTo(role_, user);
    }


    public void giveRoleTo(Group group)
    {
        alfr_.giveRoleTo(role_, group);
    }


    public void depriveRoleFrom(User user)
    {
        alfr_.depriveRoleFrom(role_, user);
    }


    public void depriveRoleFrom(Group group)
    {
        alfr_.depriveRoleFrom(role_, group);
    }


    public void depriveRoleFromAllUsers()
    {
        alfr_.depriveRoleFromAllUsers(role_);
    }


    public void depriveRoleFromAllGroups()
    {
        alfr_.depriveRoleFromAllGroups(role_);
    }
}
