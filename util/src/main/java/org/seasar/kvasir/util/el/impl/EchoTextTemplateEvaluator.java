package org.seasar.kvasir.util.el.impl;

import org.seasar.kvasir.util.el.EvaluationException;
import org.seasar.kvasir.util.el.TextTemplateEvaluator;
import org.seasar.kvasir.util.el.VariableResolver;


/**
 * 評価を行わず、引数に渡されたtextをそのまま返却するTextTemplateEvaluatorです。
 * 
 * @author manhole
 */
public class EchoTextTemplateEvaluator
    implements TextTemplateEvaluator
{

    public Object evaluate(final String text, final VariableResolver resolver)
        throws EvaluationException
    {
        return text;
    }


    public Object evaluate(final String text, final VariableResolver resolver,
        final Object defaultValue)
    {
        return text;
    }


    public String evaluateAsString(final String text,
        final VariableResolver resolver)
        throws EvaluationException
    {
        return text;
    }


    public String evaluateAsString(final String text,
        final VariableResolver resolver, final String defaultValue)
    {
        return text;
    }

}
