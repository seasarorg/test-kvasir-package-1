package org.seasar.kvasir.util.io.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.ZipFile;

import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.io.Resource;

import junit.framework.TestCase;


/**
 * @author YOKOTA Takehiko
 */
public class ZipReaderResourceTest extends TestCase
{
    private ZipFile file_;
    private ZipReaderResource root_;


    protected void setUp()
        throws Exception
    {
        file_ = new ZipFile(ClassUtils.getFileOfResource(
            getClass().getResource("test.zip")));
        root_ = new ZipReaderResource(file_);
    }


    protected void tearDown()
        throws Exception
    {
        file_.close();
    }


    public void testList()
        throws Exception
    {
        String[] children = root_.list();
        assertNotNull("正しく子リソースを取得できること", children);
        assertEquals("正しく子リソースを取得できること",
            2, children.length);
        Arrays.sort(children);
        assertEquals("正しく子リソースを取得できること",
            "dir", children[0]);
        assertEquals("正しく子リソースを取得できること",
            "text.txt", children[1]);

        children = root_.getChildResource(children[0]).list();
        assertEquals("正しく孫リソースを取得できること",
            4, children.length);
        Arrays.sort(children, new Comparator(){
            public int compare(Object o1, Object o2)
            {
                return ((String)o1).compareTo(
                    ((String)o2));
            }
        });
        assertEquals("正しく孫リソースを取得できること",
            "subdir", children[0]);
        assertEquals("正しく孫リソースを取得できること",
            "subdir2", children[1]);
        assertEquals("正しく孫リソースを取得できること",
            "text1.txt", children[2]);
        assertEquals("正しく孫リソースを取得できること",
            "text2.txt", children[3]);
    }


    public void testListResources()
        throws Exception
    {
        Resource[] children = root_.listResources();
        assertNotNull("正しく子リソースを取得できること", children);
        assertEquals("正しく子リソースを取得できること",
            2, children.length);
        Arrays.sort(children, new Comparator(){
            public int compare(Object o1, Object o2)
            {
                return ((Resource)o1).getName().compareTo(
                    ((Resource)o2).getName());
            }
        });
        assertEquals("正しく子リソースを取得できること",
            "dir", children[0].getName());
        assertEquals("正しく子リソースを取得できること",
            "text.txt", children[1].getName());

        children = children[0].listResources();
        assertEquals("正しく孫リソースを取得できること",
            4, children.length);
        Arrays.sort(children, new Comparator(){
            public int compare(Object o1, Object o2)
            {
                return ((Resource)o1).getName().compareTo(
                    ((Resource)o2).getName());
            }
        });
        assertEquals("正しく孫リソースを取得できること",
            "subdir", children[0].getName());
        assertEquals("正しく孫リソースを取得できること",
            "subdir2", children[1].getName());
        assertEquals("正しく孫リソースを取得できること",
            "text1.txt", children[2].getName());
        assertEquals("正しく孫リソースを取得できること",
            "text2.txt", children[3].getName());
    }


    public void testIsDirectory()
        throws Exception
    {
        assertTrue("正しくルートディレクトリを判定できること",
            root_.isDirectory());
        assertTrue("正しくディレクトリを判定できること",
            root_.getChildResource("dir").isDirectory());
        assertFalse("正しくリソースを判定できること",
            root_.getChildResource("text.txt").isDirectory());

    }
}
