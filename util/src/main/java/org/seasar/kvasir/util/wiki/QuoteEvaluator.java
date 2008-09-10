package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;


public class QuoteEvaluator
    implements BlockEvaluator
{
    private static final Integer    STAT_NONE = new Integer(0);
    private static final Integer    STAT_P = new Integer(1);


    public boolean canNest()
    {
        return true;
    }


    public boolean canBeNested()
    {
        return true;
    }


    public boolean shouldEvaluate(Context context, String line)
    {
        return line.startsWith("\"\"");
    }


    public boolean evaluate(Context context, String line, boolean first)
    {
        PrintWriter writer = context.getWriter();

        Integer status;
        if (first) {
            writer.println("<blockquote>");
            status = STAT_NONE;
        } else {
            status = (Integer)context.getAttribute("quote.status");
        }

        line = line.substring(2).trim();
        if (line.length() == 0) {
            if (status == STAT_P) {
                writer.println("</p>");
                status = STAT_NONE;
            }
        } else {
            if (status == STAT_NONE) {
                writer.print("<p>");
                status = STAT_P;
            }
            context.getWikiEngine().evaluateLine(context, writer, line);
        }
        context.setAttribute("quote.status", status);

        return true;
    }


    public void terminate(Context context)
    {
        PrintWriter writer = context.getWriter();
        Integer status = (Integer)context.getAttribute("quote.status");
        if (status == STAT_P) {
            writer.println("</p>");
        }
        writer.println("</blockquote>");
        context.removeAttribute("quote.status");
    }


    public void beginNestedBlock(Context context, String line)
    {
    }


    public void endNestedBlock(Context context)
    {
    }
}
