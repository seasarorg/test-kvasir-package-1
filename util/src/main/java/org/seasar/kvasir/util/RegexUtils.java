package org.seasar.kvasir.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.el.EvaluationException;
import org.seasar.kvasir.util.el.TextTemplateEvaluator;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.el.impl.PropertyHandlerVariableResolver;
import org.seasar.kvasir.util.el.impl.SimpleTextTemplateEvaluator;


/**
 * @author YOKOTA Takehiko
 */
public class RegexUtils
{
    private static final TextTemplateEvaluator  DEFAULT_EVALUATOR
        = new SimpleTextTemplateEvaluator();


    private RegexUtils()
    {
    }


    public static String convert(Pattern pattern, String template, 
        String target)
    {
        return convert(pattern, template, DEFAULT_EVALUATOR, target);
    }


    public static MapProperties find(Pattern pattern, String target)
    {
        Matcher matcher = pattern.matcher(target);
        if (matcher.find()) {
            MapProperties prop = new MapProperties();
            int count = matcher.groupCount();
            for (int j = 0; j <= count; j++) {
                String matched = matcher.group(j);
                prop.setProperty(String.valueOf(j), matched);
                prop.setProperty(j + "u", upper(matched));
                prop.setProperty(j + "l", lower(matched));
            }
            prop.setProperty("`",
                target.substring(0, matcher.start()));
            prop.setProperty("&",
                target.substring(matcher.start(), matcher.end()));
            prop.setProperty("'",
                target.substring(matcher.end()));

            return prop;
        } else {
            return null;
        }
    }


    public static String convert(Pattern pattern, String template, 
        TextTemplateEvaluator evaluator, String target)
    {
        PropertyHandler prop = find(pattern, target);
        if (prop != null) {
            VariableResolver resolver
                = new PropertyHandlerVariableResolver(prop);

            try {
                return evaluator.evaluateAsString(template, resolver);
            } catch (EvaluationException ex) {
                throw (IllegalArgumentException)new IllegalArgumentException(
                    "Can't convert: " +
                    "pattern=" + pattern + ", template=" + template +
                    "target=" + target).initCause(ex);
            }
        } else {
            return null;
        }
    }


    /*
     * private scope methods
     */

    private static String upper(String str)
    {
        if (str == null) {
            return null;
        } else if (str.length() == 0) {
            return str;
        } else {
            return Character.toUpperCase(str.charAt(0)) + str.substring(1);
        }
    }


    private static String lower(String str)
    {
        if (str == null) {
            return null;
        } else if (str.length() == 0) {
            return str;
        } else {
            return Character.toLowerCase(str.charAt(0)) + str.substring(1);
        }
    }
}
