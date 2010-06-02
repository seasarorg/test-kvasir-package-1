package org.seasar.kvasir.page.condition;

import java.util.Date;

import junit.framework.TestCase;

import org.seasar.cms.database.identity.impl.H2Identity;
import org.seasar.kvasir.page.impl.PagePluginImpl;
import org.seasar.kvasir.page.type.PageType;


public class PageConditionParserTest extends TestCase
{
    private PageConditionParser target_;


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();

        target_ = new PageConditionParser(new H2Identity(),
            new PagePluginImpl() {
                public PageType[] getPageTypes()
                {
                    return new PageType[0];
                }
            });
    }


    public void test_通常の条件式が正しくパースされること()
        throws Exception
    {
        ParsedPageCondition actual = target_.parse(null, new String[] { "id" },
            null, true, false, new Date(), 0, null, null, new Formula(
                "name='NAME'"), new Order[0]);

        assertEquals("WHERE (page.name='NAME') AND page.heimid=?", actual
            .getBase());
        Object[] params = actual.getParameters();
        assertEquals(1, params.length);
        int idx = 0;
        assertEquals(0, params[idx++]);
    }


    public void test_プロパティを含む条件式が正しくパースされること()
        throws Exception
    {
        ParsedPageCondition actual = target_.parse(null, new String[] { "id" },
            null, true, false, new Date(), 0, null, null, new Formula(
                "property('a')='A'"), new Order[0]);

        assertEquals(
            "WHERE page.id=property0.pageid AND property0.name=? AND (property0.value='A') AND page.heimid=?",
            actual.getBase());
        Object[] params = actual.getParameters();
        assertEquals(2, params.length);
        int idx = 0;
        assertEquals("a", params[idx++]);
        assertEquals(0, params[idx++]);
    }


    public void test_2つのプロパティを含む条件式が正しくパースされること()
        throws Exception
    {
        ParsedPageCondition actual = target_.parse(null, new String[] { "id" },
            null, true, false, new Date(), 0, null, null, new Formula(
                "property('a')='A' AND property('b')='B'"), new Order[0]);

        assertEquals(
            "WHERE page.id=property0.pageid AND property0.name=? AND "
                + "page.id=property1.pageid AND property1.name=? AND "
                + "(property0.value='A' AND property1.value='B') AND page.heimid=?",
            actual.getBase());
        Object[] params = actual.getParameters();
        assertEquals(3, params.length);
        int idx = 0;
        assertEquals("a", params[idx++]);
        assertEquals("b", params[idx++]);
        assertEquals(0, params[idx++]);
    }


    public void test_同じ名前の2つのプロパティを含む条件式が正しくパースされること()
        throws Exception
    {
        ParsedPageCondition actual = target_.parse(null, new String[] { "id" },
            null, true, false, new Date(), 0, null, null, new Formula(
                "property('a')='A' AND property('a')='B'"), new Order[0]);

        assertEquals(
            "WHERE page.id=property0.pageid AND property0.name=? AND "
                + "(property0.value='A' AND property0.value='B') AND page.heimid=?",
            actual.getBase());
        Object[] params = actual.getParameters();
        assertEquals(2, params.length);
        int idx = 0;
        assertEquals("a", params[idx++]);
        assertEquals(0, params[idx++]);
    }


    public void test_同じ名前の2つのプロパティでorを含む条件式が正しくパースされること()
        throws Exception
    {
        ParsedPageCondition actual = target_.parse(null, new String[] { "id" },
            null, true, false, new Date(), 0, null, null, new Formula(
                "property('a')='A' OR property('a')='B'"), new Order[0]);

        assertEquals("WHERE page.id=property0.pageid AND property0.name=? AND "
            + "(property0.value='A' OR property0.value='B') AND page.heimid=?",
            actual.getBase());
        Object[] params = actual.getParameters();
        assertEquals(2, params.length);
        int idx = 0;
        assertEquals("a", params[idx++]);
        assertEquals(0, params[idx++]);
    }


    public void test_プロパティでinを含む条件式が正しくパースされること()
        throws Exception
    {
        ParsedPageCondition actual = target_.parse(null, new String[] { "id" },
            null, true, false, new Date(), 0, null, null, new Formula(
                "property('a') in ('A', 'B')"), new Order[0]);

        assertEquals("WHERE page.id=property0.pageid AND property0.name=? AND "
            + "(property0.value in ('A', 'B')) AND page.heimid=?", actual
            .getBase());
        Object[] params = actual.getParameters();
        assertEquals(2, params.length);
        int idx = 0;
        assertEquals("a", params[idx++]);
        assertEquals(0, params[idx++]);
    }
}
