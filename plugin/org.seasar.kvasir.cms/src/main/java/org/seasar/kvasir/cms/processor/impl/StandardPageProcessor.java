package org.seasar.kvasir.cms.processor.impl;

import static org.seasar.kvasir.cms.CmsPlugin.ATTR_RESPONSECONTENTTYPE;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.processor.PageProcessor;
import org.seasar.kvasir.cms.processor.PageProcessorChain;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.template.TemplateAbility;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class StandardPageProcessor
    implements PageProcessor
{
    /*
     * PageProcessor
     */

    public void init(ServletConfig config)
    {
    }


    public void destroy()
    {
    }


    public void doProcess(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest,
        PageProcessorChain chain)
        throws ServletException, IOException
    {
        Page page = pageRequest.getMy().getPage();
        if (page != null) {
            // ページがテンプレートを持つなら
            // そのテンプレートを表示する。
            // （テンプレートの表示処理自体はテンプレートタイプ毎の
            // PageRequestProcessorに任せるためここでは何もしない。）
            TemplateAbility template = page.getAbility(TemplateAbility.class);
            if (template.getTemplate() != null) {
                String responseContentType = template.getResponseContentType();
                if (responseContentType.length() > 0) {
                    request.setAttribute(ATTR_RESPONSECONTENTTYPE,
                        responseContentType);
                }
            } else {
                // テンプレートを持たない場合は
                // ページタイプ毎のデフォルトのページにフォワードする。
                Page templatePage = null;
                String subType = page.getAbility(PropertyAbility.class)
                    .getProperty(PropertyAbility.PROP_SUBTYPE);
                Page[] lords = page.getLords();
                for (int i = 1; i < lords.length; i++) {
                    if ((templatePage = findTemplatePage(page.getType(),
                        subType, lords[i])) != null) {
                        break;
                    }
                }
                if (templatePage == null) {
                    templatePage = findTemplatePage(page.getType(), subType,
                        pageRequest.getRootPage());
                }

                if (templatePage != null) {
                    if (page.equals(templatePage)) {
                        // ループ防止。
                        return;
                    }

                    RequestDispatcher rd = request
                        .getRequestDispatcher(templatePage.getPathname());
                    if (rd != null) {
                        rd.forward(request, response);
                    }
                    return;
                }
            }
        }

        chain.doProcess(request, response, pageRequest);
    }


    Page findTemplatePage(String type, String subType, Page lord)
    {
        Page templatePage = null;
        if (subType != null) {
            templatePage = lord.getChild(Page.PATHNAME_TEMPLATES + "/" + type
                + "." + subType);
        }
        if (templatePage == null) {
            templatePage = lord.getChild(Page.PATHNAME_TEMPLATES + "/" + type);
        }
        if (templatePage == null) {
            templatePage = lord.getChild(Page.PATHNAME_TEMPLATES + "/default");
        }
        // 後方互換性のため。
        if (templatePage == null && !Page.TYPE.equals(type)) {
            templatePage = lord.getChild(Page.PATHNAME_TEMPLATES + "/"
                + Page.TYPE);
        }
        return templatePage;
    }
}
