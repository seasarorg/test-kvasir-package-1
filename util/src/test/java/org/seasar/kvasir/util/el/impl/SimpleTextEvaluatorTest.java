package org.seasar.kvasir.util.el.impl;

import junit.framework.TestCase;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.el.EvaluationException;
import org.seasar.kvasir.util.el.VariableResolver;


/**
 * @author YOKOTA Takehiko
 */
public class SimpleTextEvaluatorTest extends TestCase
{
    public void testEvaluate_String_VariableResolver()
        throws Exception
    {
        String text1 = "This is ${test} text.";
        String text2 = "This is ${loop1} text.";
        String text3 = "This is ${loop2} text.";
        String text4 = "This is ${special} text.";
        String text5 = "This is ${(plain)special} text.";
        String text6 = "This is ${(xml)special} text.";
        String text7 = "This is ${(properties)special} text.";
        String text8 = "This is ${(hoehoe)special} text.";
        PropertyHandler prop = new MapProperties();
        prop.setProperty("test", "TE${s}T");
        prop.setProperty("s", "S");
        prop.setProperty("loop1", "TE${s}T ${loop1} test");
        prop.setProperty("loop2", "TE${s}T ${loop3} test");
        prop.setProperty("loop3", "TE${s}T ${loop2} test");
        prop.setProperty("special", "<special\\value>");
        VariableResolver resolver = new PropertyHandlerVariableResolver(prop);

        SimpleTextTemplateEvaluator evaluator = new SimpleTextTemplateEvaluator();
        assertEquals("This is TE${s}T text.",
            evaluator.evaluate(text1, resolver));
        assertEquals("This is TE${s}T ${loop1} test text.",
            evaluator.evaluate(text2, resolver));
        assertEquals("This is TE${s}T ${loop3} test text.",
            evaluator.evaluate(text3, resolver));

        evaluator = new SimpleTextTemplateEvaluator(true);
        assertEquals("This is TEST text.",
            evaluator.evaluate(text1, resolver));

        try {
            evaluator.evaluate(text2, resolver);
            fail();
        } catch (EvaluationException ex) {
            ;
        }

        try {
            evaluator.evaluate(text3, resolver);
            fail();
        } catch (EvaluationException ex) {
            ;
        }

        assertEquals("This is <special\\value> text.",
            evaluator.evaluate(text4, resolver));
        assertEquals("This is <special\\value> text.",
            evaluator.evaluate(text5, resolver));
        assertEquals("This is &lt;special\\value&gt; text.",
            evaluator.evaluate(text6, resolver));
        assertEquals("This is <special\\\\value> text.",
            evaluator.evaluate(text7, resolver));
        try {
            evaluator.evaluate(text8, resolver);
            fail();
        } catch (EvaluationException ex) {
            ;
        }
    }
}
