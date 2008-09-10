package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


public class ListEvaluator
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
        return (line.startsWith("*") || line.startsWith("#"));
    }


    public boolean evaluate(Context context, String line, boolean first)
    {
        PrintWriter writer = context.getWriter();

        int[] levelAry;
        List levelList;
        if (first) {
            levelAry = new int[]{ 0 };
            context.setAttribute("list.level", levelAry);
            levelList = new ArrayList(10);
            context.setAttribute("list.levelList", levelList);
        } else {
            levelAry = (int[])context.getAttribute("list.level");
            levelList = (List)context.getAttribute("list.levelList");

            writer.println("</li>");
        }
        int level = levelAry[0];

        String tag;
        String endTag;
        char ch = line.charAt(0);
        if (ch == '*') {
            tag = "<ul>";
            endTag = "</ul>";
        } else {
            tag = "<ol>";
            endTag = "</ol>";
        }
    
        int len = line.length();
        int i = 1;
        int cnt = 1;
        for (; i < len && line.charAt(i) == ch; i++) {
            cnt++;
        }
        if (cnt > level) {
            writer.println(tag);
            int size = levelList.size();
            if (size < cnt) {
                for (int j = size; j < cnt; j++) {
                    levelList.add(null);
                }
            }
            levelList.set(cnt - 1, endTag);
        } else if (cnt < level) {
            for (int j = level - 1; j >= cnt; j--) {
                String et = (String)levelList.get(j);
                if (et != null) {
                    writer.println(et);
                    levelList.set(j, null);
                }
            }
        } else {
            String et = (String)levelList.get(level - 1);
            if (!endTag.equals(et)) {
                writer.println(et);
                writer.println(tag);
                levelList.set(level - 1, endTag);
            }
        }
        levelAry[0] = cnt;

        writer.print("<li>");
        context.getWikiEngine().evaluateLine(
            context, writer, line.substring(i).trim());

        return true;
    }


    public void terminate(Context context)
    {
        PrintWriter writer = context.getWriter();

        writer.println("</li>");

        List levelList = (List)context.getAttribute("list.levelList");
        int n = levelList.size();
        for (int i = n - 1; i >= 0; i--) {
            String endTag = (String)levelList.get(i);
            if (endTag != null) {
                writer.println(endTag);
                levelList.set(i, null);
            }
        }
        context.removeAttribute("list.level");
        context.removeAttribute("list.levelList");
    }


    public void beginNestedBlock(Context context, String line)
    {
    }


    public void endNestedBlock(Context context)
    {
    }
}
