package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;

import org.seasar.kvasir.util.html.HTMLUtils;


public class LinkEvaluator
    implements LineEvaluator
{
    protected static final String PROTOCOL_HTTP = "http";

    protected static final String PROTOCOL_HTTPS = "https";

    protected static final String PROTOCOL_FTP = "ftp";

    protected static final String PROTOCOL_MAILTO = "mailto";


    public String getBegin()
    {
        return "[[";
    }


    public String getEnd()
    {
        return "]]";
    }


    public void evaluate(Context context, PrintWriter writer, String content)
    {
        String title;
        String url;
        String styleClass;
        int pipe = content.indexOf('|');
        if (pipe >= 0) {
            title = content.substring(0, pipe).trim();
            url = content.substring(pipe + 1).trim();
        } else {
            title = null;
            url = content.trim();
        }
        int lt;
        if (url.endsWith(">") && (lt = url.indexOf('<')) >= 0) {
            styleClass = url.substring(lt + 1, url.length() - 1).trim();
            url = url.substring(0, lt).trim();
        } else {
            styleClass = null;
        }
        generateLink(writer, title, url, styleClass);
    }


    /*
     * protected scope methods
     */

    protected void generateLink(PrintWriter writer, String title, String url,
        String styleClass)
    {
        if (hasAcceptableProtocol(url)) {
            boolean titleGiven;
            if (title == null) {
                title = url;
                titleGiven = false;
            } else {
                titleGiven = true;
            }

            int idx = url.indexOf(';');
            if (idx < 0) {
                idx = url.indexOf('?');
            }
            String trunk = (idx >= 0 ? url.substring(0, idx) : url);
            if (trunk.endsWith(".gif") || trunk.endsWith(".GIF")
                || trunk.endsWith(".jpg") || trunk.endsWith(".JPG")
                || trunk.endsWith(".png") || trunk.endsWith(".PNG")) {
                writer.print("<img ");
                writer.print("alt=\"");
                if (titleGiven) {
                    writer.print(HTMLUtils.filter(title));
                }
                writer.print("\" src=\"");
                writer.print(HTMLUtils.filter(url));
                if (styleClass != null) {
                    writer.print("\" class=\"");
                    writer.print(HTMLUtils.filter(styleClass));
                }
                writer.print("\" />");
            } else {
                if (url.startsWith("mailto:") && !titleGiven) {
                    title = url.substring(7/*= "mailto:".length() */);
                }
                writer.print("<a href=\"");
                writer.print(HTMLUtils.filter(url));
                writer.print("\">");
                writer.print(HTMLUtils.filter(title));
                writer.print("</a>");
            }
        } else {
            generateLinkForKeyword(writer, title, url);
        }
    }


    protected boolean hasAcceptableProtocol(String url)
    {
        int colon = url.indexOf(':');
        if (colon < 0) {
            return false;
        }

        return isAcceptableProtocol(url.substring(0, colon));
    }


    protected boolean isAcceptableProtocol(String protocol)
    {
        return PROTOCOL_HTTP.equals(protocol)
            || PROTOCOL_HTTPS.equals(protocol) || PROTOCOL_FTP.equals(protocol)
            || PROTOCOL_MAILTO.equals(protocol);
    }


    protected void generateLinkForKeyword(PrintWriter writer, String title,
        String url)
    {
        //        writer.print(getBegin());
        //        if (title != null) {
        //            writer.print(title);
        //            writer.print("|");
        //        }
        //        writer.print(HTMLUtils.filter(url));
        //        writer.print(getEnd());
        if (url.endsWith(".gif") || url.endsWith(".jpg")
            || url.endsWith(".png")) {
            writer.print("<img ");
            writer.print("alt=\"");
            if (title != null) {
                writer.print(HTMLUtils.filter(title));
            }
            writer.print("\" src=\"");
            writer.print(HTMLUtils.filter(url));
            writer.print("\" />");
        } else {
            if (title == null) {
                title = url;
            }
            writer.print("<a href=\"");
            writer.print(HTMLUtils.filter(url));
            writer.print("\">");
            writer.print(HTMLUtils.filter(title));
            writer.print("</a>");
        }
    }
}
