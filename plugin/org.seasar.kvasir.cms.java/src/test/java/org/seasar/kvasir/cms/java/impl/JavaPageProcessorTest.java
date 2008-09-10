package org.seasar.kvasir.cms.java.impl;

import java.io.File;
import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.seasar.kvasir.cms.java.Base;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.mock.MockPage;
import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceUtils;
import org.seasar.kvasir.util.io.impl.FileResource;


/**
 * @author YOKOTA Takehiko
 */
public class JavaPageProcessorTest extends TestCase
{
    public void testNewInstance()
        throws Exception
    {
        JavaPageProcessor processor = new JavaPageProcessor();
        processor.setPlugin(new JavaPluginImpl());
        processor.setElement(new MockElement());
        processor.init(new MockServletConfig());
        Method method = JavaPageProcessor.class
            .getDeclaredMethod("newInstance", new Class[] { Page.class,
                String.class, File.class });
        method.setAccessible(true);
        Page page = new MockPage(1, PathId.HEIM_MIDGARD, "/page");
        File file = ClassUtils.getFileOfResource(getClass().getResource(
            "javaPageProcessorTest1.txt"));
        Object obj = method
            .invoke(processor, new Object[] { page, "/1", file });
        assertNotNull("オブジェクトが生成されること", obj);
        assertTrue("Baseクラスのインスタンスであること", obj instanceof Base);

        Object obj2 = method.invoke(processor,
            new Object[] { page, "/1", file });
        assertNotSame("呼び出しの度に異なるインスタンスを返すこと", obj, obj2);

        // ファイルのタイムスタンプが確実に更新されないといけないので
        // こうしている。
        Thread.sleep(4000);
        Resource resource = new FileResource(file);
        String content = ResourceUtils.readString(resource, "");
        ResourceUtils.writeString(resource, content);
        Object obj3 = method.invoke(processor,
            new Object[] { page, "/1", file });
        assertNotSame("ファイルが変更された場合に再コンパイルされること", obj.getClass()
            .getClassLoader(), obj3.getClass().getClassLoader());

        File file2 = ClassUtils.getFileOfResource(getClass().getResource(
            "javaPageProcessorTest2.txt"));
        Object obj4 = method.invoke(processor,
            new Object[] { page, "/1", file2 });
        String msg4 = null;
        try {
            ((Base)obj4).execute();
            fail();
        } catch (Exception ex) {
            msg4 = ex.getMessage();
        }
        assertEquals("別のファイルを指すようになった場合に" + "新たなファイルの内容で再コンパイルされること", "2", msg4);
    }


    public void testファイルが開きっぱなしにならないこと()
        throws Exception
    {
        File file = File.createTempFile("JavaPageProcessor", "tmp");
        JavaPageProcessor processor = new JavaPageProcessor();
        processor.setPlugin(new JavaPluginImpl());
        processor.setEncoding("UTF-8");
        JavaPageProcessor.Entry entry = processor.new Entry(file);
        entry.compile();
        assertTrue(file.delete());
    }
}
