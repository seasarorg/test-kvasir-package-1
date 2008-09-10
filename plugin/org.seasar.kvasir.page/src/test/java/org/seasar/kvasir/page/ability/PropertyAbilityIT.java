package org.seasar.kvasir.page.ability;

import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import junit.framework.Test;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.type.DirectoryMold;
import org.seasar.kvasir.test.KvasirPluginTestCase;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;

public class PropertyAbilityIT extends KvasirPluginTestCase<PagePlugin> {
    private PageAlfr pageAlfr_;

    @Override
    protected String getTargetPluginId() {
        return PagePlugin.ID;
    }

    public static Test suite() throws Exception {
        return createTestSuite(PropertyAbilityIT.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        pageAlfr_ = getPlugin().getPageAlfr();
    }

    private Page prepareTestPage() throws Exception {
        Page root = pageAlfr_.getRootPage(PathId.HEIM_MIDGARD);
        Page testPage = root.getChild("test");
        if (testPage != null) {
            testPage.delete();
        }

        return root.createChild(new PageMold().setName("test"));
    }

    private PropertyAbility preparePropertyAbilityOfTestPage() throws Exception {
        return prepareTestPage().getAbility(PropertyAbility.class);
    }

    public void testGetAndSetProperty() throws Exception {
        PropertyAbility prop = preparePropertyAbilityOfTestPage();

        prop.setProperty("test", "TEST2");
        prop.setProperty("hoehoe", "HOEHOE");

        assertEquals("TEST2", prop.getProperty("test"));

        prop.setProperty("test", "TEST");

        assertEquals("上書きもできること", "TEST", prop.getProperty("test"));

        assertTrue(prop.containsPropertyName("test"));
        assertFalse(prop.containsPropertyName("test2"));

        Set<String> nameSet = new TreeSet<String>();
        for (Enumeration<String> enm = prop.propertyNames(); enm
                .hasMoreElements();) {
            nameSet.add(enm.nextElement());
        }
        assertEquals(2, nameSet.size());
        assertTrue(nameSet.contains("test"));
        assertTrue(nameSet.contains("hoehoe"));

        prop.removeProperty("test");
        assertNull(prop.getProperty("test"));
    }

    public void testGetAndSetProperty_variant() throws Exception {
        PropertyAbility prop = preparePropertyAbilityOfTestPage();

        prop.setProperty("test", "ja_JP", "TEST2");
        prop.setProperty("hoehoe", "ja_JP", "HOEHOE");

        assertEquals("TEST2", prop.getProperty("test", "ja_JP"));

        prop.setProperty("test", "ja_JP", "TEST");

        assertEquals("上書きもできること", "TEST", prop.getProperty("test", "ja_JP"));

        assertTrue(prop.containsPropertyName("test", "ja_JP"));
        assertFalse(prop.containsPropertyName("test", "en_US"));

        Set<String> nameSet = new TreeSet<String>();
        for (Enumeration<String> enm = prop.propertyNames("ja_JP"); enm
                .hasMoreElements();) {
            nameSet.add(enm.nextElement());
        }
        assertEquals(2, nameSet.size());
        assertTrue(nameSet.contains("test"));
        assertTrue(nameSet.contains("hoehoe"));

        prop.removeProperty("test", "ja_JP");
        assertNull(prop.getProperty("test"));
    }

    public void testGetProperty_locale() throws Exception {
        PropertyAbility prop = preparePropertyAbilityOfTestPage();

        prop.setProperty("test", "ja", "TEST_ja");
        prop.setProperty("test", "TEST");

        assertEquals("TEST_ja", prop
                .getProperty("test", new Locale("ja", "JP")));

        assertTrue(prop.containsPropertyName("test", new Locale("ja", "JP")));
        assertTrue(prop.containsPropertyName("test", new Locale("en", "US")));
        assertFalse(prop.containsPropertyName("hehe", new Locale("en", "US")));
    }

    public void testFindProperty() throws Exception {
        Page root = pageAlfr_.getRootPage(PathId.HEIM_MIDGARD);
        Page testPage = root.getChild("test");
        if (testPage != null) {
            testPage.delete();
        }

        testPage = root.createChild(new DirectoryMold().setName("test"));
        testPage.setAsLord(true);
        Page childPage = testPage.createChild(new PageMold().setName("child"));

        PropertyAbility testProp = testPage.getAbility(PropertyAbility.class);
        testProp.setProperty("test", "TEST");

        PropertyAbility childProp = childPage.getAbility(PropertyAbility.class);

        assertEquals(testPage, childProp.findPageHoldingProperty("test",
                new Locale("ja", "JP")));

        assertEquals("TEST", childProp.findProperty("test", new Locale("ja",
                "JP")));
    }

    public void testSetProperties() throws Exception {
        PropertyAbility prop = preparePropertyAbilityOfTestPage();

        prop.setProperty("test", "TEST");
        prop.setProperty("fuga", "FUGA");

        PropertyHandler handler = new MapProperties(
                new TreeMap<String, String>());
        handler.setProperty("fuga", "FUGE");
        handler.setProperty("hoe", "HOE");

        prop.setProperties(handler);

        assertNull(prop.getProperty("test"));
        assertEquals("FUGE", prop.getProperty("fuga"));
        assertEquals("HOE", prop.getProperty("hoe"));
    }

    public void testSetProperties_variant() throws Exception {
        PropertyAbility prop = preparePropertyAbilityOfTestPage();

        prop.setProperty("test", "ja_JP", "TEST");
        prop.setProperty("fuga", "ja_JP", "FUGA_ja_JP");
        prop.setProperty("fuga", "en_US", "FUGA_en_US");
        prop.setProperty("fuga", "FUGA");

        PropertyHandler handler = new MapProperties(
                new TreeMap<String, String>());
        handler.setProperty("fuga", "FUGE");
        handler.setProperty("hoe", "HOE");

        prop.setProperties("ja_JP", handler);

        assertNull(prop.getProperty("test"));
        assertEquals("FUGE", prop.getProperty("fuga", "ja_JP"));
        assertEquals("HOE", prop.getProperty("hoe", "ja_JP"));
        assertEquals("他のバリアントに影響を与えないこと", "FUGA_en_US", prop.getProperty(
                "fuga", "en_US"));
        assertEquals("デフォルトバリアントに影響を与えないこと", "FUGA", prop.getProperty("fuga"));
    }

    public void testClearProperties() throws Exception {
        PropertyAbility prop = preparePropertyAbilityOfTestPage();

        prop.setProperty("test", "TEST");
        prop.setProperty("fuga", "FUGA");

        prop.clearProperties();

        assertNull(prop.getProperty("test"));
        assertNull(prop.getProperty("fuga"));
    }

    public void testClearProperties_variant() throws Exception {
        PropertyAbility prop = preparePropertyAbilityOfTestPage();

        prop.setProperty("test", "ja_JP", "TEST");
        prop.setProperty("fuga", "ja_JP", "FUGA_ja_JP");
        prop.setProperty("fuga", "en_US", "FUGA_en_US");
        prop.setProperty("fuga", "FUGA");

        prop.clearProperties("ja_JP");

        assertNull(prop.getProperty("test", "ja_JP"));
        assertNull(prop.getProperty("fuga", "ja_JP"));
        assertEquals("他のバリアントに影響を与えないこと", "FUGA_en_US", prop.getProperty(
                "fuga", "en_US"));
        assertEquals("デフォルトバリアントに影響を与えないこと", "FUGA", prop.getProperty("fuga"));
    }

    public void testClearAllProperties() throws Exception {
        PropertyAbility prop = preparePropertyAbilityOfTestPage();

        prop.setProperty("test", "ja_JP", "TEST");
        prop.setProperty("fuga", "ja_JP", "FUGA_ja_JP");
        prop.setProperty("fuga", "en_US", "FUGA_en_US");
        prop.setProperty("fuga", "FUGA");

        prop.clearAllProperties();

        assertNull(prop.getProperty("test", "ja_JP"));
        assertNull(prop.getProperty("fuga", "ja_JP"));
        assertNull(prop.getProperty("fuga", "en_US"));
        assertNull(prop.getProperty("fuga"));
    }
}
