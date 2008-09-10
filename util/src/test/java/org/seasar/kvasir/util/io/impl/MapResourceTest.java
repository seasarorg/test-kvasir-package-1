package org.seasar.kvasir.util.io.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;

import junit.framework.TestCase;


/**
 * @author YOKOTA Takehiko
 */
public class MapResourceTest extends TestCase
{
    public void testDelete()
        throws Exception
    {
        MapResource mr = MapResource.newInstance("/path");
        OutputStream os = mr.getOutputStream();
        assertFalse("outputStreamが開いているリソースは削除できないこと",
            mr.delete());
        os.close();
        assertTrue("存在するリソースを削除できること", mr.delete());

        assertFalse("ルートは削除できないこと",
            MapResource.newInstance().delete());

        mr = MapResource.newInstance("/path/to");
        mr.mkdirs();
        mr.getChildResource("resource").mkdir();
        assertFalse("子がいるディレクトリは削除できないこと", mr.delete());
        mr.getChildResource("resource").delete();
        assertTrue("子がいないディレクトリは削除できること", mr.delete());
    }


    public void testExists()
        throws Exception
    {
        MapResource mr = MapResource.newInstance("");
        assertTrue("ルートは存在すること", mr.exists());

        assertFalse("作成していないリソースは存在しないこと",
            mr.getChildResource("path").exists());

        Resource childDir = mr.getChildResource("path");
        childDir.mkdir();
        assertTrue("作成したディレクトリは存在すること", childDir.exists());

        Resource child = mr.getChildResource("resource");
        child.getOutputStream().close();
        assertTrue("作成したリソースは存在すること", child.exists());

        childDir.delete();
        assertFalse("削除したディレクトリは存在しないこと", childDir.exists());

        child.delete();
        assertFalse("削除したリソースは存在しないこと", child.exists());
    }


    public void testGetChildResource()
        throws Exception
    {
        MapResource root = MapResource.newInstance();
        MapResource child = (MapResource)root.getChildResource("/path");
        assertEquals("正しく子リソースが取得できること", "/path",
            child.getPath());

        MapResource child2 = (MapResource)root.getChildResource("path");
        assertEquals("正しく子リソースが取得できること", "/path",
            child2.getPath());

        MapResource child3 = (MapResource)root.getChildResource("path/");
        assertEquals("正しく子リソースが取得できること", "/path",
            child3.getPath());

        MapResource child4 = (MapResource)root.getChildResource("/path/");
        assertEquals("正しく子リソースが取得できること", "/path",
            child4.getPath());

        assertSame("同じ親から同じ名前の子を取得した場合は" +
            "同じオブジェクトが取得できること",
            child, child2);
    }


    public void testGetInputStream()
        throws Exception
    {
        MapResource root = MapResource.newInstance();
        MapResource child = (MapResource)root.getChildResource("resource");
        try {
            child.getInputStream();
            fail("存在しないリソースのInputStreamは取得できないこと");
        } catch (ResourceNotFoundException ex) {
            ;
        }
        child.mkdir();
        try {
            child.getInputStream();
            fail("ディレクトリのInputStreamは取得できないこと");
        } catch (ResourceNotFoundException ex) {
            ;
        }
        child.delete();

        OutputStream os = child.getOutputStream();
        os.write(1);
        os.write(2);
        os.write(3);
        os.close();
        InputStream is = child.getInputStream();
        assertEquals(1, is.read());
        assertEquals(2, is.read());
        assertEquals(3, is.read());
        assertEquals(-1, is.read());
        is.close();
    }


    public void testGetLastModifiedTime()
        throws Exception
    {
        MapResource root = MapResource.newInstance();
        MapResource child = (MapResource)root.getChildResource("resource");
        assertEquals("存在しないリソースのlastModifiedTimeは0であること",
            0L, child.getLastModifiedTime());

        long t1 = System.currentTimeMillis();
        child.mkdir();
        long t = child.getLastModifiedTime();
        long t2 = System.currentTimeMillis();
        assertTrue("ディレクトリを作成した時点で" +
             "lastModifiedTimeが設定されること",
             t1 <= t && t <= t2);
        child.delete();

        t1 = System.currentTimeMillis();
        child.getOutputStream().close();
        t = child.getLastModifiedTime();
        t2 = System.currentTimeMillis();
        assertTrue("リソースを作成した時点で" +
             "lastModifiedTimeが設定されること",
             t1 <= t && t <= t2);
    }


    public void testGetName()
        throws Exception
    {
        Resource root = MapResource.newInstance();
        assertEquals("", root.getName());

        assertEquals("resource", root.getChildResource("resource").getName());

        assertEquals("resource",
            root.getChildResource("path/to/resource").getName());
    }


    public void testGetOutputStream()
        throws Exception
    {
        MapResource root = MapResource.newInstance();
        MapResource child = (MapResource)root.getChildResource("resource");
        child.mkdir();
        try {
            child.getOutputStream();
            fail("ディレクトリのOutputStreamは取得できないこと");
        } catch (ResourceNotFoundException ex) {
            ;
        }
        child.delete();

        OutputStream os = child.getOutputStream();
        os.write(1);
        os.write(2);
        os.write(3);
        os.close();
        InputStream is = child.getInputStream();
        assertEquals(1, is.read());
        assertEquals(2, is.read());
        assertEquals(3, is.read());
        assertEquals(-1, is.read());
        is.close();
    }


    public void testGetParentResource()
        throws Exception
    {
        assertNull("ルートの親がnullであること",
            MapResource.newInstance().getParentResource());

        assertEquals("/path/to",
            ((MapResource)MapResource.newInstance("/path/to/reosurce")
            .getParentResource()).getPath());
    }


    public void testGetSize()
        throws Exception
    {
        MapResource resource = MapResource.newInstance("/resource");
        assertEquals("存在しないリソースのsizeは0であること",
            0L, resource.getSize());

        resource.mkdir();
        assertEquals("ディレクトリのsizeは0であること",
            0L, resource.getSize());
        resource.delete();

        OutputStream os = resource.getOutputStream();
        os.write(1);
        os.write(2);
        os.write(3);
        os.close();
        assertEquals(3, resource.getSize());
    }


    public void testIsDirectory()
        throws Exception
    {
        MapResource root = MapResource.newInstance();
        assertTrue("ルートはディレクトリであること", root.isDirectory());

        Resource childDir = root.getChildResource("path");
        assertFalse("存在しないリソースはディレクトリでないこと",
            childDir.isDirectory());

        childDir.mkdir();
        assertTrue("ディレクトリが正しく判定されること",
            childDir.isDirectory());
        childDir.delete();

        childDir.getOutputStream().close();
        assertFalse("リソースがディレクトリでないと判定されること",
            childDir.isDirectory());
    }


    public void testList()
        throws Exception
    {
        MapResource root = MapResource.newInstance();
        Resource childDir = root.getChildResource("path");
        childDir.getOutputStream().close();
        assertNull("リソースのlistはnullであること", childDir.list());
        childDir.delete();

        childDir.mkdir();
        childDir.getChildResource("c1");
        childDir.getChildResource("c2").mkdir();
        childDir.getChildResource("c3");
        childDir.getChildResource("c4").getOutputStream().close();
        String[] list = childDir.list();
        assertNotNull("ディレクトリのlistがnullでないこと", list);
        assertEquals("存在しないリソースはカウントされないこと",
            2, list.length);
        Set actual = new HashSet(Arrays.asList(list));
        assertTrue("存在するリソースが返されること", actual.contains("c2"));
        assertTrue("存在するリソースが返されること", actual.contains("c4"));
    }


    public void testListResources()
        throws Exception
    {
        MapResource root = MapResource.newInstance();
        Resource childDir = root.getChildResource("path");
        childDir.getOutputStream().close();
        assertNull("リソースのlistはnullであること", childDir.listResources());
        childDir.delete();

        childDir.mkdir();
        childDir.getChildResource("c1");
        Resource c2 = childDir.getChildResource("c2");
        c2.mkdir();
        childDir.getChildResource("c3");
        Resource c4 = childDir.getChildResource("c4");
        c4.getOutputStream().close();
        Resource[] list = childDir.listResources();
        assertNotNull("ディレクトリのlistがnullでないこと", list);
        assertEquals("存在しないリソースはカウントされないこと",
            2, list.length);
        Set actual = new HashSet(Arrays.asList(list));
        assertTrue("存在するリソースが返されること", actual.contains(c2));
        assertTrue("存在するリソースが返されること", actual.contains(c4));
    }


    public void testMkdir()
        throws Exception
    {
        MapResource root = MapResource.newInstance();
        assertTrue("ディレクトリに実行してもtrueが返ること", root.mkdir());

        Resource childDir = root.getChildResource("/path");
        assertTrue("正常にディレクトリが作成されること", childDir.mkdir());
        assertTrue("正常にディレクトリが作成されること", childDir.exists());
        assertTrue("正常にディレクトリが作成されること",
            childDir.isDirectory());
        childDir.delete();

        Resource childDir2 = childDir.getChildResource("to");
        assertFalse("親ディレクトリが存在しない場合はfalseを返すこと",
            childDir2.mkdir());
        assertFalse("親ディレクトリが存在しない場合にmkdir()しても" +
            "ディレクトリが作成されないこと",
            childDir2.exists());
        assertFalse("親ディレクトリが存在しない場合にmkdir()しても" +
            "ディレクトリが作成されないこと",
            childDir2.isDirectory());
    }


    public void testMkdirs()
        throws Exception
    {
        MapResource root = MapResource.newInstance();
        assertTrue("ディレクトリに実行してもtrueが返ること", root.mkdirs());

        Resource childDir = root.getChildResource("/path");
        assertTrue("正常にディレクトリが作成されること", childDir.mkdirs());
        assertTrue("正常にディレクトリが作成されること", childDir.exists());
        assertTrue("正常にディレクトリが作成されること",
            childDir.isDirectory());
        childDir.delete();

        Resource childDir2 = childDir.getChildResource("to");
        assertTrue("親ディレクトリが存在しなくても" +
            "ディレクトリが作成されること", childDir2.mkdirs());
        assertTrue("親ディレクトリが存在しなくても" +
            "ディレクトリが作成されること", childDir2.exists());
        assertTrue("親ディレクトリが存在しなくても" +
            "ディレクトリが作成されること", childDir2.isDirectory());
    }


    public void testSetLastModifiedTime()
        throws Exception
    {
        MapResource root = MapResource.newInstance();
        root.setLastModifiedTime(100L);
        assertEquals("正しくlastModifiedTimeが設定されること",
            100L, root.getLastModifiedTime());
    }
}
