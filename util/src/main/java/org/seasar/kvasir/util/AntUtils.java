package org.seasar.kvasir.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.seasar.kvasir.util.io.Resource;


public class AntUtils
{
    private static final String QUOTED_CHARS = "[]{}()|^$+*.";


    private AntUtils()
    {
    }


    public static String buildRegexPatternStringFromPattern(String pattern,
        char delimiter)
    {
        String delimiterRegex;
        if (QUOTED_CHARS.indexOf(delimiter) >= 0) {
            delimiterRegex = "\\" + String.valueOf(delimiter);
        } else {
            delimiterRegex = String.valueOf(delimiter);
        }

        StringBuffer sb = new StringBuffer();
        for (Window window = new Window(pattern, 4); window.canShift(); window
            .shift()) {
            if (window.charAt(0) == '*') {
                if (window.charAt(1) == '*') {
                    if (window.charAt(2) == delimiter) {
                        if (window.charAt(3) == '\0') {
                            // **/$
                            sb.append(".*");
                        } else {
                            // **/
                            sb.append("(.*").append(delimiterRegex)
                                .append(")?");
                        }
                        window.shift(2);
                        continue;
                    } else if (window.charAt(2) == '\0') {
                        // **$
                        sb.append(".*");
                        window.shift();
                        continue;
                    } else {
                        throw new IllegalArgumentException("Illegal pattern: "
                            + pattern);
                    }
                } else {
                    // *
                    sb.append("[^").append(delimiterRegex).append("]*");
                    continue;
                }
            } else if (window.charAt(0) == delimiter) {
                if (window.charAt(1) == '*') {
                    if (window.charAt(2) == '*') {
                        if (window.charAt(3) == '\0') {
                            // /**$
                            sb.append("(").append(delimiterRegex).append(".*")
                                .append(")?");
                            window.shift(2);
                            continue;
                        }
                    }
                } else if (window.charAt(1) == '\0') {
                    // /$
                    sb.append("(").append(delimiterRegex).append(".*").append(
                        ")?");
                    continue;
                }
            } else {
                if (window.charAt(1) == '*' && window.charAt(2) == '*') {
                    throw new IllegalArgumentException("Illegal pattern: "
                        + pattern);
                }
            }

            if (window.charAt(0) == '?') {
                sb.append("[^").append(delimiterRegex).append("]");
            } else if (window.charAt(0) == '*') {
                sb.append("[^").append(delimiterRegex).append("]*");
            } else if (QUOTED_CHARS.indexOf(window.charAt(0)) >= 0) {
                sb.append('\\').append(window.charAt(0));
            } else {
                sb.append(window.charAt(0));
            }
        }

        return sb.toString();
    }


    public static boolean matches(String name, String pattern, char delimiter)
    {
        return Pattern.matches(buildRegexPatternStringFromPattern(pattern,
            delimiter), name);
    }


    public static String[] getPaths(Resource basedir, String[] includes,
        String[] excludes)
    {
        List pathList = new ArrayList();
        getPaths(basedir, null, includes, excludes, pathList);
        return (String[])pathList.toArray(new String[0]);
    }


    static void getPaths(Resource dir, String path, String[] includes,
        String[] excludes, List pathList)
    {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        Resource[] children = dir.listResources();
        for (int i = 0; i < children.length; i++) {
            String name = children[i].getName();
            if (children[i].isDirectory()) {
                getPaths(children[i],
                    (path == null ? name : path + "/" + name), includes,
                    excludes, pathList);
            } else {
                String childPath;
                if (path == null) {
                    childPath = name;
                } else {
                    childPath = path + "/" + name;
                }
                if (matches(childPath, includes, excludes)) {
                    pathList.add(childPath);
                }
            }
        }
    }


    static boolean matches(String name, String[] includes, String[] excludes)
    {
        if (includes.length > 0) {
            boolean matched = false;
            for (int i = 0; i < includes.length; i++) {
                if (matches(name, includes[i], '/')) {
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }

        for (int i = 0; i < excludes.length; i++) {
            if (matches(name, excludes[i], '/')) {
                return false;
            }
        }

        return true;
    }


    public static Resource[] getResources(Resource basedir, String[] includes,
        String[] excludes)
    {
        String[] paths = getPaths(basedir, includes, excludes);
        Resource[] resources = new Resource[paths.length];
        for (int i = 0; i < paths.length; i++) {
            resources[i] = basedir.getChildResource(paths[i]);
        }
        return resources;
    }
}
