package org.seasar.kvasir.cms.kdiary.kdiary;

public class KdiaryUtils
{
    private KdiaryUtils()
    {
    }


    public static String toJavaDateFormat(String tdiaryDateFormat)
    {
        if (tdiaryDateFormat == null) {
            return null;
        }

        int pre = 0;
        int idx;
        StringBuilder sb = new StringBuilder();
        while ((idx = tdiaryDateFormat.indexOf('%', pre)) >= 0) {
            sb.append(tdiaryDateFormat.substring(pre, idx));
            idx++;
            char ch = (idx < tdiaryDateFormat.length() ? tdiaryDateFormat
                .charAt(idx) : '\0');
            if (ch == 'Y') {
                sb.append("yyyy");
            } else if (ch == 'm') {
                sb.append("MM");
            } else if (ch == 'b') {
                sb.append("MMM");
            } else if (ch == 'B') {
                sb.append("MMMM");
            } else if (ch == 'd') {
                sb.append("dd");
            } else if (ch == 'a') {
                sb.append("EEE");
            } else if (ch == 'A') {
                sb.append("EEEE");
            } else {
                sb.append("%");
                idx--;
            }
            pre = idx + 1;
        }
        sb.append(tdiaryDateFormat.substring(pre));
        return sb.toString();
    }
}
