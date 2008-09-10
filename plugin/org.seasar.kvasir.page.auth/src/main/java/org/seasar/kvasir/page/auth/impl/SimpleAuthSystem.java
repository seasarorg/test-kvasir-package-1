package org.seasar.kvasir.page.auth.impl;

import org.seasar.kvasir.base.Lifecycle;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.auth.AuthSystem;
import org.seasar.kvasir.page.auth.extension.AuthSystemElement;
import org.seasar.kvasir.page.auth.protocol.AuthProtocol;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.PropertyUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 */
public class SimpleAuthSystem
    implements AuthSystem, Lifecycle
{
    public static final String PAGEPROP_AUTHPROTOCOL = AuthPlugin.ID
        + ".authProtocol";

    /**
     * パスワード認証プロトコルを表すIDです。
     */
    String PROTOCOL_PAP = "pap";

    /**
     * 時刻同期認証プロトコルを表すIDです。
     */
    String PROTOCOL_TSAP = "tsap";

    /**
     * デフォルトの認証プロトコルのIDです。
     */
    String PROTOCOL_DEFAULT = PROTOCOL_PAP;

    private PagePlugin pagePlugin_;

    private AuthPlugin plugin_;

    private PageAlfr pageAlfr_;

    private AuthSystemElement element_;

    private ComponentContainer container_;

    private final KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    public SimpleAuthSystem()
    {
    }


    /*
     * AuthSystem
     */

    public String getId()
    {
        return element_.getFullId();
    }


    public User getUser(int heimId, String name)
    {
        String pathname;
        if (name.startsWith("/")) {
            pathname = name;
        } else {
            pathname = Page.PATHNAME_USERS + "/" + name;
        }
        User user = pageAlfr_.getPage(User.class, heimId, pathname);
        if ((user == null || user.isConcealed())
            && heimId != PathId.HEIM_MIDGARD) {
            user = pageAlfr_.getPage(User.class, PathId.HEIM_MIDGARD,
                pathname);
        }
        if (user == null || user.isConcealed()) {
            return null;
        }
        return user;
    }


    public boolean authenticate(int heimId, String name, String password)
    {
        User user = getUser(heimId, name);
        if (user == null || user.isConcealed()) {
            return false;
        }

        AuthProtocol ap = getAuthProtocol(user);
        if (ap == null) {
            return false;
        }

        return ap.authenticate(user.getPassword(), password);
    }


    public void changePassword(int heimId, String name, String password)
    {
        User user = getUser(heimId, name);
        if (user == null) {
            return;
        }
        changePassword(user, password);
    }


    public void changePassword(User user, String password)
    {
        AuthProtocol ap = getAuthProtocol(user);
        if (ap == null) {
            return;
        }
        user.setPassword(ap.getInnerExpression(password));
    }


    /*
     * Lifecycle
     */

    public boolean start()
    {
        pageAlfr_ = pagePlugin_.getPageAlfr();
        container_ = plugin_.getComponentContainer();

        return true;
    }


    public void stop()
    {
        pagePlugin_ = null;
        plugin_ = null;

        pageAlfr_ = null;
        container_ = null;
    }


    /*
     * private scope methods
     */

    private AuthProtocol getAuthProtocol(User user)
    {
        PropertyAbility prop = (PropertyAbility)user
            .getAbility(PropertyAbility.class);
        String authProtocol = PropertyUtils.valueOf(prop
            .getProperty(PAGEPROP_AUTHPROTOCOL), PROTOCOL_DEFAULT);
        AuthProtocol ap = null;
        if (container_ != null) {
            ap = (AuthProtocol)container_.getComponent("authProtocol."
                + authProtocol);
        }
        if (ap == null) {
            log_.error("AuthProtocol is not found: " + authProtocol);
        }
        return ap;
    }


    /*
     * for framework
     */

    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    public void setPlugin(AuthPlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setElement(AuthSystemElement element)
    {
        element_ = element;
    }
}
