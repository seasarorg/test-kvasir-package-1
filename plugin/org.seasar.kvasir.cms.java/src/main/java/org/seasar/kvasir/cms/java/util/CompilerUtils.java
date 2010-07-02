package org.seasar.kvasir.cms.java.util;

import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.seasar.kvasir.cms.java.CompileException;
import org.seasar.kvasir.cms.java.JavaPlugin;
import org.seasar.kvasir.util.io.IORuntimeException;


public class CompilerUtils
{
    private static final Pattern PATTERN_PACKAGE = Pattern
        .compile("^package\\s+([a-zA-Z_0-9\\.]+)\\s*;");

    private static final Pattern PATTERN_CLASS = Pattern
        .compile("(\\s+class\\s+)([a-zA-Z_0-9]+)(\\s)");

    private static final Pattern PATTERN_ROOTPACKAGENAME = Pattern
        .compile("^rootPackageName\\s*=\\s*([a-zA-Z_0-9\\.]+)\\s*");

    private static final String SUFFIX_ROOTCLASS = "_Root";


    private CompilerUtils()
    {
    }


    @SuppressWarnings("unchecked")
    public static <T> Class<T> compile(JavaPlugin plugin, String localPathname,
        String classNameSuffix, Class<T> superClass, String source)
    {
        Class<T> clazz;

        Matcher rootPackageNameMatcher = PATTERN_ROOTPACKAGENAME
            .matcher(source);
        if (rootPackageNameMatcher.find()) {
            // クラスローダからJavaクラスを探すモード。
            String className = getClassName(rootPackageNameMatcher.group(1),
                localPathname, classNameSuffix);
            try {
                clazz = (Class<T>)Thread.currentThread()
                    .getContextClassLoader().loadClass(className);
            } catch (ClassNotFoundException ex) {
                throw new IORuntimeException("Class corresponding path ("
                    + localPathname + ") does not exist: " + className, ex);
            }
        } else {
            try {
                Matcher packageMatcher = PATTERN_PACKAGE.matcher(source);
                Matcher classMatcher = PATTERN_CLASS.matcher(source);
                if (packageMatcher.find() && classMatcher.find()) {
                    // クラスパス上に同名のクラスがあっても優先されるようにクラス名を変更しておく。
                    String suffix = String.valueOf(System.currentTimeMillis());
                    String classSimpleName = classMatcher.group(2) + suffix;
                    String className = packageMatcher.group(1) + "."
                        + classSimpleName;
                    source = classMatcher.replaceFirst(classMatcher.group(1)
                        + classSimpleName + classMatcher.group(3));
                    try {
                        clazz = (Class<T>)plugin.compile(
                            new StringReader(source),
                            Thread.currentThread().getContextClassLoader())
                            .loadClass(className);
                    } catch (ClassNotFoundException ex) {
                        throw new IORuntimeException(ex);
                    }
                } else {
                    clazz = (Class<T>)plugin.compileClassBody(new StringReader(
                        source),
                        superClass != null ? superClass : Object.class, Thread
                            .currentThread().getContextClassLoader());
                }
            } catch (CompileException ex) {
                throw new IllegalArgumentException(ex);
            }
        }

        return clazz;
    }


    static String getClassName(String rootPackageName, String localPathname,
        String classNameSuffix)
    {
        String className;
        int slash = localPathname.lastIndexOf('/');
        if (slash >= 0) {
            String dir = localPathname.substring(0, slash + 1);
            String name = localPathname.substring(slash + 1);
            char ch = name.charAt(0);
            if ('0' <= ch && ch <= '9') {
                name = "_" + name;
            }
            className = rootPackageName + dir.replace('/', '.')
                + capitalize(name.replace('.', '_')) + classNameSuffix;
        } else {
            className = rootPackageName + "." + SUFFIX_ROOTCLASS
                + classNameSuffix;
        }
        return className;
    }


    static String capitalize(String name)
    {
        if (name.length() == 0) {
            return name;
        } else {
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
    }
}
