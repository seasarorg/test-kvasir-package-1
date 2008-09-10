package org.seasar.kvasir.cms.manage.manage.dto;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.seasar.kvasir.cms.manage.dto.PageTree;
import org.seasar.kvasir.cms.util.PresentationUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.html.HTMLUtils;


public class PageTreeImpl
    implements PageTree
{
    private static final String[] MARKICONS = new String[] { "/img/space.gif",
        "/img/minus.gif", "/img/plus.gif" };

    private String topURI_;

    private Page rootPage_;

    private Set<String> openSet_ = new HashSet<String>();


    public PageTreeImpl(String topURI, Page rootPage)
    {
        topURI_ = topURI;
        rootPage_ = rootPage;
    }


    /*
     * public scope methods
     */

    public void setStatus(String pathname, boolean open)
    {
        if (open) {
            int index = 0;
            while ((index = pathname.indexOf("/", index)) >= 0) {
                openSet_.add(pathname.substring(0, index));
                index++;
            }
            openSet_.add(pathname);
        } else {
            openSet_.remove(pathname);
        }
    }


    public String render(Locale locale)
    {
        StringBuffer sb = new StringBuffer();
        constructNodeItem(sb, 0, rootPage_, locale);
        return sb.toString();
    }


    public int getHeimId()
    {
        return rootPage_.getHeimId();
    }


    /*
     * private scope methods
     */

    private void constructNodeItem(StringBuffer sb, int depth, Page page,
        Locale locale)
    {
        PropertyAbility prop = page.getAbility(PropertyAbility.class);
        String pathname = page.getPathname();
        int nSubPages = page.getChildrenCount();
        boolean open = openSet_.contains(pathname);

        int markIcon;
        if (nSubPages == 0) {
            markIcon = 0;
        } else if (open) {
            markIcon = 1;
        } else {
            markIcon = 2;
        }

        constructRow(sb, depth, pathname, page.getName()
            + " ("
            + PropertyUtils.valueOf(prop.getProperty(
                PropertyAbility.PROP_LABEL, locale), "") + ")", markIcon,
            PresentationUtils.getIconURL(page));

        if (open) {
            int subDepth = depth + 1;

            Page[] children = page.getChildren();
            for (int i = 0; i < children.length; i++) {
                constructNodeItem(sb, subDepth, children[i], locale);
            }
        }
    }


    private void constructRow(StringBuffer sb, int depth, String pathname,
        String title, int markIcon, String iconURI)
    {
        sb.append("<tr><td nowrap=\"nowrap\">");

        for (int i = 0; i < depth; i++) {
            sb.append("<img src=\"").append(topURI_).append(MARKICONS[0])
                .append("\" border=\"0\" />");
        }

        if (markIcon != 0) {
            sb.append("<a name=\"").append(pathname).append("\" href=\"")
                .append(topURI_).append("/menu-manage.do").append(pathname)
                .append("?status=").append(markIcon == 1 ? "close" : "open")
                .append("#").append(pathname).append("\">");
        }
        sb.append("<img src=\"").append(topURI_).append(MARKICONS[markIcon])
            .append("\" border=\"0\">");
        if (markIcon != 0) {
            sb.append("</a>");
        }

        String itemLink = new StringBuffer().append(
            "<a target=\"manageMain\" href=\"").append(topURI_).append(
            "/main-manage.do").append(pathname).append("\">").toString();

        sb.append(itemLink).append("<img src=\"").append(iconURI).append(
            "\" width=\"16\" height=\"16\" border=\"0\" alt=\"\" />").append(
            "</a>&nbsp;").append(itemLink).append(HTMLUtils.filter(title))
            .append("</a>").append("</td></tr>");
    }
}
