package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;


public class SectionEvaluator
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
        return line.startsWith("!");
    }


    public boolean evaluate(Context context, String line, boolean first)
    {
        int len = line.length();
        int i = 1;
        int cnt = 3;
        for (; i < len && line.charAt(i) == '!'; i++) {
            cnt++;
        }

        PrintWriter writer = context.getWriter();
        writer.print("<h");
        writer.print(cnt);
        writer.print(">");
        context.getWikiEngine().evaluateLine(
            context, writer, line.substring(i).trim());
        writer.print("</h");
        writer.print(cnt);
        writer.println(">");

        return false;
    }


    public void terminate(Context context)
    {
    }


    public void beginNestedBlock(Context context, String line)
    {
    }


    public void endNestedBlock(Context context)
    {
    }
}
