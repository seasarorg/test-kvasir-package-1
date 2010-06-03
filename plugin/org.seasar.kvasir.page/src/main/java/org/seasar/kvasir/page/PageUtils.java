package org.seasar.kvasir.page;

import static org.seasar.kvasir.page.Page.SYMBOL_LORD;
import static org.seasar.kvasir.page.Page.SYMBOL_LORD_CHAR;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.annotation.ForTest;


/**
 * @author YOKOTA Takehiko
 */
public class PageUtils
{
    public static final NameEncodeRule NER_FILENAME = new NameEncodeRule() {
        public boolean isEncoded(char ch)
        {
            return !(((ch >= '0') && (ch <= '9'))
                || ((ch >= 'a') && (ch <= 'z')) || ((ch >= 'A') && (ch <= 'Z'))
                || (ch == '-') || (ch == '_') || (ch == '.'));
        }
    };

    public static final NameEncodeRule NER_PROPNAME = new NameEncodeRule() {
        public boolean isEncoded(char ch)
        {
            return (ch == '$');
        }
    };

    private static final String SEGMENT_PARENT = "..";

    private static final String SEGMENT_CURRENT = ".";

    private static PagePlugin pagePlugin_;

    private static PageAlfr pageAlfr_;


    private PageUtils()
    {
    }


    public static PagePlugin getPlugin()
    {
        if (pagePlugin_ != null) {
            return pagePlugin_;
        } else {
            return (PagePlugin)Asgard.getKvasir().getPluginAlfr().getPlugin(
                PagePlugin.class);
        }
    }


    @ForTest
    public static void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }


    public static PageAlfr getPageAlfr()
    {
        if (pageAlfr_ != null) {
            return pageAlfr_;
        } else {
            return getPlugin().getPageAlfr();
        }
    }


    @ForTest
    public static void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    public static String getParentPathname(String pathname)
    {
        if (pathname == null) {
            return null;
        }
        int slash = pathname.lastIndexOf('/');
        if (slash < 0) {
            return null;
        }
        return pathname.substring(0, slash);
    }


    public static String getName(String pathname)
    {
        if (pathname == null) {
            return null;
        }
        int slash = pathname.lastIndexOf('/');
        if (slash < 0) {
            return pathname;
        }
        return pathname.substring(slash + 1);
    }


    public static String getAbsolutePathname(String pathname, Page basePage)
    {
        return getAbsolutePathname(pathname, (basePage != null) ? basePage
            .getLord() : null, basePage);

    }


    public static String getAbsolutePathname(String pathname,
        Page baseLordPage, Page basePage)
    {
        return getAbsolutePathname(pathname,
            (baseLordPage != null ? baseLordPage.getPathname() : null),
            basePage);
    }


    public static String getAbsolutePathname(String pathname,
        String baseLordPathname, Page basePage)
    {
        return getAbsolutePathname(pathname, baseLordPathname,
            basePage != null ? basePage.getPathname() : null);
    }


    public static String getAbsolutePathname(String pathname,
        String baseLordPathname, String basePathname)
    {
        if (pathname == null) {
            return null;
        } else if (pathname.equals(SEGMENT_CURRENT)) {
            return basePathname;
        }

        String absolutePathname;
        if (pathname.startsWith(SYMBOL_LORD)) {
            if (baseLordPathname != null) {
                absolutePathname = baseLordPathname
                    + pathname.substring(SYMBOL_LORD.length());
            } else {
                absolutePathname = pathname.substring(SYMBOL_LORD.length());
            }
        } else if (pathname.length() == 0 || pathname.startsWith("/")) {
            absolutePathname = pathname;
        } else {
            if (basePathname != null) {
                absolutePathname = basePathname + "/" + pathname;
                // 20061225 nodeでないPageについてもnodeと同じように相対パスを解釈した方が
                // 都合が良い（ページの本文内に子ページのリンクを埋め込む場合など）ため、nodeとの
                // 区別を止めた。
                //                if (basePage.isNode()) {
                //                    return basePage.getPathname() + "/" + pathname;
                //                } else {
                //                    return basePage.getParentPathname() + "/" + pathname;
                //                }
            } else {
                absolutePathname = "/" + pathname;
            }
        }

        if (absolutePathname.indexOf(SEGMENT_PARENT) < 0) {
            // 効率化のため。
            return absolutePathname;
        }

        int pre = 0;
        int idx;
        LinkedList<String> segmentList = new LinkedList<String>();
        while ((idx = absolutePathname.indexOf('/', pre)) >= 0) {
            String segment = absolutePathname.substring(pre, idx);
            if (segment.equals(SEGMENT_PARENT)) {
                if (!segmentList.isEmpty()) {
                    segmentList.removeLast();
                }
            } else {
                segmentList.addLast(segment);
            }
            pre = idx + 1;
        }
        String segment = absolutePathname.substring(pre);
        if (segment.equals(SEGMENT_PARENT)) {
            if (!segmentList.isEmpty()) {
                segmentList.removeLast();
            }
        } else {
            segmentList.addLast(segment);
        }
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (Iterator<String> itr = segmentList.iterator(); itr.hasNext();) {
            sb.append(delim).append(itr.next());
            delim = "/";
        }

        return sb.toString();
    }


    public static void checkIfNode(Page page)
    {
        if (page == null) {
            throw new NullPointerException();
        } else if (!page.isNode()) {
            throw new UnsupportedOperationException("It isn's a node page");
        }
    }


    public static String encodeName(String str, NameEncodeRule ner)
    {
        if (str == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        byte[] bytes;
        try {
            bytes = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }

        for (int i = 0; i < bytes.length; i++) {
            int ch;
            if (bytes[i] >= 0) {
                ch = bytes[i];
            } else {
                ch = bytes[i] + 256;
            }
            if ((ch == '%') || ner.isEncoded((char)ch)) {
                sb.append('%');
                String hex = Integer.toHexString(ch);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            } else {
                sb.append((char)ch);
            }
        }

        return sb.toString();
    }


    public static String decodeName(String encoded)
    {
        if (encoded == null) {
            return null;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int n = encoded.length();
        for (int i = 0; i < n; i++) {
            char ch = encoded.charAt(i);
            if (ch == '%') {
                if (i < n - 2) {
                    char hi = encoded.charAt(++i);
                    char lo = encoded.charAt(++i);
                    int chr = Character.digit(hi, 16) * 16
                        + Character.digit(lo, 16);
                    out.write(chr);
                } else {
                    break;
                }
            } else {
                out.write(ch);
            }
        }

        try {
            return out.toString("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }


    public static String encodePathname(Page basePage, String pathname)
    {
        if (basePage == null) {
            return pathname;
        }
        if (pathname == null) {
            return null;
        }

        String basePathname = basePage.getPathname();
        if (pathname.equals(basePathname)) {
            return ".";
        } else if (pathname.startsWith(basePathname + "/")) {
            return pathname.substring(basePathname.length() + 1);
        }

        Page page = basePage;
        StringBuilder prefix = new StringBuilder();
        prefix.append(SYMBOL_LORD);
        Page lord;
        while (!(lord = page.getLord()).isRoot()) {
            String lordPathname = lord.getPathname();
            if (pathname.equals(lordPathname)) {
                return prefix.toString();
            } else if (pathname.startsWith(lordPathname + "/")) {
                return prefix.append(pathname.substring(lordPathname.length()))
                    .toString();
            }
            page = lord.getParent();
            prefix.append("/").append(SYMBOL_LORD);
        }
        return pathname;
    }


    public static Page decodeToPage(Page basePage, String encodedPathname)
    {
        if (encodedPathname == null) {
            return null;
        }

        PageAlfr alfr = getPageAlfr();
        if (basePage == null) {
            int heimId = PathId.HEIM_MIDGARD;
            if (encodedPathname.length() == 0
                || encodedPathname.startsWith("/")) {
                // 絶対パス名。
                return alfr.getPage(heimId, encodedPathname);
            } else if (encodedPathname.startsWith(SYMBOL_LORD)) {
                // Lordからの相対パス名。
                return alfr.getPage(heimId, stripLordPrefix(encodedPathname));
            } else if (encodedPathname.equals(".")) {
                // ベースページ自体。
                return alfr.getRootPage(heimId);
            } else {
                // 相対パス名。
                return alfr.getPage(heimId, "/" + encodedPathname);
            }
        }

        int heimId = basePage.getHeimId();
        if (encodedPathname.length() == 0 || encodedPathname.startsWith("/")) {
            // 絶対パス名。
            return getPageAndFindInMidgardUnlessExists(alfr, heimId,
                encodedPathname);
        } else if (encodedPathname.startsWith(SYMBOL_LORD)) {
            // Lordからの相対パス名。
            String path = stripLordPrefix(encodedPathname);
            Page page = basePage;
            Page lord;
            while (!(lord = page.getLord()).isRoot()) {
                Page p = alfr.getPage(heimId, lord.getPathname() + path);
                if (p != null) {
                    return p;
                }
                page = lord.getParent();
            }
            return getPageAndFindInMidgardUnlessExists(alfr, heimId, path);
        } else if (encodedPathname.equals(".")) {
            // ベースページ自体。
            // basePageがnodeかどうかは見ていないことに注意。
            // 特殊なケースではbasePageがnodeかどうかが変わりうる
            // ため、それに影響されないようにこうしている。
            return basePage;
        } else {
            // 相対パス名。
            // basePageがnodeかどうかは見ていないことに注意。
            // 特殊なケースではbasePageがnodeかどうかが変わりうる
            // ため、それに影響されないようにこうしている。
            Page decoded = getPageAndFindInMidgardUnlessExists(alfr, heimId,
                basePage.getPathname() + "/" + encodedPathname);
            if (decoded == null) {
                decoded = getPageAndFindInMidgardUnlessExists(alfr, heimId, "/"
                    + encodedPathname);
            }
            return decoded;
        }
    }


    static Page getPageAndFindInMidgardUnlessExists(PageAlfr alfr, int heimId,
        String pathname)
    {
        Page page = alfr.getPage(heimId, pathname);
        if (page == null && heimId != PathId.HEIM_MIDGARD) {
            // 見つからなかったのでMIDGARDも探す。
            page = alfr.getPage(PathId.HEIM_MIDGARD, pathname);
        }
        return page;
    }


    public static Page decodeToPage(Class<?> clazz, Page basePage,
        String encodedPathname)
    {
        Page page = decodeToPage(basePage, encodedPathname);
        if (page != null && !clazz.isAssignableFrom(page.getClass())) {
            page = null;
        }
        return page;
    }


    public static String getFilePath(int pageId)
    {
        String id = String.valueOf(pageId);
        StringBuffer sb = new StringBuffer();
        for (int i = (3 - id.length() % 3) % 3; i > 0; i--) {
            sb.append('0');
        }
        sb.append(id);
        StringBuffer path = new StringBuffer();
        for (int i = sb.length() / 3; i > 0; i--) {
            int pos = i * 3;
            path.append('/');
            path.append(sb.substring(pos - 3, pos));
        }
        return path.toString();
    }


    public static boolean isLooped(Page fromPage, Page toParentPage)
    {
        return isLooped(fromPage.getPathname(), toParentPage.getPathname());
    }


    public static boolean isLooped(String fromPathname, String toParentPathname)
    {
        return (toParentPathname.equals(fromPathname) || toParentPathname
            .startsWith(fromPathname + "/"));
    }


    /*
     * private scope methods
     */

    /*
     * @/@/../@/pathから@/@/../@を取り除いて/pathにします。
     */
    private static String stripLordPrefix(String pathname)
    {
        int len = pathname.length();
        if (len == 0 || pathname.charAt(0) != SYMBOL_LORD_CHAR) {
            return pathname;
        }

        int i;
        for (i = 1; i < len; i += 2) {
            if (pathname.charAt(i) == '/' && i + 1 < len
                && pathname.charAt(i + 1) == SYMBOL_LORD_CHAR) {
                continue;
            } else {
                break;
            }
        }
        return pathname.substring(i);
    }


    /*
     * inner classes
     */

    public static interface NameEncodeRule
    {
        boolean isEncoded(char ch);
    }


    /**
     * 指定された「yyyy-MM-dd HH:mm:ss」形式の文字列をDateオブジェクトに変換します。
     * <p>文字列が「yyyy-MM-dd HH:mm:ss」形式でない場合や、
     * 例えば「2010-03-50 00:00:00」のような実際に存在しない日付の場合は
     * ParseExceptionがスローされます。
     * </p>
     * <p>文字列の前後の空白は自動的に除去されます。</p>
     * 
     * @param dateString 「yyyy-MM-dd HH:mm:ss」形式の文字列。
     * 空文字列を指定した場合やnullを指定した場合はnullが返されます。
     * @return Dateオブジェクト。
     * @throws ParseException 形式が不正だった場合。実際に存在しない日付の場合。
     */
    public static Date parseDate(String dateString)
        throws ParseException
    {
        if (dateString == null) {
            return null;
        }
        dateString = dateString.trim();
        if (dateString.length() == 0) {
            return null;
        }
        DateFormat format = new SimpleDateFormat(Page.DATEFORMAT);
        ParsePosition position = new ParsePosition(0);
        Date parsed = format.parse(dateString, position);
        if (position.getIndex() < dateString.length()
            || !format.format(parsed).equals(dateString)) {
            throw new ParseException(dateString, position.getIndex());
        }
        return parsed;
    }


    /**
     * 指定されたDateオブジェクトをPage標準の日付文字列に変換します。
     * <p>変換結果の日付文字列は「yyyy-MM-dd HH:mm:ss」形式の文字列です。
     * </p>
     * 
     * @param date Dateオブジェクト。
     * nullが渡された場合はnullを返します。
     * @return 日付文字列。
     * @see Page#DATEFORMAT
     */
    public static String formatDate(Date date)
    {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(Page.DATEFORMAT).format(date);
    }


    public static boolean isValidName(String name)
    {
        return getPlugin().isValidName(name);
    }


    /**
     * 指定されたパス名を絶対パス名に変換します。
     * <p>パス名としてnullや相対パス名を指定した場合は変換せず元のパス名をそのまま返します。
     * </p>
     * 
     * @param pathname パス名。
     * @param basePathnameTokens 変換の基準となるパス名をトークンに分解したもの。
     * @return 相対パス名。
     */
    public static String toRelativePathname(String pathname,
        String[] basePathnameTokens)
    {
        if (pathname == null || !pathname.startsWith("/")) {
            // 絶対パス名でないので何もしない。
            return pathname;
        }

        String[] tokens = tokenizePathname(pathname);

        int length = Math.min(tokens.length - 1, basePathnameTokens.length - 1);
        int i;
        for (i = 0; tokens[i].equals(basePathnameTokens[i]) && i < length; i++) {
        }

        StringBuilder sb = new StringBuilder();
        // 共通の祖先ディレクトリまでさかのぼる回数。
        int depth = (basePathnameTokens.length - 2) - (i - 1);
        if (depth == 0) {
            sb.append("./");
        } else {
            for (int j = 0; j < depth; j++) {
                sb.append("../");
            }
        }

        String delim = "";
        for (int j = i; j < tokens.length; j++) {
            sb.append(delim).append(tokens[j]);
            delim = "/";
        }

        return sb.toString();
    }


    /**
     * パス名をトークンに分解します。
     * <p>例えば"/a/b/c.html"は"", "a", "b", "c.html"に分解されます。
     * </p>
     * @param pathname パス名。
     * @return トークンの配列。
     */
    public static String[] tokenizePathname(String pathname)
    {
        return pathname.split("/", -1);
    }


    /**
     * 指定されたPageの配列からランダムにPageを取り出して並べた配列を返します。
     * <p>元の配列の中身は変更されません。
     * </p>
     * 
     * @param pages Pageの配列。nullを指定してはいけません。
     * @param length 結果の配列の長さ。
     * 引数で与えられた配列よりも長い長さを指定した場合、引数で与えられた配列と同じ長さになります。
     * </p>
     * 
     * @return 配列。
     * nullが返されることはありません。
     */
    public static Page[] randomize(Page[] pages, int length)
    {
        List<Page> randomized = new ArrayList<Page>();
        LinkedList<Page> list = new LinkedList<Page>(Arrays.asList(pages));
        if (length < 0 || length > pages.length) {
            length = pages.length;
        }
        for (int i = 0; i < length; i++) {
            randomized.add(list.remove((int)(Math.random() * list.size())));
        }
        return randomized.toArray(new Page[0]);
    }
}
