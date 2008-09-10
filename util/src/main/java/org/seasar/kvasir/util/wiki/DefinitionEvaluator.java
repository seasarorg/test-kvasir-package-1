package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;


public class DefinitionEvaluator
    implements BlockEvaluator
{
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
        return line.startsWith(":");
    }


    public boolean evaluate(Context context, String line, boolean first)
    {
        PrintWriter writer = context.getWriter();
        WikiEngine engine = context.getWikiEngine();

        int[] levelAry;
        if (first) {
            levelAry = new int[]{ 0 };
            context.setAttribute("definition.level", levelAry);
            writer.println("<dl>");
        } else {
            levelAry = (int[])context.getAttribute("definition.level");
            writer.println("</dd>");
        }

        String key;
        String value;
        int colon = engine.indexOfSkipLineEvaluators(line, ':', 1);
        if (colon >= 0) {
            key = line.substring(1, colon).trim();
            value = line.substring(colon + 1).trim();
        } else {
            key = line.substring(1).trim();
            value = "";
        }
        writer.print("<dt>");
        engine.evaluateLine(context, writer, key);
        writer.print("</dt><dd>");
        engine.evaluateLine(context, writer, value);

        return true;
    }


    public void terminate(Context context)
    {
        PrintWriter writer = context.getWriter();
        writer.println("</dd>");
        writer.println("</dl>");
    }


    public void beginNestedBlock(Context context, String line)
    {
    }


    public void endNestedBlock(Context context)
    {
    }
}
