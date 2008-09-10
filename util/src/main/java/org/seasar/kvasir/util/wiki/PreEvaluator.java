package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;

import org.seasar.kvasir.util.html.HTMLUtils;


public class PreEvaluator
    implements BlockEvaluator
{
    public boolean canNest()
    {
        return false;
    }


    public boolean canBeNested()
    {
        return true;
    }


    public boolean shouldEvaluate(Context context, String line)
    {
        return (line.startsWith(" ") || line.startsWith("\t"));
    }


    public boolean evaluate(Context context, String line, boolean first)
    {
        PrintWriter writer = context.getWriter();

        if (first) {
            writer.print("<pre>");
        }

        writer.println(HTMLUtils.filter(
            line.charAt(0) == ' ' ? line.substring(1) : line));

        return true;
    }


    public void terminate(Context context)
    {
        PrintWriter writer = context.getWriter();
        writer.println("</pre>");
    }


    public void beginNestedBlock(Context context, String line)
    {
    }


    public void endNestedBlock(Context context)
    {
    }
}
