package org.seasar.kvasir.cms.util;

import static org.seasar.kvasir.cms.util.ServletUtils.getOriginalContextPath;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.html.HTMLUtils;


/**
 * @author YOKOTA Takehiko
 */
public class PresentationUtils extends
    org.seasar.kvasir.base.webapp.util.PresentationUtils
{
    /**
     * ページオブジェクトのためのアイコンイメージへのパスを保持するための
     * ページプロパティのキーです。
     */
    public static final String PROP_PAGE_ICON = "page.icon";

    public static final String ICON_PATH_PREFIX = Page.PATHNAME_PLUGINS
        + "/img/icons";

    public static final String ICON_PATH_SUFFIX = ".gif";

    private static final String PAGEURI_BEGIN_CHARS = "\"'(";

    private static final String PAGEURI_END_CHARS = "\"')?";

    private static final String PREFIX_PAGEURI = "page:";

    public static final String CLASS_PAGENOTFOUND = "pageNotFound";

    private static final String PREFIX_PAGE = "page:";


    protected PresentationUtils()
    {
    }


    /**
     * 指定されたページのアイコンを表わすURLを返します。
     * <p>返されるURLはホスト相対URLのこともあります。</p>
     *
     * @param page ページ。
     * @return アイコンのURL。
     */
    public static String getIconURL(Page page)
    {
        String site = getCmsPlugin().getSite(page.getHeimId());
        String contextPath = ServletUtils.getOriginalContextPath();
        StringBuilder sb = new StringBuilder();
        if (site != null) {
            sb.append(site);
        }
        if (contextPath != null) {
            sb.append(contextPath);
        }

        return getIconURL(page, sb.toString());
    }


    static CmsPlugin getCmsPlugin()
    {
        return Asgard.getKvasir().getPluginAlfr().getPlugin(CmsPlugin.class);
    }


    /**
     * 指定されたページのアイコンを表わすURLを返します。
     * <p><code>baseURL</code>としてWebアプリケーションのトップページのURL
     * を指定した場合は、返されるURLは完全なURLになります。
     * コンテキストパスを指定した場合は、
     * 返されるURLはホスト相対URLになることもあります。</p>
     *
     * @param page ページ。
     * @param baseURL WebアプリケーションのトップページのURL
     * またはコンテキストパス。
     * @return アイコンのURL。
     */
    public static String getIconURL(Page page, String baseURL)
    {
        PropertyAbility prop = page.getAbility(PropertyAbility.class);
        String icon = prop.getProperty(PROP_PAGE_ICON);

        String iconURL;
        if (icon == null) {
            String type = page.getType();
            StringBuilder sb = new StringBuilder().append(baseURL).append(
                ICON_PATH_PREFIX).append('/').append(type);
            if (page.isConcealed()) {
                sb.append("_concealed");
            }
            iconURL = sb.append(ICON_PATH_SUFFIX).toString();
        } else if (icon.startsWith("http:") || icon.startsWith("https:")) {
            iconURL = icon;
        } else {
            if (icon.startsWith(PREFIX_PAGE)) {
                icon = icon.substring(PREFIX_PAGE.length());
            }
            iconURL = new StringBuilder().append(baseURL).append(
                org.seasar.kvasir.page.PageUtils
                    .getAbsolutePathname(icon, page)).toString();
        }

        return iconURL;
    }


    public static String getHTMLBodyString(Page page, Locale locale,
        HttpServletRequest request, HttpServletResponse response)
    {
        if (page == null) {
            return null;
        }
        Content content = page.getAbility(ContentAbility.class)
            .getLatestContent(locale);
        if (content != null) {
            return filter(content.getBodyHTMLString(null), request, response,
                page);
        } else {
            return "";
        }
    }


    public static String filter(String html, HttpServletRequest request,
        HttpServletResponse response, Page basePage)
    {
        return replacePageURI(html, request, response, basePage);
    }


    protected static String replacePageURI(String html,
        HttpServletRequest request, HttpServletResponse response, Page basePage)
    {
        if (html == null || request == null || response == null) {
            return html;
        }
        StringBuilder sb = new StringBuilder();
        int pre = 0;
        int idx;
        while ((idx = html.indexOf(PREFIX_PAGEURI, pre)) >= 0) {
            if (idx == 0
                || PAGEURI_BEGIN_CHARS.indexOf(html.charAt(idx - 1)) < 0) {
                sb.append(html.substring(pre, idx + PREFIX_PAGEURI.length()));
                pre = idx + PREFIX_PAGEURI.length();
            } else {
                sb.append(html.substring(pre, idx));
                idx += PREFIX_PAGEURI.length();
                int endIdx;
                for (endIdx = idx; endIdx < html.length(); endIdx++) {
                    if (PAGEURI_END_CHARS.indexOf(html.charAt(endIdx)) >= 0) {
                        break;
                    }
                }

                boolean notExist = false;
                String absolutePathname = PageUtils.getAbsolutePathname(html
                    .substring(idx, endIdx), basePage);
                if (basePage != null
                    && PageUtils.getPageAlfr().getPage(basePage.getHeimId(),
                        HTMLUtils.trimQueryAndParameter(absolutePathname)) == null) {
                    notExist = true;
                }
                String path;
                if (absolutePathname.startsWith("/")) {
                    path = getOriginalContextPath(request) + absolutePathname;
                } else {
                    // absolutePathnameが「;a=b」などの場合。
                    path = getOriginalContextPath(request) + "/"
                        + absolutePathname;
                }
                sb
                    .append(response.encodeURL(HTMLUtils
                        .reencode(path, "UTF-8")));
                pre = endIdx;

                if (notExist) {
                    // エレメントについて、pathに対応するPageオブジェクトが存在しないことを
                    // 外から判別できるようにクラスを追加しておく。
                    char ch = '\0';
                    for (idx = pre; idx < html.length()
                        && (ch = html.charAt(idx)) != '<' && ch != '>'; idx++) {
                    }
                    if (idx < html.length() && ch == '>') {
                        int i = sb.length() - 1;
                        ch = '\0';
                        for (; i >= 0 && (ch = sb.charAt(i)) != '<'
                            && ch != '>'; i--) {
                        }
                        if (i >= 0 && ch == '<') {
                            // <...>で囲われているので、エレメントとみなしてクラスを追加しておく。
                            String element = HTMLUtils.addClassName(sb
                                .substring(i)
                                + html.substring(pre, idx + 1),
                                CLASS_PAGENOTFOUND);
                            sb.delete(i, sb.length());
                            sb.append(element);
                            pre = idx + 1;
                        }
                    }
                }
            }
        }
        sb.append(html.substring(pre));
        return sb.toString();
    }


    /**
     * 指定されたページの子ページのうち、現在のログインユーザがPEEK権限を持つものを返します。
     * <p>不可視のページは返されません。</p>
     *
     * @param page ページ。
     * @return 子ページ。
     */
    public static Page[] getChildren(Page page)
    {
        return getChildren(page, Asgard.getKvasir().getPluginAlfr().getPlugin(
            AuthPlugin.class).getCurrentActor());
    }


    static Page[] getChildren(Page page, User actor)
    {
        if (page == null) {
            return null;
        }
        return page.getChildren(new PageCondition().setUser(actor)
            .setPrivilege(Privilege.ACCESS_PEEK).setIncludeConcealed(false));
    }
}
