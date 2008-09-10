package org.seasar.kvasir.util.io.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import junit.framework.TestCase;

import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;

/**
 * @author YOKOTA Takehiko
 */
public class ZipWriterResourceTest extends TestCase
{
    private File file_;
    private ZipOutputStream zos_;
    private ZipWriterResource root_;


    protected void setUp()
        throws Exception
    {
        file_ = File.createTempFile("kvasir", null);
        file_.deleteOnExit();
        zos_ = new ZipOutputStream(new FileOutputStream(file_));
        root_ = new ZipWriterResource(zos_);
    }


    protected void tearDown()
        throws Exception
    {
        if (zos_ != null) {
            zos_.putNextEntry(new ZipEntry("dummy"));
            zos_.closeEntry();
            zos_.close();
        }
    }


    public void testExists()
        throws Exception
    {
        assertTrue("ルートは存在すること", root_.exists());

        assertFalse("出力していないリソースは存在しないと判定されること",
            root_.getChildResource("resource.txt").exists());

        assertFalse("出力していないリソースは何度getChildResource()しても" +
            "存在しないと判定されること",
            root_.getChildResource("resource.txt").exists());

        root_.getChildResource("resource.txt").getOutputStream().close();
        assertTrue("出力したリソースは存在していると判定されること",
            root_.getChildResource("resource.txt").exists());

        root_.getChildResource("dir").mkdir();
        assertTrue("作成したディレクトリは存在していると判定されること",
            root_.getChildResource("dir").exists());
    }


    public void testGetParentResource()
        throws Exception
    {
        assertNull("ルートの親はnullであること", root_.getParentResource());

        assertEquals("親が正しく取得できること", "path/to",
            ((ZipWriterResource)root_.getChildResource("path/to/resource.txt")
            .getParentResource()).getPath());
    }


    public void testMkdir()
        throws Exception
    {
        assertFalse("親が存在しないとディレクトリを作成できないこと",
            root_.getChildResource("path/to/dir").mkdir());

        assertTrue("正しくディレクトリを作成できること",
            root_.getChildResource("dir").mkdir());
        zos_.close();
        zos_ = null;
        ZipFile zipFile = new ZipFile(file_);
        try {
            assertNotNull("正しくディレクトリを作成できること",
                zipFile.getEntry("dir/"));
        } finally {
            zipFile.close();
        }
    }


    public void testMkdirs()
        throws Exception
    {
        assertTrue("親が存在しなくてもディレクトリを作成できること",
            root_.getChildResource("dir/subdir").mkdirs());
        zos_.close();
        zos_ = null;

        ZipFile zipFile = new ZipFile(file_);
        try {
            assertNotNull("正しくディレクトリを作成できること",
                zipFile.getEntry("dir/"));
            assertNotNull("正しくディレクトリを作成できること",
                zipFile.getEntry("dir/subdir/"));
        } finally {
            zipFile.close();
        }
    }


    public void testMkdirs2()
        throws Exception
    {
        root_ = new ZipWriterResource(zos_, false);
    
        Resource resource = root_.getChildResource("path/to/resource.txt");
        resource.getParentResource().mkdirs();
        resource = root_.getChildResource("path2/to2/resource2.txt");
        resource.getParentResource().mkdirs();
        resource.getOutputStream().close();
        try {
            resource = root_.getChildResource("path2/to2/resource3.txt");
            resource.getParentResource().mkdirs();
            resource.getOutputStream().close();
        } catch (IORuntimeException ex) {
            fail("同じディレクトリについて2度mkdirs()しても" +
                "エラーにならないこと");
        }
        zos_.close();
        zos_ = null;
        ZipFile zipFile = new ZipFile(file_);
        try {
            assertNull("mkdirs()してもgetOutputStream()しないと" +
                "ディレクトリがZIPファイル内に作成されないこと",
                zipFile.getEntry("path/"));
            assertNull("mkdirs()してもgetOutputStream()しないと" +
                "ディレクトリがZIPファイル内に作成されないこと",
                zipFile.getEntry("path/to/"));
            assertNotNull("mkdirs()してgetOutputStream()すると" +
                "ディレクトリがZIPファイル内に作成されること",
                zipFile.getEntry("path2/"));
            assertNotNull("mkdirs()してgetOutputStream()すると" +
                "ディレクトリがZIPファイル内に作成されること",
                zipFile.getEntry("path2/to2/"));
            assertNull("mkdirs()してgetOutputStream()しても" +
                "ルートディレクトリはZIPファイル内に作成されないこと",
                zipFile.getEntry("/"));
        } finally {
            zipFile.close();
        }
    }


    public void testList()
        throws Exception
    {
        Resource r1 = root_.getChildResource("path/to/resource1.txt");
        r1.getParentResource().mkdirs();
        r1.getOutputStream().close();
        root_.getChildResource("path/to/resource2.txt");
        root_.getChildResource("dir1").mkdir();
        root_.getChildResource("dir2");
    
        String[] children = root_.getChildResource("path").list();
        assertNotNull("正しく子リソースを取得できること", children);
        assertEquals("正しく子リソースを取得できること", 1, children.length);
        assertEquals("正しく子リソースを取得できること",
            "to", children[0]);
    
        children = root_.getChildResource("path/to").list();
        assertNotNull("正しく子リソースを取得できること", children);
        assertEquals("正しく子リソースを取得できること", 1, children.length);
        assertEquals("正しく子リソースを取得できること",
            "resource1.txt",
            children[0]);
    
        children = root_.list();
        Arrays.sort(children);
        assertNotNull("正しく子リソースを取得できること", children);
        assertEquals("正しく子リソースを取得できること", 2, children.length);
        assertEquals("正しく子リソースを取得できること",
            "dir1",
            children[0]);
        assertEquals("正しく子リソースを取得できること",
            "path",
            children[1]);
    }


    public void testListResources()
        throws Exception
    {
        Resource r1 = root_.getChildResource("path/to/resource1.txt");
        r1.getParentResource().mkdirs();
        r1.getOutputStream().close();
        root_.getChildResource("path/to/resource2.txt");
        root_.getChildResource("dir1").mkdir();
        root_.getChildResource("dir2");

        Resource[] children = root_.getChildResource("path").listResources();
        assertNotNull("正しく子リソースを取得できること", children);
        assertEquals("正しく子リソースを取得できること", 1, children.length);
        assertEquals("正しく子リソースを取得できること",
            "path/to", ((ZipWriterResource)children[0]).getPath());

        children = root_.getChildResource("path/to").listResources();
        assertNotNull("正しく子リソースを取得できること", children);
        assertEquals("正しく子リソースを取得できること", 1, children.length);
        assertEquals("正しく子リソースを取得できること",
            "path/to/resource1.txt",
            ((ZipWriterResource)children[0]).getPath());

        children = root_.listResources();
        Arrays.sort(children, new Comparator(){
            public int compare(Object o1, Object o2)
            {
                return ((Resource)o1).getName().compareTo(
                    ((Resource)o2).getName());
            }
        });
        assertNotNull("正しく子リソースを取得できること", children);
        assertEquals("正しく子リソースを取得できること", 2, children.length);
        assertEquals("正しく子リソースを取得できること",
            "dir1",
            ((ZipWriterResource)children[0]).getPath());
        assertEquals("正しく子リソースを取得できること",
            "path",
            ((ZipWriterResource)children[1]).getPath());
    }


    public void testGetInputStream()
        throws Exception
    {
        Resource resource = root_.getChildResource("resource.txt");
        try {
            resource.getInputStream();
            fail("存在しないリソースに関してはResourceNotFoundExceptionが" +
                 "スローされること");
        } catch (ResourceNotFoundException ex) {
            ;
        }

        resource.getOutputStream().close();
        try {
            resource.getInputStream();
            fail("存在するリソースに関してはUnsupportedOperationExceptionが" +
                "スローされること");
        } catch (UnsupportedOperationException ex) {
            ;
        }
    }


    /*
     * 通常のファイルと空ディレクトリが正しく出力できること。
     */
    public void test1()
        throws Exception
    {
        Resource resource = root_.getChildResource("path/to/resource.txt");
        resource.getParentResource().mkdirs();
        OutputStream os = resource.getOutputStream();
        os.write(1);
        os.write(2);
        os.write(3);
        os.close();

        resource = root_.getChildResource("dir");
        resource.mkdir();

        zos_.close();
        zos_ = null;

        ZipFile zipFile = new ZipFile(file_);
        try {
            assertNotNull(zipFile.getEntry("path/"));
            assertNotNull(zipFile.getEntry("path/to/"));
            ZipEntry entry = zipFile.getEntry("path/to/resource.txt");
            assertNotNull(entry);
            assertNotNull(zipFile.getEntry("dir/"));

            InputStream in = zipFile.getInputStream(entry);
            try {
                assertEquals(1, in.read());
                assertEquals(2, in.read());
                assertEquals(3, in.read());
                assertEquals(-1, in.read());
            } finally {
                in.close();
            }
        } finally {
            zipFile.close();
        }
    }


    /*
     * createEmptyDirectory==falseの時に、通常のファイルと空でないディレクトリ
     * が正しく出力できること。
     */
    public void test2()
        throws Exception
    {
        root_ = new ZipWriterResource(zos_, false);

        Resource resource = root_.getChildResource("path/to/resource.txt");
        resource.getParentResource().mkdirs();
        OutputStream os = resource.getOutputStream();
        os.write(1);
        os.write(2);
        os.write(3);
        os.close();

        resource = root_.getChildResource("dir");
        resource.mkdir();

        zos_.close();
        zos_ = null;

        ZipFile zipFile = new ZipFile(file_);
        try {
            assertNotNull(zipFile.getEntry("path/"));
            assertNotNull(zipFile.getEntry("path/to/"));
            ZipEntry entry = zipFile.getEntry("path/to/resource.txt");
            assertNotNull(entry);
            assertNull("空のディレクトリは出力されないこと",
                zipFile.getEntry("dir/"));

            InputStream in = zipFile.getInputStream(entry);
            try {
                assertEquals(1, in.read());
                assertEquals(2, in.read());
                assertEquals(3, in.read());
                assertEquals(-1, in.read());
            } finally {
                in.close();
            }
        } finally {
            zipFile.close();
        }
    }
}
