package org.seasar.kvasir.util.el.impl;

import java.util.HashSet;
import java.util.Set;

import org.seasar.kvasir.util.collection.PropertiesUtils;
import org.seasar.kvasir.util.el.EvaluationException;
import org.seasar.kvasir.util.el.TextTemplateEvaluator;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.html.HTMLUtils;


/**
 * <p><b>同期化：</b>
 * このクラスのサブクラスはスレッドセーフである必要があります。</p>
 * 
 * @author YOKOTA Takehiko
 */
abstract public class ParametrizedTextTemplateEvaluator
   implements TextTemplateEvaluator
{
    public static final String      PAREN_BEGIN = "${";
    public static final String      PAREN_END = "}";
    public static final String      OPTION_BEGIN = "(";
    public static final String      OPTION_END = ")";

    public static final String      TYPE_PLAIN = "plain";
    public static final String      TYPE_XML = "xml";
    public static final String      TYPE_PROPERTIES = "properties";


    private boolean     evaluateRecursively_;


    abstract protected Object evaluateExpression(String expression,
        VariableResolver resolver)
            throws EvaluationException;


    protected ParametrizedTextTemplateEvaluator()
    {
        this(false);
    }


    protected ParametrizedTextTemplateEvaluator(boolean evaluateRecursively)
    {
        evaluateRecursively_ = evaluateRecursively;
    }


    /*
     * TextTemplateEvaluator
     */

    public Object evaluate(String text, VariableResolver resolver)
        throws EvaluationException
    {
        return evaluateText(text, resolver, null);
    }


    public Object evaluate(String text, VariableResolver resolver,
        Object defaultValue)
    {
        try {
            return evaluate(text, resolver);
        } catch (EvaluationException ex) {
            return defaultValue;
        }
    }


    public String evaluateAsString(String text, VariableResolver resolver)
        throws EvaluationException
    {
        Object evaluated = evaluate(text, resolver);
        if (evaluated != null) {
            return String.valueOf(evaluated);
        } else {
            return null;
        }
    }


    public String evaluateAsString(String text, VariableResolver resolver,
        String defaultValue)
    {
        try {
            return evaluateAsString(text, resolver);
        } catch (EvaluationException ex) {
            return defaultValue;
        }
    }


    /*
     * private scope methods
     */

    private Object evaluateText(String text, VariableResolver resolver,
        Set set)
            throws EvaluationException
    {
        if (text == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        int pre = 0;
        int idx;
        while ((idx = text.indexOf(PAREN_BEGIN, pre)) >= 0) {
            sb.append(text.substring(pre, idx));
            idx += PAREN_BEGIN.length();
            int end = text.indexOf(PAREN_END, idx);
            if (end < 0) {
                throw new EvaluationException("Missing end paren after: "
                    + text.substring(idx));
            }
            pre = end + PAREN_END.length();
            String key = text.substring(idx, end).trim();
            String type = TYPE_PLAIN;
            if (key.startsWith(OPTION_BEGIN)) {
                int optionEnd = key.indexOf(OPTION_END, OPTION_BEGIN.length());
                if (optionEnd >= 0) {
                    type = key.substring(
                        OPTION_BEGIN.length(), optionEnd).trim();
                    key = key.substring(optionEnd + OPTION_END.length()).trim();
                }
            }
            if (set != null && set.contains(key)) {
                throw new EvaluationException(
                    "Loop detected: key=" + key);
            }
            Object evaluated = evaluateExpression(key, resolver);
            if (evaluateRecursively_ && evaluated instanceof String
            && ((String)evaluated).indexOf(PAREN_BEGIN) >= 0) {
                String k = (String)evaluated;
                if (set == null) {
                    set = new HashSet();
                }
                set.add(key);
                evaluated = evaluateText(k, resolver, set);
                set.remove(key);
            }
            if (idx == PAREN_BEGIN.length() && pre == text.length()) {
                // このケースではtypeを無視している。
                return evaluated;
            }
            if (TYPE_PLAIN.equals(type)) {
                ;
            } else if (TYPE_XML.equals(type)) {
                evaluated = HTMLUtils.filter(String.valueOf(evaluated));
            } else if (TYPE_PROPERTIES.equals(type)) {
                evaluated = PropertiesUtils.escape(String.valueOf(evaluated));
            } else {
                throw new EvaluationException("Unknown filter type: "
                    + type);
            }
            sb.append(evaluated);
        }
        if (pre < text.length()) {
            sb.append(text.substring(pre));
        }

        return sb.toString();
    }
}
