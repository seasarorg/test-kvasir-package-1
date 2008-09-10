package org.seasar.kvasir.util;

import java.util.Locale;

import junit.framework.TestCase;


/**
 * @author YOKOTA Takehiko
 */
public class LocaleUtilsTest extends TestCase
{

    public void testGetSuffixes()
    {
        String[] suffixes = LocaleUtils.getSuffixes(null);
        assertNotNull(suffixes);
        assertEquals(0, suffixes.length);

        suffixes = LocaleUtils.getSuffixes(new Locale("ja", "JP", "TOKYO"));
        assertEquals(3, suffixes.length);
        assertEquals("ja_JP_TOKYO", suffixes[0]);
        assertEquals("ja_JP", suffixes[1]);
        assertEquals("ja", suffixes[2]);

        suffixes = LocaleUtils.getSuffixes(new Locale("ja", "JP", ""));
        assertEquals(2, suffixes.length);
        assertEquals("ja_JP", suffixes[0]);
        assertEquals("ja", suffixes[1]);

        suffixes = LocaleUtils.getSuffixes(new Locale("ja", "", ""));
        assertEquals(1, suffixes.length);
        assertEquals("ja", suffixes[0]);

        suffixes = LocaleUtils.getSuffixes(new Locale("ja", "", "TOKYO"));
        assertEquals(2, suffixes.length);
        assertEquals("ja__TOKYO", suffixes[0]);
        assertEquals("ja", suffixes[1]);

        suffixes = LocaleUtils.getSuffixes(new Locale("", "JP", "TOKYO"));
        assertEquals(2, suffixes.length);
        assertEquals("_JP_TOKYO", suffixes[0]);
        assertEquals("_JP", suffixes[1]);

        suffixes = LocaleUtils.getSuffixes(new Locale("", "", "TOKYO"));
        assertEquals(1, suffixes.length);
        assertEquals("__TOKYO", suffixes[0]);

        suffixes = LocaleUtils.getSuffixes(new Locale(""));
        assertEquals(0, suffixes.length);
    }


    public void testGetLocale()
        throws Exception
    {
        assertNull(LocaleUtils.getLocale(null));
        assertEquals(new Locale("ja"), LocaleUtils.getLocale("ja"));
        assertEquals(new Locale("ja", "JP"), LocaleUtils.getLocale("ja_JP"));
        assertEquals(new Locale("ja", "JP", "WIN"), LocaleUtils
            .getLocale("ja_JP_WIN"));
        assertEquals(new Locale("", "JP"), LocaleUtils.getLocale("_JP"));
        assertEquals(new Locale("ja", "", "WIN"), LocaleUtils
            .getLocale("ja__WIN"));
        // Locale的にはあり得ないらしい。
        assertEquals(new Locale("", "", "WIN"), LocaleUtils.getLocale("__WIN"));
    }


    public void testGetString()
        throws Exception
    {
        assertEquals("ja", LocaleUtils.getString(new Locale("ja")));
        assertEquals("ja_JP", LocaleUtils.getString(new Locale("ja", "JP")));
        assertEquals("ja_JP_WIN", LocaleUtils.getString(new Locale("ja", "JP",
            "WIN")));
        assertEquals("_JP", LocaleUtils.getString(new Locale("", "JP")));
        assertEquals("ja__WIN", LocaleUtils.getString(new Locale("ja", "",
            "WIN")));
        // Locale的にはあり得ないらしいが、利便性のため。
        assertEquals("__WIN", LocaleUtils.getString(new Locale("", "", "WIN")));
    }

}
