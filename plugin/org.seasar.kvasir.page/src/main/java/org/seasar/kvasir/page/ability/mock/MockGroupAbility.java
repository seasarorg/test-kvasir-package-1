package org.seasar.kvasir.page.ability.mock;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.GroupAbility;
import org.seasar.kvasir.page.type.User;


public class MockGroupAbility extends MockPageAbility
    implements GroupAbility
{
    public MockGroupAbility(Page page)
    {
        super(page);
    }


    public void add(User user)
    {
    }


    public void clear()
    {
    }


    public boolean contains(User user)
    {
        return false;
    }


    public User[] getUsers()
    {
        return null;
    }


    public void remove(User user)
    {
    }
}
