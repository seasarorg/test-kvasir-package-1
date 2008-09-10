package org.seasar.kvasir.base;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.io.FileUtils;
import org.seasar.kvasir.util.io.ResourceUtils;
import org.seasar.kvasir.util.io.impl.FileResource;


/**
 * @author YOKOTA Takehiko
 */
public class AsgardTest extends TestCase
{
    public void testEstablish()
    {
        // ホームディレクトリのパスを取得する。
        URL test = getClass().getClassLoader()
            .getResource("kvasir.xproperties");
        File projectDir = new File(test.getPath()).getParentFile()
            .getParentFile().getParentFile();
        File homeDir = new File(projectDir, "target/test-home");
        ResourceUtils.deleteChildren(new FileResource(homeDir));

        PropertyHandler prop = new MapProperties();
        prop.setProperty(Globals.PROP_SYSTEM_HOME_DIR, FileUtils
            .toAbstractPath(homeDir.getPath()));

        try {
            Asgard.establish("kvasir.xproperties", null, prop, AsgardTest.class
                .getClassLoader());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Can't establish Asgard");
        }
    }


    public void testGetFileOfResource()
        throws Exception
    {
        URL landmark = getClass().getResource("AsgardTest_landmark.txt");
        URL target = getClass().getResource("AsgardTest_target.txt");
        File expected = ClassUtils.getFileOfResource(target);

        File actual = Asgard.getFileOfResource(
            "org/seasar/kvasir/base/AsgardTest_target.txt",
            "org/seasar/kvasir/base/AsgardTest_landmark.txt", ClassUtils
                .getFileOfResource(landmark));

        assertEquals("正しくリソースを表すFileを取得できること", expected, actual);
    }
}
