package org.seasar.kvasir.base;

import junit.framework.TestCase;


/**
 * @author YOKOTA Takehiko
 * @author manhole
 */
public class VersionTest extends TestCase
{
    public void testCompareTo()
    {
        Version v0_9_0 = new Version(0, 9, 0, "");
        Version v1_0_0 = new Version(1, 0, 0, "");
        Version v1_0_0_EA1 = new Version(1, 0, 0, "-EA1");
        Version v1_0_0_SNAPSHOT = new Version(1, 0, 0, "-SNAPSHOT");
        Version v1_0_0_EA1_SNAPSHOT = new Version(1, 0, 0, "-EA1-SNAPSHOT");
        Version v1_0_1 = new Version(1, 0, 1, "");

        assertTrue("1.0.0の方が0.9.0よりも大きいこと", v1_0_0.compareTo(v0_9_0) > 0);
        assertTrue("1.0.0の方が1.0.0-EA1よりも大きいこと",
            v1_0_0.compareTo(v1_0_0_EA1) > 0);
        assertTrue("1.0.0の方が1.0.0-SNAPSHOTよりも大きいこと", v1_0_0
            .compareTo(v1_0_0_SNAPSHOT) > 0);
        assertTrue("1.0.0-EA1の方が1.0.0-EA1-SNAPSHOTよりも大きいこと", v1_0_0_EA1
            .compareTo(v1_0_0_EA1_SNAPSHOT) > 0);
        assertTrue("1.0.0-SNAPSHOTの方が1.0.0-EA1よりも大きいこと", v1_0_0_SNAPSHOT
            .compareTo(v1_0_0_EA1) > 0);
        assertTrue("1.0.0-SNAPSHOTの方が1.0.0-EA1-SNAPSHOTよりも大きいこと",
            v1_0_0_SNAPSHOT.compareTo(v1_0_0_EA1_SNAPSHOT) > 0);
        assertTrue("1.0.1の方が1.0.0よりも大きいこと", v1_0_1.compareTo(v1_0_0) > 0);
        assertTrue("1.0.1の方が1.0.0-EA1よりも大きいこと",
            v1_0_1.compareTo(v1_0_0_EA1) > 0);
        assertTrue("1.0.1の方が1.0.0-SNAPSHOTよりも大きいこと", v1_0_1
            .compareTo(v1_0_0_SNAPSHOT) > 0);
        assertTrue("1.0.1の方が1.0.0-EA1-SNAPSHOTよりも大きいこと", v1_0_1
            .compareTo(v1_0_0_EA1_SNAPSHOT) > 0);
    }


    public void testGetString()
        throws Exception
    {
        assertEquals("0.9.0", new Version(0, 9, 0, "").getString());
        assertEquals("1.0.0", new Version(1, 0, 0, "").getString());
        assertEquals("1.0.0-EA1", new Version(1, 0, 0, "-EA1").getString());
        assertEquals("1.0.0-SNAPSHOT", new Version(1, 0, 0, "-SNAPSHOT")
            .getString());
        assertEquals("1.0.0-EA1-SNAPSHOT",
            new Version(1, 0, 0, "-EA1-SNAPSHOT").getString());
        assertEquals("1.0.1", new Version(1, 0, 1, "").getString());
    }


    public void testIsSnapshot()
        throws Exception
    {
        assertEquals(true, new Version(1, 0, 0, "-SNAPSHOT").isSnapshot());
        assertEquals(false, new Version(1, 0, 0, "-EA1").isSnapshot());
        assertEquals(false, new Version(1, 0, 0, "").isSnapshot());
    }

}
