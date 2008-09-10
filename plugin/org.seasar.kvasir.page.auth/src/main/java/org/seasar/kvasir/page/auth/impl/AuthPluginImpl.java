package org.seasar.kvasir.page.auth.impl;

import java.util.HashMap;
import java.util.Map;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.KvasirUtils;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.auth.AuthSystem;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.PropertyUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class AuthPluginImpl extends AbstractPlugin<EmptySettings>
    implements AuthPlugin
{
    private static final String[] DEFAULT_AUTHSYSTEMIDS = new String[] { DEFAULT_AUTHSYSTEMID };

    private PagePlugin pagePlugin_;

    private PageAlfr pageAlfr_;

    private Map<Object, AuthSystem> authSystemMap_;

    private AuthSystem[] authSystems_;

    private ThreadLocal<User> actors_;


    /*
     * constructors
     */

    public AuthPluginImpl()
    {
    }


    /*
     * AuthPlugin
     */

    public AuthSystem getDefaultAuthSystem()
    {
        return getAuthSystem(DEFAULT_AUTHSYSTEMID);
    }


    public User authenticate(int heimId, String name, String password)
    {
        String[] authSystemIds = PropertyUtils
            .toLines(getProperty(PROP_AUTHSYSTEM));
        if (authSystemIds.length == 0) {
            authSystemIds = DEFAULT_AUTHSYSTEMIDS;
        }
        for (int i = 0; i < authSystemIds.length; i++) {
            AuthSystem authSystem = getAuthSystem(authSystemIds[i]);
            if (authSystem == null) {
                log_.warn("AuthSystem is not found: id=" + authSystemIds[i]);
                continue;
            }

            User user = authSystem.getUser(heimId, name);
            if (user == null) {
                // 対応するユーザがいないのでこの認証システムを
                // スキップする。
                continue;
            }
            if (authSystem.authenticate(heimId, name, password)) {
                return user;
            } else {
                return null;
            }
        }

        return null;
    }


    public User actAs(User user)
    {
        User oldUser = actors_.get();
        actors_.set(user);

        return oldUser;
    }


    public User getCurrentActor()
    {
        User user = getCurrentActualActor();
        if (user == null) {
            user = pageAlfr_.getPage(User.class, Page.ID_ANONYMOUS_USER);
        }
        return user;
    }


    public User getCurrentActualActor()
    {
        return (User)actors_.get();
    }


    public AuthSystem getAuthSystem(Object key)
    {
        return authSystemMap_.get(key);
    }


    @SuppressWarnings("unchecked")
    public <T extends AuthSystem> T getAuthSystem(Class<T> key)
    {
        return (T)authSystemMap_.get(key);
    }


    public AuthSystem[] getAuthSystems()
    {
        return authSystems_;
    }


    /*
     * AbstractPlugin
     */

    protected boolean doStart()
    {
        pageAlfr_ = pagePlugin_.getPageAlfr();
        actors_ = new ThreadLocal<User>();

        authSystemMap_ = new HashMap<Object, AuthSystem>();
        authSystems_ = getExtensionComponents(AuthSystem.class);
        KvasirUtils.start(authSystems_);
        for (int i = 0; i < authSystems_.length; i++) {
            authSystemMap_.put((Class<? extends AuthSystem>)authSystems_[i]
                .getClass(), authSystems_[i]);
            authSystemMap_.put(authSystems_[i].getId(), authSystems_[i]);
        }

        return true;
    }


    protected void doStop()
    {
        pagePlugin_ = null;

        pageAlfr_ = null;
        KvasirUtils.stop(authSystems_);
        authSystemMap_ = null;
        actors_ = null;
    }


    /*
     * for framework
     */

    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }
}
