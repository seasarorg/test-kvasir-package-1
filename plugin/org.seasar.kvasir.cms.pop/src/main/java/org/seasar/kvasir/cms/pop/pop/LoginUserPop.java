package org.seasar.kvasir.cms.pop.pop;

import java.util.Map;

import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.StringUtils;
import org.seasar.kvasir.util.html.HTMLUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class LoginUserPop extends GenericPop
{
    public static final String ID = PopPlugin.ID + ".loginUserPop";

    private static final String SYMBOL_NAME = "%N";

    private AuthPlugin authPlugin_;


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    /*
     * Pop
     */

    @Override
    protected RenderedPop render(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        RenderedPop rendered = super.render(context, args, popScope);

        User actor = authPlugin_.getCurrentActor();
        if (actor.isAnonymous()) {
            rendered.setBody("");
        } else {
            PropertyAbility prop = actor.getAbility(PropertyAbility.class);
            String localizedName = prop.getProperty(PropertyAbility.PROP_LABEL,
                context.getLocale());
            rendered.setBody(StringUtils.replace(rendered.getBody(),
                SYMBOL_NAME, HTMLUtils.filter(localizedName)));
        }
        return rendered;
    }
}
