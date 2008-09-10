package org.seasar.kvasir.util.collection;

import java.util.Locale;

import junit.framework.TestCase;

import org.seasar.kvasir.util.io.impl.JavaResource;


/**
 * @author YOKOTA Takehiko
 * @author manhole
 */
public class I18NPropertiesTest extends TestCase
{

    public void testGetPropertyStringLocale()
    {
        final I18NProperties prop = new I18NProperties(new JavaResource(
            "org/seasar/kvasir/util/collection"), "testI18NProperties",
            ".xproperties");
        assertEquals("Test-ja", prop.getProperty("test", Locale.JAPAN));
    }


    public void testNested()
        throws Exception
    {
        // ## Arrange ##
        final String dir = I18NPropertiesTest.class.getPackage().getName()
            .replace('.', '/');

        final I18NProperties p1 = new I18NProperties(new JavaResource(dir),
            "I18NPropertiesTest-testNested1", ".xproperties");
        final I18NProperties p2 = new I18NProperties(new JavaResource(dir),
            "I18NPropertiesTest-testNested2", ".xproperties", p1);
        final I18NProperties p3 = new I18NProperties(new JavaResource(dir),
            "I18NPropertiesTest-testNested3", ".xproperties", p2);

        // ## Act ##
        // ## Assert ##

        assertEquals("aaa1", p1.getProperty("aaa"));
        assertEquals("bbb1", p1.getProperty("bbb"));
        assertEquals("ccc1", p1.getProperty("ccc"));
        assertEquals("ddd1", p1.getProperty("ddd"));

        assertEquals("aaa1", p2.getProperty("aaa"));
        assertEquals("bbb2", p2.getProperty("bbb"));
        assertEquals("ccc2", p2.getProperty("ccc"));
        assertEquals("ddd1", p2.getProperty("ddd"));

        assertEquals("aaa1", p3.getProperty("aaa"));
        assertEquals("bbb2", p3.getProperty("bbb"));
        assertEquals("ccc3", p3.getProperty("ccc"));
        assertEquals("ddd3", p3.getProperty("ddd"));

        final Locale locale = new Locale("");
        assertEquals("aaa1", p1.getProperty("aaa", locale));
        assertEquals("bbb1", p1.getProperty("bbb", locale));
        assertEquals("ccc1", p1.getProperty("ccc", locale));
        assertEquals("ddd1", p1.getProperty("ddd", locale));

        assertEquals("aaa1", p2.getProperty("aaa", locale));
        assertEquals("bbb2", p2.getProperty("bbb", locale));
        assertEquals("ccc2", p2.getProperty("ccc", locale));
        assertEquals("ddd1", p2.getProperty("ddd", locale));

        assertEquals("aaa1", p3.getProperty("aaa", locale));
        assertEquals("bbb2", p3.getProperty("bbb", locale));
        assertEquals("ccc3", p3.getProperty("ccc", locale));
        assertEquals("ddd3", p3.getProperty("ddd", locale));
    }


    /**
     * evaluateVariableがtrueの場合は、${...}部分が解釈されること。
     */
    public void testEvaluateVariable_variant()
        throws Exception
    {
        // ## Arrange ##
        final String dir = I18NPropertiesTest.class.getPackage().getName()
            .replace('.', '/');

        final I18NProperties prop = new I18NProperties(new JavaResource(dir),
            "I18NPropertiesTest-testVariable", ".xproperties");

        // ## Act ##
        prop.setEvaluateVariable(true);

        // ## Assert ##
        assertEquals("埋め込み値の解釈が行なわれること", "ABC", prop.getProperty("a"));

        assertEquals("埋め込み値に対応する値がない場合は空文字列として扱われること", "AC", prop
            .getProperty("c"));

        assertEquals("埋め込み値の解釈が再帰的に行なわれること", "DEF", prop.getProperty("e"));
    }


    /**
     * evaluateVariableがtrueの場合は、${...}部分が解釈されること。
     */
    public void testEvaluateVariable_locale()
        throws Exception
    {
        // ## Arrange ##
        final String dir = I18NPropertiesTest.class.getPackage().getName()
            .replace('.', '/');

        final I18NProperties prop = new I18NProperties(new JavaResource(dir),
            "I18NPropertiesTest-testVariable", ".xproperties");

        // ## Act ##
        prop.setEvaluateVariable(true);

        // ## Assert ##
        assertEquals("埋め込み値の解釈が行なわれること", "ABC", prop.getProperty("a",
            Locale.ENGLISH));

        assertEquals("埋め込み値に対応する値がない場合は空文字列として扱われること", "AC", prop
            .getProperty("c"));

        assertEquals("埋め込み値の解釈が再帰的に行なわれること", "DEF", prop.getProperty("e"));
    }


    /**
     * evaluateVariableがデフォルト値の場合は、${...}部分がそのまま返却されること。
     */
    public void testNoEvaluateVariable_variant()
        throws Exception
    {
        // ## Arrange ##
        final String dir = I18NPropertiesTest.class.getPackage().getName()
            .replace('.', '/');

        final I18NProperties prop = new I18NProperties(new JavaResource(dir),
            "I18NPropertiesTest-testVariable", ".xproperties");

        // ## Act ##
        // ## Assert ##
        assertEquals("A${b}C", prop.getProperty("a"));

        assertEquals("A${d}C", prop.getProperty("c"));

        assertEquals("${abc}", prop.getProperty("e"));
    }

}
