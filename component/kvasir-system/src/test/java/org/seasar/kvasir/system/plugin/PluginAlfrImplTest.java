package org.seasar.kvasir.system.plugin;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import junit.framework.TestCase;

import org.seasar.kvasir.base.Globals;
import org.seasar.kvasir.base.Identifier;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.container.ComponentContainerFactory;
import org.seasar.kvasir.base.container.ComponentNotFoundRuntimeException;
import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.AbstractGenericElement;
import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.base.descriptor.Property;
import org.seasar.kvasir.base.mock.MockKvasir;
import org.seasar.kvasir.base.mock.container.MockComponentContainer;
import org.seasar.kvasir.base.mock.plugin.MockPlugin;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.PluginAlfrSettings;
import org.seasar.kvasir.base.plugin.RemoteRepository;
import org.seasar.kvasir.base.plugin.descriptor.Export;
import org.seasar.kvasir.base.plugin.descriptor.Extension;
import org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint;
import org.seasar.kvasir.base.plugin.descriptor.Import;
import org.seasar.kvasir.base.plugin.descriptor.Library;
import org.seasar.kvasir.base.plugin.descriptor.Patch;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.descriptor.Requires;
import org.seasar.kvasir.base.plugin.descriptor.Runtime;
import org.seasar.kvasir.base.plugin.descriptor.impl.PluginDescriptorImpl;
import org.seasar.kvasir.base.plugin.impl.PluginImpl;
import org.seasar.kvasir.base.plugin.impl.PluginProperties;
import org.seasar.kvasir.base.util.XOMUtils;
import org.seasar.kvasir.system.container.PluggableS2ComponentContainer;
import org.seasar.kvasir.system.container.PluggableS2ComponentContainerFactory;
import org.seasar.kvasir.system.container.descriptor.Component;
import org.seasar.kvasir.system.container.descriptor.Components;
import org.seasar.kvasir.system.plugin.PluginAlfrImpl.ClassLoaderPair;
import org.seasar.kvasir.test.ProjectMetaData;
import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.collection.I18NProperties;
import org.seasar.kvasir.util.collection.I18NPropertyHandler;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceUtils;
import org.seasar.kvasir.util.io.impl.JavaResource;

import net.skirnir.xom.Element;


/**
 * @author YOKOTA Takehiko
 */
public class PluginAlfrImplTest extends TestCase
{
    private ProjectMetaData metaData_;

    private PluginAlfrImpl target_;

    private MockKvasir kvasir_;


    private void preparePluginResources(String[] pluginNames)
    {
        for (int i = 0; i < pluginNames.length; i++) {
            Resource src = metaData_.getTestResourcesSourceDirectory()
                .getChildResource("plugins/" + pluginNames[i]);
            Resource dest = metaData_.getTestHomeDirectory().getChildResource(
                "plugins/" + pluginNames[i]);
            ResourceUtils.copy(src, dest, new String[] { ".svn", "CVS" });
        }
    }


    private AbstractPlugin<?>[] loadPlugins(String[] pluginNames)
        throws Exception
    {
        List<Plugin<?>> list = new ArrayList<Plugin<?>>(pluginNames.length);
        for (int i = 0; i < pluginNames.length; i++) {
            String pluginName = pluginNames[i];

            Resource pluginDir = metaData_.getTestResourcesSourceDirectory()
                .getChildResource("plugins/" + pluginName);
            Resource pluginConfigDir = metaData_
                .getTestHomeDirectory()
                .getChildResource(Globals.CONFIGURATION_DIR + "/plugins/config");
            Resource pluginWorkDir = metaData_.getTestHomeDirectory()
                .getChildResource(Globals.CONFIGURATION_DIR + "/plugins/work");
            Resource pluginXML = pluginDir.getChildResource("plugin.xml");
            PluginDescriptor descriptor = target_.readXMLResourceAsBean(
                pluginXML, PluginDescriptor.class);
            descriptor.setSystemDirectories(pluginDir, pluginConfigDir,
                pluginWorkDir);
            descriptor.setResolvedResourcesDirectory(pluginDir);
            Plugin<?> plugin = new PluginImpl<Object>();
            I18NPropertyHandler prop = new I18NProperties(pluginDir, "plugin",
                ".xproperties");
            plugin.setProperties(new PluginProperties(descriptor.getId(), null,
                prop));
            plugin.setDescriptor(descriptor);
            descriptor.setPlugin(plugin);
            plugin.setInnerClassLoader(getClass().getClassLoader());
            plugin.setOuterClassLoader(getClass().getClassLoader());

            list.add(plugin);
        }
        return list.toArray(new AbstractPlugin[0]);
    }


    protected void setUp()
        throws Exception
    {
        metaData_ = new ProjectMetaData(getClass());
        Resource testHomeDirectory = metaData_.getTestHomeDirectory();
        assertEquals(true, ResourceUtils.deleteChildren(testHomeDirectory));
        ResourceUtils.copy(metaData_.getTestHomeSourceDirectory(),
            testHomeDirectory);
        target_ = new PluginAlfrImpl() {
            @Override
            Plugin<?> newPluginInstance()
            {
                return new PluginImpl<Object>();
            }
        };
        final Resource configurationDirectory = testHomeDirectory
            .getChildResource("configuration");
        configurationDirectory.mkdirs();
        ResourceUtils.deleteChildren(configurationDirectory);
        final Resource runtimeWorkDirectory = testHomeDirectory
            .getChildResource("rtwork");
        runtimeWorkDirectory.mkdirs();
        ResourceUtils.deleteChildren(runtimeWorkDirectory);
        kvasir_ = new MockKvasir();
        kvasir_.setHomeDirectory(testHomeDirectory);
        target_.setKvasir(kvasir_);

        ComponentContainerFactory.initialize(
            PluggableS2ComponentContainerFactory.class.getName(), getClass()
                .getClassLoader());
    }


    public void testReadPluginXML()
        throws Exception
    {
        preparePluginResources(new String[] { "plugin1" });

        Resource pluginXML = metaData_.getTestResourcesSourceDirectory()
            .getChildResource("plugins/plugin1/plugin.xml");
        PluginDescriptor descriptor = target_.readXMLResourceAsBean(pluginXML,
            PluginDescriptor.class);

        assertEquals("plugin.id", descriptor.getId());
        assertEquals("plugin.name", descriptor.getName());
        assertEquals("plugin.provider-name", descriptor.getProviderName());
        assertEquals("1.0.0", descriptor.getVersionString());

        ExtensionPoint[] extensionPoints = descriptor.getExtensionPoints();
        assertNotNull(extensionPoints);
        assertEquals(2, extensionPoints.length);
        ExtensionPoint extensionPoint = extensionPoints[0];
        assertEquals("extension1s", extensionPoint.getId());
        assertEquals("org.seasar.kvasir.system.plugin.Element1", extensionPoint
            .getElementClassName());

        Extension[] extensions = descriptor.getExtensions();
        assertNotNull(extensions);
        assertEquals(2, extensions.length);
        Extension extension = extensions[0];
        assertEquals("plugin.id.extension1s", extension.getPoint());

        Requires requires = descriptor.getRequires();
        assertNotNull(requires);
        Import[] imports = requires.getImports();
        assertNotNull(imports);
        assertEquals(2, imports.length);
        Import imprt = imports[0];
        assertEquals("org.seasar.kvasir.import1", imprt.getPlugin());
        assertEquals("1.0.0", imprt.getVersionString());
        assertEquals("perfect", imprt.getMatchString());

        Runtime runtime = descriptor.getRuntime();
        assertNotNull(runtime);
        Library[] libraries = runtime.getLibraries();
        assertNotNull(libraries);
        assertEquals(2, libraries.length);
        Library library = libraries[0];
        assertEquals("conf", library.getName());
        assertTrue(library.isFilter());
        assertEquals("ISO-8859-1", library.getEncoding());

        Export[] exports = libraries[1].getExports();
        assertNotNull(exports);
        assertEquals(2, exports.length);
        Export export = exports[0];
        assertEquals("org.seasar.kvasir.*", export.getName());
    }


    public void testGatherExtensionPoints()
        throws Exception
    {
        preparePluginResources(new String[] { "plugin3" });
        AbstractPlugin<?>[] plugins = loadPlugins(new String[] { "plugin3" });

        Map<PluginLocalKey, ExtensionPoint> extensionPointMap = new HashMap<PluginLocalKey, ExtensionPoint>();
        target_.gatherExtensionPoints(plugins[0].getDescriptor()
            .getExtensionPoints(), extensionPointMap);
        assertEquals(7, extensionPointMap.size());
        assertNotNull(extensionPointMap.get(target_
            .generateKey("plugin3.points")));
        assertNotNull(extensionPointMap.get(target_
            .generateKey("plugin3.points2")));
        assertNotNull(extensionPointMap.get(target_.generateKey("plugin3",
            Element1.class)));
        assertNotNull("スーパークラスについてもMapにエントリがputされていること", extensionPointMap
            .get(target_.generateKey("plugin3", AbstractGenericElement.class)));
        assertTrue(extensionPointMap.containsKey(target_.generateKey("plugin3",
            AbstractElement.class)));
        assertNull(extensionPointMap.get(target_.generateKey("plugin3",
            AbstractElement.class)));
        assertNotNull("実装しているインタフェースについてもMapにエントリがputされていること",
            extensionPointMap.get(target_.generateKey("plugin3",
                Interface1.class)));
        assertNotNull(extensionPointMap.get(target_.generateKey("plugin3",
            Element2.class)));
    }


    public void testDeployResources()
        throws Exception
    {
        preparePluginResources(new String[] { "plugin2" });
        AbstractPlugin<?>[] plugins = loadPlugins(new String[] { "plugin2" });

        AbstractPlugin<?> plugin = plugins[0];
        target_.filterResources(plugin.getDescriptor(), plugins[0]
            .getProperties());

        Resource resourcesDir = plugin.getDescriptor()
            .getRuntimeDeltaResourcesDirectory();
        Properties p = new Properties();
        InputStream in = resourcesDir.getChildResource(
            "filter2/file.properties").getInputStream();
        p.load(in);
        in.close();
        assertEquals("VALUE", p.getProperty("name"));

        p = new Properties();
        in = resourcesDir.getChildResource("filter2/sub/subfile.properties")
            .getInputStream();
        p.load(in);
        in.close();
        assertEquals("VALUE", p.getProperty("name"));

        assertFalse(resourcesDir.getChildResource("filter3").exists());
    }


    public void testCreateClassLoaders()
        throws Exception
    {
        preparePluginResources(new String[] { "plugin3", "plugin4" });
        AbstractPlugin<?>[] plugins = loadPlugins(new String[] { "plugin3",
            "plugin4" });

        AbstractPlugin<?> plugin3 = plugins[0];
        AbstractPlugin<?> plugin4 = plugins[1];
        Map<String, PluginDescriptor> descriptorMap = new HashMap<String, PluginDescriptor>();
        descriptorMap.put("plugin3", plugin3.getDescriptor());
        descriptorMap.put("plugin4", plugin4.getDescriptor());
        target_.sortPluginDescriptorsByDependencies(descriptorMap);

        Kvasir kvasir = new MockKvasir() {
            public ClassLoader getCommonClassLoader()
            {
                return getClass().getClassLoader();
            }
        };
        target_.setKvasir(kvasir);
        Map<String, ClassLoaderPair> pairMap = new HashMap<String, ClassLoaderPair>();
        ClassLoaderPair pair3 = target_.createClassLoader(plugin3
            .getDescriptor(), pairMap);
        pairMap.put(plugin3.getId(), pair3);
        ClassLoaderPair pair4 = target_.createClassLoader(plugin4
            .getDescriptor(), pairMap);
        pairMap.put(plugin4.getId(), pair4);

        ClassLoader cl = pair3.getInnerClassLoader();
        assertNotNull(cl);
        assertNotNull(cl.getResource("resource1.properties"));
        assertNotNull(cl.getResource("resource2.properties"));

        cl = pair3.getOuterClassLoader();
        assertNotNull(cl);
        assertNull(cl.getResource("resource1.properties"));
        assertNotNull(cl.getResource("resource2.properties"));

        cl = pair4.getInnerClassLoader();
        assertNotNull(cl);
        Properties prop = new Properties();
        InputStream in = cl.getResourceAsStream("resource1.properties");
        prop.load(in);
        in.close();
        assertEquals("plugin4-resource1", prop.getProperty("name"));
        prop = new Properties();
        in = cl.getResourceAsStream("resource2.properties");
        prop.load(in);
        in.close();
        assertEquals("plugin4-resource2", prop.getProperty("name"));
        List<URL> list = new ArrayList<URL>();
        for (Enumeration<URL> enm = cl.getResources("resource2.properties"); enm
            .hasMoreElements();) {
            list.add(enm.nextElement());
        }
        assertEquals(2, list.size());
    }


    public void testNormalizeEextensionElements1()
        throws Exception
    {
        List<ExtensionElement> extensionElementList = new ArrayList<ExtensionElement>();
        extensionElementList
            .add(new MockExtensionElement("1", null, null, null));
        extensionElementList
            .add(new MockExtensionElement("2", null, null, null));

        Collection<ExtensionElement> actual = target_
            .normalizeEextensionElements(extensionElementList);
        assertEquals(2, actual.size());
        Iterator<ExtensionElement> iterator = actual.iterator();
        assertEquals("1", iterator.next().getId());
        assertEquals("2", iterator.next().getId());
    }


    public void testNormalizeEextensionElements2()
        throws Exception
    {
        List<ExtensionElement> extensionElementList = new ArrayList<ExtensionElement>();
        extensionElementList
            .add(new MockExtensionElement("1", null, null, null));
        extensionElementList.add(new MockExtensionElement("1", "remove", null,
            null));
        extensionElementList
            .add(new MockExtensionElement("2", null, null, null));

        Collection<ExtensionElement> actual = target_
            .normalizeEextensionElements(extensionElementList);
        Iterator<ExtensionElement> iterator = actual.iterator();
        assertEquals("2", iterator.next().getId());
    }


    public void testNormalizeEextensionElements3()
        throws Exception
    {
        List<ExtensionElement> extensionElementList = new ArrayList<ExtensionElement>();
        extensionElementList.add(new MockExtensionElement("2", "replace", null,
            null));
        extensionElementList
            .add(new MockExtensionElement("1", null, null, null));
        extensionElementList
            .add(new MockExtensionElement("2", null, null, null));

        Collection<ExtensionElement> actual = target_
            .normalizeEextensionElements(extensionElementList);
        Iterator<ExtensionElement> iterator = actual.iterator();
        ExtensionElement extensionElement = iterator.next();
        assertEquals("2", extensionElement.getId());
        assertEquals("replace", extensionElement.getAction());
        assertEquals("1", iterator.next().getId());
    }


    public void testNormalizeEextensionElements4()
        throws Exception
    {
        List<ExtensionElement> extensionElementList = new ArrayList<ExtensionElement>();
        extensionElementList
            .add(new MockExtensionElement("1", null, null, null));
        extensionElementList
            .add(new MockExtensionElement("2", null, null, null));
        extensionElementList.add(new MockExtensionElement("1", "replace", null,
            null));

        Collection<ExtensionElement> actual = target_
            .normalizeEextensionElements(extensionElementList);
        Iterator<ExtensionElement> iterator = actual.iterator();
        ExtensionElement extensionElement = iterator.next();
        assertEquals("1", extensionElement.getId());
        assertEquals("replace", extensionElement.getAction());
        assertEquals("2", iterator.next().getId());
    }


    public void testNormalizeEextensionElements5()
        throws Exception
    {
        List<ExtensionElement> extensionElementList = new ArrayList<ExtensionElement>();
        extensionElementList
            .add(new MockExtensionElement("1", null, null, null));
        extensionElementList.add(new MockExtensionElement("2", null, "dummy.1",
            null));

        Collection<ExtensionElement> actual = target_
            .normalizeEextensionElements(extensionElementList);
        Iterator<ExtensionElement> iterator = actual.iterator();
        assertEquals("2", iterator.next().getId());
        assertEquals("1", iterator.next().getId());
    }


    public void testNormalizeEextensionElements6()
        throws Exception
    {
        List<ExtensionElement> extensionElementList = new ArrayList<ExtensionElement>();
        extensionElementList.add(new MockExtensionElement("1", null, null,
            "dummy.2"));
        extensionElementList
            .add(new MockExtensionElement("2", null, null, null));

        Collection<ExtensionElement> actual = target_
            .normalizeEextensionElements(extensionElementList);
        Iterator<ExtensionElement> iterator = actual.iterator();
        assertEquals("2", iterator.next().getId());
        assertEquals("1", iterator.next().getId());
    }


    private ExtensionElement[] gatherExtensionElements(
        Class<? extends ExtensionElement> elementClass,
        final Class<?> component1Class, final Class<?> component2Class,
        Element[] elements)
    {
        final Plugin<?> plugin = new MockPlugin<Object>() {
            public String getId()
            {
                return "plugin.id";
            }


            public ClassLoader getInnerClassLoader()
            {
                return getClass().getClassLoader();
            }


            public ComponentContainer getComponentContainer()
            {
                return new MockComponentContainer() {
                    @Override
                    public Class<?> getComponentClass(Object key)
                    {
                        if ("component1".equals(key)) {
                            return component1Class;
                        } else if ("component2".equals(key)) {
                            return component2Class;
                        } else {
                            return null;
                        }
                    }


                    @Override
                    public Object getComponent(Object key)
                    {
                        if ("component1".equals(key)) {
                            try {
                                return component1Class.newInstance();
                            } catch (InstantiationException ex) {
                                throw new RuntimeException(ex);
                            } catch (IllegalAccessException ex) {
                                throw new RuntimeException(ex);
                            }
                        } else if ("component2".equals(key)) {
                            try {
                                return component2Class.newInstance();
                            } catch (InstantiationException ex) {
                                throw new RuntimeException(ex);
                            } catch (IllegalAccessException ex) {
                                throw new RuntimeException(ex);
                            }
                        } else {
                            throw new ComponentNotFoundRuntimeException();
                        }
                    }


                    @Override
                    public boolean hasComponent(Object key)
                    {
                        return (getComponentClass(key) != null);
                    }
                };
            }
        };

        Extension parent = new Extension() {
            public String getPoint()
            {
                return "plugin.id.point";
            }


            public PluginDescriptor getParent()
            {
                return new PluginDescriptorImpl() {
                    public String getId()
                    {
                        return "plugin.id";
                    }


                    public Plugin<?> getPlugin()
                    {
                        return plugin;
                    };
                };
            };
        };
        ElementPair[] elementPairs = new ElementPair[elements.length];
        for (int i = 0; i < elementPairs.length; i++) {
            elementPairs[i] = new ElementPair(elements[i], parent);
        }

        ExtensionPoint extensionPoint = new ExtensionPoint("point",
            elementClass);
        extensionPoint.setParent(parent.getParent());
        return target_.gatherExtensionElements(extensionPoint, Arrays
            .asList(elementPairs));
    }


    public void testGatherExtensionElements()
        throws Exception
    {
        preparePluginResources(new String[] { "plugin3" });
        Plugin<?>[] plugins = loadPlugins(new String[] { "plugin3" });

        Plugin<?> plugin3 = plugins[0];
        Map<String, PluginDescriptor> descriptorMap = new HashMap<String, PluginDescriptor>();
        descriptorMap.put("plugin3", plugin3.getDescriptor());
        Kvasir kvasir = new MockKvasir() {
            public ClassLoader getCommonClassLoader()
            {
                return getClass().getClassLoader();
            }
        };
        target_.setKvasir(kvasir);
        target_.sortPluginDescriptorsByDependencies(descriptorMap);
        Map<String, ClassLoaderPair> pairMap = new HashMap<String, ClassLoaderPair>();
        ClassLoaderPair pair3 = target_.createClassLoader(plugin3
            .getDescriptor(), pairMap);
        pairMap.put(plugin3.getId(), pair3);

        Map<PluginLocalKey, ExtensionPoint> extensionPointMap = new HashMap<PluginLocalKey, ExtensionPoint>();
        target_.gatherExtensionPoints(plugin3.getDescriptor()
            .getExtensionPoints(), extensionPointMap);
        Map<String, ExtensionElement[]> eesMap = target_
            .gatherExtensionElements(new PluginDescriptor[] { plugin3
                .getDescriptor() }, extensionPointMap);

        ExtensionPoint extensionPoint = target_.getExtensionPoint(
            extensionPointMap, target_.generateKey("plugin3.points"));
        assertSame(Element1.class, extensionPoint.getElementClass());
        ExtensionElement[] ees = eesMap.get("plugin3.points");
        assertEquals(2, ees.length);
        assertTrue(
            "構築されたExtensionElementの配列の型はextensionPointのelementClassの配列になっていること",
            ees instanceof Element1[]);
        assertTrue(ees[0] instanceof Element1);
        assertTrue(ees[1] instanceof Element1);

        assertNotNull(((Element1)ees[0]).getChild());

        Property[] props = ((Element1)ees[0]).getProperties();
        assertEquals(2, props.length);
        assertEquals("name1", props[0].getName());
        assertEquals("value1", props[0].getValue());
        assertEquals("name2", props[1].getName());
        assertEquals("value2", props[1].getValue());
    }


    public void testGetExtensionComponents()
        throws Exception
    {
        PluginAlfrImpl target = new PluginAlfrImpl() {
            @Override
            ExtensionPoint getExtensionPoint(
                Map<PluginLocalKey, ExtensionPoint> extensionPointMap,
                PluginLocalKey key)
            {
                return null;
            }
        };
        Hoe[] actual = target.getExtensionComponents(Hoe.class, "", true);
        assertNotNull("該当するエントリが見つからない場合でもnullを返さないこと", actual);
        assertTrue("該当するエントリが見つからない場合でも返される配列の型は引数と同じであること",
            actual instanceof Hoe[]);
    }


    public void testGetExtensionComponents2()
        throws Exception
    {
        PluginAlfrImpl target = new PluginAlfrImpl() {
            @Override
            Object[] getExtensionComponents(ExtensionPoint extensionPoint,
                Class<?> componentClass, boolean ascending)
            {
                if (extensionPoint.getId().endsWith(".hoes")) {
                    return new Hoe[] { new Hoe(extensionPoint.getFullId()) };
                } else {
                    return new Fuga[] { new Fuga(extensionPoint.getFullId()) };
                }
            }


            @Override
            ExtensionPoint getExtensionPoint(
                Map<PluginLocalKey, ExtensionPoint> extensionPointMap,
                PluginLocalKey key)
            {
                ExtensionPoint ep1 = new ExtensionPoint() {
                    @Override
                    public String getFullId()
                    {
                        return "plugin.id.fugas";
                    }
                };
                ep1.setId("fugas");
                ExtensionPoint ep2 = new ExtensionPoint() {
                    @Override
                    public String getFullId()
                    {
                        return "plugin2.id.fugas";
                    }
                };
                ep2.setId("fugas");

                if (key.getLocalKey() == Fuga.class) {
                    if (key.getPluginId().equals("plugin.id")) {
                        return ep1;
                    } else if (key.getPluginId().equals("plugin2.id")) {
                        return ep2;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        };

        Fuga[] actual = target.getExtensionComponents(Fuga.class, "plugin2.id",
            true);
        assertEquals("指定したプラグインに属するものだけが返されること", 1, actual.length);
        assertEquals("plugin2.id.fugas", actual[0].getId());

    }


    public void testBindClassToExtensionPoint()
        throws Exception
    {
        PluginAlfrImpl target = new PluginAlfrImpl();
        Map<PluginLocalKey, ExtensionPoint> extensionPointMap = new HashMap<PluginLocalKey, ExtensionPoint>();
        ExtensionPoint ep1 = new ExtensionPoint("hoes", NullElement.class) {
            @Override
            public String getFullId()
            {
                return "plugin.id.hoes";
            }
        };
        ExtensionPoint ep2 = new ExtensionPoint("hehe.hoes", NullElement.class) {
            @Override
            public String getFullId()
            {
                return "plugin.id.hehe.hoes";
            }
        };
        target.bindClassToExtensionPoint(extensionPointMap, Hoe.class,
            "plugin.id", ep1);
        target.bindClassToExtensionPoint(extensionPointMap, Hoe.class,
            "plugin.id", ep2);

        try {
            target.getExtensionPoint(extensionPointMap, target.generateKey(
                "plugin.id", Hoe.class));
            fail("同じプラグイン内に同じコンポーネントクラスに関するExtensionPointエントリが重複している場合はエラーになること");
        } catch (IllegalStateException expected) {
        }
    }


    @org.seasar.kvasir.base.descriptor.annotation.Component(isa = Hoe.class)
    public static class NullElement extends AbstractElement
    {
    }


    public void testGetExtensionElements()
        throws Exception
    {
        PluginAlfrImpl target = new PluginAlfrImpl() {
            @Override
            ExtensionPoint getExtensionPoint(
                Map<PluginLocalKey, ExtensionPoint> extensionPointMap,
                PluginLocalKey key)
            {
                if (key.getPluginId().equals("com.example.plugin")
                    && key.getLocalKey() == Interface1.class) {
                    return new ExtensionPoint() {
                        @Override
                        public String getFullId()
                        {
                            return "com.example.plugin.interface1s";
                        }


                        @Override
                        public Class<? extends ExtensionElement> getElementClass()
                        {
                            return Element1.class;
                        }
                    };
                } else {
                    return null;
                }
            }
        };
        Map<String, ExtensionElement[]> map = new HashMap<String, ExtensionElement[]>();
        Element1 element1 = new Element1();
        Element1 element2 = new Element1();
        map.put("com.example.plugin.interface1s", new Element1[] { element1,
            element2 });

        Interface1[] elements = null;
        try {
            elements = target.getExtensionElements(Interface1.class,
                "com.example.plugin", map, false);
        } catch (ClassCastException ex) {
            fail("引数で指定した型で正しく受け取れること");
        }
        assertNotNull(elements);
        assertEquals(2, elements.length);
        assertSame("正しく並び替えられていること", element2, elements[0]);
        assertSame(element1, elements[1]);

        assertEquals(
            "指定したExtensionElementに対応するExtensionPointを持たないプラグインIDを指定した場合は空の配列を返すこと",
            0, target.getExtensionElements(Interface1.class,
                "com.example.plugin2", map, true).length);
    }


    public void testReadPluginSettings1_同一IDでバージョンの異なる2つのプラグインが存在する場合()
        throws Exception
    {
        Resource pluginsDir = metaData_.getTestClassesDirectory()
            .getChildResource("org/seasar/kvasir/system/plugin/resolve/test1");
        Map<String, PluginDescriptor> descriptorMap = new TreeMap<String, PluginDescriptor>();
        Map<String, Map<Version, PluginDescriptor>> descriptorsMap = new HashMap<String, Map<Version, PluginDescriptor>>();
        Map<Identifier, List<Patch>> patchesMap = new HashMap<Identifier, List<Patch>>();

        target_.readPluginSettings(pluginsDir, null, descriptorMap,
            descriptorsMap, patchesMap);

        assertEquals(2, descriptorMap.size());
        PluginDescriptor pd = descriptorMap.get("hoge1");
        assertNotNull(pd);
        assertEquals("1.0.1", pd.getVersionString());
        Map<Version, PluginDescriptor> map = descriptorsMap.get("hoge1");
        assertNotNull(map);
        assertEquals(2, map.size());
        assertNotNull(map.get(new Version("1.0.0")));
        assertNotNull(map.get(new Version("1.0.1")));
        PluginDescriptor[] pds = map.values().toArray(new PluginDescriptor[0]);
        assertEquals(new Version("1.0.1"), pds[0].getVersion());
        assertEquals(new Version("1.0.0"), pds[1].getVersion());
        assertEquals(0, patchesMap.size());
    }


    public void testReadPluginSettings2_パッチが存在する場合()
        throws Exception
    {
        Resource pluginsDir = metaData_.getTestClassesDirectory()
            .getChildResource("org/seasar/kvasir/system/plugin/resolve/test2");
        Map<String, PluginDescriptor> descriptorMap = new TreeMap<String, PluginDescriptor>();
        Map<String, Map<Version, PluginDescriptor>> descriptorsMap = new HashMap<String, Map<Version, PluginDescriptor>>();
        Map<Identifier, List<Patch>> patchesMap = new HashMap<Identifier, List<Patch>>();

        target_.readPluginSettings(pluginsDir, null, descriptorMap,
            descriptorsMap, patchesMap);

        assertEquals(2, patchesMap.size());
        List<Patch> list = patchesMap.get(new Identifier("hoge1-1.0.0"));
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("hoge1.patch1-1.0.1", new Identifier(list.get(0).getId(),
            list.get(0).getVersion()).getString());
        assertEquals("hoge1.patch2-1.0.0", new Identifier(list.get(1).getId(),
            list.get(1).getVersion()).getString());
    }


    public void testResolveInheritance1_同一IDでバージョンの異なる2つのプラグインが存在する場合()
        throws Exception
    {
        Resource pluginsDir = metaData_.getTestClassesDirectory()
            .getChildResource("org/seasar/kvasir/system/plugin/resolve/test1");
        Map<String, PluginDescriptor> descriptorMap = new TreeMap<String, PluginDescriptor>();
        Map<String, Map<Version, PluginDescriptor>> descriptorsMap = new HashMap<String, Map<Version, PluginDescriptor>>();
        Map<Identifier, List<Patch>> patchesMap = new HashMap<Identifier, List<Patch>>();
        target_.readPluginSettings(pluginsDir, null, descriptorMap,
            descriptorsMap, patchesMap);

        PluginDescriptor descriptor = target_.resolveInheritance(descriptorMap
            .get("hoge1"), patchesMap, descriptorsMap, new HashSet<String>());

        Resource actual = descriptor.getResolvedResourcesDirectory();
        assertTrue(actual.getChildResource("plugin.xml").exists());
        PluginDescriptor pd = target_.readXMLResourceAsBean(actual
            .getChildResource("plugin.xml"), PluginDescriptor.class);
        assertEquals(new Identifier("hoge1-1.0.1"), pd.getIdentifier());
        assertSame(descriptor.getRawResourcesDirectory(), descriptor
            .getResolvedResourcesDirectory());
    }


    public void testResolveInheritance2_パッチが存在する場合()
        throws Exception
    {
        Resource pluginsDir = metaData_.getTestClassesDirectory()
            .getChildResource("org/seasar/kvasir/system/plugin/resolve/test2");
        Map<String, PluginDescriptor> descriptorMap = new TreeMap<String, PluginDescriptor>();
        Map<String, Map<Version, PluginDescriptor>> descriptorsMap = new HashMap<String, Map<Version, PluginDescriptor>>();
        Map<Identifier, List<Patch>> patchesMap = new HashMap<Identifier, List<Patch>>();
        target_.readPluginSettings(pluginsDir, null, descriptorMap,
            descriptorsMap, patchesMap);

        PluginDescriptor descriptor = target_.resolveInheritance(descriptorMap
            .get("hoge1"), patchesMap, descriptorsMap, new HashSet<String>());
        assertTrue(descriptor.isResolved());

        Resource actual = descriptor.getResolvedResourcesDirectory();
        Library[] libraries = descriptor.getRuntime().getLibraries();
        assertEquals(4, libraries.length);
        assertEquals("hoge1-1.0.0", libraries[0].getName());
        assertEquals("classes", libraries[1].getName());
        assertEquals("patch1-1.0.1", libraries[2].getName());
        assertEquals("patch2-1.0.0", libraries[3].getName());
        I18NProperties prop = new I18NProperties(actual, "plugin",
            ".xproperties");
        assertEquals("patch2-1.0.0", prop.getProperty("name"));
        assertEquals("true", prop.getProperty("patch1-1.0.1"));
        assertEquals("true", prop.getProperty("patch2-1.0.0"));
        assertEquals("patch2-1.0.0", ResourceUtils.readString(
            actual.getChildResource("resource.txt"), "").trim());
        Component[] cs = target_.readXMLResourceAsBean(
            actual.getChildResource("conf/plugin.dicon"), Components.class)
            .getComponents();
        assertEquals(3, cs.length);
        assertEquals("patch2-1.0.0", cs[0].getClassName());
        assertEquals("name", cs[0].getName());
        assertEquals("patch1-1.0.1", cs[1].getClassName());
        assertEquals("patch2-1.0.0", cs[2].getClassName());
        ClassLoader cl = new URLClassLoader(new URL[] { ClassUtils
            .getURLForURLClassLoader(actual.getChildResource("classes")
                .getURL()) });
        assertNotNull(cl.getResource("resource.txt"));
        assertNotNull(cl.getResource("resource-patch2-1.0.0.txt"));
    }


    public void testResolveInheritance3_古いバージョンのプラグインを親にするプラグインが存在する場合()
        throws Exception
    {
        Resource pluginsDir = metaData_.getTestClassesDirectory()
            .getChildResource("org/seasar/kvasir/system/plugin/resolve/test3");
        Map<String, PluginDescriptor> descriptorMap = new TreeMap<String, PluginDescriptor>();
        Map<String, Map<Version, PluginDescriptor>> descriptorsMap = new HashMap<String, Map<Version, PluginDescriptor>>();
        Map<Identifier, List<Patch>> patchesMap = new HashMap<Identifier, List<Patch>>();
        target_.readPluginSettings(pluginsDir, null, descriptorMap,
            descriptorsMap, patchesMap);

        PluginDescriptor descriptor = descriptorMap.get("hoge1");
        assertEquals("1.0.1", descriptor.getVersionString());

        descriptor = target_.resolveInheritance(descriptorMap.get("fuga1"),
            patchesMap, descriptorsMap, new HashSet<String>());
        assertTrue(descriptor.isResolved());
        assertEquals("1.0.0", descriptor.getVersionString());

        Resource actual = descriptor.getResolvedResourcesDirectory();
        Library[] libraries = descriptor.getRuntime().getLibraries();
        assertEquals(3, libraries.length);
        assertEquals("hoge1-1.0.0", libraries[0].getName());
        assertEquals("classes", libraries[1].getName());
        assertEquals("fuga1-1.0.0", libraries[2].getName());
        I18NProperties prop = new I18NProperties(actual, "plugin",
            ".xproperties");
        assertEquals("fuga1-1.0.0", prop.getProperty("name"));
        assertEquals("true", prop.getProperty("fuga1-1.0.0"));
        assertEquals("fuga1-1.0.0", ResourceUtils.readString(
            actual.getChildResource("resource.txt"), "").trim());
        Component[] cs = target_.readXMLResourceAsBean(
            actual.getChildResource("conf/plugin.dicon"), Components.class)
            .getComponents();
        assertEquals(3, cs.length);
        assertEquals("fuga1-1.0.0", cs[0].getClassName());
        assertEquals("name", cs[0].getName());
        assertEquals("hoge1-1.0.0", cs[1].getClassName());
        assertEquals("fuga1-1.0.0", cs[2].getClassName());
        ClassLoader cl = new URLClassLoader(new URL[] { ClassUtils
            .getURLForURLClassLoader(actual.getChildResource("classes")
                .getURL()) });
        assertNotNull(cl.getResource("resource.txt"));
        assertNotNull(cl.getResource("resource-fuga1-1.0.0.txt"));
    }


    public void testResolveInheritance4_継承型プラグインにパッチが当たっている場合()
        throws Exception
    {
        Resource pluginsDir = metaData_.getTestClassesDirectory()
            .getChildResource("org/seasar/kvasir/system/plugin/resolve/test4");
        Map<String, PluginDescriptor> descriptorMap = new TreeMap<String, PluginDescriptor>();
        Map<String, Map<Version, PluginDescriptor>> descriptorsMap = new HashMap<String, Map<Version, PluginDescriptor>>();
        Map<Identifier, List<Patch>> patchesMap = new HashMap<Identifier, List<Patch>>();
        target_.readPluginSettings(pluginsDir, null, descriptorMap,
            descriptorsMap, patchesMap);

        PluginDescriptor descriptor = target_.resolveInheritance(descriptorMap
            .get("fuga1"), patchesMap, descriptorsMap, new HashSet<String>());
        assertTrue(descriptor.isResolved());
        assertEquals("1.0.0", descriptor.getVersionString());
        assertNull("baseタグは除去されること", descriptor.getBase());

        Resource actual = descriptor.getResolvedResourcesDirectory();
        Library[] libraries = descriptor.getRuntime().getLibraries();
        assertEquals(5, libraries.length);
        assertEquals("hoge1-1.0.0", libraries[0].getName());
        assertEquals("classes", libraries[1].getName());
        assertEquals("patch1-1.0.0", libraries[2].getName());
        assertEquals("fuga1-1.0.0", libraries[3].getName());
        assertEquals("patch2-1.0.0", libraries[4].getName());
        I18NProperties prop = new I18NProperties(actual, "plugin",
            ".xproperties");
        assertEquals("patch2-1.0.0", prop.getProperty("name"));
        assertEquals("true", prop.getProperty("fuga1-1.0.0"));
        assertEquals("true", prop.getProperty("patch1-1.0.0"));
        assertEquals("true", prop.getProperty("patch2-1.0.0"));
        assertEquals("patch2-1.0.0", ResourceUtils.readString(
            actual.getChildResource("resource.txt"), "").trim());
        Component[] cs = target_.readXMLResourceAsBean(
            actual.getChildResource("conf/plugin.dicon"), Components.class)
            .getComponents();
        assertEquals(5, cs.length);
        assertEquals("patch2-1.0.0", cs[0].getClassName());
        assertEquals("name", cs[0].getName());
        assertEquals("hoge1-1.0.0", cs[1].getClassName());
        assertEquals("patch1-1.0.0", cs[2].getClassName());
        assertEquals("fuga1-1.0.0", cs[3].getClassName());
        assertEquals("patch2-1.0.0", cs[4].getClassName());
        ClassLoader cl = new URLClassLoader(new URL[] { ClassUtils
            .getURLForURLClassLoader(actual.getChildResource("classes")
                .getURL()) });
        assertNotNull(cl.getResource("resource.txt"));
        assertNotNull(cl.getResource("resource-fuga1-1.0.0.txt"));
        assertNotNull(cl.getResource("resource-patch1-1.0.0.txt"));
        assertNotNull(cl.getResource("resource-patch2-1.0.0.txt"));
    }


    public void testBindClassToExtensionPoint_インタフェースを指定した場合にNullPointerExceptionが出ないこと()
        throws Exception
    {
        try {
            target_.bindClassToExtensionPoint(
                new HashMap<PluginLocalKey, ExtensionPoint>(),
                Interface1.class, "plugin.id", new ExtensionPoint());
        } catch (NullPointerException ex) {
            fail();
        }
    }


    public void testGetAllPluginDescriptors()
        throws Exception
    {
        final Resource pluginsDir = metaData_.getTestClassesDirectory()
            .getChildResource("org/seasar/kvasir/system/plugin/plugins1");
        target_.setPluginsDirectory(pluginsDir);
        target_.start();

        PluginDescriptor[] actual = target_.getAllPluginDescriptors();
        Arrays.sort(actual, new Comparator<PluginDescriptor>() {
            public int compare(PluginDescriptor o1, PluginDescriptor o2)
            {
                return o1.getId().compareTo(o2.getId());
            }
        });
        assertEquals(2, actual.length);
        assertEquals("plugin1", actual[0].getId());
        assertEquals("disabledなプラグインについても取得できること", "plugin2", actual[1].getId());
    }


    public void testIsPluginLibrary()
        throws Exception
    {
        assertTrue(target_.isPluginLibrary(new URL(
            "jar:file:/C:/build/test-home/plugins/target/plugin.id.jar!/"),
            "plugin.id"));
    }


    public void test_diconを持たないプラグインがdiconを持つプラグインに依存している場合に依存プラグインのdiconからcomponentContainerを構築してしまわないこと()
        throws Exception
    {
        final Resource pluginsDir = metaData_.getTestClassesDirectory()
            .getChildResource("org/seasar/kvasir/system/plugin/plugins2");
        target_.setPluginsDirectory(pluginsDir);
        target_.start();

        assertFalse(((PluggableS2ComponentContainer)target_
            .getPlugin("plugin1").getComponentContainer()).getS2Container()
            .getPath().equals(
                ((PluggableS2ComponentContainer)target_.getPlugin("plugin2")
                    .getComponentContainer()).getS2Container().getPath()));
    }


    public void test_ClassからComponentContainer設定を読み込めること()
        throws Exception
    {
        final Resource pluginsDir = metaData_.getTestClassesDirectory()
            .getChildResource("org/seasar/kvasir/system/plugin/plugins3");
        Resource preparer = pluginsDir
            .getChildResource("plugin1-1.0.0/conf/PluginPreparer.class");
        if (!preparer.exists()) {
            ResourceUtils.copy(new JavaResource("PluginPreparer.class",
                getClass().getClassLoader()), preparer);
        }
        target_.setPluginsDirectory(pluginsDir);
        target_.start();

        final Plugin<?> plugin = target_.getPlugin("plugin1");
        assertTrue(plugin instanceof Plugin1);
    }


    /**
     * デフォルト設定がありユーザ設定が無い場合は、
     * デフォルト設定からPluginAlfrSettingsを構築する。
     */
    public void testGetDefaultSettings()
        throws Exception
    {
        // ## Arrange ##
        final Resource systemDirectory = kvasir_.getSystemDirectory();
        final Resource child = systemDirectory
            .getChildResource(PluginAlfrImpl.SETTINGS_FILE_NAME);

        final PluginAlfrSettings pluginAlfrSettings = new PluginAlfrSettings();
        {
            final RemoteRepository remoteRepository = new RemoteRepository();
            remoteRepository.setRepositoryId("aaaId");
            remoteRepository.setUrl("file://aaa");
            remoteRepository.setEnabled(true);
            pluginAlfrSettings.addRemoteRepository(remoteRepository);
        }
        {
            final RemoteRepository remoteRepository = new RemoteRepository();
            remoteRepository.setRepositoryId("bbbId");
            remoteRepository.setUrl("file://bbb");
            remoteRepository.setEnabled(false);
            pluginAlfrSettings.addRemoteRepository(remoteRepository);
        }
        final OutputStream os = child.getOutputStream();
        XOMUtils.toXML(pluginAlfrSettings, os);
        IOUtils.closeQuietly(os);

        // ## Act ##
        target_.constructSettings();
        final PluginAlfrSettings settings = target_.getSettings();

        // ## Assert ##
        assertNotNull(settings);
        final RemoteRepository[] remoteRepositories = settings
            .getRemoteRepositories();
        assertEquals(2, remoteRepositories.length);
        assertEquals("aaaId", remoteRepositories[0].getRepositoryId());
        assertEquals("file://aaa", remoteRepositories[0].getUrl());
        assertEquals(true, remoteRepositories[0].isEnabled());
        assertEquals("bbbId", remoteRepositories[1].getRepositoryId());
        assertEquals("file://bbb", remoteRepositories[1].getUrl());
        assertEquals(false, remoteRepositories[1].isEnabled());
    }


    /**
     * ユーザ設定がある場合は、デフォルト設定ではなくユーザ設定から
     * PluginAlfrSettingsを構築する。
     */
    public void testGetCustomizedSettings()
        throws Exception
    {
        // ## Arrange ##
        {
            final Resource systemDirectory = kvasir_.getSystemDirectory();
            final Resource file = systemDirectory
                .getChildResource(PluginAlfrImpl.SETTINGS_FILE_NAME);

            final PluginAlfrSettings pluginAlfrSettings = new PluginAlfrSettings();
            final RemoteRepository remoteRepository = new RemoteRepository();
            remoteRepository.setRepositoryId("aaaId");
            remoteRepository.setUrl("file://aaa");
            pluginAlfrSettings.addRemoteRepository(remoteRepository);
            final OutputStream os = file.getOutputStream();
            XOMUtils.toXML(pluginAlfrSettings, os);
            IOUtils.closeQuietly(os);
        }
        {
            final Resource configurationDirectory = kvasir_
                .getConfigurationDirectory();
            final Resource systemDirectory = configurationDirectory
                .getChildResource(Globals.SYSTEM_DIR);
            systemDirectory.mkdirs();
            final Resource file = systemDirectory
                .getChildResource(PluginAlfrImpl.SETTINGS_FILE_NAME);
            final PluginAlfrSettings pluginAlfrSettings = new PluginAlfrSettings();
            final RemoteRepository remoteRepository = new RemoteRepository();
            remoteRepository.setRepositoryId("bbbb");
            remoteRepository.setUrl("file://bbbb");
            pluginAlfrSettings.addRemoteRepository(remoteRepository);
            final OutputStream os = file.getOutputStream();
            XOMUtils.toXML(pluginAlfrSettings, os);
            IOUtils.closeQuietly(os);
        }

        // ## Act ##
        target_.constructSettings();
        final PluginAlfrSettings settings = target_.getSettings();

        // ## Assert ##
        assertNotNull(settings);
        final RemoteRepository[] remoteRepositories = settings
            .getRemoteRepositories();
        assertEquals(1, remoteRepositories.length);
        assertEquals("bbbb", remoteRepositories[0].getRepositoryId());
        assertEquals("file://bbbb", remoteRepositories[0].getUrl());
    }


    public void testSettingsForUpdate()
        throws Exception
    {
        // ## Arrange ##
        {
            final Resource systemDirectory = kvasir_.getSystemDirectory();
            final Resource file = systemDirectory
                .getChildResource(PluginAlfrImpl.SETTINGS_FILE_NAME);

            final PluginAlfrSettings pluginAlfrSettings = new PluginAlfrSettings();
            final RemoteRepository remoteRepository = new RemoteRepository();
            remoteRepository.setRepositoryId("aaaId");
            remoteRepository.setUrl("file://aaa");
            pluginAlfrSettings.addRemoteRepository(remoteRepository);
            final OutputStream os = file.getOutputStream();
            XOMUtils.toXML(pluginAlfrSettings, os);
            IOUtils.closeQuietly(os);
        }

        // ## Act ##
        target_.constructSettings();
        final PluginAlfrSettings settings1 = target_.getSettings();
        final PluginAlfrSettings settings2 = target_.getSettings();
        final PluginAlfrSettings settings3 = target_.getSettingsForUpdate();
        final PluginAlfrSettings settings4 = target_.getSettingsForUpdate();

        // ## Assert ##
        assertSame("同じインスタンスを返すこと", settings1, settings2);
        assertNotSame("異なるインスタンスを返すこと", settings1, settings3);
        assertNotSame("異なるインスタンスを返すこと", settings3, settings4);
    }


    public void testStoreSettings()
        throws Exception
    {
        // ## Arrange ##
        {
            final Resource systemDirectory = kvasir_.getSystemDirectory();
            final Resource file = systemDirectory
                .getChildResource(PluginAlfrImpl.SETTINGS_FILE_NAME);

            final PluginAlfrSettings pluginAlfrSettings = new PluginAlfrSettings();
            final RemoteRepository remoteRepository = new RemoteRepository();
            remoteRepository.setRepositoryId("aaaa");
            remoteRepository.setUrl("file://aaaa");
            pluginAlfrSettings.addRemoteRepository(remoteRepository);
            final OutputStream os = file.getOutputStream();
            XOMUtils.toXML(pluginAlfrSettings, os);
            IOUtils.closeQuietly(os);
        }

        // ## Act ##
        {
            target_.constructSettings();
            final PluginAlfrSettings settings = target_.getSettingsForUpdate();
            assertEquals(1, settings.getRemoteRepositories().length);
            assertEquals("aaaa", settings.getRemoteRepositories()[0]
                .getRepositoryId());

            final RemoteRepository repo = new RemoteRepository();
            repo.setRepositoryId("bbb");
            repo.setUrl("file:///bbb");
            settings.addRemoteRepository(repo);
            target_.storeSettings(settings);
        }

        // ## Assert ##
        {
            final PluginAlfrSettings settings = target_.getSettingsForUpdate();
            final RemoteRepository[] remoteRepositories = settings
                .getRemoteRepositories();
            assertEquals(2, remoteRepositories.length);
            assertEquals("aaaa", remoteRepositories[0].getRepositoryId());
            assertEquals("bbb", remoteRepositories[1].getRepositoryId());
        }
    }


    /*
     * aaa.bbb-1.0.0はstaging側で置き換えられる
     * aaa.ccc-2.0.0はstaging側が追加される
     * aaa.dddは1.0.0が消されることなく2.0.0が追加される
     */
    public void testStagePlugins()
        throws Exception
    {
        // ## Arrange ##
        final Resource pluginsDirectory = kvasir_.getPluginsDirectory();
        final Resource stagingDirectory = kvasir_.getStagingDirectory()
            .getChildResource(Globals.PLUGINS_DIR);
        stagingDirectory.mkdir();
        // plugins
        {
            final Resource p = pluginsDirectory
                .getChildResource("aaa.bbb-1.0.0");
            p.mkdir();
            final Resource f = p.getChildResource("aaa-bbb-zzz.txt");
            final OutputStream os = f.getOutputStream();
            os.write("bbbbb-zzzzz".getBytes());
            os.close();

            p.getChildResource("sub1").mkdir();
            p.getChildResource("sub2").mkdir();
        }
        {
            final Resource p = pluginsDirectory
                .getChildResource("aaa.ddd-1.0.0");
            p.mkdir();
            final Resource f = p.getChildResource("aaa-ddd-zzz.txt");
            final OutputStream os = f.getOutputStream();
            os.write("ddddd-zzzzz".getBytes());
            os.close();
        }
        // staging
        {
            final Resource p = stagingDirectory
                .getChildResource("aaa.bbb-1.0.0");
            p.mkdir();
            final Resource f = p.getChildResource("aaa-bbb.txt");
            final OutputStream os = f.getOutputStream();
            os.write("bbbbb".getBytes());
            os.close();
        }
        {
            final Resource p = stagingDirectory
                .getChildResource("aaa.ccc-2.0.0");
            p.mkdir();
            final Resource f = p.getChildResource("aaa-ccc.txt");
            final OutputStream os = f.getOutputStream();
            os.write("ccccc".getBytes());
            os.close();
        }
        {
            final Resource p = stagingDirectory
                .getChildResource("aaa.ddd-2.0.0");
            p.mkdir();
            final Resource f = p.getChildResource("aaa-ddd.txt");
            final OutputStream os = f.getOutputStream();
            os.write("ddddd".getBytes());
            os.close();
        }

        assertEquals(2, pluginsDirectory.listResources().length);
        assertEquals(3, stagingDirectory.listResources().length);

        // ## Act ##
        target_.stagePlugins();

        // ## Assert ##
        assertEquals(4, pluginsDirectory.listResources().length);
        assertEquals("[staging/plugins] directory should be empty", 0,
            stagingDirectory.listResources().length);

        // aaa.bbbはstaging側で置き換えられていること
        final Resource[] resources = pluginsDirectory.getChildResource(
            "aaa.bbb-1.0.0").listResources();
        assertEquals(1, resources.length);
        assertEquals("aaa-bbb.txt", resources[0].getName());
    }


    /*
     * デフォルトのファイルを作成するため
     */
    public void _test1()
        throws Exception
    {
        final PluginAlfrSettings pluginAlfrSettings = new PluginAlfrSettings();
        {
            final RemoteRepository remoteRepository = new RemoteRepository();
            remoteRepository.setRepositoryId("maven.seasar.org");
            remoteRepository.setUrl("http://maven.seasar.org/maven2/");
            pluginAlfrSettings.addRemoteRepository(remoteRepository);
        }

        final StringWriter stringWriter = new StringWriter();
        XOMUtils.toXML(pluginAlfrSettings, stringWriter);
        System.out.println(stringWriter);
    }

}
