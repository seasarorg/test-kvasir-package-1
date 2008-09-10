package org.seasar.kvasir.system;

import static org.seasar.kvasir.base.Globals.BUILD_NUMBER;
import static org.seasar.kvasir.base.Globals.CONFIGURATION_DIR;
import static org.seasar.kvasir.base.Globals.PROP_BUILD_NUMBER;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_ENABLECLUSTERING;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_HOME_DIR;
import static org.seasar.kvasir.base.Globals.RUNTIMEWORK_DIR;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.seasar.cms.pluggable.Configuration;
import org.seasar.kvasir.base.Globals;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.KvasirLifecycleListener;
import org.seasar.kvasir.base.KvasirProperties;
import org.seasar.kvasir.base.KvasirUtils;
import org.seasar.kvasir.base.SessionListener;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.annotation.ForTest;
import org.seasar.kvasir.base.classloader.CachedClassLoader;
import org.seasar.kvasir.base.classloader.CompositeClassLoader;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.container.ComponentContainerFactory;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.base.util.StructuredPropertyUtils;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.impl.FileResource;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 *
 * @author YOKOTA Takehiko
 * @author manhole
 */
public class KvasirImpl
    implements Kvasir
{

    private static final long STOP_WAIT_MILLIS = 500;

    /**
     * Configurationディレクトリの中の、構造化プロパティを格納するためのディレクトリの相対パスです。
     */
    private static final String PATH_STRUCTURED_PROPERTY = "structures";

    /**
     * 構造化プロパティを保存する際のリソース名のサフィックスです。
     */
    private static final String SUFFIX_STRUCTURED_PROPERTY = ".xml";

    private static final String SP = System.getProperty("line.separator");

    private KvasirProperties prop_;

    private Map<String, Object> attr_;

    private ComponentContainer container_;

    private ComponentContainer rootContainer_;

    private ClassLoader commonClassLoader_;

    private PluginAlfr alfr_;

    private Resource homeDirectory_;

    private Resource configurationDirectory_;

    private Resource runtimeWorkDirectory_;

    private ClassLoader classLoader_;

    private ThreadLocal<ClassLoader> contextClassLoader_ = new ThreadLocal<ClassLoader>();

    private ThreadLocal<ClassLoader> currentClassLoader_ = new ThreadLocal<ClassLoader>();

    private ThreadLocal<Long> beginTime_ = new ThreadLocal<Long>();

    private SessionListener[] sessionListeners_;

    private KvasirLifecycleListener[] kvasirLifecycleListeners_;

    private volatile int sessions_ = 0;

    private Boolean started_ = Boolean.FALSE;

    private boolean underDevelopment_;

    private boolean standalone_;

    private long buildNumber_;

    private final KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    public synchronized boolean start(KvasirProperties prop,
        ComponentContainer container, ClassLoader commonClassLoader,
        ClassLoader systemClassLoader)
    {
        if (!Boolean.FALSE.equals(started_)) {
            return true;
        }
        started_ = null;

        StringBuilder sb = new StringBuilder();
        sb.append("Kvasir/Sora is starting");
        if (underDevelopment_) {
            sb.append(" UNDER DEVELOPMENT MODE");
        }
        log_.info(sb.toString());

        InputStream in = getClass().getClassLoader().getResourceAsStream(
            BUILD_NUMBER);
        if (in != null) {
            Properties buildNubmerProp = new Properties();
            try {
                buildNubmerProp.load(in);
            } catch (IOException ex) {
                throw new IORuntimeException("Can't load '" + BUILD_NUMBER
                    + "' resource: "
                    + getClass().getClassLoader().getResource(BUILD_NUMBER), ex);
            } finally {
                IOUtils.closeQuietly(in);
            }
            buildNumber_ = PropertyUtils.valueOf(buildNubmerProp
                .getProperty(PROP_BUILD_NUMBER), 0L);
        } else {
            buildNumber_ = 0;
        }

        boolean startedSuccessfully = true;
        String detailMessage = null;
        String[] failedPluginsIdsToStart;
        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            // 初期化中にcommons-loggingを使うクラスを読み込んだり動かしたりすると
            // 環境によっては（ex. Eclipse上のテスト環境）common/libのではない
            // commons-logging.jarを見つけてしまってcommons-loggingに「複数使うな」と
            // 怒られてしまうことがあるため、こうしている。
            Thread.currentThread().setContextClassLoader(commonClassLoader);

            prop_ = prop;
            standalone_ = !PropertyUtils.valueOf(prop
                .getProperty(PROP_SYSTEM_ENABLECLUSTERING), false);

            attr_ = new HashMap<String, Object>();
            container_ = container;
            rootContainer_ = ComponentContainerFactory.getInstance()
                .getRootContainer();
            commonClassLoader_ = commonClassLoader;

            homeDirectory_ = new FileResource(new File(
                getProperty(PROP_SYSTEM_HOME_DIR)));
            configurationDirectory_ = homeDirectory_
                .getChildResource(CONFIGURATION_DIR);
            configurationDirectory_.mkdirs();
            runtimeWorkDirectory_ = homeDirectory_
                .getChildResource(RUNTIMEWORK_DIR);
            runtimeWorkDirectory_.mkdirs();

            // PluginAlfrは先に起動しておく。
            if (!alfr_.start()) {
                started_ = Boolean.FALSE;
                return false;
            }
            failedPluginsIdsToStart = alfr_.getFailedPluginIdsToStart();
            if (failedPluginsIdsToStart.length > 0) {
                startedSuccessfully = false;
                detailMessage = "Failed to start plugin";
            }

            // 全てのコンポーネントの登録を完了させる。
            ComponentContainerFactory.getInstance().freeze();

            // ClassLoaderを生成する。
            generateClassLoader();

            // SessionListenerを登録する。
            sessionListeners_ = alfr_.getExtensionComponents(
                SessionListener.class, Globals.PLUGINID, true);
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }

        started_ = Boolean.TRUE;

        // hotdeploy対象のクラスが使われる可能性があるのでbeginSession()している。
        beginSession();
        try {
            // 各プラグインに初期化処理の終了を通知する。
            if (!alfr_.notifyKvasirStarted()) {
                startedSuccessfully = false;
                detailMessage = "Failed notification to Plugins that Kvasir/Sora has been started";
            }

            kvasirLifecycleListeners_ = alfr_.getExtensionComponents(
                KvasirLifecycleListener.class, Globals.PLUGINID, true);
            for (int i = 0; i < kvasirLifecycleListeners_.length; i++) {
                try {
                    kvasirLifecycleListeners_[i].notifyKvasirStarted();
                } catch (Throwable t) {
                    log_.error(
                        "KvasirLifecycleListener has thrown exception when started: lifecycleListener="
                            + kvasirLifecycleListeners_[i], t);
                    startedSuccessfully = false;
                    detailMessage = "Failed notification to lifecycle listeners that Kvasir/Sora has been started";
                }
            }

            if (startedSuccessfully) {
                log_.info("Kvasir/Sora has been successfully started.");
            } else {
                sb = new StringBuilder();
                sb.append(
                    "Kvasir/Sora has started"
                        + " with some problems unfortunately.").append(SP);
                if (detailMessage != null) {
                    sb.append("Detail: " + detailMessage);
                }
                if (failedPluginsIdsToStart.length > 0) {
                    sb.append("Plugins failed to start are:").append(SP);
                    for (int i = 0; i < failedPluginsIdsToStart.length; i++) {
                        sb.append("\t").append(failedPluginsIdsToStart[i])
                            .append(SP);
                    }
                }
                log_.info(sb.toString());
            }

            return true;
        } finally {
            endSession();
        }
    }


    public synchronized void stop(int timeoutSeconds)
    {
        if (!Boolean.TRUE.equals(started_)) {
            return;
        }
        started_ = null;

        // セッションが全て終了するのを待つ。
        long time = timeoutSeconds * 1000L;
        try {
            while ((sessions_ > 0) && (time > 0)) {
                Thread.sleep(STOP_WAIT_MILLIS);
                time -= STOP_WAIT_MILLIS;
            }
        } catch (InterruptedException ex) {
            ;
        }

        ClassLoader old = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader_);

            for (int i = 0; i < kvasirLifecycleListeners_.length; i++) {
                try {
                    kvasirLifecycleListeners_[i].notifyKvasirStopping();
                } catch (Throwable t) {
                    log_
                        .error(
                            "KvasirLifecycleListener has thrown exception while stopping: lifecycleListener="
                                + kvasirLifecycleListeners_[i], t);
                }
            }

            ComponentContainerFactory.destroy();

            KvasirUtils.stop(alfr_);
            alfr_ = null;

            //            container_.destroy();
            container_ = null;

            KvasirUtils.stop(sessionListeners_);
            sessionListeners_ = null;
            kvasirLifecycleListeners_ = null;

            prop_ = null;
            attr_ = null;
            commonClassLoader_ = null;
            underDevelopment_ = false;

            homeDirectory_ = null;
            classLoader_ = null;

            started_ = Boolean.FALSE;
        } finally {
            Thread.currentThread().setContextClassLoader(old);
        }
    }


    public boolean isStarted()
    {
        return Boolean.TRUE.equals(started_);
    }


    public synchronized boolean beginSession()
    {
        if (!isStarted()) {
            return false;
        }

        if (log_.isDebugEnabled()) {
            log_.debug("KVASIR SESSION BEGIN: " + Thread.currentThread());
        }
        beginTime_.set(System.currentTimeMillis());

        // コンテキストクラスローダの設定をする。
        Thread thread = Thread.currentThread();
        contextClassLoader_.set(thread.getContextClassLoader());
        thread.setContextClassLoader(classLoader_);

        ComponentContainerFactory componentContainerFactory = ComponentContainerFactory
            .getInstance();
        try {
            componentContainerFactory.beginSession();
        } catch (Throwable t) {
            log_.error("ComponentContainerFactory#beginSession() failed", t);
        }

        // HotDeployに対応するため、再度コンテキストクラスローダの設定をする。
        ClassLoader classLoader = componentContainerFactory
            .getCurrentClassLoader();
        currentClassLoader_.set(classLoader);
        thread.setContextClassLoader(classLoader);

        Plugin<?> plugin = alfr_.getPluginUnderDevelopment();
        if (plugin != null) {
            try {
                plugin.refresh();
            } catch (Throwable t) {
                log_.error("Plugin#refresh() failed: plugin-id="
                    + plugin.getId(), t);
            }
        }

        for (int i = 0; i < sessionListeners_.length; i++) {
            try {
                sessionListeners_[i].notifyBeginSession();
            } catch (Throwable t) {
                log_.error("notification of beginning session failed", t);
            }
        }

        sessions_++;
        return true;
    }


    public long endSession()
    {
        for (int i = 0; i < sessionListeners_.length; i++) {
            try {
                sessionListeners_[i].notifyEndSession();
            } catch (Throwable t) {
                log_.error("notification of finishing session failed", t);
            }
        }

        try {
            ComponentContainerFactory.getInstance().endSession();
        } catch (Throwable t) {
            log_.error("ComponentContainerFactory#endSession() failed", t);
        }

        Thread.currentThread().setContextClassLoader(contextClassLoader_.get());
        contextClassLoader_.set(null);
        currentClassLoader_.set(null);

        sessions_--;

        long beginTime = beginTime_.get();
        beginTime_.set(null);

        if (log_.isDebugEnabled()) {
            log_.debug("KVASIR SESSION END: " + Thread.currentThread());
        }

        return (System.currentTimeMillis() - beginTime);
    }


    public boolean isInSession()
    {
        return (contextClassLoader_.get() != null);
    }


    public ClassLoader getCurrentClassLoader()
    {
        ClassLoader cl = currentClassLoader_.get();
        if (cl != null) {
            return cl;
        } else {
            return classLoader_;
        }
    }


    public Version getVersion()
    {
        return Globals.BASE_VERSION;
    }


    public long getBuildNumber()
    {
        return buildNumber_;
    }


    public Resource getHomeDirectory()
    {
        return homeDirectory_;
    }


    @ForTest
    void setHomeDirectory(final Resource homeDirectory)
    {
        homeDirectory_ = homeDirectory;
    }


    public Resource getConfigurationDirectory()
    {
        return configurationDirectory_;
    }


    public Resource getRuntimeWorkDirectory()
    {
        return runtimeWorkDirectory_;
    }


    public ClassLoader getCommonClassLoader()
    {
        return commonClassLoader_;
    }


    public ClassLoader getClassLoader()
    {
        return classLoader_;
    }


    public boolean isUnderDevelopment()
    {
        return underDevelopment_;
    }


    public boolean isStandalone()
    {
        return standalone_;
    }


    public String getProperty(String name)
    {
        return prop_.getProperty(name);
    }


    public String getProperty(String name, String defaultValue)
    {
        String value = getProperty(name);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }


    public int getProperty(String name, int defaultValue)
    {
        String value = getProperty(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }


    public void setProperty(String name, String value)
    {
        prop_.setProperty(name, value);
    }


    public void removeProperty(String name)
    {
        prop_.removeProperty(name);
    }


    public Enumeration<String> propertyNames()
    {
        return prop_.propertyNames();
    }


    public void storeProperties()
    {
        prop_.store();
    }


    public ComponentContainer getComponentContainer()
    {
        return container_;
    }


    public ComponentContainer getRootComponentContainer()
    {
        return rootContainer_;
    }


    public PluginAlfr getPluginAlfr()
    {
        return alfr_;
    }


    public synchronized Object getAttribute(String name)
    {
        return attr_.get(name);
    }


    public synchronized void setAttribute(String name, Object value)
    {
        attr_.put(name.intern(), value);
    }


    public synchronized void removeAttribute(String name)
    {
        attr_.remove(name);
    }


    public synchronized Enumeration<String> getAttributeNames()
    {
        return Collections.enumeration(attr_.keySet());
    }


    /*
     public ClassLoader getOriginalContextClassLoader()
     {
     ClassLoader cl = (ClassLoader)contextClassLoader_.get();
     if (cl != null) {
     return cl;
     } else {
     return Thread.currentThread().getContextClassLoader();
     }
     }
     */

    @SuppressWarnings("unchecked")
    public synchronized <T> T getStructuredProperty(String name,
        Class<T> structureClass)
    {
        return StructuredPropertyUtils.getStructuredProperty(
            getStructuredPropertyResource(name), structureClass);
    }


    Resource getStructuredPropertyResource(String name)
    {
        return getConfigurationDirectory().getChildResource(
            PATH_STRUCTURED_PROPERTY + "/" + name + SUFFIX_STRUCTURED_PROPERTY);
    }


    public synchronized void setStructuredProperty(String name, Object structure)
    {
        StructuredPropertyUtils.setStructuredProperty(
            getStructuredPropertyResource(name), structure);
    }


    private void generateClassLoader()
    {
        ClassLoader classLoader = null;

        Plugin<?>[] plugins = alfr_.getPlugins();
        List<ClassLoader> clList = new ArrayList<ClassLoader>(plugins.length);
        for (int i = 0; i < plugins.length; i++) {
            ClassLoader cl = plugins[i].getOuterClassLoader();
            if (cl == null) {
                continue;
            }
            clList.add(cl);
        }
        if (clList.size() == 0) {
            classLoader = commonClassLoader_;
        } else {
            classLoader = new CachedClassLoader(new CompositeClassLoader(clList
                .toArray(new ClassLoader[0]), commonClassLoader_));
        }

        classLoader_ = classLoader;
    }


    public void setConfiguration(Configuration configuration)
    {
        underDevelopment_ = configuration.isUnderDevelopment();
    }


    public void setPluginAlfr(PluginAlfr alfr)
    {
        alfr_ = alfr;
    }


    public Resource getSystemDirectory()
    {
        return getHomeDirectory().getChildResource(Globals.SYSTEM_DIR);
    }


    public Resource getPluginsDirectory()
    {
        return getHomeDirectory().getChildResource(Globals.PLUGINS_DIR);
    }


    public Resource getStagingDirectory()
    {
        return getHomeDirectory().getChildResource(Globals.STAGING_DIR);
    }

}
