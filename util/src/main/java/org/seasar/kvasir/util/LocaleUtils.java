package org.seasar.kvasir.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.seasar.kvasir.util.io.Resource;


/**
 * @author YOKOTA Takehiko
 */
public class LocaleUtils
{
    public static final Locale DEFAULT = new Locale("");


    private LocaleUtils()
    {
    }


    public static Resource[] findResources(Resource dir, String baseName,
        String suffix, Locale locale)
    {
        Set set = getResourceNameSet(baseName, suffix, locale);

        List list = new ArrayList(set.size() + 1);

        for (Iterator itr = set.iterator(); itr.hasNext();) {
            Resource resource = dir.getChildResource((String)itr.next());
            if (resource.exists()) {
                list.add(resource);
            }
        }

        return (Resource[])list.toArray(new Resource[0]);
    }


    public static Resource findResource(Resource dir, String baseName,
        String suffix, Locale locale)
    {
        Set set = getResourceNameSet(baseName, suffix, locale);

        for (Iterator itr = set.iterator(); itr.hasNext();) {
            Resource resource = dir.getChildResource((String)itr.next());
            if (resource.exists()) {
                return resource;
            }
        }

        return null;
    }


    static Set getResourceNameSet(String baseName, String suffix, Locale locale)
    {
        Set set = new LinkedHashSet();

        String[] suffixes = getSuffixes(locale);
        for (int i = 0; i < suffixes.length; i++) {
            String name = baseName + "_" + suffixes[i] + suffix;
            set.add(name);
        }

        // こうする必要はないかも。20051017
        //        suffixes = getSuffixes(Locale.getDefault());
        //        for (int i = 0; i < suffixes.length; i++) {
        //            String name = baseName + "_" + suffixes[i] + suffix;
        //            set.add(name);
        //        }

        set.add(baseName + suffix);

        return set;
    }


    /**
     * 指定されたロケールに関するサフィックスを、範囲の狭い順に並べて返します。
     * <p>このメソッドは<code>getSuffixes(locale, false)</code>と同じです。
     * </p>
     *  
     * @param locale ロケール。nullを渡すと空の配列が返されます。
     * @return サフィックスの配列。nullが返されることはありません。
     * @see #getSuffixes(Locale, boolean)
     */
    public static String[] getSuffixes(Locale locale)
    {
        return getSuffixes(locale, false);
    }


    /**
     * 指定されたロケールに関するサフィックスを、範囲の狭い順に並べて返します。
     * <p>例えば<code>ja_JP</code>というロケールに対しては
     * <code>{ "ja_JP", "ja", "" }</code>という文字列の配列を返します。
     * （<code>includeDefault</code>がfalseの場合は最後の空文字列は返しません。）
     * </p>
     *  
     * @param locale ロケール。nullを渡すと空の配列が返されます。
     * @param includeDefault デフォルトのサフィックス（空文字列）を結果に含めるかどうか。
     * @return サフィックスの配列。nullが返されることはありません。
     * @see #getSuffixes(Locale)
     */
    public static String[] getSuffixes(Locale locale, boolean includeDefault)
    {
        if (locale == null) {
            return new String[0];
        }
        List list = new ArrayList(3);
        String l = locale.getLanguage();
        String c = locale.getCountry();
        String v = locale.getVariant();
        if (v.length() > 0) {
            list.add(l + "_" + c + "_" + v);
        }
        if (c.length() > 0) {
            list.add(l + "_" + c);
        }
        if (l.length() > 0) {
            list.add(l);
        }
        if (includeDefault) {
            list.add("");
        }
        return (String[])list.toArray(new String[0]);
    }


    public static Locale getLocale(String str)
    {
        if (str == null) {
            return null;
        }

        int delim1 = str.indexOf('_');
        if (delim1 < 0) {
            return new Locale(str);
        }
        int delim2 = str.indexOf('_', delim1 + 1);
        if (delim2 < 0) {
            return new Locale(str.substring(0, delim1), str
                .substring(delim1 + 1));
        }
        return new Locale(str.substring(0, delim1), str.substring(delim1 + 1,
            delim2), str.substring(delim2 + 1));
    }


    public static String getString(Locale locale)
    {
        if (locale != null) {
            String string = locale.toString();
            if (string.length() == 0 && "".equals(locale.getLanguage())
                && "".equals(locale.getCountry())
                && locale.getVariant() != null
                && !"".equals(locale.getVariant())) {
                return "__" + locale.getVariant();
            } else {
                return string;
            }
        } else {
            return null;
        }
    }
}
