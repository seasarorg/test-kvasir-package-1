package org.seasar.kvasir.cms.pop.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;


public class PresentationUtils extends
    org.seasar.kvasir.cms.util.PresentationUtils
{
    protected PresentationUtils()
    {
    }


    public static String getHTMLBodyString(Page page, PopContext context)
    {
        return getHTMLBodyString(page, context.getLocale(), context
            .getRequest(), context.getResponse());
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


    public static String getHTMLBodyString(Page page, String mediaType,
        String previewBody, PopContext context)
    {
        return getHTMLBodyString(page, mediaType, previewBody, context
            .getRequest(), context.getResponse());
    }


    public static String getHTMLBodyString(Page page, String mediaType,
        String previewBody, HttpServletRequest request,
        HttpServletResponse response)
    {
        if (previewBody == null) {
            return null;
        }
        return filter(ContentUtils.getBodyHTMLString(mediaType, previewBody,
            null), request, response, page);
    }


    public static String filter(String html, PopContext context)
    {
        return replacePageURI(PopUtils.evaluateText(context.getContainerPage(),
            context.getRequest(), context.getResponse(), html), context);
    }


    public static String filter(String html, HttpServletRequest request,
        HttpServletResponse response, Page basePage)
    {
        return replacePageURI(PopUtils.evaluateText(basePage, request,
            response, html), request, response, basePage);
    }


    protected static String replacePageURI(String html, PopContext context)
    {
        return replacePageURI(html, context.getRequest(),
            context.getResponse(),
            (context.getContainerPage() != null) ? context.getContainerPage()
                : context.getThatPage());
    }
}
