package org.seasar.kvasir.util.collection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * 任意の<code>java.util.Map</code>オブジェクトをベースにすることのできる
 * <code>java.util.Properties</code>クラスの代替クラスです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class MapProperties
    implements PropertyHandler
{
    private static final String[] PADDINGS = new String[] { null, "000", "00",
        "0", "" };

    private PropertyHandler[] defaults_;

    private Map map_;

    private boolean treatingValueNotSpecifiedAsNullValue_;

    private String[] defaultPropertyNames_;


    /*
     * constructors
     */

    public MapProperties()
    {
        this(new Hashtable(), null, false);
    }


    public MapProperties(Map map)
    {
        this(map, null, false);
    }


    public MapProperties(MapProperties parent)
    {
        this(new MapProperties[] { parent });
    }


    public MapProperties(Map map, PropertyHandler parent)
    {
        this(map, new PropertyHandler[] { parent });
    }


    public MapProperties(PropertyHandler[] parents)
    {
        this(new Hashtable(), parents, false);
    }


    public MapProperties(Map map, PropertyHandler[] parents)
    {
        this(map, parents, false);
    }


    public MapProperties(Map map, PropertyHandler[] parents,
        boolean treatingValueNotSpecifiedAsNullValue)
    {
        if (parents != null && parents.length == 0) {
            parents = null;
        }
        map_ = map;
        defaults_ = parents;
        treatingValueNotSpecifiedAsNullValue_ = treatingValueNotSpecifiedAsNullValue;
    }


    /*
     * public scope methods
     */

    public String getProperty(String name)
    {
        return getProperty(name, true);
    }


    public String getProperty(String name, boolean recursive)
    {
        if (map_.containsKey(name)) {
            return (String)map_.get(name);
        }
        if (recursive && defaults_ != null) {
            for (int i = 0; i < defaults_.length; i++) {
                if (defaults_[i] == null) {
                    continue;
                }
                String value = (String)defaults_[i].getProperty(name);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }


    public String getProperty(String name, String defaultValue)
    {
        String value = getProperty(name);
        if (value == null) {
            value = defaultValue;
        }

        return value;
    }


    public boolean containsPropertyName(String name)
    {
        return containsPropertyName(name, true);
    }


    public boolean containsPropertyName(String name, boolean recursive)
    {
        if (map_.containsKey(name)) {
            return true;
        }
        if (recursive && defaults_ != null) {
            for (int i = 0; i < defaults_.length; i++) {
                if (defaults_[i] == null) {
                    continue;
                }
                if (defaults_[i].containsPropertyName(name)) {
                    return true;
                }
            }
        }
        return false;
    }


    public int size()
    {
        return map_.size();
    }


    public void load(InputStream in)
        throws IOException
    {
        load(in, "ISO-8859-1");
    }


    public void load(InputStream in, String encoding)
        throws UnsupportedEncodingException, IOException
    {
        load(new InputStreamReader(in, encoding));
    }


    public void load(Reader in)
        throws IOException
    {
        BufferedReader reader;
        if (in instanceof BufferedReader) {
            reader = (BufferedReader)in;
        } else {
            reader = new BufferedReader(in);
        }
        try {
            String line;
            boolean continuing = false;
            String name = null;
            StringBuffer sb = null;
            while ((line = reader.readLine()) != null) {
                String tline = trimLeft(line);
                if (!continuing) {
                    if (tline.length() == 0 || tline.startsWith("#")
                        || tline.startsWith("!")) {
                        continue;
                    }
                    Pair pair = unescape(tline, "=: ");
                    name = pair.unescaped;
                    String value;
                    if (pair.nextIndex == tline.length()) {
                        if (treatingValueNotSpecifiedAsNullValue_) {
                            value = null;
                        } else {
                            value = "";
                        }
                    } else {
                        String tail = trimLeft(tline.substring(pair.nextIndex));
                        if (tail.length() == 0) {
                            if (treatingValueNotSpecifiedAsNullValue_) {
                                value = null;
                            } else {
                                value = "";
                            }
                        } else {
                            char ch = tail.charAt(0);
                            if (ch == '=' || ch == ':') {
                                tail = trimLeft(tail.substring(1));
                            }
                            pair = unescape(tail, "");
                            if (pair.endsWithBackSlash) {
                                continuing = true;
                                if (sb == null) {
                                    sb = new StringBuffer(pair.unescaped
                                        .length() * 3 + 1);
                                }
                                sb.append(pair.unescaped);
                                continue;
                            } else {
                                value = pair.unescaped;
                            }
                        }
                    }
                    map_.put(name.intern(), (value != null ? value.intern()
                        : null));
                    name = null;
                } else {
                    Pair pair = unescape(tline, "");
                    sb.append(pair.unescaped);
                    if (!pair.endsWithBackSlash) {
                        map_.put(name.intern(), sb.toString().intern());
                        sb.delete(0, sb.length());
                        name = null;
                        continuing = false;
                    }
                }
            }
            if (name != null) {
                map_.put(name.intern(), sb.toString().intern());
            }
        } finally {
            try {
                reader.close();
            } catch (Throwable t) {
                ;
            }
        }
    }


    public Enumeration propertyNames()
    {
        return propertyNames(true);
    }


    public Enumeration propertyNames(boolean recursive)
    {
        if (!recursive || defaults_ == null) {
            return Collections.enumeration(map_.keySet());
        } else {
            Set set = new HashSet();
            if (defaultPropertyNames_ == null) {
                for (int i = 0; i < defaults_.length; i++) {
                    for (Enumeration enm = defaults_[i].propertyNames(); enm
                        .hasMoreElements();) {
                        set.add(enm.nextElement());
                    }
                }
                defaultPropertyNames_ = (String[])set.toArray(new String[0]);
            } else {
                for (int i = 0; i < defaultPropertyNames_.length; i++) {
                    set.add(defaultPropertyNames_[i]);
                }
            }
            for (Iterator itr = map_.keySet().iterator(); itr.hasNext();) {
                set.add(itr.next());
            }
            return Collections.enumeration(set);
        }
    }


    public void setProperty(String name, String value)
    {
        map_.put(name.intern(), (value != null ? value.intern() : null));
    }


    public void removeProperty(String name)
    {
        map_.remove(name);
    }


    public void clearProperties()
    {
        map_.clear();
    }


    public void store(OutputStream out)
        throws IOException
    {
        store(out, "ISO-8859-1");
    }


    public void store(OutputStream out, String encoding)
        throws UnsupportedEncodingException, IOException
    {
        boolean unicodeEscape = "ISO-8859-1".equalsIgnoreCase(encoding);

        store(new OutputStreamWriter(out, encoding), unicodeEscape);
    }


    public void store(Writer out)
        throws IOException
    {
        store(out, false);
    }


    public void store(Writer out, boolean unicodeEscape)
        throws IOException
    {
        BufferedWriter writer;
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter)out;
        } else {
            writer = new BufferedWriter(out);
        }

        Iterator itr = map_.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry entry = (Map.Entry)itr.next();
            String name = (String)entry.getKey();
            String value = (String)entry.getValue();
            writer.write(escape(name, unicodeEscape));
            if (treatingValueNotSpecifiedAsNullValue_ && value == null) {
                ;
            } else {
                writer.write("=");
                writer.write(escape(value, unicodeEscape));
            }
            writer.newLine();
        }
        writer.flush();
    }


    /*
     * private scope methods
     */

    private String escape(String str, boolean unicodeEscape)
    {
        int n = str.length();
        StringBuffer sb = new StringBuffer(n * 3 + 1);
        for (int i = 0; i < n; i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                sb.append("\\\\");
            } else if (ch == '\t') {
                sb.append("\\t");
            } else if (ch == '\n') {
                sb.append("\\n");
            } else if (ch == '\r') {
                sb.append("\\r");
            } else if (ch < 0x20 || ch > 0x7e) {
                if (!unicodeEscape && ch > 0xff) {
                    sb.append(ch);
                } else {
                    sb.append("\\u");
                    String hex = Integer.toHexString(ch);
                    sb.append(PADDINGS[hex.length()]);
                    sb.append(hex);
                }
            } else if (ch == '#' || ch == '!' || ch == '=' || ch == ':') {
                sb.append("\\");
                sb.append(ch);
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }


    private Pair unescape(String str, String delims)
    {
        int n = str.length();
        StringBuffer sb = new StringBuffer(n + 1);
        Pair pair = new Pair();
        pair.nextIndex = n;
        for (int i = 0; i < n; i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                if (i + 1 == n) {
                    pair.endsWithBackSlash = true;
                } else {
                    ch = str.charAt(++i);
                    if (ch == 't') {
                        sb.append('\t');
                    } else if (ch == 'n') {
                        sb.append('\n');
                    } else if (ch == 'r') {
                        sb.append('\r');
                    } else if (ch == 'u') {
                        sb.append((char)Integer.parseInt(str.substring(i + 1,
                            i + 5), 16));
                        i += 4;
                    } else {
                        sb.append(ch);
                    }
                }
            } else if (delims.indexOf(ch) >= 0) {
                pair.nextIndex = i;
                break;
            } else {
                sb.append(ch);
            }
        }
        pair.unescaped = sb.toString();

        return pair;
    }


    private String trimLeft(String str)
    {
        int n = str.length();
        for (int i = 0; i < n; i++) {
            char ch = str.charAt(i);
            if (ch > 0x20) {
                return (i == 0 ? str : str.substring(i));
            }
        }
        return "";
    }


    /*
     * inner classes
     */

    private static class Pair
    {
        public String unescaped;

        public int nextIndex;

        public boolean endsWithBackSlash;
    }
}
