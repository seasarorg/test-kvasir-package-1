package org.seasar.kvasir.cms.pop.pop;

import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.util.html.HTMLUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class SiteTitlePop extends GenericPop
{
    public static final String ID = PopPlugin.ID + ".siteTitlePop";

    public static final String PROP_SITETITLE = "siteTitle";

    public static final String PROP_IMAGEURL = "imageURL";

    public static final String PROP_IMAGEALIGN = "imageAlign";

    public static final String PROP_IMAGEWIDTH = "imageWidth";

    public static final String PROP_IMAGEHEIGHT = "imageHeight";

    private static final String PX = "px";


    @Override
    protected RenderedPop render(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        String imageWidth = getProperty(popScope, PROP_IMAGEWIDTH);
        if (!isEmpty(imageWidth) && !imageWidth.endsWith(PX)) {
            imageWidth = imageWidth + PX;
        }

        String imageHeight = getProperty(popScope, PROP_IMAGEHEIGHT);
        if (!isEmpty(imageHeight) && !imageHeight.endsWith(PX)) {
            imageHeight = imageHeight + PX;
        }

        String imageURL = getProperty(popScope, PROP_IMAGEURL);
        if (!isEmpty(imageURL)) {
            String imageAlign = getProperty(popScope, PROP_IMAGEALIGN);
            StringBuilder outerStyle = new StringBuilder();
            String innerStyle = null;
            if (!isEmpty(imageWidth)) {
                if (imageAlign.indexOf("left") >= 0) {
                    innerStyle = "margin-left:" + imageWidth;
                } else if (imageAlign.indexOf("right") >= 0) {
                    innerStyle = "margin-right:" + imageWidth;
                }
            }
            outerStyle.append("background-image:url(").append(
                HTMLUtils.reencode(imageURL)).append(
                "); background-repeat:no-repeat; ").append(
                "background-position:").append(imageAlign);
            if (!isEmpty(imageHeight)) {
                outerStyle.append("; ").append("height:").append(imageHeight);
            }
            setProperty(popScope, "outerStyle", outerStyle);
            setProperty(popScope, "innerStyle", innerStyle);
        }

        return super.render(context, args, popScope);
    }


    @Override
    public String getProperty(PopContext context, String id, String variant)
    {
        if (PROP_SITETITLE.equals(id)) {
            return getRootPage(context).getAbility(PropertyAbility.class)
                .getProperty(PropertyAbility.PROP_LABEL, variant);
        } else {
            return super.getProperty(context, id, variant);
        }
    }


    @Override
    public String getProperty(PopContext context, String id, Locale locale)
    {
        if (PROP_SITETITLE.equals(id)) {
            return getRootPage(context).getAbility(PropertyAbility.class)
                .getProperty(PropertyAbility.PROP_LABEL, locale);
        } else {
            return super.getProperty(context, id, locale);
        }
    }


    @Override
    protected void setProperty0(PopContext context, String id, String variant,
        String value)
    {
        if (PROP_SITETITLE.equals(id)) {
            getRootPage(context).getAbility(PropertyAbility.class).setProperty(
                PropertyAbility.PROP_LABEL, variant, value);
        } else {
            super.setProperty0(context, id, variant, value);
        }
    }


    Page getRootPage(PopContext context)
    {
        if (context.getContainerPage() != null) {
            return context.getContainerPage().getRoot();
        } else {
            return context.getPageRequest().getRootPage();
        }
    }


    @Override
    public void removeProperty0(PopContext context, String id, String variant)
    {
        if (PROP_SITETITLE.equals(id)) {
            getRootPage(context).getAbility(PropertyAbility.class)
                .removeProperty(PropertyAbility.PROP_LABEL, variant);
        } else {
            super.removeProperty0(context, id, variant);
        }
    }
}
