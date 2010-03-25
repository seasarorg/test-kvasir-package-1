package org.seasar.kvasir.cms.pop.util;

import static org.seasar.kvasir.cms.pop.Pop.INSTANCEID_DEFAULT;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.cms.CmsPlugin;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.pop.Pane;
import org.seasar.kvasir.cms.pop.Pop;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.util.ServletUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.util.html.HTMLUtils;


/**
 * @author YOKOTA Takehiko
 */
public class PopUtils
{
    private static KvasirLog log_ = KvasirLogFactory.getLog(PopUtils.class);


    public PopUtils()
    {
    }


    public static PopContext newContext(Page containerPage,
        HttpServletRequest request, HttpServletResponse response,
        PageRequest pageRequest)
    {
        return getPlugin().newContext(containerPage, request, response,
            pageRequest);
    }


    public static PopContext newContext(Page containerPage,
        HttpServletRequest request, HttpServletResponse response)
    {
        return getPlugin().newContext(containerPage, request, response);

    }


    static PopPlugin getPlugin()
    {
        return Asgard.getKvasir().getPluginAlfr().getPlugin(PopPlugin.class);
    }


    public static Pane getPane(int heimId, String paneId)
    {
        return getPane(heimId, paneId, true);
    }


    public static Pane getPane(int heimId, String paneId, boolean create)
    {
        return getPlugin().getPane(heimId, paneId, create);
    }


    public static Pop getPop(int heimId, Object key)
    {
        return getPop(heimId, key, true);
    }


    public static Pop getPop(int heimId, Object key, int instanceId)
    {
        return getPop(heimId, key, instanceId, true);
    }


    public static Pop getPop(int heimId, Object key, boolean create)
    {
        return getPop(heimId, key, INSTANCEID_DEFAULT, create);
    }


    public static Pop getPop(int heimId, Object key, int instanceId,
        boolean create)
    {
        return getPlugin().getPop(heimId, key, instanceId, create);
    }


    /**
     * テキスト中のPOP指定の部分を、
     * POP指定を評価した結果に置き換えた文字列を返します。
     * <p>このメソッドは
     * {@link #evaluateText(Page,HttpServletRequest,HttpServletResponse,String,String,String,String,boolean)}
     * で<code>begin</code>として<code>{{</code>を指定し、
     * <code>end</code>として<code>}}</code>を指定し、
     * <code>error</code>として"!!ERROR!!"を指定し、
     * <code>debug</code>としてfalseを指定した場合と同じです。</p>
     *
     * @param containerPage テキストが何かのPageオブジェクトの本文やテンプレート等である場合はそのPageオブジェクト。
     * テキストが特定のPageオブジェクトと関係ない場合はnullを指定して下さい。
     * @param request requestオブジェクト。
     * HTTPリクエストをトリガとして呼び出していない場合はnullを指定して下さい。
     * @param response responseオブジェクト。
     * HTTPリクエストをトリガとして呼び出していない場合はnullを指定して下さい。
     * @param body 評価するテキスト。
     * @return 評価結果。
     */
    public static String evaluateText(Page containerPage,
        HttpServletRequest request, HttpServletResponse response, String body)
    {
        return evaluateText(containerPage, request, response, "{{", "}}", body,
            "!!ERROR!!", false);
    }


    /**
     * テキスト中のPOP指定の部分を、
     * POP指定を評価した結果に置き換えた文字列を返します。
     * <p>このメソッドは
     * {@link #evaluateText(Page,Locale,HttpServletRequest,HttpServletResponse,PageRequest,String,String,String,String,boolean)}
     * でロケールとして<code>LocaleUtils.findLocale(request)</code>の結果を指定し、
     * PageRequestとして
     * <code>request</code>にバインドされている
     * HTTPリクエスト時のPageRequestを指定した場合と同じです。</p>
     *
     * @param containerPage テキストが何かのPageオブジェクトの本文やテンプレート等である場合はそのPageオブジェクト。
     * テキストが特定のPageオブジェクトと関係ない場合はnullを指定して下さい。
     * @param request requestオブジェクト。
     * HTTPリクエストをトリガとして呼び出していない場合はnullを指定して下さい。
     * @param response responseオブジェクト。
     * HTTPリクエストをトリガとして呼び出していない場合はnullを指定して下さい。
     * @param body 評価するテキスト。
     * @return 評価結果。
     */
    public static String evaluateText(Page containerPage,
        HttpServletRequest request, HttpServletResponse response, String begin,
        String end, String body, String error, boolean debug)
    {
        return evaluateText(containerPage, request, response,
            getPageRequest(request), begin, end, body, error, debug);
    }


    static PageRequest getPageRequest(HttpServletRequest request)
    {
        if (request != null) {
            return (PageRequest)request
                .getAttribute(CmsPlugin.ATTR_PAGEREQUEST);
        } else {
            return null;
        }
    }


    /**
     * 文字列中のPOP指定の部分を、
     * POP指定を評価した結果に置き換えた文字列を返します。
     * <p>POP指定とは、
     * <code>begin</code>で始まり<code>end</code>で終わる文字列で、
     * 内容は
     * <i>POP名</i>
     * <code> [</code><i>引数列</i><code>]</code>です。
     * 引数列は、引数を「,」で区切ったものです。
     * 引数は、「'」で囲まれた文字列、もしくは「"」で囲まれた文字列です。
     * 引数中にクオート文字自身を含みたい場合は「\」でエスケープして下さい。</p>
     * <p>評価に失敗したPOP指定の部分は
     * errorで指定した文字列になります。
     * errorとしてnullを指定した場合はPOP指定のままになります。
     * ただし、debugがtrueである場合はスタックトレースに置き換えられます。
     * </p>
     * <p>存在しないPOPを指定した場合はPOP指定のままになります。
     * </p>
     *
     * @param containerPage テキストが何かのPageオブジェクトの本文やテンプレート等である場合はそのPageオブジェクト。
     * テキストが特定のPageオブジェクトと関係ない場合はnullを指定して下さい。
     * @param locale ロケール。
     * @param request requestオブジェクト。
     * HTTPリクエストをトリガとして呼び出していない場合はnullを指定して下さい。
     * @param response responseオブジェクト。
     * HTTPリクエストをトリガとして呼び出していない場合はnullを指定して下さい。
     * @param pageRequest PageRequestオブジェクト。
     * HTTPリクエストをトリガとして呼び出していない場合はnullを指定して下さい。
     * @param begin POP指定の開始文字列。
     * @param end POP指定の終了文字列。
     * @param body 評価するテキスト。
     * @param error POPの評価時にエラーが発生した時の置き換え文字列。
     * @param debug デバッグモードかどうか。
     * @return 評価結果。
     */
    public static String evaluateText(Page containerPage,
        HttpServletRequest request, HttpServletResponse response,
        PageRequest pageRequest, String begin, String end, String body,
        String error, boolean debug)
    {
        if (log_.isDebugEnabled()) {
            log_.debug("PopUtils#evaluateText START: begin=" + begin + ", end="
                + end + ", body=" + body + ", error=" + error + ", debug="
                + debug);
        }
        if (body == null || body.indexOf(begin) < 0) {
            return body;
        }

        int heimId = getHeimId(containerPage, pageRequest);

        PopContext context = newContext(containerPage, request, response,
            pageRequest);
        StringBuilder sb = new StringBuilder();

        int beginLen = begin.length();
        int endLen = end.length();

        int pre = 0;
        int idx;
        while ((idx = body.indexOf(begin, pre)) >= 0) {
            sb.append(body.substring(pre, idx));
            int endIdx = body.indexOf(end, idx + beginLen);
            if (endIdx < 0) {
                pre = idx;
                break;
            }

            String str = body.substring(idx + beginLen, endIdx).trim();
            String key;
            String popId;
            int instanceId = Pop.INSTANCEID_DEFAULT;
            String[] args;
            int space = str.indexOf(" ");
            if (space >= 0) {
                key = str.substring(0, space);
                args = parseArguments(str.substring(space + 1), "'\"", ',');
            } else {
                key = str;
                args = new String[0];
            }
            int delim = key.indexOf(Pop.INSTANCE_DELIMITERCHAR);
            if (delim < 0) {
                popId = key;
            } else {
                popId = key.substring(0, delim);
                try {
                    instanceId = Integer.parseInt(key.substring(delim + 1));
                } catch (NumberFormatException ignore) {
                }
            }

            Pop pop = getPop(heimId, popId, instanceId);
            String result = null;
            if (log_.isDebugEnabled()) {
                log_.debug("POP is: " + pop + ", pop-key=" + key);
            }
            try {
                if (pop != null) {
                    result = pop.render(context, args).getBody();
                } else {
                    result = HTMLUtils.filter(body.substring(idx, endIdx
                        + end.length()));
                }
            } catch (Throwable t) {
                log_.error("Can't evaluate POP: pop-key=" + str, t);
                if (debug) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    t.printStackTrace(pw);
                    pw.flush();
                    result = HTMLUtils.filterLines(sw.toString(), true);
                } else {
                    result = error;
                }
            }
            if (result != null) {
                sb.append(result);
            } else {
                sb.append(body.substring(idx, endIdx + endLen));
            }
            pre = endIdx + endLen;
        }
        if (pre < body.length()) {
            sb.append(body.substring(pre));
        }

        return sb.toString();
    }


    static int getHeimId(Page containerPage, PageRequest pageRequest)
    {
        if (containerPage != null) {
            return containerPage.getHeimId();
        } else if (pageRequest != null) {
            return pageRequest.getRootPage().getHeimId();
        } else {
            return PathId.HEIM_MIDGARD;
        }
    }


    /**
     * 引数列を表す文字列を解釈します。
     * <p>引数列は、引数をdelimで指定された文字で区切ったものです。
     * 引数は、quotes
     * で指定された文字列に含まれるいずれかの文字で囲まれた文字列です。
     * 引数中にクオート文字を含めたい場合は「\」でエスケープして下さい。</p>
     *
     * @param str 引数列を表す文字列。
     * @param quotes クオート文字。
     * @param delim 区切り記号。
     * @return 引数。
     */
    static String[] parseArguments(String str, String quotes, char delim)
    {
        if (str == null) {
            return new String[0];
        }

        str = str.trim();

        int n = str.length();
        if (n == 0) {
            return new String[0];
        }

        List<String> list = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while (true) {
            char quote = str.charAt(i++);
            int j = 0;
            for (; j < quotes.length(); j++) {
                if (quote == quotes.charAt(j)) {
                    break;
                }
            }
            if (j == quotes.length()) {
                throw new IllegalArgumentException("Illegl format: "
                    + str.substring(i));
            }

            boolean escaped = false;
            for (; i < n; i++) {
                char ch = str.charAt(i);
                if (!escaped) {
                    if (ch == quote) {
                        break;
                    } else if (ch == '\\') {
                        escaped = true;
                    } else {
                        sb.append(ch);
                    }
                } else {
                    sb.append(ch);
                    escaped = false;
                }
            }
            if (escaped) {
                throw new IllegalArgumentException("Illegl format: " + str);
            }

            list.add(sb.toString());
            sb.delete(0, sb.length());

            for (i++; i < n; i++) {
                if (str.charAt(i) != ' ') {
                    break;
                }
            }

            if (i == n) {
                break;
            } else if (str.charAt(i) != delim) {
                throw new IllegalArgumentException("Illegl format: "
                    + str.substring(i));
            }

            for (i++; i < n; i++) {
                if (str.charAt(i) != ' ') {
                    break;
                }
            }

            if (i == n) {
                throw new IllegalArgumentException("Illegl format: " + str);
            }
        }

        return list.toArray(new String[0]);
    }


    /**
     * ペインをレンダリングします。
     * <p>ペインをレンダリングした結果として、ペインが持っているPOPをレンダリングした{@link RenderedPop}の配列を返します。
     * テンプレートでは、例えばRenderedPopをpopという名前で変数定義しておき、以下のようなHTMLを記述することで
     * POPを表示することができます。
     * </p>
     * <pre>
     * &lt;div tal:attributes="id pop/id; class string:pop ${pop/popId}"&gt;
     *   &lt;h2 class="title" tal:content="structure pop/title"&gt;POP Title&lt;/h2&gt;
     *   &lt;div class="body" tal:content="structure pop/body"&gt;POP Body&lt;/div&gt;
     * &lt;/div&gt;
     * </pre>
     * 
     * @param request requestオブジェクト。
     * @param response responseオブジェクト。
     * @param pageRequest PageRequestオブジェクト。
     * @param paneId ペインのID。
     * @return RenderedPopの配列。
     * nullが返されることはありません。
     */
    public static RenderedPop[] renderPane(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest, String paneId)
    {
        Pane pane = getPane(pageRequest.getRootPage().getHeimId(), paneId,
            false);
        if (pane == null) {
            return new RenderedPop[0];
        }
        PopContext context = newContext(null, request, response, pageRequest);
        Pop[] pops = pane.getPops();
        List<RenderedPop> list = new ArrayList<RenderedPop>();
        for (int i = 0; i < pops.length; i++) {
            RenderedPop rendered = pops[i].render(context, new String[0]);
            if (!rendered.isEmpty()) {
                list.add(rendered);
            }
        }
        return list.toArray(new RenderedPop[0]);
    }


    public static RenderedPop renderPop(Page containerPage,
        HttpServletRequest request, HttpServletResponse response,
        PageRequest pageRequest, Object key)
    {
        return renderPop(containerPage, request, response, pageRequest, key,
            INSTANCEID_DEFAULT);
    }


    public static RenderedPop renderPop(Page containerPage,
        HttpServletRequest request, HttpServletResponse response,
        PageRequest pageRequest, Object key, int instanceId)
    {
        return renderPop(containerPage, request, response, pageRequest, key,
            instanceId, new String[0]);
    }


    public static RenderedPop renderPop(Page containerPage,
        HttpServletRequest request, HttpServletResponse response,
        PageRequest pageRequest, Object key, int instanceId, String[] args)
    {
        Pop pop = getPop(getHeimId(containerPage, pageRequest), key, instanceId);
        if (pop == null) {
            return null;
        } else {
            return pop.render(newContext(containerPage, request, response,
                pageRequest), args);
        }
    }


    /**
     * 一時的な属性をセッションに保存します。
     * <p>popFullIdで指定されたPOPのために、指定された名前で一時的な属性をセッションに保存します。
     * </p>
     * <p>セッションが存在しない場合は作成されます。
     * </p>
     * <p>このメソッドで保存された属性は、{@link PopContext}が持つ{@link PopContext#VARNAME_TRANSIENT}
     * オブジェクトから値を取り出すと自動的に削除されます。
     * 例えば<code>PopUtils.settransientAttribute("message", "エラー")</code>
     * としておいてテンプレートに
     * <pre>
     *   &lt;p tal:content="transient/message"&gt;MESSAGE&lt;/p&gt;
     * </pre>
     * と書いておくと、最初は「エラー」と表示されますが、もう一度テンプレートをレンダリングした場合は表示されません。
     * </p>
     * 
     * @param popFullId POPのフルID。
     * @param name 属性名。
     * @param value 属性値。
     */
    public static void setTransientAttribute(String popFullId, String name,
        Object value)
    {
        HttpServletRequest request = ServletUtils.getHttpServletRequest();
        if (request != null) {
            request.getSession().setAttribute(getTransientKey(popFullId, name),
                value);
        }
    }


    public static String getTransientKey(String popFullId, String name)
    {
        return "pop." + popFullId + "." + name;
    }
}
