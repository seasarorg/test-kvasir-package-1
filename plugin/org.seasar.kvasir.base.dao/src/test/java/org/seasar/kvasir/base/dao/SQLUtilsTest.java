package org.seasar.kvasir.base.dao;

import org.seasar.kvasir.base.dao.SQLUtils;

import junit.framework.TestCase;


/**
 * @author YOKOTA Takehiko
 */
public class SQLUtilsTest extends TestCase
{
    public void testEscape()
    {
        assertEquals("100%_hoehoe|fufufu",
            SQLUtils.escape("100%_hoehoe|fufufu", "", '/'));
        assertEquals("100|%|_hoehoe||fufufu",
            SQLUtils.escape("100%_hoehoe|fufufu", "%_", '|'));
    }
}
