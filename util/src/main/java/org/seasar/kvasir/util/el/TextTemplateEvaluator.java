package org.seasar.kvasir.util.el;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。</p>
 * 
 * @author YOKOTA Takehiko
 */
public interface TextTemplateEvaluator
{
    Object evaluate(String text, VariableResolver resolver)
        throws EvaluationException;

    Object evaluate(String text, VariableResolver resolver,
        Object defaultValue);

    String evaluateAsString(String text, VariableResolver resolver)
        throws EvaluationException;

    String evaluateAsString(String text, VariableResolver resolver,
        String defaultValue);
}
