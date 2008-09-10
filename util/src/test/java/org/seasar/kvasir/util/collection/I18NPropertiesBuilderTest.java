package org.seasar.kvasir.util.collection;

import junit.framework.TestCase;


public class I18NPropertiesBuilderTest extends TestCase
{

    public void testBuild()
        throws Exception
    {
        // ## Arrange ##
        final I18NPropertiesBuilder builder = new I18NPropertiesBuilder();
        final String base = I18NPropertiesTest.class.getName()
            .replace('.', '/');
        builder.addPath(base + "-testNested1.xproperties");
        builder.addPath(base + "-testNested2.xproperties");
        builder.addPath(base + "-testNested3.xproperties");

        // ## Act ##
        final I18NProperties properties = builder.build();

        // ## Assert ##
        assertEquals("aaa1", properties.getProperty("aaa"));
        assertEquals("bbb2", properties.getProperty("bbb"));
        assertEquals("ccc3", properties.getProperty("ccc"));
        assertEquals("ddd3", properties.getProperty("ddd"));
    }


    public void testBuildNoPath()
        throws Exception
    {
        // ## Arrange ##
        final I18NPropertiesBuilder builder = new I18NPropertiesBuilder();

        // ## Act ##
        // ## Assert ##
        try {
            builder.build();
            fail("pathが1つも指定されていない場合は例外");
        } catch (final IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * evaluateVariableがtrueの場合は、${...}部分が解釈されること。
     */
    public void testEvaluateVariable()
        throws Exception
    {
        // ## Arrange ##
        final I18NPropertiesBuilder builder = new I18NPropertiesBuilder();
        final String base = I18NPropertiesTest.class.getName()
            .replace('.', '/');
        builder.addPath(base + "-testVariable.xproperties");

        // ## Act ##
        builder.setEvaluateVariable(true);
        final I18NProperties prop = builder.build();

        // ## Assert ##
        assertEquals("埋め込み値の解釈が行なわれること", "ABC", prop.getProperty("a"));
        assertEquals("埋め込み値に対応する値がない場合は空文字列として扱われること", "AC", prop
            .getProperty("c"));
        assertEquals("埋め込み値の解釈が再帰的に行なわれること", "DEF", prop.getProperty("e"));
    }


    /**
     * evaluateVariableがデフォルト値の場合は、${...}部分がそのまま返却されること。
     */
    public void testNoEvaluateVariable()
        throws Exception
    {
        // ## Arrange ##
        final I18NPropertiesBuilder builder = new I18NPropertiesBuilder();
        final String base = I18NPropertiesTest.class.getName()
            .replace('.', '/');
        builder.addPath(base + "-testVariable.xproperties");

        // ## Act ##
        //builder.setEvaluateVariable(true);
        final I18NProperties prop = builder.build();

        // ## Assert ##
        assertEquals("A${b}C", prop.getProperty("a"));

        assertEquals("A${d}C", prop.getProperty("c"));

        assertEquals("${abc}", prop.getProperty("e"));
    }

}
