package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;

import org.seasar.kvasir.util.html.HTMLUtils;


public class StrongEvaluator
    implements LineEvaluator
{
    public String getBegin()
    {
        return "'''";
    }


    public String getEnd()
    {
        return "'''";
    }


    public void evaluate(Context context, PrintWriter writer, String content)
    {
        writer.print("<strong>");
        writer.print(HTMLUtils.filter(content));
        writer.print("</strong>");
    }
}
