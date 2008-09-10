package org.seasar.kvasir.cms.toolbox.toolbox.web;

import org.seasar.cms.ymir.extension.annotation.Out;
import org.seasar.cms.ymir.scope.impl.RequestScope;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.toolbox.toolbox.dto.RdfEntryDto;
import org.seasar.kvasir.cms.util.ServletUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.condition.Order;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.util.LocaleUtils;


public class RssPage extends RssPageBase
{
    public static final int SUMMARY_LENGTH = 128;

    public static final String SUFFIX_LEADING = "...";


    @Override
    public void _render()
    {
        lang_ = LocaleUtils.getString(getLocale());
        String domain = ServletUtils.getDomainURL();
        rdfURL_ = domain + getURL();
        basePathname_ = getYmirRequest().getPathInfo();

        Page page = getPageAlfr().getPage(getHeimId(), basePathname_);
        if (page != null) {
            PropertyAbility prop = page.getAbility(PropertyAbility.class);
            title_ = prop.getProperty(PropertyAbility.PROP_LABEL, getLocale());
            description_ = prop.getProperty(PropertyAbility.PROP_DESCRIPTION,
                getLocale());

            Page[] children = page.getChildren(new PageCondition()
                .setIncludeConcealed(false).setOnlyListed(true).setUser(
                    getCurrentActor()).setPrivilege(Privilege.ACCESS_VIEW)
                .setOrder(new Order(PageCondition.FIELD_ORDERNUMBER, false)));
            rdfEntries_ = new RdfEntryDto[children.length];
            for (int i = 0; i < children.length; i++) {
                rdfEntries_[i] = new RdfEntryDto(children[i], domain
                    + toURL(children[i].getPathname()), getLocale());
            }
        }
    }


    @Out(name = CmsPlugin.ATTR_RESPONSECONTENTTYPE, scopeClass = RequestScope.class)
    public String getContentType()
    {
        return "text/xml; charset=UTF-8";
    }
}
