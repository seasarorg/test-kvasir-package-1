package org.seasar.kvasir.page.ability.impl;

import org.seasar.kvasir.page.ability.AbstractPageAbility;
import org.seasar.kvasir.page.ability.GroupAbility;
import org.seasar.kvasir.page.ability.GroupAbilityAlfr;
import org.seasar.kvasir.page.type.Group;
import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class GroupAbilityImpl extends AbstractPageAbility
    implements GroupAbility
{
    private GroupAbilityAlfr    alfr_;
    private Group               group_;


    public GroupAbilityImpl(GroupAbilityAlfr alfr, Group group)
    {
        super(alfr, group);
        alfr_ = alfr;
        group_ = group;
    }


    public User[] getUsers()
    {
        return alfr_.getUsers(group_);
    }


    public void add(User user)
    {
        alfr_.add(group_, user);
    }


    public void remove(User user)
    {
        alfr_.remove(group_, user);
    }


    public void clear()
    {
        alfr_.clear(group_);
    }


    public boolean contains(User user)
    {
        return alfr_.contains(group_, user);
    }
}
