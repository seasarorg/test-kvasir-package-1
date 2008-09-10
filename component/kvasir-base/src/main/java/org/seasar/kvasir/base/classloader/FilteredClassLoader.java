package org.seasar.kvasir.base.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import org.seasar.kvasir.util.AntUtils;


/**
 * 指定したクラスローダが持つクラスやリソースのうち、指定したパターンにマッチするものだけが取り出せるような
 * クラスローダです。
 * <p>パターンとしては、Ant類似のパターンに加えて「!」をつけた否定表現を指定することができます。
 * </p>
 * <p>パターンを指定しなかった場合は何も取り出すことはできません。
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class FilteredClassLoader extends ClassLoader
{
    private static final String NOT_PREFIX = "!";

    private static final String CLASS_SUFFIX = ".class";

    private PatternBag[] classPatternBags_;

    private PatternBag[] resourcePatternBags_;

    private Map<String, Boolean> matchedMap_ = Collections
        .synchronizedMap(new HashMap<String, Boolean>());


    public FilteredClassLoader(ClassLoader classLoader,
        String[] classPatternStrings, String[] resourcePatternStrings)
    {
        super(classLoader);
        classPatternBags_ = compilePatterns(classPatternStrings, '.');
        resourcePatternBags_ = compilePatterns(resourcePatternStrings, '/');
    }


    PatternBag[] compilePatterns(String[] patternStrings, char delimiter)
    {
        PatternBag[] bags = new PatternBag[patternStrings.length];
        for (int i = 0; i < patternStrings.length; i++) {
            bags[i] = new PatternBag(patternStrings[i], delimiter);
        }
        return bags;
    }


    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("Filtered( classPatterns={ ");
        String delim = "";
        for (int i = 0; i < classPatternBags_.length; i++) {
            sb.append(delim).append('"').append(
                classPatternBags_[i].getString()).append('"');
            delim = ", ";
        }
        sb.append(" }, resourcePatterns={ ");
        delim = "";
        for (int i = 0; i < resourcePatternBags_.length; i++) {
            sb.append(delim).append('"').append(
                resourcePatternBags_[i].getString()).append('"');
            delim = ", ";
        }
        sb.append(" }, parent=").append(getParent()).append(" )");
        return sb.toString();
    }


    public synchronized Class<?> loadClass(String name)
        throws ClassNotFoundException
    {
        if (!isClassMatched(name)) {
            throw new ClassNotFoundException(name);
        }
        return super.loadClass(name);
    }


    public URL getResource(String name)
    {
        if (!isResourceMatched(name)) {
            return null;
        }
        return super.getResource(name);
    }


    public Enumeration<URL> getResources(String name)
        throws IOException
    {
        if (!isResourceMatched(name)) {
            return new Vector<URL>().elements();
        }
        return super.getResources(name);
    }


    public boolean isClassMatched(String name)
    {
        Boolean matched = matchedMap_.get(name);
        if (matched != null) {
            return matched.booleanValue();
        }

        if (!hasClassEntry(name)) {
            return false;
        }
        return addEntryToMatchedMap(name, classPatternBags_, '.');
    }


    boolean hasClassEntry(String name)
    {
        try {
            super.loadClass(name, false);
            return true;
        } catch (NoClassDefFoundError e) {
            // クラスが存在した場合で、そのクラスが参照できない別の
            // クラスに依存している場合にはこのErrorがスローされる。
            // （resolve=falseでも、親クラスや実装しているインタフェースは参照できる必要がある。）
            return true;
        } catch (ClassNotFoundException ex) {
            ;
        }
        return false;
    }


    boolean isResourceMatched(String name)
    {
        Boolean matched = matchedMap_.get(name);
        if (matched != null) {
            return matched.booleanValue();
        }

        if (name.endsWith(CLASS_SUFFIX)) {
            if (isClassMatched(name.substring(0,
                name.length() - CLASS_SUFFIX.length()).replace('/', '.'))) {
                return true;
            }
        }
        if (!hasResourceEntry(name)) {
            return false;
        }
        return addEntryToMatchedMap(name, resourcePatternBags_, '/');
    }


    boolean hasResourceEntry(String name)
    {
        return (super.getResource(name) != null);
    }


    boolean addEntryToMatchedMap(String name, PatternBag[] patternBags,
        char delimiter)
    {
        Boolean matched = Boolean.FALSE;
        for (int i = 0; i < patternBags.length; i++) {
            if (patternBags[i].matches(name)) {
                matched = (patternBags[i].isNot() ? Boolean.FALSE
                    : Boolean.TRUE);
                break;
            }
        }
        matchedMap_.put(name, matched);

        return matched.booleanValue();
    }


    static class PatternBag
    {
        private String string_;

        private Pattern pattern_;

        private boolean not_;


        public PatternBag(String patternString, char delimiter)
        {
            string_ = patternString;
            if (patternString.startsWith(NOT_PREFIX)) {
                patternString = patternString.substring(NOT_PREFIX.length());
                not_ = true;
            } else {
                not_ = false;
            }
            pattern_ = Pattern.compile(AntUtils
                .buildRegexPatternStringFromPattern(patternString, delimiter));
        }


        @Override
        public String toString()
        {
            return getString();
        }


        public String getString()
        {
            return string_;
        }


        public boolean matches(String input)
        {
            return pattern_.matcher(input).matches();
        }


        public boolean isNot()
        {
            return not_;
        }
    }


    String[] getClassPatternStrings()
    {
        return getPatternStrings(classPatternBags_);
    }


    public String[] getResourcePatternStrings()
    {
        return getPatternStrings(resourcePatternBags_);
    }


    String[] getPatternStrings(PatternBag[] bags)
    {
        String[] strings = new String[bags.length];
        for (int i = 0; i < bags.length; i++) {
            strings[i] = bags[i].getString();
        }
        return strings;
    }
}