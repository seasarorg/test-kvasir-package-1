package org.seasar.kvasir.system.plugin;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import junitx.framework.ListAssert;

import org.seasar.framework.util.ResourceUtil;
import org.seasar.kvasir.base.Globals;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.mock.MockKvasir;
import org.seasar.kvasir.base.mock.plugin.MockPluginAlfr;
import org.seasar.kvasir.base.plugin.PluginAlfrSettings;
import org.seasar.kvasir.base.plugin.PluginCandidate;
import org.seasar.kvasir.base.plugin.PluginVersions;
import org.seasar.kvasir.base.plugin.RemoteRepository;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.descriptor.impl.PluginDescriptorImpl;
import org.seasar.kvasir.base.plugin.descriptor.mock.MockPluginDescriptor;
import org.seasar.kvasir.test.ProjectMetaData;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceUtils;
import org.seasar.kvasir.util.io.impl.FileResource;


/**
 * @author manhole
 */
public class PluginUpdaterImplTest extends TestCase
{

    private PluginUpdaterImpl updater_;

    private MockPluginAlfr mockPluginAlfr_;

    private MockKvasir kvasir_;

    private ProjectMetaData metaData_;

    private PluginAlfrSettings pluginAlfrSettings_;


    protected void setUp()
        throws Exception
    {
        super.setUp();
        metaData_ = new ProjectMetaData(getClass());
        final Resource testHomeDirectory = metaData_.getTestHomeDirectory();
        ResourceUtils.delete(testHomeDirectory, true);
        testHomeDirectory.mkdirs();
        final File f = File.createTempFile(PluginUpdaterImplTest.class
            .getSimpleName(), null, testHomeDirectory.toFile());
        f.delete();
        f.mkdirs();

        final Resource homeDirectory = new FileResource(f);
        System.out.println(homeDirectory);
        homeDirectory.mkdirs();

        kvasir_ = new MockKvasir();
        kvasir_.setHomeDirectory(homeDirectory);

        final File classFile = ResourceUtil.getResourceAsFile(
            PluginUpdaterImplTest.class.getName(), "class");

        pluginAlfrSettings_ = new PluginAlfrSettings();
        final File remoteRepo = new File(classFile.getParentFile(), "remote-2");
        final RemoteRepository remoteRepository = new RemoteRepository();
        remoteRepository.setRepositoryId("fooRepoId");
        remoteRepository.setUrl(remoteRepo.toURI().toURL().toExternalForm());
        remoteRepository.setEnabled(true);
        pluginAlfrSettings_.addRemoteRepository(remoteRepository);

        updater_ = new PluginUpdaterImpl();
        mockPluginAlfr_ = new MockPluginAlfr();
        mockPluginAlfr_.setSettings(pluginAlfrSettings_);
        updater_.setPluginAlfr(mockPluginAlfr_);
        updater_.setKvasir(kvasir_);
    }


    @Override
    protected void tearDown()
        throws Exception
    {
        ResourceUtils.delete(metaData_.getTestHomeDirectory(), true);
        pluginAlfrSettings_ = null;
        mockPluginAlfr_ = null;
        updater_ = null;
        super.tearDown();
    }


    /**
     * 利用可能なバージョン番号を取得する。
     */
    public void testGetAvailableVersions()
        throws Exception
    {
        // ## Arrange ##

        // ## Act ##
        final List<Version> versions = updater_.getAvailableVersions(
            "com.example", false);

        // ## Assert ##
        ListAssert.assertEquals(
            Arrays.asList(new Version[] { new Version("1.0.0"),
                new Version("1.0.1"), new Version("1.0.2") }), versions);
    }


    /**
     * SNAPSHOT以外の利用可能なバージョン番号を取得する。
     */
    public void testGetAvailableVersions_excludeSnapshot()
        throws Exception
    {
        // ## Arrange ##

        // ## Act ##
        final List<Version> versions = updater_.getAvailableVersions(
            "com.snapshot.ccc", true);

        // ## Assert ##
        ListAssert.assertEquals(Arrays.asList(new Version[] {
            new Version("1.0.0"), new Version("2.0.0") }), versions);
    }


    public void testGetAvailableVersions_includeSnapshot()
        throws Exception
    {
        // ## Arrange ##

        // ## Act ##
        final List<Version> versions = updater_.getAvailableVersions(
            "com.snapshot.ccc", false);

        // ## Assert ##
        ListAssert.assertEquals(Arrays.asList(new Version[] {
            new Version("1.0.0"), new Version("2.0.0"),
            new Version("3.0.0-SNAPSHOT") }), versions);
    }


    /**
     * disabledなレポジトリからは取得しない。
     */
    public void testGetAvailableVersions_DisabledRepo()
        throws Exception
    {
        // ## Arrange ##
        pluginAlfrSettings_.getRemoteRepositories()[0].setEnabled(false);

        // ## Act ##
        final List<Version> versions = updater_.getAvailableVersions(
            "com.example", false);

        // ## Assert ##
        assertEquals(0, versions.size());
    }


    /**
     * 今使っているバージョンより新しいバージョンを取得する。
     */
    public void testGetNewerVersion()
        throws Exception
    {
        // ## Arrange ##

        // ## Act ##
        final MockPluginDescriptor plugin = new MockPluginDescriptor();
        plugin.setId("com.example");
        plugin.setVersion(new Version("1.0.0"));
        final Version version = updater_.getNewerVersion(plugin);

        // ## Assert ##
        assertEquals("1.0.2", version.toString());
    }


    /**
     * 既に最新バージョンが手元にある場合はnullが返されること。
     */
    public void testGetNewerVersion2()
        throws Exception
    {
        // ## Arrange ##

        // ## Act ##
        final MockPluginDescriptor plugin = new MockPluginDescriptor();
        plugin.setId("com.example");
        plugin.setVersion(new Version("1.0.2"));
        final Version version = updater_.getNewerVersion(plugin);

        // ## Assert ##
        assertEquals(null, version);
    }


    /**
     * インストールされているプラグインが無いので、1つも返らない。
     */
    public void testUpdatedPlugins1()
        throws Exception
    {
        // ## Arrange ##
        mockPluginAlfr_.setAllPluginDescriptors(new PluginDescriptor[] {});

        // ## Act ##
        PluginVersions[] pluginVersions = updater_.getUpdatedPlugins(false);

        // ## Assert ##
        assertEquals(0, pluginVersions.length);
    }


    /**
     * 今インストールされている全プラグインについて、
     * 新しいバージョンの一覧を取得する。
     * 更新対象があるプラグインのみエントリが返ること。
     */
    public void testUpdatedPlugins2()
        throws Exception
    {
        // ## Arrange ##
        final PluginDescriptor plugin1 = new PluginDescriptorImpl();
        plugin1.setId("com.example");
        plugin1.setVersionString("1.0.0");
        final PluginDescriptor plugin2 = new PluginDescriptorImpl();
        plugin2.setId("foo.bar");
        plugin2.setVersionString("2.0.0");
        final PluginDescriptor plugin3 = new PluginDescriptorImpl();
        plugin3.setId("com.example.aaa");
        plugin3.setVersionString("1.5.0");
        mockPluginAlfr_.setAllPluginDescriptors(new PluginDescriptor[] {
            plugin1, plugin2, plugin3 });

        // ## Act ##
        final PluginVersions[] pluginVersions = updater_
            .getUpdatedPlugins(false);

        // ## Assert ##
        assertEquals(2, pluginVersions.length);
        {
            final PluginVersions versions = pluginVersions[0];
            assertEquals("com.example", versions.getId());
            final Version[] v = versions.getVersions();
            assertEquals(2, v.length);
            assertEquals("1.0.1", v[0].getString());
            assertEquals("1.0.2", v[1].getString());
        }
        {
            final PluginVersions versions = pluginVersions[1];
            assertEquals("com.example.aaa", versions.getId());
            final Version[] v = versions.getVersions();
            assertEquals(2, v.length);
            assertEquals("2.0.0", v[0].getString());
            assertEquals("3.1.0", v[1].getString());
        }
    }


    /*
     * exclude=falseでSNAPSHOTの方がバージョンが大きい場合には、
     * SNAPSHOTを含めること。
     */
    public void testUpdatedPlugins_withSnapshot1()
        throws Exception
    {
        // ## Arrange ##
        final PluginDescriptor plugin1 = new PluginDescriptorImpl();
        plugin1.setId("com.snapshot.bbb");
        plugin1.setVersionString("0.9.0");
        mockPluginAlfr_
            .setAllPluginDescriptors(new PluginDescriptor[] { plugin1 });

        // ## Act ##
        final PluginVersions[] pluginVersions = updater_
            .getUpdatedPlugins(false);

        // ## Assert ##
        assertEquals(1, pluginVersions.length);
        {
            final PluginVersions versions = pluginVersions[0];
            assertEquals("com.snapshot.bbb", versions.getId());
            final Version[] v = versions.getVersions();
            assertEquals(1, v.length);
            assertEquals("1.0.0-SNAPSHOT", v[0].getString());
        }
    }


    /*
     * exclude=trueでSNAPSHOTの方がバージョンが大きい場合には、
     * SNAPSHOTを含めないこと。
     */
    public void testUpdatedPlugins_excludeSnapshot1()
        throws Exception
    {
        // ## Arrange ##
        final PluginDescriptor plugin1 = new PluginDescriptorImpl();
        plugin1.setId("com.snapshot.bbb");
        plugin1.setVersionString("0.9.0");
        mockPluginAlfr_
            .setAllPluginDescriptors(new PluginDescriptor[] { plugin1 });

        // ## Act ##
        final PluginVersions[] pluginVersions = updater_
            .getUpdatedPlugins(true);

        // ## Assert ##
        assertEquals(0, pluginVersions.length);
    }


    /*
     * exclude=falseでインストールされているSNAPSHOTが同じバージョンのときは、
     * SNAPSHOTを含めること。
     */
    public void testUpdatedPlugins_withSnapshot2()
        throws Exception
    {
        // ## Arrange ##
        final PluginDescriptor plugin1 = new PluginDescriptorImpl();
        plugin1.setId("com.snapshot.bbb");
        plugin1.setVersionString("1.0.0-SNAPSHOT");
        mockPluginAlfr_
            .setAllPluginDescriptors(new PluginDescriptor[] { plugin1 });

        // ## Act ##
        final PluginVersions[] pluginVersions = updater_
            .getUpdatedPlugins(false);

        // ## Assert ##
        assertEquals(1, pluginVersions.length);
        {
            final PluginVersions versions = pluginVersions[0];
            assertEquals("com.snapshot.bbb", versions.getId());
            final Version[] v = versions.getVersions();
            assertEquals(1, v.length);
            assertEquals("1.0.0-SNAPSHOT", v[0].getString());
        }
    }


    /*
     * exclude=trueでSNAPSHOTがインストールされているのと同じバージョンのときは、
     * SNAPSHOTを含めないこと。
     */
    public void testUpdatedPlugins_excludeSnapshot2()
        throws Exception
    {
        // ## Arrange ##
        final PluginDescriptor plugin1 = new PluginDescriptorImpl();
        plugin1.setId("com.snapshot.bbb");
        plugin1.setVersionString("1.0.0-SNAPSHOT");
        mockPluginAlfr_
            .setAllPluginDescriptors(new PluginDescriptor[] { plugin1 });

        // ## Act ##
        final PluginVersions[] pluginVersions = updater_
            .getUpdatedPlugins(true);

        // ## Assert ##
        assertEquals(0, pluginVersions.length);
    }


    /*
     * exclude=trueの場合はSNAPSHOTバージョンが含まれること。
     */
    public void testUpdatedPlugins_withSnapshot3()
        throws Exception
    {
        // ## Arrange ##
        final PluginDescriptor plugin1 = new PluginDescriptorImpl();
        plugin1.setId("com.snapshot.ccc");
        plugin1.setVersionString("1.0.0");
        mockPluginAlfr_
            .setAllPluginDescriptors(new PluginDescriptor[] { plugin1 });

        // ## Act ##
        final PluginVersions[] pluginVersions = updater_
            .getUpdatedPlugins(false);

        // ## Assert ##
        assertEquals(1, pluginVersions.length);
        {
            final PluginVersions versions = pluginVersions[0];
            assertEquals("com.snapshot.ccc", versions.getId());
            final Version[] v = versions.getVersions();
            assertEquals(2, v.length);
            assertEquals("2.0.0", v[0].getString());
            assertEquals("3.0.0-SNAPSHOT", v[1].getString());
        }
    }


    /*
     * exclude=trueの場合はSNAPSHOTバージョンが含まれないこと。
     */
    public void testUpdatedPlugins_excludeSnapshot3()
        throws Exception
    {
        // ## Arrange ##
        final PluginDescriptor plugin1 = new PluginDescriptorImpl();
        plugin1.setId("com.snapshot.ccc");
        plugin1.setVersionString("1.0.0");
        mockPluginAlfr_
            .setAllPluginDescriptors(new PluginDescriptor[] { plugin1 });

        // ## Act ##
        final PluginVersions[] pluginVersions = updater_
            .getUpdatedPlugins(true);

        // ## Assert ##
        assertEquals(1, pluginVersions.length);
        {
            final PluginVersions versions = pluginVersions[0];
            assertEquals("com.snapshot.ccc", versions.getId());
            final Version[] v = versions.getVersions();
            assertEquals(1, v.length);
            assertEquals("2.0.0", v[0].getString());
        }
    }


    /**
     * プラグインIDを指定して、プラグインをインストールする
     */
    public void testInstallNewPlugin()
        throws Exception
    {
        // ## Arrange ##
        final Resource pluginsStaging = kvasir_.getStagingDirectory()
            .getChildResource(Globals.PLUGINS_DIR);

        /*
         * このディレクトリはまだ存在しないこと。
         */
        final Resource installedPluginDir = pluginsStaging
            .getChildResource("com.example-1.0.1");
        assertEquals(false, installedPluginDir.exists());

        // ## Act ##
        updater_.installPlugin("com.example", "1.0.1", false);

        // ## Assert ##
        {
            final Resource[] resources = pluginsStaging.listResources();
            for (int i = 0; i < resources.length; i++) {
                final Resource resource = resources[i];
                System.out.println("[" + i + "] "
                    + resource.toFile().getAbsolutePath());
            }
            assertEquals(1, resources.length);
        }
        /*
         * kvasir/plugins/com.example-1.0.1が作成されていること。
         */
        assertEquals(true, installedPluginDir.exists());
        assertEquals(true, installedPluginDir.isDirectory());
    }


    /*
     * TODO 指定されたArtifactが"kvasir-plugin"では無い場合はエラーにする(?)
     */

    /**
     * 依存先Artifactも一緒にDL・installされること。
     */
    public void testInstallDependencyPlugin()
        throws Exception
    {
        // ## Arrange ##
        final Resource pluginsStaging = kvasir_.getStagingDirectory()
            .getChildResource(Globals.PLUGINS_DIR);

        // ## Act ##
        updater_.installPlugin("com.depend.dep1", "1.0.0", false);

        // ## Assert ##
        {
            final Resource[] resources = pluginsStaging.listResources();
            for (int i = 0; i < resources.length; i++) {
                final Resource resource = resources[i];
                System.out.println("[" + i + "] "
                    + resource.toFile().getAbsolutePath());
            }
            assertEquals(3, resources.length);
        }
        {
            final Resource installedPluginDir = pluginsStaging
                .getChildResource("com.depend.dep1-1.0.0");
            assertEquals(true, installedPluginDir.exists());
            assertEquals(true, installedPluginDir.isDirectory());
        }
        {
            final Resource installedPluginDir = pluginsStaging
                .getChildResource("com.depend.dep2-1.0.2");
            assertEquals(true, installedPluginDir.exists());
            assertEquals(true, installedPluginDir.isDirectory());
        }
        {
            final Resource installedPluginDir = pluginsStaging
                .getChildResource("com.depend.dep3-1.0.3");
            assertEquals(true, installedPluginDir.exists());
            assertEquals(true, installedPluginDir.isDirectory());
        }
        {
            final Resource installedPluginDir = pluginsStaging
                .getChildResource("com.depend.dep4-1.0.4");
            assertEquals(false, installedPluginDir.exists());
        }
    }


    /*
     * exclude=trueのとき、更新対象のプラグインが依存する先がSNAPSHOTで、それが
     * 既にインストールされている場合は、依存する先のプラグインを対象に含めないこと。
     */
    public void testInstallDependencyPlugin_excludeSnapshot()
        throws Exception
    {
        // ## Arrange ##
        final Resource pluginsStaging = kvasir_.getStagingDirectory()
            .getChildResource(Globals.PLUGINS_DIR);

        final PluginDescriptor plugin1 = new PluginDescriptorImpl();
        plugin1.setId("com.snapshot.ddd");
        plugin1.setVersionString("1.0.0");
        final PluginDescriptor plugin2 = new PluginDescriptorImpl();
        plugin2.setId("com.snapshot.eee");
        plugin2.setVersionString("1.0.0-SNAPSHOT");
        mockPluginAlfr_.setAllPluginDescriptors(new PluginDescriptor[] {
            plugin1, plugin2 });

        // ## Act ##
        updater_.installPlugin("com.snapshot.ddd", "2.0.0", true);

        // ## Assert ##
        {
            final Resource[] resources = pluginsStaging.listResources();
            for (int i = 0; i < resources.length; i++) {
                final Resource resource = resources[i];
                System.out.println("[" + i + "] "
                    + resource.toFile().getAbsolutePath());
            }
            assertEquals(1, resources.length);
        }
        {
            final Resource installedPluginDir = pluginsStaging
                .getChildResource("com.snapshot.ddd-2.0.0");
            assertEquals(true, installedPluginDir.exists());
            assertEquals(true, installedPluginDir.isDirectory());
        }
    }


    /**
     * インストール対象プラグインが依存するプラグイン一覧を取得する。
     */
    public void testGetInstallCandidate()
        throws Exception
    {
        // ## Arrange ##
        kvasir_.getStagingDirectory().getChildResource(Globals.PLUGINS_DIR);

        // ## Act ##
        final PluginCandidate candidates = updater_.getInstallCandidate(
            "com.depend.dep1", "1.0.0", false);

        // ## Assert ##
        assertEquals("com.depend.dep1", candidates.getId());
        assertEquals("1.0.0", candidates.getVersion().toString());

        final PluginCandidate[] dependencies = candidates.getDependencies();
        Arrays.sort(dependencies, new Comparator<PluginCandidate>() {
            public int compare(PluginCandidate o1, PluginCandidate o2)
            {
                return o1.getId().compareTo(o2.getId());
            }
        });
        assertEquals(2, dependencies.length);
        assertEquals("com.depend.dep2", dependencies[0].getId());
        assertEquals("1.0.2", dependencies[0].getVersion().toString());
        assertEquals("com.depend.dep3", dependencies[1].getId());
        assertEquals("1.0.3", dependencies[1].getVersion().toString());
    }


    /**
     * インストール対象プラグインが依存するプラグイン一覧を取得する。
     * 既にインストールされているプラグインは除かれていること。
     */
    public void testGetInstallCandidate_excludeAlreadyInstalled()
        throws Exception
    {
        // ## Arrange ##
        kvasir_.getStagingDirectory().getChildResource(Globals.PLUGINS_DIR);

        {
            // 既にインストールされているものとする

            final PluginDescriptor plugin2 = new PluginDescriptorImpl();
            plugin2.setId("com.depend.dep2");
            plugin2.setVersionString("1.0.0");
            final PluginDescriptor plugin3 = new PluginDescriptorImpl();
            plugin3.setId("com.depend.dep3");
            plugin3.setVersionString("1.0.3");
            mockPluginAlfr_.setAllPluginDescriptors(new PluginDescriptor[] {
                plugin2, plugin3 });
        }

        // ## Act ##
        final PluginCandidate candidates = updater_.getInstallCandidate(
            "com.depend.dep1", "1.0.0", false);

        // ## Assert ##
        assertEquals("com.depend.dep1", candidates.getId());
        assertEquals("1.0.0", candidates.getVersion().toString());

        final PluginCandidate[] dependencies = candidates.getDependencies();
        for (int i = 0; i < dependencies.length; i++) {
            final PluginCandidate candidate = dependencies[i];
            System.out.println("[" + i + "]" + candidate.getId());
        }
        assertEquals(1, dependencies.length);
        assertEquals("com.depend.dep2", dependencies[0].getId());
        assertEquals("1.0.2", dependencies[0].getVersion().toString());
    }


    /**
     * FIXME
     */
    public void testGetInstallCandidate_excludeSnapshot()
        throws Exception
    {
        // ## Arrange ##
        final PluginDescriptor plugin2 = new PluginDescriptorImpl();
        plugin2.setId("com.snapshot.eee");
        plugin2.setVersionString("1.0.0-SNAPSHOT");
        mockPluginAlfr_
            .setAllPluginDescriptors(new PluginDescriptor[] { plugin2 });

        // ## Act ##
        final PluginCandidate candidates = updater_.getInstallCandidate(
            "com.snapshot.ddd", "1.0.0", true);

        // ## Assert ##
        assertEquals("com.snapshot.ddd", candidates.getId());
        assertEquals("1.0.0", candidates.getVersion().toString());

        final PluginCandidate[] dependencies = candidates.getDependencies();
        assertEquals(0, dependencies.length);
    }


    /**
     * FIXME
     */
    public void testGetInstallCandidate_includeSnapshot()
        throws Exception
    {
        // ## Arrange ##

        final PluginDescriptor plugin2 = new PluginDescriptorImpl();
        plugin2.setId("com.snapshot.eee");
        plugin2.setVersionString("1.0.0-SNAPSHOT");
        mockPluginAlfr_
            .setAllPluginDescriptors(new PluginDescriptor[] { plugin2 });

        // ## Act ##
        final PluginCandidate candidates = updater_.getInstallCandidate(
            "com.snapshot.ddd", "1.0.0", false);

        // ## Assert ##
        assertEquals("com.snapshot.ddd", candidates.getId());
        assertEquals("1.0.0", candidates.getVersion().toString());

        final PluginCandidate[] dependencies = candidates.getDependencies();
        assertEquals(1, dependencies.length);
        assertEquals("com.snapshot.eee", dependencies[0].getId());
        assertEquals("1.0.0-SNAPSHOT", dependencies[0].getVersion().toString());
    }


    /**
     * プラグインが既にインストールされているか判定する。
     */
    public void testContainsPlugin()
        throws Exception
    {
        // ## Arrange ##
        final PluginDescriptor plugin1 = new PluginDescriptorImpl();
        plugin1.setId("com.example.aaa");
        plugin1.setVersionString("1.0.0");
        final PluginDescriptor plugin2 = new PluginDescriptorImpl();
        plugin2.setId("com.example.ccc");
        plugin2.setVersionString("1.0.1");
        mockPluginAlfr_.setAllPluginDescriptors(new PluginDescriptor[] {
            plugin1, plugin2 });

        // ## Act ##
        // ## Assert ##
        assertEquals(true, updater_.containsPlugin("com.example.aaa"));
        assertEquals(false, updater_.containsPlugin("com.example.bbb"));
        assertEquals(true, updater_.containsPlugin("com.example.ccc"));
    }

}
