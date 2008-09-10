package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;


public class ParagraphEvaluator
    implements BlockEvaluator
{
    public boolean canNest()
    {
        return false;
    }


    public boolean canBeNested()
    {
        return false;
    }


    public boolean shouldEvaluate(Context context, String line)
    {
        return (line.length() > 0);
    }


    public boolean evaluate(Context context, String line, boolean first)
    {
        PrintWriter writer = context.getWriter();

        if (first) {
            writer.print("<p>");
        }

        context.getWikiEngine().evaluateLine(context, writer, line);

        return true;
    }


    public void terminate(Context context)
    {
        PrintWriter writer = context.getWriter();
        writer.println("</p>");
    }


    public void beginNestedBlock(Context context, String line)
    {
    }


    public void endNestedBlock(Context context)
    {
    }
}
