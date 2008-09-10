package org.seasar.kvasir.base.plugin;

import java.util.Locale;

import org.seasar.kvasir.base.Identifier;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.Lifecycle;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.impl.PluginProperties;
import org.seasar.kvasir.base.util.ArrayUtils;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * この抽象クラスのサブクラスは<code>start()</code>
 * が呼び出された後はスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class AbstractPlugin<S>
    implements Plugin<S>
{
    private Kvasir kvasir_;

    private PluginAlfr pluginAlfr_;

    private Identifier directoryIdentifier_;

    private PluginDescriptor descriptor_;

    private ComponentContainer container_;

    private PluginProperties prop_;

    private ClassLoader innerClassLoader_;

    private ClassLoader outerClassLoader_;

    private boolean underDevelopment_;

    private Resource projectDirectory_;

    private Resource homeSourceDirectory_;

    protected S settings_;

    @SuppressWarnings("unchecked")
    private SettingsListener<S>[] settingsListeners_ = (SettingsListener<S>[])new SettingsListener<?>[0];

    private boolean started_ = false;

    private Lifecycle[] objectsInLifecycle_ = new Lifecycle[0];

    protected KvasirLog log_ = KvasirLogFactory.getLog(getClass());


    abstract protected boolean doStart();


    abstract protected void doStop();


    /*
     * constructors
     */

    protected AbstractPlugin()
    {
    }


    /*
     * Object
     */

    @Override
    public String toString()
    {
        if (descriptor_ == null) {
            return "(undefined)";
        } else {
            return getId();
        }
    }


    /*
     * Plugin
     */

    public Object getAdapter(Object key)
    {
        return null;
    }


    @SuppressWarnings("unchecked")
    public <T> T getAdapter(Class<T> key)
    {
        return (T)getAdapter((Object)key);
    }


    public String getId()
    {
        if (descriptor_ == null) {
            return null;
        } else {
            return descriptor_.getId();
        }
    }


    public Version getVersion()
    {
        return descriptor_.getVersion();
    }


    public boolean isDisabled()
    {
        return descriptor_.isDisabled();
    }


    public boolean isUnderDevelopment()
    {
        return underDevelopment_;
    }


    public void setUnderDevelopment(boolean underDevelopment)
    {
        underDevelopment_ = underDevelopment;
    }


    public Resource getProjectDirectory()
    {
        return projectDirectory_;
    }


    public Resource getHomeSourceDirectory()
    {
        return homeSourceDirectory_;
    }


    public void setProjectDirectory(Resource projectDirectory)
    {
        projectDirectory_ = projectDirectory;
        if (projectDirectory_ != null) {
            homeSourceDirectory_ = projectDirectory
                .getChildResource("src/main/plugin");
        } else {
            homeSourceDirectory_ = null;
        }
    }


    public PluginDescriptor getDescriptor()
    {
        return descriptor_;
    }


    public Resource getHomeDirectory()
    {
        return descriptor_.getRuntimeResourcesDirectory();
    }


    public Resource getConfigurationDirectory()
    {
        return descriptor_.getConfigurationDirectory();
    }


    public ComponentContainer getComponentContainer()
    {
        return container_;
    }


    public ClassLoader getInnerClassLoader()
    {
        return innerClassLoader_;
    }


    public ClassLoader getOuterClassLoader()
    {
        return outerClassLoader_;
    }


    public String getProperty(String name)
    {
        return prop_.getProperty(name);
    }


    public String getProperty(String name, String variant)
    {
        return prop_.getProperty(name, variant);
    }


    public String getProperty(String name, Locale locale)
    {
        return prop_.getProperty(name, locale);
    }


    public String resolveString(String str)
    {
        return resolveString(str, Locale.getDefault());
    }


    public String resolveString(String str, Locale locale)
    {
        return resolveString(str, locale, false);
    }


    public String resolveString(String str, Locale locale,
        boolean returnNullIfNotExists)
    {
        if (str == null) {
            return null;
        } else if (str.startsWith("%")) {
            String key = str.substring(1);
            String resolved = prop_.getProperty(key, locale);
            if (resolved != null) {
                return resolved;
            } else {
                if (returnNullIfNotExists) {
                    return null;
                } else {
                    return "!" + key + "!";
                }
            }
        } else {
            return str;
        }
    }


    public ExtensionElement[] getExtensionElements(String point)
    {
        return getExtensionElements(point, true);
    }


    public ExtensionElement[] getExtensionElements(String point,
        boolean ascending)
    {
        return pluginAlfr_.getExtensionElements(getId() + "." + point,
            ascending);
    }


    public <T> T[] getExtensionElements(Class<T> elementClass)
    {
        return getExtensionElements(elementClass, true);
    }


    public <T> T[] getExtensionElements(Class<T> elementClass, boolean ascending)
    {
        return pluginAlfr_.getExtensionElements(elementClass, getId(),
            ascending);
    }


    public Object[] getExtensionComponents(String point)
    {
        return getExtensionComponents(point, true);
    }


    public Object[] getExtensionComponents(String point, boolean ascending)
    {
        return pluginAlfr_.getExtensionComponents(getId() + "." + point,
            ascending);
    }


    public <T> T[] getExtensionComponents(Class<T> componentClass)
    {
        return getExtensionComponents(componentClass, true);
    }


    public <T> T[] getExtensionComponents(Class<T> componentClass,
        boolean ascending)
    {
        return pluginAlfr_.getExtensionComponents(componentClass, getId(),
            ascending);
    }


    public void notifyKvasirStarted()
    {
    }


    public void addSettingsListener(SettingsListener<S> listener)
    {
        settingsListeners_ = ArrayUtils.add(settingsListeners_, listener);
    }


    public final void notifySettingsUpdated(SettingsEvent<S> event)
    {
        for (int i = 0; i < settingsListeners_.length; i++) {
            settingsListeners_[i].notifyUpdated(event);
        }
    }


    public KvasirLog getLog()
    {
        return log_;
    }


    public void addToLifecycle(Object object)
        throws IllegalStateException
    {
        if (kvasir_.isStarted()) {
            throw new IllegalStateException(
                "Can't call this method since Kvasir has already been started: plugin-id="
                    + getId() + ", object=" + object);
        }
        if (object == null || !(object instanceof Lifecycle)) {
            return;
        }
        Lifecycle added = (Lifecycle)object;
        Lifecycle[] lifecycles = new Lifecycle[objectsInLifecycle_.length + 1];
        System.arraycopy(objectsInLifecycle_, 0, lifecycles, 0,
            objectsInLifecycle_.length);
        lifecycles[objectsInLifecycle_.length] = added;
        if (started_) {
            startObjectInLifecycle(added);
        }
    }


    boolean startObjectInLifecycle(Lifecycle lifecycle)
    {
        boolean started = false;
        Throwable throwable = null;
        try {
            started = lifecycle.start();
        } catch (Throwable t) {
            throwable = t;
        }
        if (!started) {
            log_.error("Can't start object: plugin-id=" + getId() + ", object="
                + lifecycle, throwable);
        }
        return started;
    }


    void stopObjectInLifecycle(Lifecycle lifecycle)
    {
        try {
            lifecycle.stop();
        } catch (Throwable t) {
            log_.error("Can't stop object: plugin-id=" + getId() + ", object="
                + lifecycle, t);
        }
    }


    /*
     * protected scope methods
     */

    protected Kvasir getKvasir()
    {
        return kvasir_;
    }


    protected PluginAlfr getPluginAlfr()
    {
        return pluginAlfr_;
    }


    /*
     * Lifecycle
     */

    public final boolean start()
    {
        if (started_) {
            if (log_.isDebugEnabled()) {
                log_.debug("already started: " + getId());
            }
            return true;
        }

        if (descriptor_ == null) {
            if (log_.isDebugEnabled()) {
                log_.debug("Failure: Plugin descriptor is not set: " + getId());
            }
            return false;
        }

        settings_ = getSettings0();

        boolean started;
        try {
            if (log_.isDebugEnabled()) {
                log_.debug("doStart: " + getId());
            }
            started = doStart();
            if (log_.isDebugEnabled()) {
                log_.debug("successfully doStarted: " + getId());
            }
        } catch (Throwable t) {
            log_.error("Can't start Plugin: id=" + getId(), t);
            started = false;
        }

        for (int i = 0; i < objectsInLifecycle_.length; i++) {
            startObjectInLifecycle(objectsInLifecycle_[i]);
        }

        started_ = started;
        return started;
    }


    public final boolean isStarted()
    {
        return started_;
    }


    public final void stop()
    {
        if (!started_) {
            return;
        }

        for (int i = objectsInLifecycle_.length - 1; i >= 0; i--) {
            stopObjectInLifecycle(objectsInLifecycle_[i]);
        }
        objectsInLifecycle_ = new Lifecycle[0];

        try {
            doStop();
        } catch (Throwable t) {
            log_.error("Can't stop plugin gracefully: id=" + getId(), t);
        }

        if (container_ != null) {
            try {
                container_.destroy();
            } catch (Throwable t) {
                log_.error("Can't stop plugin container: id=" + getId(), t);
            }
        }
        container_ = null;

        settings_ = null;

        started_ = false;
    }


    public Class<S> getSettingsClass()
    {
        return null;
    }


    public S getSettings()
    {
        return settings_;
    }


    public S getSettingsForUpdate()
    {
        return getSettings0();
    }


    public S newSettings()
    {
        Class<S> settingsClass = getSettingsClass();
        if (settingsClass == null) {
            return null;
        } else {
            try {
                return settingsClass.newInstance();
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    S getSettings0()
    {
        Class<S> settingsClass = getSettingsClass();
        if (settingsClass == null) {
            return null;
        } else {
            S settings = descriptor_.loadSettings(settingsClass);
            if (settings == null) {
                try {
                    settings = settingsClass.newInstance();
                } catch (InstantiationException ex) {
                    throw new RuntimeException(
                        "structureClass must have public default constructor: class="
                            + settingsClass, ex);
                } catch (IllegalAccessException ex) {
                    throw new RuntimeException(
                        "structureClass must have public default constructor: class="
                            + settingsClass, ex);
                }
            }
            return settings;
        }
    }


    public <T> T getStructuredProperty(String name, Class<T> structureClass)
    {
        return descriptor_.getStructuredProperty(name, structureClass);
    }


    public synchronized void storeSettings(S settings)
    {
        /*
         * setStructuredProperty()とsettings_への代入がatomicに行なわれないと
         * 不整合が生じるため、synchronizedにしている。
         */

        if (settings == null) {
            throw new IllegalArgumentException(
                "structuredProperty must not be null");
        }
        Class<S> structureClass = getSettingsClass();
        if (structureClass == null) {
            throw new UnsupportedOperationException(
                "This plugin does not have settings.");
        }
        descriptor_.storeSettings(settings);

        S oldSettings = settings_;
        settings_ = getSettings0();

        notifySettingsUpdated(new SettingsEvent<S>(oldSettings, settings_));
    }


    public synchronized void resetSettings()
    {
        /*
         * カスタムSettingsファイルの削除とsettings_への代入がatomicに行なわれないと
         * 不整合が生じるため、synchronizedにしている。
         */

        Class<S> structureClass = getSettingsClass();
        if (structureClass == null) {
            throw new UnsupportedOperationException(
                "This plugin does not have settings.");
        }

        descriptor_.resetSettings();

        settings_ = getSettings0();
    }


    public void setStructuredProperty(String name, Object structure)
    {
        descriptor_.setStructuredProperty(name, structure);
    }


    public synchronized void refresh()
    {
        reloadProperties();
    }


    /*
     * for framework
     */

    public PluginProperties getProperties()
    {
        return prop_;
    }


    public void setProperties(PluginProperties prop)
    {
        prop_ = prop;
    }


    public void storeProperties()
    {
        prop_.store();
    }


    public void reloadProperties()
    {
        setProperties(descriptor_.loadPluginProperties());
    }


    public void setDescriptor(PluginDescriptor descriptor)
    {
        descriptor_ = descriptor;
        descriptor_.setPlugin(this);
    }


    public void setInnerClassLoader(ClassLoader innerClassLoader)
    {
        innerClassLoader_ = innerClassLoader;
    }


    public void setOuterClassLoader(ClassLoader outerClassLoader)
    {
        outerClassLoader_ = outerClassLoader;
    }


    public void setComponentContainer(ComponentContainer container)
    {
        container_ = container;
    }


    public final void setKvasir(Kvasir kvasir)
    {
        kvasir_ = kvasir;
    }


    public final void setPluginAlfr(PluginAlfr pluginAlfr)
    {
        pluginAlfr_ = pluginAlfr;
    }
}
