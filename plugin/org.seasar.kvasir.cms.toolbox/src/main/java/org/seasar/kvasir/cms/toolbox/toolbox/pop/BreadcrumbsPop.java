package org.seasar.kvasir.cms.toolbox.toolbox.pop;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.pop.GenericPop;
import org.seasar.kvasir.cms.toolbox.ToolboxPlugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;

import net.skirnir.freyja.render.html.ATag;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class BreadcrumbsPop extends GenericPop
{
    public static final String ID = ToolboxPlugin.ID + ".breadcrumbsPop";

    public static final String PROP_TOPLABEL = "topLabel";


    @Override
    protected RenderedPop render(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        Page page = context.getThatPage();
        if (page == null || page.isRoot()) {
            return new RenderedPop(getId(), getPopId(), "", "");
        }

        Locale locale = context.getLocale();
        LinkedList<ATag> list = new LinkedList<ATag>();
        while (!page.isRoot()) {
            String label = page.getAbility(PropertyAbility.class).getProperty(
                PropertyAbility.PROP_LABEL, locale);
            if (label == null) {
                label = page.getName();
            }
            list.addFirst(new ATag(page.getPathname(), label));
            page = page.getParent();
        }
        String topLabel = getProperty(popScope, PROP_TOPLABEL);
        if (topLabel != null && topLabel.length() > 0) {
            list.addFirst(new ATag(page.getPathname(), topLabel));
        }
        if (list.size() > 0) {
            list.get(0).setStyleClass("first");
        }

        popScope.put(PROP_TITLE, "");
        popScope.put("breadcrumbs", list);
        return super.render(context, args, popScope);
    }
}
