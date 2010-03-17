package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class TableEvaluator
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
        return line.startsWith("|");
    }


    public boolean evaluate(Context context, String line, boolean first)
    {
        PrintWriter writer = context.getWriter();

        // 行を解析する。

        StringTokenizer st = new StringTokenizer(line, "|", true);
        boolean delim = false;
        List list = new ArrayList();
        String option = null;
        while (st.hasMoreTokens()) {
            String tkn = st.nextToken();
            if (!delim) {
                if (tkn.equals("|")) {
                    delim = true;
                } else {
                    list.add(tkn);
                }
            } else {
                if (tkn.equals("|")) {
                    list.add("");
                } else {
                    list.add(tkn);
                    delim = false;
                }
            }
        }
        if (!delim && list.size() > 0) {
            option = (String)list.remove(list.size() - 1);
        }
        String[] fieldStrings = (String[])list.toArray(new String[0]);

        // カラム数が異なる場合はバッファをフラッシュする。

        Integer fieldsCount = (Integer)context
            .getAttribute("table.fieldsCount");
        if (fieldsCount != null
            && fieldsCount.intValue() != fieldStrings.length) {
            terminate(context);
            first = true;
        }

        // この行に関する処理を行なう。

        Field[] formats;
        List theadList;
        List tbodyList;
        List tfootList;
        if (first) {
            writer.println("<table>");

            formats = new Field[fieldStrings.length];
            theadList = new ArrayList();
            tbodyList = new ArrayList();
            tfootList = new ArrayList();

            context.setAttribute("table.fieldsCount", new Integer(
                fieldStrings.length));
            context.setAttribute("table.formats", formats);
            context.setAttribute("table.theadList", theadList);
            context.setAttribute("table.tbodyList", tbodyList);
            context.setAttribute("table.tfootList", tfootList);
        } else {
            formats = (Field[])context.getAttribute("table.formats");
            theadList = (List)context.getAttribute("table.theadList");
            tbodyList = (List)context.getAttribute("table.tbodyList");
            tfootList = (List)context.getAttribute("table.tfootList");
        }

        if ("c".equals(option)) {
            // 書式指定行。

            for (int i = 0; i < fieldStrings.length; i++) {
                formats[i] = new Field(fieldStrings[i], null);
                String width = formats[i].content;
                if (width.length() > 0) {
                    try {
                        width = Integer.parseInt(width) + "px";
                    } catch (NumberFormatException ex) {
                        width = null;
                    }
                } else {
                    width = null;
                }
                formats[i].width = width;
                formats[i].content = null;
            }
        } else {
            // 通常の行。

            List targetList;
            if ("h".equals(option)) {
                // ヘッダ行。
                targetList = theadList;
            } else if ("f".equals(option)) {
                // フッタ行。
                targetList = tfootList;
            } else {
                targetList = tbodyList;
            }
            Field[] fields = new Field[fieldStrings.length];
            for (int i = 0; i < fieldStrings.length; i++) {
                fields[i] = new Field(fieldStrings[i], formats[i]);
            }
            targetList.add(fields);
        }

        return true;
    }


    public void terminate(Context context)
    {
        WikiEngine engine = context.getWikiEngine();
        PrintWriter writer = context.getWriter();

        List theadList = (List)context.getAttribute("table.theadList");
        List tbodyList = (List)context.getAttribute("table.tbodyList");
        List tfootList = (List)context.getAttribute("table.tfootList");

        printTableBlock(context, engine, writer, theadList, "thead");
        printTableBlock(context, engine, writer, tbodyList, "tbody");
        printTableBlock(context, engine, writer, tfootList, "tfoot");

        writer.println("</table>");

        context.removeAttribute("table.fieldsCount");
        context.removeAttribute("table.formats");
        context.removeAttribute("table.theadList");
        context.removeAttribute("table.tbodyList");
        context.removeAttribute("table.tfootList");
    }


    public void beginNestedBlock(Context context, String line)
    {
    }


    public void endNestedBlock(Context context)
    {
    }


    /*
     * private scope methods
     */

    private void printTableBlock(Context context, WikiEngine engine,
        PrintWriter writer, List list, String blockTag)
    {
        Field[][] fieldArray = (Field[][])list.toArray(new Field[0][]);
        if (fieldArray.length == 0) {
            return;
        }

        writer.print("<");
        writer.print(blockTag);
        writer.println(">");
        for (int i = 0; i < fieldArray.length; i++) {
            Field[] fields = fieldArray[i];

            writer.println("<tr>");
            int colspan = 1;
            for (int j = 0; j < fields.length; j++) {
                Field field = fields[j];
                String fieldTag;
                String content;
                if (field.content.equals(">")) {
                    colspan++;
                    continue;
                } else if (field.content.equals("~")) {
                    continue;
                } else if (field.content.startsWith("~")) {
                    fieldTag = "th";
                    content = field.content.substring(1);
                } else {
                    fieldTag = "td";
                    content = field.content;
                }

                writer.print("<");
                writer.print(fieldTag);

                if (colspan > 1) {
                    writer.print(" colspan=\"");
                    writer.print(colspan);
                    writer.print("\"");
                    colspan = 1;
                }

                int rowspan = 1;
                int row = i + 1;
                while (row < fieldArray.length
                    && fieldArray[row][j].content.equals("~")) {
                    rowspan++;
                    row++;
                }
                if (rowspan > 1) {
                    writer.print(" rowspan=\"");
                    writer.print(rowspan);
                    writer.print("\"");
                }

                if (field.align == null && field.bgcolor == null
                    && field.color == null && field.size == null
                    && field.width == null) {
                    writer.print(">");
                } else {
                    writer.print(" style=\"");
                    if (field.align != null) {
                        writer.print("text-align:");
                        writer.print(field.align);
                        writer.print(";");
                    }
                    if (field.bgcolor != null) {
                        writer.print("background-color:");
                        writer.print(field.bgcolor);
                        writer.print(";");
                    }
                    if (field.color != null) {
                        writer.print("color:");
                        writer.print(field.color);
                        writer.print(";");
                    }
                    if (field.size != null) {
                        writer.print("font-size:");
                        writer.print(field.size);
                        writer.print(";");
                    }
                    if (field.width != null) {
                        writer.print("width:");
                        writer.print(field.width);
                        writer.print(";");
                    }
                    writer.print("\">");
                }
                writer.print(engine.evaluateLine(context, content));
                writer.print("</");
                writer.print(fieldTag);
                writer.println(">");
            }
            writer.println("</tr>");
        }
        writer.print("</");
        writer.print(blockTag);
        writer.println(">");
    }


    /*
     * inner classes
     */

    private static class Field
    {
        private static final String LEFT = "LEFT:";

        private static final String CENTER = "CENTER:";

        private static final String RIGHT = "RIGHT:";

        private static final String BGCOLOR = "BGCOLOR(";

        private static final String COLOR = "COLOR(";

        private static final String SIZE = "SIZE(";

        private static final String TAIL = "):";

        public String align;

        public String bgcolor;

        public String color;

        public String size;

        public String width;

        public String content;


        public Field(String str, Field defaultField)
        {
            if (defaultField != null) {
                align = defaultField.align;
                bgcolor = defaultField.bgcolor;
                color = defaultField.color;
                size = defaultField.size;
                width = defaultField.width;
            }

            int pre = 0;
            while (true) {
                int idx = str.indexOf(LEFT, pre);
                if (idx == pre) {
                    align = "left";
                    pre = idx + LEFT.length();
                    continue;
                }

                idx = str.indexOf(CENTER, pre);
                if (idx == pre) {
                    align = "center";
                    pre = idx + CENTER.length();
                    continue;
                }

                idx = str.indexOf(RIGHT, pre);
                if (idx == pre) {
                    align = "right";
                    pre = idx + RIGHT.length();
                    continue;
                }

                idx = str.indexOf(BGCOLOR, pre);
                if (idx == pre) {
                    idx = idx + BGCOLOR.length();
                    int idx2 = str.indexOf(TAIL, idx);
                    if (idx2 >= idx) {
                        bgcolor = str.substring(idx, idx2);
                        pre = idx2 + TAIL.length();
                        continue;
                    }
                    break;
                }

                idx = str.indexOf(COLOR, pre);
                if (idx == pre) {
                    idx = idx + COLOR.length();
                    int idx2 = str.indexOf(TAIL, idx);
                    if (idx2 >= idx) {
                        color = str.substring(idx, idx2);
                        pre = idx2 + TAIL.length();
                        continue;
                    }
                    break;
                }

                idx = str.indexOf(SIZE, pre);
                if (idx == pre) {
                    idx = idx + SIZE.length();
                    int idx2 = str.indexOf(TAIL, idx);
                    if (idx2 >= idx) {
                        try {
                            size = Integer.parseInt(str.substring(idx, idx2))
                                + "px";
                            pre = idx2 + TAIL.length();
                            continue;
                        } catch (NumberFormatException ex) {
                            ;
                        }
                    }
                    break;
                }

                break;
            }
            content = str.substring(pre);
        }
    }
}
