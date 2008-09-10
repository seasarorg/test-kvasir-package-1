package org.seasar.kvasir.util.el.impl;

import org.seasar.kvasir.util.el.EvaluationException;
import org.seasar.kvasir.util.el.VariableResolver;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class SimpleTextTemplateEvaluator extends ParametrizedTextTemplateEvaluator
{
    public SimpleTextTemplateEvaluator()
    {
        super();
    }


    public SimpleTextTemplateEvaluator(boolean evaluateRecursively)
    {
        super(evaluateRecursively);
    }


    protected Object evaluateExpression(String expression,
        VariableResolver resolver)
            throws EvaluationException
    {
        return resolver.getValue(expression);
    }
}
