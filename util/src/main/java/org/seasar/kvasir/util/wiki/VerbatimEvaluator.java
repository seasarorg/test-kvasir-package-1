package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;


public class VerbatimEvaluator
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
        return line.startsWith("`");
    }


    public boolean evaluate(Context context, String line, boolean first)
    {
        PrintWriter writer = context.getWriter();

        writer.println(line.substring(1));

        return true;
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
