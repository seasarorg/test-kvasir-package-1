package org.seasar.kvasir.page.auth.mock;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.impl.PluginImpl;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.auth.AuthSystem;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.page.type.mock.MockUser;


public class MockAuthPlugin extends PluginImpl<EmptySettings>
    implements AuthPlugin
{
    private User user_;

    private User anonymousUser_ = new MockUser(Page.ID_ANONYMOUS_USER,
        Page.PATHNAME_ANONYMOUS_USER);


    public User actAs(User user)
    {
        User old = user_;
        user_ = user;
        return old;
    }


    public User authenticate(int heimId, String name, String password)
    {
        return null;
    }


    public AuthSystem getAuthSystem(Object key)
    {
        return null;
    }


    public AuthSystem[] getAuthSystems()
    {
        return null;
    }


    public User getCurrentActor()
    {
        return (user_ != null ? user_ : anonymousUser_);
    }


    public User getCurrentActualActor()
    {
        return user_;
    }


    public AuthSystem getDefaultAuthSystem()
    {
        return null;
    }
}
