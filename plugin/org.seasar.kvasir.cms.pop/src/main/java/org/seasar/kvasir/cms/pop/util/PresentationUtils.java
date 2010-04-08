package org.seasar.kvasir.cms.pop.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.page.Page;


/**
 * 画面表示に関するユーティリティメソッドを提供するクラスです。
 * 
 * @author skirnir
 */
public class PresentationUtils extends
    org.seasar.kvasir.cms.util.PresentationUtils
{
    protected PresentationUtils()
    {
    }


    /**
     * POPのレンダリング処理において、指定されたページの本文をレンダリングしたHTMLを返します。
     * 
     * @param page ページ。nullを指定した場合nullが返されます。
     * @param context POPをレンダリングする処理のコンテキスト。
     * nullを指定してはいけません。
     * @param filter 本文中の簡易記法を展開するかどうか。
     * @return 本文をレンダリングしたHTML。
     */
    public static String getHTMLBodyString(Page page, PopContext context,
        boolean filter)
    {
        return getHTMLBodyString(page, context.getLocale(), context
            .getRequest(), context.getResponse(), filter);
    }


    /**
     * 指定されたページの本文をレンダリングしたHTMLを返します。
     * 
     * @param page ページ。nullを指定した場合nullが返されます。
     * @param locale 表示ロケール。nullを指定した場合、ロケールなしとみなされます。
     * @param request リクエストオブジェクト。
     * @param response レスポンスオブジェクト。
     * @param filter 本文中の簡易記法を展開するかどうか。
     * @return 本文をレンダリングしたHTML。
     */
    public static String getHTMLBodyString(Page page, Locale locale,
        HttpServletRequest request, HttpServletResponse response, boolean filter)
    {
        String body = org.seasar.kvasir.cms.util.PresentationUtils
            .getHTMLBodyString(page, locale, request, response, false);
        if (filter) {
            body = filter(body, request, response, page);
        }
        return body;
    }


    /**
     * POPのレンダリング処理において、指定されたページのプレビュー用の本文をレンダリングしたHTMLを返します。
     * 
     * @param page ページ。
     * @param mediaType プレビュー用の本文のメディアタイプ。
     * nullを指定してはいけません。
     * @param previewBody プレビュー用の本文。nullを指定した場合nullが返されます。
     * @param context POPをレンダリングする処理のコンテキスト。
     * nullを指定してはいけません。
     * @param filter 本文中の簡易記法を展開するかどうか。
     * @return 本文をレンダリングしたHTML。
     */
    public static String getHTMLBodyString(Page page, String mediaType,
        String previewBody, PopContext context, boolean filter)
    {
        return getHTMLBodyString(page, mediaType, previewBody, context
            .getRequest(), context.getResponse(), filter);
    }


    /**
     * 指定されたページのプレビュー用の本文をレンダリングしたHTMLを返します。
     * 
     * @param page ページ。
     * @param mediaType プレビュー用の本文のメディアタイプ。
     * nullを指定してはいけません。
     * @param previewBody プレビュー用の本文。nullを指定した場合nullが返されます。
     * @param request リクエストオブジェクト。
     * @param response レスポンスオブジェクト。
     * @param filter 本文中の簡易記法を展開するかどうか。
     * @return 本文をレンダリングしたHTML。
     */
    public static String getHTMLBodyString(Page page, String mediaType,
        String previewBody, HttpServletRequest request,
        HttpServletResponse response, boolean filter)
    {
        if (previewBody == null) {
            return null;
        }
        String body = ContentUtils.getBodyHTMLString(mediaType, previewBody,
            null);
        if (filter) {
            body = filter(body, request, response, page);
        }
        return body;
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
