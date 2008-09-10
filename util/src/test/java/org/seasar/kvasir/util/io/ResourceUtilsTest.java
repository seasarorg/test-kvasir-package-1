package org.seasar.kvasir.util.io;

import org.seasar.kvasir.util.io.impl.MapResource;

import junit.framework.TestCase;


/**
 * @author YOKOTA Takehiko
 */
public class ResourceUtilsTest extends TestCase
{
    public void testCopy_Resource_Resource()
        throws Exception
    {
        Resource root = MapResource.newInstance();
        Resource from = root.getChildResource("/from");
        from.mkdir();
        from.getChildResource("f1").getOutputStream().close();
        Resource f2 = from.getChildResource("f2");
        f2.mkdir();
        f2.getChildResource("f21").getOutputStream().close();
        Resource to = root.getChildResource("/to");
        ResourceUtils.copy(from, to);
        assertTrue(to.exists());
        assertTrue(to.isDirectory());
        assertEquals(2, to.list().length);
        assertTrue(to.getChildResource("f1").exists());
        assertTrue(to.getChildResource("f2").exists());
        assertEquals(1, to.getChildResource("f2").list().length);
        assertTrue(to.getChildResource("f2/f21").exists());
    }


    public void testCopy_Resource_Resource_StringArray()
        throws Exception
    {
        Resource root = MapResource.newInstance();
        Resource from = root.getChildResource("/from");
        from.mkdir();
        from.getChildResource("f1").getOutputStream().close();
        Resource f2 = from.getChildResource("f2");
        f2.mkdir();
        f2.getChildResource("f21").getOutputStream().close();
        Resource f3 = from.getChildResource("f3");
        f3.mkdir();
        f3.getChildResource("f31").getOutputStream().close();
        f3.getChildResource("f32").getOutputStream().close();
        Resource to = root.getChildResource("/to");
        ResourceUtils.copy(from, to,
            new String[]{ "f1", "f2", "f31" });
        assertTrue(to.exists());
        assertTrue(to.isDirectory());
        assertEquals(1, to.list().length);
        assertEquals(1, to.getChildResource("f3").list().length);
    }
}
