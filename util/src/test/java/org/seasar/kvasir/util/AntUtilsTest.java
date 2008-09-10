package org.seasar.kvasir.util;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import junit.framework.TestCase;

import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.impl.FileResource;


/**
 * @author YOKOTA Takehiko
 */
public class AntUtilsTest extends TestCase
{
    public void testIsMatched1()
        throws Exception
    {
        assertTrue(AntUtils.matches("hoehoe", "*", '/'));
        assertTrue(AntUtils.matches("hoe/hoe", "hoe/*", '/'));
        assertTrue(AntUtils.matches("hoe/", "hoe/*", '/'));
        assertTrue(AntUtils.matches("hoe/h", "hoe/?", '/'));
        assertTrue(AntUtils.matches("hoe/hoe", "**/", '/'));
        assertTrue(AntUtils.matches("hoe/hoe", "**", '/'));
        assertTrue(AntUtils.matches("hoe/hoe", "hoe/**", '/'));
        assertTrue(AntUtils.matches("hoe", "hoe/**", '/'));
        assertTrue(AntUtils.matches("hoe/fuga/hoge", "hoe/**", '/'));
        assertTrue(AntUtils.matches("hoe/fuga/hoge", "hoe/", '/'));
        assertTrue(AntUtils.matches("hoefuga", "*fuga", '/'));
        assertTrue(AntUtils.matches("hoe/fuga", "hoe/*uga", '/'));
        assertTrue(AntUtils.matches("hoe/fuga", "hoe/*fuga", '/'));
        assertTrue(AntUtils.matches("hoefuga", "hoe*", '/'));
        assertTrue(AntUtils.matches("hoe/fuga", "hoe/fug*", '/'));
        assertTrue(AntUtils.matches("hoe/fuga", "hoe/fuga*", '/'));
    }


    public void testIsMatched2()
        throws Exception
    {
        assertFalse(AntUtils.matches("hoehoe", "fugafuga", '/'));
        assertFalse(AntUtils.matches("hoehoe", "?", '/'));
        assertFalse(AntUtils.matches("hoe/", "hoe/?", '/'));
        assertFalse(AntUtils.matches("hoe/ho", "hoe/?", '/'));
        assertFalse(AntUtils.matches("hoe/h", "hoe?h", '/'));
        assertFalse(AntUtils.matches("hoe/fuga/hoge", "hoe/*", '/'));
        assertFalse(AntUtils.matches("hoe/fuga", "hoa/*uga", '/'));
        assertFalse(AntUtils.matches("hoe/fuga", "hoa/fug*", '/'));
    }


    public void testIsMatched3()
        throws Exception
    {
        assertTrue(AntUtils.matches("hoe.properties", "*.properties", '/'));
        assertFalse(AntUtils.matches("a/hoe.properties", "*.properties", '/'));
        assertTrue(AntUtils.matches("a/hoe.properties", "**/*.properties", '/'));
    }


    public void testIsMatched4()
        throws Exception
    {
        assertTrue(AntUtils.matches("META-INF/hoe.properties", "META-INF/**",
            '/'));
        assertTrue(AntUtils.matches("META-INF/a/hoe.properties", "META-INF/**",
            '/'));
    }


    public void testIsMatched_antManual1()
        throws Exception
    {
        assertTrue(AntUtils.matches("CVS/Repository", "**/CVS/*", '/'));
        assertTrue(AntUtils.matches("org/apache/CVS/Entries", "**/CVS/*", '/'));
        assertTrue(AntUtils.matches("org/apache/jakarta/tools/ant/CVS/Entries",
            "**/CVS/*", '/'));
        assertFalse(AntUtils.matches("org/apache/CVS/foo/bar/Entries",
            "**/CVS/*", '/'));
    }


    public void testIsMatched_antManual2()
        throws Exception
    {
        assertTrue(AntUtils.matches(
            "org/apache/jakarta/tools/ant/docs/index.html",
            "org/apache/jakarta/**", '/'));
        assertTrue(AntUtils.matches("org/apache/jakarta/test.xml",
            "org/apache/jakarta/**", '/'));
        assertFalse(AntUtils.matches("org/apache/xyz.java",
            "org/apache/jakarta/**", '/'));
    }


    public void testIsMatched_antManual3()
        throws Exception
    {
        assertTrue(AntUtils.matches("org/apache/CVS/Entries",
            "org/apache/**/CVS/*", '/'));
        assertTrue(AntUtils.matches("org/apache/jakarta/tools/ant/CVS/Entries",
            "org/apache/**/CVS/*", '/'));
        assertFalse(AntUtils.matches("org/apache/CVS/foo/bar/Entries",
            "org/apache/**/CVS/*", '/'));
    }


    public void testGetPaths()
        throws Exception
    {
        Resource basedir = new FileResource(new File(ClassUtils
            .getBaseDirectory(getClass()), "org/seasar/kvasir/util/ant"));
        String[] actual = AntUtils.getPaths(basedir,
            new String[] { "**/*File.class" },
            new String[] { "**/EFile.class" });
        assertEquals(2, actual.length);
        Arrays.sort(actual);
        assertEquals("CFile.class", actual[0]);
        assertEquals("dir/AFile.class", actual[1]);
    }


    public void testGetResources()
        throws Exception
    {
        Resource basedir = new FileResource(new File(ClassUtils
            .getBaseDirectory(getClass()), "org/seasar/kvasir/util/ant"));
        Resource[] actual = AntUtils.getResources(basedir,
            new String[] { "**/*File.class" },
            new String[] { "**/EFile.class" });
        assertEquals(2, actual.length);
        Arrays.sort(actual, new Comparator() {
            public int compare(Object o0, Object o1)
            {
                return ((Resource)o0).getURL().toExternalForm().compareTo(
                    ((Resource)o1).getURL().toExternalForm());
            }
        });
        assertEquals(basedir.getChildResource("CFile.class"), actual[0]);
        assertEquals(basedir.getChildResource("dir/AFile.class"), actual[1]);
    }
}
