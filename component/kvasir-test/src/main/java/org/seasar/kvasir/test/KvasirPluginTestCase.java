package org.seasar.kvasir.test;

import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_DEVELOPEDPLUGINID;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_DEVELOPEDPLUGIN_ADDITIONALJARS;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_DEVELOPEDPLUGIN_CLASSES_DIR;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_DEVELOPEDPLUGIN_HOME_DIR;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_DEVELOPEDPLUGIN_PROJECT_DIR;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_HOME_DIR;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.classloader.OverriddenURLClassLoader;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.io.FileUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceUtils;
import org.seasar.kvasir.util.io.impl.JavaResource;

/**
 * Kvasir/Soraを起動した状態でプラグインのテストを行なうための抽象クラスです。
 * <p>Kvasir/Soraを起動した状態でプラグインのテストを行ないたい場合は、
 * まずこのクラスのサブクラスを「*IT」という名前で作成して以下のstatic methodを追加して下さい。
 * （作成したクラスの名前をPluginITとします）：
 * </p>
 * <pre>
 *     public static Test suite()
 *         throws Exception
 *     {
 *         return createTestSuite(PluginIT.class);
 *     }
 * </pre>
 * <p>また、{@link #getTargetPluginId()}メソッドを実装しておいて下さい。
 * このメソッドはテストするプラグインのIDを返すようにして下さい。</p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class KvasirPluginTestCase<P extends Plugin<?>> extends
        TestCase {
    private static final String JAR_SUFFIX = ".jar!/";

    private static final String KVASIR_XPROPERTIES = "kvasir.xproperties";

    /**
     * クラスパスに通したいJarの一覧です。
     * <p>直接Jarを指定できないので、Jarが持っているべきクラスの名前を1つずつ書いています。</p>
     * <p>kvasir-pluginのpom.xmlに書いてあるmaven-kvasir-pluginの
     * configurationの、baseLibrariesタグとadditionalBaseLibrariesタグ
     * に書いてある内容と同じにします。</p>
     *
     * FIXME pom.xmlをMaven Embedderで読み込んで、当該タグの情報を読んで利用するようにしよう。
     */
    private static final String[] BASE_LIBRARIES = new String[] {
            "org.apache.xerces.jaxp.SAXParserFactoryImpl", // 「SAXParserFactoryImpl見つからない問題」に対処するため。
            "org.seasar.kvasir.base.Asgard", // kvasir-base
            "org.seasar.kvasir.util.ClassUtils", // kvasir-util
            "net.skirnir.xom.XOMapper", // xom
            "net.skirnir.xom.annotation.Bean", // xom-tiger
    };

    /**
     * クラスパスに通したいテスト用のJarの一覧です。
     * <p>直接Jarを指定できないので、Jarが持っているべきクラスの名前を1つずつ書いています。</p>
     * <p>kvasir-pluginのpom.xmlに書いてあるmaven-kvasir-pluginの
     * configurationの、testLibrariesタグとadditionalTestLibrariesタグ
     * に書いてある内容と同じにします。</p>
     *
     * FIXME pom.xmlをMaven Embedderで読み込んで、当該タグの情報を読んで利用するようにしよう。
     */
    private static final String[] TEST_LIBRARIES = new String[] {
            "org.seasar.kvasir.test.KvasirPluginTestCase", // kvasir-test
            "javax.servlet.Servlet", // servlet-api
            "org.seasar.kvasir.webapp.Globals", // kvasir-webapp
    };

    private static String pluginId_;

    private static ProjectMetaData metaData_;

    private static Class<? extends TestCase> actualTestClass_;

    private Kvasir kvasir_;

    private P plugin_;

    private ComponentContainer container_;

    /*
     * abstract methods
     */

    abstract protected String getTargetPluginId();

    /*
     * static methods
     */

    public static Test createTestSuite(
            final Class<? extends KvasirPluginTestCase<?>> clazz)
            throws Exception {
        metaData_ = new ProjectMetaData(clazz);

        ClassLoader cl = createClassLoader(BASE_LIBRARIES, TEST_LIBRARIES,
                new JUnitFilteredClassLoader(KvasirPluginTestCase.class
                        .getClassLoader(), KvasirPluginTestCase.class
                        .getClassLoader().getParent()));

        String pluginId = clazz.newInstance().getTargetPluginId();
        boolean shouldPrepareTestHome = (!metaData_.isRunningFromMaven2() && !metaData_
                .isKvasirEclipsePluginProject());

        return (Test) cl.loadClass(KvasirPluginTestCase.class.getName())
                .getMethod(
                        "createTestSuite0",
                        new Class[] { Class.class, Class.class, String.class,
                                Boolean.TYPE }).invoke(
                        null,
                        new Object[] { KvasirPluginTestCase.class, clazz,
                                pluginId,
                                Boolean.valueOf(shouldPrepareTestHome) });
    }

    static ClassLoader createClassLoader(String[] baseJarLandmarks,
            String[] testJarLandmarks, ClassLoader parent) {
        List<URL> urlList = new ArrayList<URL>();
        for (int i = 0; i < baseJarLandmarks.length; i++) {
            URL url = findBelongingJarOrDirectoryURL(baseJarLandmarks[i]);
            if (url != null) {
                urlList.add(url);
            }
        }
        for (int i = 0; i < testJarLandmarks.length; i++) {
            URL url = findBelongingJarOrDirectoryURL(testJarLandmarks[i]);
            if (url != null) {
                urlList.add(url);
            }
        }
        return new URLClassLoader(urlList.toArray(new URL[0]), parent);
    }

    static URL findBelongingJarOrDirectoryURL(String className) {
        ClassLoader cl = KvasirPluginTestCase.class.getClassLoader();
        String resourceName = className.replace('.', '/').concat(".class");
        URL resource = cl.getResource(resourceName);
        if (resource != null) {
            String externalForm = resource.toExternalForm();
            int idx = externalForm.lastIndexOf(JAR_SUFFIX);
            if (idx >= 0) {
                try {
                    return new URL(externalForm.substring(0, idx
                            + JAR_SUFFIX.length()));
                } catch (MalformedURLException ex) {
                    System.err.println("[ERROR] Can't create URL from: "
                            + externalForm);
                    return null;
                }
            } else if (externalForm.endsWith(resourceName)) {
                try {
                    return new URL(externalForm.substring(0, externalForm
                            .length()
                            - resourceName.length()));
                } catch (MalformedURLException ex) {
                    throw new RuntimeException(
                            "[ERROR] Can't create URL from: " + externalForm,
                            ex);

                }
            } else {
                throw new RuntimeException("[ERROR] Unsupported Jar URL: "
                        + externalForm);
            }
        }
        return null;
    }

    public static Test createTestSuite0(
            final Class<? extends KvasirPluginTestCase<?>> originalKvasirPluginTestClass,
            final Class<? extends TestCase> clazz, final String pluginId,
            final boolean shouldPrepareTestHome)

    throws Exception {
        pluginId_ = pluginId;
        metaData_ = new ProjectMetaData(clazz);

        final TestSuite suite = new TestSuite(clazz);
        TestSetup wrapper = new TestSetup(suite) {
            @Override
            public void setUp() throws Exception {
                try {
                    onceSetUp(originalKvasirPluginTestClass, clazz,
                            shouldPrepareTestHome);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    suite.addTest(error(ex.getMessage()));
                }
            }

            @Override
            public void tearDown() throws Exception {
                onceTearDown();
            }
        };
        return wrapper;
    }

    protected static Test error(final String message) {
        return new TestCase("error") {
            protected void runTest() {
                fail(message);
            }
        };
    }

    protected static void onceSetUp(
            Class<? extends KvasirPluginTestCase<?>> originalKvasirPluginTestClass,
            Class<? extends TestCase> clazz, boolean shouldPrepareTestHome)
            throws Exception {
        if (shouldPrepareTestHome) {
            prepareTestHome();
        }

        prepareS2ContainerDicon();

        PropertyHandler prop = new MapProperties();
        prop.setProperty(PROP_SYSTEM_HOME_DIR, FileUtils
                .toAbstractPath(getTestHomeDirectory().toFile()
                        .getCanonicalPath()));
        prop.setProperty(PROP_SYSTEM_DEVELOPEDPLUGINID, pluginId_);
        prop.setProperty(PROP_SYSTEM_DEVELOPEDPLUGIN_PROJECT_DIR, metaData_
                .getProjectDirectory().toFile().getCanonicalPath());

        Resource outerLibrariesPropResource = metaData_.getClassesDirectory()
                .getParentResource().getChildResource(
                        "outerLibraries.properties");
        if (!outerLibrariesPropResource.exists()) {
            // 古い+PLUSTで構築したプロジェクトではここを通る。互換性のためこうしている。
            System.out
                    .println("[WARN] outerLibraries.properties does not exist: "
                            + outerLibrariesPropResource);
        } else {
            prop.setProperty(PROP_SYSTEM_DEVELOPEDPLUGIN_HOME_DIR, metaData_
                    .getProjectDirectory().getChildResource("src/main/plugin")
                    .toFile().getCanonicalPath());

            MapProperties outerLibrariesProp = new MapProperties();
            outerLibrariesProp
                    .load(outerLibrariesPropResource.getInputStream());
            String outerLibraries = outerLibrariesProp
                    .getProperty("outerLibraries");
            if (outerLibraries != null) {
                prop.setProperty(PROP_SYSTEM_DEVELOPEDPLUGIN_ADDITIONALJARS,
                        outerLibraries);
            }
        }

        Resource classesDirectory = metaData_.getClassesDirectory();
        if (classesDirectory != null) {
            prop.setProperty(PROP_SYSTEM_DEVELOPEDPLUGIN_CLASSES_DIR,
                    classesDirectory.toFile().getCanonicalPath());
        }
        clazz.getMethod("preEstablish", new Class[0]).invoke(
                clazz.newInstance(), new Object[0]);
        Asgard.establish(KVASIR_XPROPERTIES, null, prop, Asgard.class
                .getClassLoader());

        Plugin<?> plugin = Asgard.getKvasir().getPluginAlfr().getPlugin(
                pluginId_);
        if (plugin == null) {
            String message = "[ERROR] Plugin instance does not exist: pluginId="
                    + pluginId_;
            System.err.println(message);
            throw new NullPointerException(message);
        }
        ClassLoader cl = new OverriddenURLClassLoader(
                new URL[] { getTestClassesDirectory().getURL() }, plugin
                        .getInnerClassLoader());
        if ("true".equals(System.getProperty("debug"))) {
            System.out.println("[DEBUG] ClassLoader=" + cl);
        }

        originalKvasirPluginTestClass.getMethod("setActualTestClass",
                new Class[] { Class.class }).invoke(null,
                new Object[] { cl.loadClass(clazz.getName()) });
    }

    static void prepareS2ContainerDicon() {
        Resource s2containerResource = metaData_.getTestHomeDirectory()
                .getChildResource("common/classes/s2container.dicon");
        if (!s2containerResource.exists()) {
            ResourceUtils.copy(new JavaResource("s2container.dicon",
                    KvasirPluginTestCase.class.getClassLoader()),
                    s2containerResource);
        }
    }

    public static void setActualTestClass(Class<? extends TestCase> testClass) {
        actualTestClass_ = testClass;
    }

    static void prepareTestHome() {
        Resource actualDirectory = metaData_.getTestHomeDirectory();
        Resource mavenTestHomeSourceDirectory = metaData_
                .getMavenTestHomeSourceDirectory();
        Resource mavenTestHomeDirectory = metaData_.getMavenTestHomeDirectory();

        if (!actualDirectory.equals(mavenTestHomeDirectory)) {
            if (mavenTestHomeSourceDirectory.exists()
                    && !mavenTestHomeDirectory.exists()) {
                // まだMaven2が実行されていない。
                throw new IllegalStateException(
                        "Run 'mvn pre-integration-test' first");
            }

            // Maven2のテストホームをコピーしておく。
            ResourceUtils.delete(actualDirectory, true);
            ResourceUtils.copy(mavenTestHomeDirectory, actualDirectory);
        }
    }

    public void preEstablish() {
    }

    protected static void onceTearDown() throws Exception {
        Asgard.ragnarok(5);
    }

    @Override
    public void runBare() throws Throwable {
        TestCase testCase = actualTestClass_.newInstance();
        testCase.setName(getName());
        actualTestClass_.getMethod("runBare0", new Class[0]).invoke(testCase,
                new Object[0]);
    }

    @SuppressWarnings("unchecked")
    public void runBare0() throws Throwable {
        kvasir_ = Asgard.getKvasir();
        plugin_ = (P) kvasir_.getPluginAlfr().getPlugin(getTargetPluginId());
        container_ = plugin_.getComponentContainer();

        kvasir_.beginSession();
        try {
            super.runBare();
        } finally {
            kvasir_.endSession();
        }
    }

    public void testStart() throws Throwable {
        KvasirLogFactory factory = KvasirLogFactory.getFactory();
        assertEquals(0, factory.getFatalCount() + factory.getErrorCount()
                + factory.getWarnCount());
        assertFalse(
                "Plugin ("
                        + getTargetPluginId()
                        + ") is disabled. See log file: "
                        + getTestHomeDirectory().getChildResource(
                                "rtwork/all-log.txt"), plugin_.isDisabled());
    }

    protected Kvasir getKvasir() {
        return kvasir_;
    }

    protected P getPlugin() {
        return plugin_;
    }

    protected ComponentContainer getComponentContainer() {
        return container_;
    }

    protected Object getComponent(Object key) {
        return container_.getComponent(key);
    }

    protected <T> T getComponent(Class<? extends T> key) {
        return container_.getComponent(key);
    }

    //    public final void testDelegate()
    //        throws Exception
    //    {
    //        assertFalse("onceSetUp not failed", onceSetUpFailed_);
    //
    //        KvasirPluginTestCase launcher = class_.newInstance();
    //        String pluginId = launcher.getTargetPluginId();
    //        assertNotNull("Specify pluginId", pluginId);
    //
    //        Plugin plugin = Asgard.getKvasir().getPluginAlfr().getPlugin(pluginId);
    //        assertNotNull("Can't find plugin instance: " + pluginId, plugin);
    //
    //        String testPackage = class_.getPackage().getName();
    //        Resource testDir = getTestClassesDirectory().getChildResource(
    //            testPackage.replace('.', '/'));
    //        String[] names = testDir.list();
    //        List<String> testList = new ArrayList<String>(names.length);
    //        for (int i = 0; i < names.length; i++) {
    //            String name = names[i];
    //            if (!name.endsWith("Actual.class")) {
    //                continue;
    //            }
    //            testList
    //                .add(testPackage
    //                    + "."
    //                    + name
    //                        .substring(0, name.length() - 6/*= ".class".length() */));
    //        }
    //        String[] testClassNames = testList.toArray(new String[0]);
    //
    //        ClassLoader cl = new OverriddenURLClassLoader(
    //            new URL[] { getTestClassesDirectory().getURL() }, plugin
    //                .getInnerClassLoader());
    //        if ("true".equals(System.getProperty("debug"))) {
    //            System.out.println("[DEBUG] ClassLoader=" + cl);
    //        }
    //        List<Dependant> list = new ArrayList<Dependant>(testClassNames.length);
    //        for (int i = 0; i < testClassNames.length; i++) {
    //            Class<?> testClass = Class.forName(testClassNames[i], true, cl);
    //            if ("true".equals(System.getProperty("debug"))) {
    //                System.out.println("[DEBUG] testClass=" + testClass
    //                    + ", loaded from: " + testClass.getClassLoader());
    //                if (new URLClassLoader(new URL[] { getTestClassesDirectory()
    //                    .getURL() }, null).getResource(testClassNames[i].replace(
    //                    '.', '/')
    //                    + ".class") != null) {
    //                    if (testClass.getClassLoader() != cl) {
    //                        System.out.println("[DEBUG] Can' t load testClass ("
    //                            + testClassNames[i] + ") from loader ("
    //                            + getTestClassesDirectory().getURL()
    //                            + ") though resource exists.");
    //                    }
    //                } else {
    //                    System.out.println("[DEBUG] Can' t load testClass ("
    //                        + testClassNames[i] + ") from loader: "
    //                        + getTestClassesDirectory().getURL());
    //                }
    //            }
    //            list.add(new TestDependant(testClass));
    //        }
    //        Dependencies deps = new Dependencies(list);
    //
    //        Asgard.getKvasir().beginSession();
    //        try {
    //            TestSuite actualTestSuite = new TestSuite();
    //            Dependency[] ds = deps.getDependencies();
    //            for (int i = 0; i < ds.length; i++) {
    //                Dependency d = ds[i];
    //                Class testClass = ((TestDependant)d.getSource()).getTestClass();
    //                if (d.isDisabled()) {
    //                    String fieldValue;
    //                    try {
    //                        Field field = testClass
    //                            .getField(TestDependant.FIELD_DEPENDENCIES);
    //                        fieldValue = (String)field.get(null);
    //                    } catch (Throwable t) {
    //                        fieldValue = "(UNRESOLVED)";
    //                    }
    //                    throw new RuntimeException(
    //                        "Can't resolve dependencies for " + testClass.getName()
    //                            + ": " + fieldValue);
    //                }
    //                System.out.println("Run Kvasir Test: " + testClass.getName());
    //                actualTestSuite.addTestSuite(testClass);
    //            }
    //
    //            final TestResult result = new TestResult();
    //            TestRunner runner = new TestRunner() {
    //                @Override
    //                public TestResult doRun(Test suite, boolean wait)
    //                {
    //                    long startTime = System.currentTimeMillis();
    //                    suite.run(result);
    //                    long endTime = System.currentTimeMillis();
    //                    long runTime = endTime - startTime;
    //                    System.out.println("Time: "
    //                        + NumberFormat.getInstance().format(
    //                            (double)runTime / 1000));
    //                    return result;
    //                }
    //            };
    //            runner.doRun(actualTestSuite);
    //            printResult(result);
    //            assertEquals(0, result.errorCount());
    //            assertEquals(0, result.failureCount());
    //        } finally {
    //            Asgard.getKvasir().endSession();
    //        }
    //    }

    /**
     * PluginのMavenプロジェクトのトップディレクトリを返します。
     *
     * @return プロジェクトのトップディレクトリを表すResourceオブジェクト。
     */
    protected static Resource getProjectDirectory() {
        return metaData_.getProjectDirectory();
    }

    protected static Resource getTestHomeSourceDirectory() {
        return metaData_.getTestHomeSourceDirectory();
    }

    protected static Resource getTestClassesDirectory() {
        return metaData_.getTestClassesDirectory();
    }

    /**
     * テストのためのホームディレクトリを返します。
     * <p>ホームディレクトリは、テストクラスの格納ディレクトリと同じ階層にある
     * <code>test-home</code>です。
     * </p>
     *
     * @return テストのためのホームディレクトリを表すResourceオブジェクト。
     */
    protected static Resource getTestHomeDirectory() {
        return metaData_.getTestHomeDirectory();
    }
}
