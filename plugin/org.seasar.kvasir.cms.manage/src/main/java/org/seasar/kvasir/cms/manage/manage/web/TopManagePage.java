package org.seasar.kvasir.cms.manage.manage.web;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import net.skirnir.freyja.render.html.OptionTag;

import org.seasar.kvasir.cms.setting.CmsPluginSettings;
import org.seasar.kvasir.cms.setting.HeimElement;
import org.seasar.kvasir.cms.util.CmsUtils;
import org.seasar.kvasir.cms.util.ServletUtils;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.PropertyAbility;


public class TopManagePage extends PageBase
{
    private HttpServletRequest httpRequest_;

    private Integer heimId_;

    private OptionTag[] sites_;

    private String top_;


    public void setHttpRequest(HttpServletRequest request)
    {
        httpRequest_ = request;
    }


    public void setHeimId(Integer heimId)
    {
        heimId_ = heimId;
    }


    public String do_execute()
    {
        Map<Integer, OptionTag> siteMap = new TreeMap<Integer, OptionTag>();

        if (CmsUtils.getHeimId() == PathId.HEIM_MIDGARD) {
            CmsPluginSettings settings = getCmsPlugin().getSettings();
            HeimElement[] heims = settings.getHeims();
            for (int i = 0; i < heims.length; i++) {
                int id = heims[i].getId();
                String label = getCmsPlugin().getSite(id);
                if (label == null) {
                    label = getPageAlfr().getRootPage(id).getAbility(
                        PropertyAbility.class).getProperty(
                        PropertyAbility.PROP_LABEL, getLocale());
                    if (label == null) {
                        label = getResource("app.label.top-manage.site.unmapped")
                            + ":" + id;
                    }
                }
                siteMap.put(id, newOptionTag(id, label));
            }
            if (!siteMap.containsKey(Integer.valueOf(PathId.HEIM_MIDGARD))) {
                String domainURL = ServletUtils.getDomainURL(httpRequest_);
                siteMap.put(PathId.HEIM_MIDGARD, newOptionTag(
                    PathId.HEIM_MIDGARD, domainURL));
            }
            siteMap.put(PathId.HEIM_ALFHEIM, newOptionTag(PathId.HEIM_ALFHEIM,
                getResource("app.label.top-manage.site.alfheim")));
        }
        sites_ = siteMap.values().toArray(new OptionTag[0]);

        String top = getCmsPlugin().getSite(getCurrentHeimId());
        if (top != null) {
            top += getPageRequest().getContextPath();
        } else {
            top = ServletUtils.getWebappURL();
        }
        top_ = top;

        return "/top-manage.html";
    }


    OptionTag newOptionTag(int id, String site)
    {
        int currentHeimId = getCurrentHeimId();
        return new OptionTag(String.valueOf(id), site)
            .setSelected(id == currentHeimId);
    }


    public String do_changeSite()
    {
        // Heimを切り替えられるのは現在のHeimがMIDGARDである場合だけ。
        if (CmsUtils.getHeimId() == PathId.HEIM_MIDGARD && heimId_ != null) {
            setCurrentHeimId(heimId_);
        }

        return "redirect:";
    }


    public OptionTag[] getSites()
    {
        return sites_;
    }


    public String getTop()
    {
        return top_;
    }
}
