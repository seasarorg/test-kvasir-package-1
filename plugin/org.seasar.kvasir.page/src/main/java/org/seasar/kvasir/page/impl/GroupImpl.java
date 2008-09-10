package org.seasar.kvasir.page.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageWrapper;
import org.seasar.kvasir.page.ability.GroupAbilityAlfr;
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
public class GroupImpl extends PageWrapper
    implements Group
{
    private GroupAbilityAlfr    groupAlfr_;
    private RoleAbilityAlfr     roleAlfr_;


    public GroupImpl(Page page, GroupAbilityAlfr groupAlfr,
        RoleAbilityAlfr roleAlfr)
    {
        super(page);
        groupAlfr_ = groupAlfr;
        roleAlfr_ = roleAlfr;
    }


    public boolean contains(User user)
    {
        return groupAlfr_.contains(this, user);
    }


    public User[] getUsers()
    {
        return groupAlfr_.getUsers(this);
    }


    public void add(User user)
    {
        groupAlfr_.add(this, user);
    }


    public void remove(User user)
    {
        groupAlfr_.remove(this, user);
    }


    public void clear()
    {
        groupAlfr_.clear(this);
    }


    public Role[] getRoles()
    {
        return roleAlfr_.getRoles(this);
    }
}
