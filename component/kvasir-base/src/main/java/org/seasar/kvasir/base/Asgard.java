package org.seasar.kvasir.base;

import static org.seasar.kvasir.base.Globals.COMMON_CLASSES_DIR;
import static org.seasar.kvasir.base.Globals.COMMON_LIB_DIR;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_COMPONENTCONTAINERFACTORY_CLASSNAME;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_COMPONENTCONTAINER_CONFIG_RESOURCE;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_FILTER_RESOURCES;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_HOME_DIR;
import static org.seasar.kvasir.base.Globals.RESOURCE_PATTERN_FOR_METAINF_EXCLUSION;
import static org.seasar.kvasir.base.Globals.SYSTEM_CLASSES_DIR;
import static org.seasar.kvasir.base.Globals.SYSTEM_LIB_DIR;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.seasar.kvasir.base.classloader.FilteredClassLoader;
import org.seasar.kvasir.base.classloader.OverriddenURLClassLoader;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.container.ComponentContainerFactory;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.el.TextTemplateEvaluator;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.el.impl.PropertyHandlerVariableResolver;
import org.seasar.kvasir.util.el.impl.SimpleTextTemplateEvaluator;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceUtils;
import org.seasar.kvasir.util.io.impl.FileResource;
import org.seasar.kvasir.util.io.impl.JavaResource;


public class Asgard
{
    private static Boolean established_ = Boolean.FALSE;

    private static Kvasir kvasir_;


    public static synchronized Kvasir getKvasir()
    {
        if (Boolean.FALSE.equals(established_)) {
            throw new IllegalStateException("Not established yet");
        }
        return kvasir_;
    }


    /**
     * Kvasir/Soraの利用を開始します。
     *
     * @param defaultConfigResourcePath デフォルト設定リソースのパス。
     * 読み込み専用です。nullを指定することもできます。
     * @param customConfigResourcePath カスタム設定リソースのパス。
     * {@link #store()}を呼び出すとこの設定リソースに設定が保存されます。
     * カスタム設定リソース中で定義されている設定は、
     * デフォルト設定リソース中で定義されている設定よりも優先されます。
     * nullを指定することもできます。
     * @param tempConfig 一時な設定を保持しているPropertyHandlerオブジェクト。
     * nullを指定することもできます。
     * この設定はどの設定よりも優先されます。
     * @param classLoader 設定リソースを読み込むためのクラスローダ。
     * nullが指定された場合はコンテキストクラスローダを使用します。
     */
    public static synchronized boolean establish(
        String defaultConfigResourcePath, String customConfigResourcePath,
        PropertyHandler tempConfig, ClassLoader classLoader)
    {
        if (!Boolean.FALSE.equals(established_)) {
            return true;
        }
        established_ = null;

        // 設定ファイルを読み込む。

        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        // customConfigResourcePathが存在しない場合は生成しておく。
        if (customConfigResourcePath != null
            && defaultConfigResourcePath != null) {
            File defaultConfigFile = ClassUtils.getFileOfResource(classLoader
                .getResource(defaultConfigResourcePath));
            if (defaultConfigFile != null) {
                File customConfigFile = getFileOfResource(
                    customConfigResourcePath, defaultConfigResourcePath,
                    defaultConfigFile);
                if (!customConfigFile.exists()) {
                    ResourceUtils.writeString(
                        new FileResource(customConfigFile), "");
                }
            }
        }

        KvasirProperties prop = new KvasirProperties(defaultConfigResourcePath,
            customConfigResourcePath, tempConfig, classLoader);

        String homeDirectoryPath = prop.getProperty(PROP_SYSTEM_HOME_DIR);
        if (homeDirectoryPath == null) {
            throw new IllegalArgumentException("Please specify property: "
                + PROP_SYSTEM_HOME_DIR);
        }
        File homeDir = new File(homeDirectoryPath);

        // 設定を各種設定ファイルに埋め込む。

        File systemClassesDir = new File(homeDir, SYSTEM_CLASSES_DIR);
        String resources = prop.getProperty(PROP_SYSTEM_FILTER_RESOURCES);
        if (resources != null) {
            TextTemplateEvaluator evaluator = new SimpleTextTemplateEvaluator(
                true);
            VariableResolver resolver = new PropertyHandlerVariableResolver(
                prop);

            String[] resourcePaths = PropertyUtils.toLines(resources, ",");
            for (int i = 0; i < resourcePaths.length; i++) {
                String path = resourcePaths[i];
                Resource src = new JavaResource(path, classLoader);
                String destPath = path.startsWith("_") ? path.substring(1)
                    : path;
                Resource dest = new FileResource(new File(systemClassesDir,
                    destPath));
                if (!dest.exists()) {
                    KvasirUtils.filterResource(src, dest, "UTF-8", evaluator,
                        resolver);
                }
            }
        }

        // ルートクラスローダを生成する。

        ClassLoader rootCl = Asgard.class.getClassLoader();

        // コモンクラスローダを生成する。

        File commonClassesDir = new File(homeDir, COMMON_CLASSES_DIR);
        File commonLibDir = new File(homeDir, COMMON_LIB_DIR);
        ClassLoader commonCl = new FilteredClassLoader(
            new OverriddenURLClassLoader(getURLsForClassLoader(
                commonClassesDir, commonLibDir), rootCl),
            // xercesがクラスパス上にある時に「SAXParserFactoryImplが見つからない」
            // というExceptionがスローされることがある問題に対処するために、
            // META-INF/services/**は通すようにしている。
            new String[] { "**" }, new String[] { "META-INF/services/**",
                RESOURCE_PATTERN_FOR_METAINF_EXCLUSION, "**" });

        // システムクラスローダを生成する。

        File systemLibDir = new File(homeDir, SYSTEM_LIB_DIR);
        ClassLoader systemCl = new OverriddenURLClassLoader(
            getURLsForClassLoader(systemClassesDir, systemLibDir), commonCl);

        // ロガーを利用するための初期化を行なう。

        KvasirLogFactory.initializeFactory(systemCl);

        // コンポーネントコンテナを生成する。

        String ccfClassName = prop
            .getProperty(PROP_SYSTEM_COMPONENTCONTAINERFACTORY_CLASSNAME);
        if (ccfClassName == null) {
            throw new IllegalArgumentException("Please specify property: "
                + PROP_SYSTEM_COMPONENTCONTAINERFACTORY_CLASSNAME);
        }
        ComponentContainerFactory.initialize(ccfClassName, systemCl);
        ComponentContainerFactory ccFactory = ComponentContainerFactory
            .getInstance();

        String ccConfigResourcePath = prop
            .getProperty(PROP_SYSTEM_COMPONENTCONTAINER_CONFIG_RESOURCE);
        if (ccConfigResourcePath == null) {
            throw new IllegalArgumentException("Please specify property: "
                + PROP_SYSTEM_COMPONENTCONTAINER_CONFIG_RESOURCE);
        }
        ComponentContainer container = ccFactory.createContainer(
            ccConfigResourcePath, systemCl, new ComponentContainer[0]);

        // Kvasirインスタンスを設定する。

        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            // KvasirインスタンスにDIされるコンポーネントにcommons-loggingを使うクラスがあると、
            // 環境によっては（ex. Eclipse上のテスト環境）common/libのではない
            // commons-logging.jarを見つけてしまってcommons-loggingに「複数使うな」と
            // 怒られてしまうことがあるため、こうしている。
            Thread.currentThread().setContextClassLoader(systemCl);

            kvasir_ = (Kvasir)container.getComponent(Kvasir.class);
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }

        if (!kvasir_.start(prop, container, commonCl, systemCl)) {
            established_ = Boolean.FALSE;
            return false;
        }

        established_ = Boolean.TRUE;

        return true;
    }


    public static synchronized void ragnarok(int timeoutSeconds)
    {
        if (!Boolean.TRUE.equals(established_)) {
            return;
        }
        established_ = null;

        kvasir_.stop(timeoutSeconds);
        kvasir_ = null;

        KvasirLogFactory.destroyFactory();

        established_ = Boolean.FALSE;
    }


    public static ClassLoader getBaseClassLoader()
    {
        return Asgard.class.getClassLoader();
    }


    /*
     * package scope methods
     */

    static File getFileOfResource(String path, String landmarkPath,
        File landmarkFile)
    {
        File rootFile = landmarkFile.getParentFile();
        int idx;
        int pre = 0;
        while ((idx = landmarkPath.indexOf('/', pre)) >= 0) {
            rootFile = rootFile.getParentFile();
            pre = idx + 1;
        }
        File file = new File(rootFile, path.replace('/', File.separatorChar));

        return file;
    }


    /*
     * private socpe methods
     */

    private static URL[] getURLsForClassLoader(File classesDir, File libDir)
    {
        List<URL> list = new ArrayList<URL>();
        URL url = ClassUtils.getURLForURLClassLoader(classesDir);
        if (url != null) {
            list.add(url);
        }
        File[] files = libDir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].getName().endsWith(".jar")) {
                    list.add(ClassUtils.getURLForURLClassLoader(files[i]));
                }
            }
        }
        return list.toArray(new URL[0]);
    }
}
