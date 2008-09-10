package org.seasar.kvasir.system;

import junit.framework.TestCase;

import org.seasar.kvasir.base.Globals;
import org.seasar.kvasir.test.ProjectMetaData;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.impl.MapResource;


public class KvasirImplTest extends TestCase
{
    private ProjectMetaData project_;


    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        project_ = new ProjectMetaData(getClass());
    }


    public void testGetStructuredProperty()
        throws Exception
    {
        final Resource configurationDir = project_.getTestHomeDirectory()
            .getChildResource("configuration");
        Resource dir = configurationDir.getChildResource("structures");
        dir.mkdirs();
        IOUtils.pipe(getClass().getClassLoader().getResourceAsStream(
            getClass().getName().replace('.', '/').concat(
                "_testGetStructuredProperty.xml")), dir.getChildResource(
            "name.xml").getOutputStream());

        Structure expected = new Structure();
        expected.setName("NAME");
        Body body = new Body();
        body.setContent("CONTENT");
        expected.setBody(body);

        KvasirImpl target = new KvasirImpl() {
            @Override
            public Resource getConfigurationDirectory()
            {
                return configurationDir;
            }
        };

        assertEquals("正しく構造化プロパティが取得できること", expected, target
            .getStructuredProperty("name", Structure.class));

        assertNull("データが存在しない場合はnullが返されること", target.getStructuredProperty(
            "none", Structure.class));
    }


    public void testSetStructuredProperty()
        throws Exception
    {
        final Resource configurationDir = project_.getTestHomeDirectory()
            .getChildResource("configuration");
        Resource dir = configurationDir.getChildResource("structures");
        dir.mkdirs();
        Resource resource = dir.getChildResource("name.xml");
        resource.delete();
        String expected = IOUtils.readString(getClass().getClassLoader()
            .getResourceAsStream(
                getClass().getName().replace('.', '/').concat(
                    "_testGetStructuredProperty.xml")), "UTF-8", true);

        Structure structure = new Structure();
        structure.setName("NAME");
        Body body = new Body();
        body.setContent("CONTENT");
        structure.setBody(body);

        KvasirImpl target = new KvasirImpl() {
            @Override
            public Resource getConfigurationDirectory()
            {
                return configurationDir;
            }
        };

        target.setStructuredProperty("name", structure);

        String actual = IOUtils.readString(resource.getInputStream(), "UTF-8",
            true);
        assertEquals(expected, actual);
    }


    public void testSetStructuredProperty2()
        throws Exception
    {
        final Resource configurationDir = project_.getTestHomeDirectory()
            .getChildResource("configuration");
        Resource dir = configurationDir.getChildResource("structures");
        dir.mkdirs();
        IOUtils.pipe(getClass().getClassLoader().getResourceAsStream(
            getClass().getName().replace('.', '/').concat(
                "_testGetStructuredProperty.xml")), dir.getChildResource(
            "name.xml").getOutputStream());

        KvasirImpl target = new KvasirImpl() {
            @Override
            public Resource getConfigurationDirectory()
            {
                return configurationDir;
            }
        };

        assertNotNull(target.getStructuredProperty("name", Structure.class));

        target.setStructuredProperty("name", null);

        assertNull("構造化プロパティとしてnullを指定した場合はデータが削除されること", target
            .getStructuredProperty("name", Structure.class));
    }


    public void testGetSystemDirectory()
        throws Exception
    {
        // ## Arrange ##
        final KvasirImpl kvasir = new KvasirImpl();
        kvasir.setHomeDirectory(new MapResource());

        // ## Act ##
        final Resource dir = kvasir.getSystemDirectory();

        // ## Assert ##
        assertEquals("systemディレクトリを返すこと", Globals.SYSTEM_DIR, dir.getName());
    }


    public void testGetPluginsDirectory()
        throws Exception
    {
        // ## Arrange ##
        final KvasirImpl kvasir = new KvasirImpl();
        kvasir.setHomeDirectory(new MapResource());

        // ## Act ##
        final Resource dir = kvasir.getPluginsDirectory();

        // ## Assert ##
        assertEquals("pluginsディレクトリを返すこと", Globals.PLUGINS_DIR, dir.getName());
    }


    public void testGetStagingDirectory()
        throws Exception
    {
        // ## Arrange ##
        final KvasirImpl kvasir = new KvasirImpl();
        kvasir.setHomeDirectory(new MapResource());

        // ## Act ##
        final Resource dir = kvasir.getStagingDirectory();

        // ## Assert ##
        assertEquals("stagingディレクトリを返すこと", Globals.STAGING_DIR, dir.getName());
    }

}
