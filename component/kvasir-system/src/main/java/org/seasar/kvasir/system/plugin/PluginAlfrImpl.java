package org.seasar.kvasir.system.plugin;

import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_DEVELOPEDPLUGINID;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_DEVELOPEDPLUGIN_ADDITIONALJARS;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_DEVELOPEDPLUGIN_CLASSES_DIR;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_DEVELOPEDPLUGIN_HOME_DIR;
import static org.seasar.kvasir.base.Globals.PROP_SYSTEM_DEVELOPEDPLUGIN_PROJECT_DIR;
import static org.seasar.kvasir.base.Globals.RESOURCE_PATTERN_FOR_METAINF_EXCLUSION;
import static org.seasar.kvasir.base.plugin.Plugin.PROP_ENABLED;
import static org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor.PROPERTIES_BASENAME;
import static org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor.PROPERTIES_SUFFIX;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.seasar.framework.util.ArrayUtil;
import org.seasar.kvasir.base.Globals;
import org.seasar.kvasir.base.Identifier;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.KvasirUtils;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.annotation.ForPreparingMode;
import org.seasar.kvasir.base.annotation.ForTest;
import org.seasar.kvasir.base.classloader.CachedClassLoader;
import org.seasar.kvasir.base.classloader.CompositeClassLoader;
import org.seasar.kvasir.base.classloader.FilteredURLClassLoader;
import org.seasar.kvasir.base.classloader.OverriddenURLClassLoader;
import org.seasar.kvasir.base.classloader.FilteredURLClassLoader.FilteredURL;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.container.ComponentContainerFactory;
import org.seasar.kvasir.base.descriptor.ActionType;
import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.extension.CorePluginElement;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.base.plugin.PluginAlfrSettings;
import org.seasar.kvasir.base.plugin.PluginUpdater;
import org.seasar.kvasir.base.plugin.SettingsEvent;
import org.seasar.kvasir.base.plugin.descriptor.Base;
import org.seasar.kvasir.base.plugin.descriptor.ElementClassMetaData;
import org.seasar.kvasir.base.plugin.descriptor.Extension;
import org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint;
import org.seasar.kvasir.base.plugin.descriptor.Library;
import org.seasar.kvasir.base.plugin.descriptor.Patch;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.descriptor.Runtime;
import org.seasar.kvasir.base.plugin.impl.PluginDependant;
import org.seasar.kvasir.base.plugin.impl.PluginProperties;
import org.seasar.kvasir.base.util.ArrayUtils;
import org.seasar.kvasir.base.util.XOMUtils;
import org.seasar.kvasir.system.container.descriptor.Components;
import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.dependency.Dependant;
import org.seasar.kvasir.util.dependency.Dependencies;
import org.seasar.kvasir.util.dependency.Dependency;
import org.seasar.kvasir.util.dependency.LoopDetectedException;
import org.seasar.kvasir.util.el.TextTemplateEvaluator;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.el.impl.PropertyHandlerVariableResolver;
import org.seasar.kvasir.util.el.impl.SimpleTextTemplateEvaluator;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;
import org.seasar.kvasir.util.io.ResourceUtils;
import org.seasar.kvasir.util.io.impl.FileResource;
import org.seasar.kvasir.util.io.impl.OverriddenResource;

import net.skirnir.xom.Attribute;
import net.skirnir.xom.Element;
import net.skirnir.xom.IllegalSyntaxException;
import net.skirnir.xom.ValidationException;
import net.skirnir.xom.XMLParser;
import net.skirnir.xom.XMLParserFactory;
import net.skirnir.xom.XOMapper;
import net.skirnir.xom.XOMapperFactory;
import net.skirnir.xom.annotation.impl.AnnotationBeanAccessorFactory;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * ただしこのクラスのメソッドが返すオブジェクトは変更しないで下さい。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PluginAlfrImpl
    implements PluginAlfr
{

    private static final String JAR_URL_SUFFIX = ".jar!/";

    static final String SETTINGS_FILE_NAME = "plugin-alfr-settings.xml";

    private Kvasir kvasir_;

    private XMLParser parser_ = XMLParserFactory.newInstance();

    private boolean started_ = false;

    private Resource pluginsDirectory_;

    private String developedPluginId_;

    private Resource developedPluginProjectDirectory_;

    private Resource developedPluginClassesDirectory_;

    private Resource developedPluginHomeDirectory_;

    private Resource[] developedPluginAdditinoalJars_;

    private Plugin<?> pluginUnderDevelopment_;

    private Map<String, PluginDescriptor> descriptorMap_;

    private PluginDescriptor[] descriptors_;

    private Map<Object, Plugin<?>> enabledPluginMap_;

    private Plugin<?>[] enabledPlugins_;

    private Dependencies pluginDependencies_;

    private Map<PluginLocalKey, ExtensionPoint> extensionPointMap_;

    private Map<String, ExtensionElement[]> extensionElementMap_;

    private String[] failedPluginIdsToStart_ = new String[0];

    private KvasirLog log_ = KvasirLogFactory.getLog(getClass());

    private PluginUpdater pluginUpdater_;

    private PluginAlfrSettings pluginAlfrSettings_;


    public PluginAlfrImpl()
    {
    }


    public void setKvasir(Kvasir kvasir)
    {
        kvasir_ = kvasir;
    }


    /*
     * PluginAlfr
     */

    public Plugin<?> getPlugin(String id)
    {
        return enabledPluginMap_.get(id);
    }


    @SuppressWarnings("unchecked")
    public <T extends Plugin<?>> T getPlugin(Class<T> clazz)
    {
        return (T)enabledPluginMap_.get(clazz);
    }


    public Plugin<?>[] getPlugins()
    {
        return enabledPlugins_;
    }


    public ExtensionPoint getExtensionPoint(String point)
    {
        return getExtensionPoint(extensionPointMap_, generateKey(point));
    }


    public ExtensionPoint getExtensionPoint(Object key, String pluginId)
    {
        return getExtensionPoint(extensionPointMap_, generateKey(pluginId, key));
    }


    ExtensionPoint getExtensionPoint(
        Map<PluginLocalKey, ExtensionPoint> extensionPointMap,
        PluginLocalKey key)
    {
        if (extensionPointMap.containsKey(key)) {
            ExtensionPoint point = extensionPointMap.get(key);
            if (point == null) {
                throw new IllegalStateException(
                    "Duplicate extension-point entry for: " + key);
            } else {
                return point;
            }
        } else {
            return null;
        }
    }


    public ExtensionElement[] getExtensionElements(String point)
    {
        return getExtensionElements(point, true);
    }


    public ExtensionElement[] getExtensionElements(String point,
        boolean ascending)
    {
        return getExtensionElements(getExtensionPoint(point),
            extensionElementMap_, ascending);
    }


    ExtensionElement[] getExtensionElements(ExtensionPoint extensionPoint,
        Map<String, ExtensionElement[]> extensionElementMap, boolean ascending)
    {
        if (extensionPoint == null) {
            return new ExtensionElement[0];
        } else {
            ExtensionElement[] elements = extensionElementMap
                .get(extensionPoint.getFullId());
            if (elements != null) {
                ExtensionElement[] newElements = (ExtensionElement[])Array
                    .newInstance(extensionPoint.getElementClass(),
                        elements.length);
                System.arraycopy(elements, 0, newElements, 0, elements.length);
                if (!ascending) {
                    ArrayUtils.reverse(newElements);
                }
                return newElements;
            } else {
                return (ExtensionElement[])Array.newInstance(extensionPoint
                    .getElementClass(), 0);
            }
        }
    }


    public <T> T[] getExtensionElements(Class<T> elementClass, String pluginId)
    {
        return getExtensionElements(elementClass, pluginId, true);
    }


    @SuppressWarnings("unchecked")
    public <T> T[] getExtensionElements(Class<T> elementClass, String pluginId,
        boolean ascending)
    {
        return getExtensionElements(elementClass, pluginId,
            extensionElementMap_, ascending);
    }


    @SuppressWarnings("unchecked")
    <T> T[] getExtensionElements(Class<T> elementClass, String pluginId,
        Map<String, ExtensionElement[]> extensionElementMap, boolean ascending)
    {
        ExtensionPoint extensionPoint = getExtensionPoint(extensionPointMap_,
            generateKey(pluginId, elementClass));
        if (extensionPoint == null) {
            return (T[])Array.newInstance(elementClass, 0);
        } else {
            return (T[])getExtensionElements(extensionPoint,
                extensionElementMap, ascending);
        }
    }


    public Object[] getExtensionComponents(String point)
    {
        return getExtensionComponents(point, true);
    }


    public Object[] getExtensionComponents(String point, boolean ascending)
    {
        return getExtensionComponents(getExtensionPoint(point), null, ascending);
    }


    Object[] getExtensionComponents(ExtensionPoint extensionPoint,
        Class<?> componentClass, boolean ascending)
    {
        if (extensionPoint == null) {
            return new Object[0];
        } else {
            ExtensionElement[] elements = getExtensionElements(extensionPoint,
                extensionElementMap_, ascending);
            List<Object> componentList = new ArrayList<Object>();
            for (int i = 0; i < elements.length; i++) {
                Object component = elements[i].getComponent();
                if (component != null) {
                    componentList.add(component);
                }
            }
            if (componentClass == null) {
                componentClass = extensionPoint.getElementClassMetaData()
                    .getIsa();
            }
            if (componentClass != null) {
                return componentList.toArray((Object[])Array.newInstance(
                    componentClass, 0));
            } else {
                return componentList.toArray();
            }
        }
    }


    public <T> T[] getExtensionComponents(Class<T> componentClass,
        String pluginId)
    {
        return getExtensionComponents(componentClass, pluginId, true);
    }


    @SuppressWarnings("unchecked")
    public <T> T[] getExtensionComponents(Class<T> componentClass,
        String pluginId, boolean ascending)
    {
        ExtensionPoint extensionPoint = getExtensionPoint(extensionPointMap_,
            generateKey(pluginId, componentClass));
        if (extensionPoint == null) {
            return (T[])Array.newInstance(componentClass, 0);
        } else {
            return (T[])getExtensionComponents(extensionPoint, componentClass,
                ascending);
        }
    }


    public String[] getFailedPluginIdsToStart()
    {
        return failedPluginIdsToStart_;
    }


    @SuppressWarnings("unchecked")
    public boolean notifyKvasirStarted()
    {
        Plugin<?>[] plugins = getPlugins();
        boolean madePluginDisabled = false;
        for (int i = 0; i < plugins.length; i++) {
            plugins[i].notifySettingsUpdated(new SettingsEvent(null, plugins[i]
                .getSettings()));
        }
        for (int i = 0; i < plugins.length; i++) {
            try {
                plugins[i].notifyKvasirStarted();
            } catch (Throwable t) {
                log_.error("Failed: notifyKvasirStarted(): id="
                    + plugins[i].getId(), t);
                plugins[i].getDescriptor().setAsDisabled();
                madePluginDisabled = true;
                disableDependentPlugins(plugins[i].getId(), pluginDependencies_);
            }
        }
        if (madePluginDisabled) {
            for (Iterator<Map.Entry<Object, Plugin<?>>> itr = enabledPluginMap_
                .entrySet().iterator(); itr.hasNext();) {
                Map.Entry<Object, Plugin<?>> entry = itr.next();
                if (entry.getValue().isDisabled()) {
                    itr.remove();
                }
            }
            List<Plugin> pluginList = new ArrayList<Plugin>();
            for (int i = 0; i < enabledPlugins_.length; i++) {
                if (!enabledPlugins_[i].isDisabled()) {
                    pluginList.add(enabledPlugins_[i]);
                }
            }
            enabledPlugins_ = pluginList.toArray(new Plugin[0]);
        }
        return !madePluginDisabled;
    }


    @ForPreparingMode
    public final boolean start()
    {
        if (started_) {
            return true;
        }
        try {
            prepareForDevelopingStatus();

            descriptorMap_ = new TreeMap<String, PluginDescriptor>();
            enabledPluginMap_ = new LinkedHashMap<Object, Plugin<?>>();
            extensionPointMap_ = new HashMap<PluginLocalKey, ExtensionPoint>();

            constructSettings();
            stagePlugins();

            Resource[] additionalPlugins = developedPluginHomeDirectory_ != null ? new Resource[] { developedPluginHomeDirectory_ }
                : new Resource[0];
            scan(getPluginsDirectory(), additionalPlugins);
            constructPluginGraph();
            startPlugins();

            started_ = true;

            return true;
        } catch (RuntimeException ex) {
            log_.error("Can't start PluginAlfr", ex);
            return false;
        }
    }


    /*
     * plugin-alfr-settings.xmlを読む。
     * ユーザ設定があればそちらを、無ければデフォルトを読む。
     * 両方とも無ければ空のインスタンスを作る。
     */
    void constructSettings()
    {
        pluginAlfrSettings_ = readSettings();
    }


    private PluginAlfrSettings readSettings()
    {
        final Resource configurationDirectory = kvasir_
            .getConfigurationDirectory();
        final Resource system = configurationDirectory
            .getChildResource(Globals.SYSTEM_DIR);
        final Resource userSettings = system
            .getChildResource(SETTINGS_FILE_NAME);
        if (userSettings.exists()) {
            return readSettingsFromResource(userSettings);
        }

        final Resource systemDirectory = kvasir_.getSystemDirectory();
        final Resource defaultSettings = systemDirectory
            .getChildResource(SETTINGS_FILE_NAME);
        if (defaultSettings.exists()) {
            return readSettingsFromResource(defaultSettings);
        }
        return null;
    }


    private PluginAlfrSettings readSettingsFromResource(
        final Resource defaultSettings)
    {
        try {
            return XOMUtils.toBean(defaultSettings.getInputStream(),
                PluginAlfrSettings.class);
        } catch (final ValidationException ex) {
            throw new RuntimeException(ex);
        } catch (final IllegalSyntaxException ex) {
            throw new RuntimeException(ex);
        } catch (final ResourceNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    void stagePlugins()
    {
        final Resource pluginsDirectory = kvasir_.getPluginsDirectory();
        final Resource stagingDirectory = kvasir_.getStagingDirectory()
            .getChildResource(Globals.PLUGINS_DIR);
        if (!stagingDirectory.exists()) {
            return;
        }
        for (final Resource stagingPlugin : stagingDirectory.listResources()) {
            final String pluginDirectoryName = stagingPlugin.getName();
            final Resource target = pluginsDirectory
                .getChildResource(pluginDirectoryName);
            if (target.exists()) {
                ResourceUtils.deleteChildren(target);
                if (log_.isInfoEnabled()) {
                    log_.info("replace: " + pluginDirectoryName);
                }
            } else {
                target.mkdir();
                if (log_.isInfoEnabled()) {
                    log_.info("install: " + pluginDirectoryName);
                }
            }
            ResourceUtils.copy(stagingPlugin, target);
            ResourceUtils.delete(stagingPlugin, true);
        }
    }


    @ForPreparingMode
    Resource getPluginsDirectory()
    {
        if (pluginsDirectory_ != null) {
            return pluginsDirectory_;
        } else {
            return kvasir_.getHomeDirectory().getChildResource(
                Globals.PLUGINS_DIR);
        }
    }


    @ForTest
    void setPluginsDirectory(Resource pluginsDirectory)
    {
        pluginsDirectory_ = pluginsDirectory;
    }


    @ForPreparingMode
    void prepareForDevelopingStatus()
    {
        if (kvasir_.isUnderDevelopment()) {
            developedPluginId_ = kvasir_
                .getProperty(PROP_SYSTEM_DEVELOPEDPLUGINID);
            if (developedPluginId_ != null) {
                String dir = kvasir_
                    .getProperty(PROP_SYSTEM_DEVELOPEDPLUGIN_CLASSES_DIR);
                if (dir != null) {
                    developedPluginClassesDirectory_ = new FileResource(dir);
                }
                dir = kvasir_
                    .getProperty(PROP_SYSTEM_DEVELOPEDPLUGIN_PROJECT_DIR);
                if (dir != null) {
                    developedPluginProjectDirectory_ = new FileResource(dir);
                }
                dir = kvasir_.getProperty(PROP_SYSTEM_DEVELOPEDPLUGIN_HOME_DIR);
                if (dir != null) {
                    developedPluginHomeDirectory_ = new FileResource(dir);
                }
                String[] jars = PropertyUtils.toLines(kvasir_
                    .getProperty(PROP_SYSTEM_DEVELOPEDPLUGIN_ADDITIONALJARS));
                developedPluginAdditinoalJars_ = new Resource[jars.length];
                for (int i = 0; i < jars.length; i++) {
                    developedPluginAdditinoalJars_[i] = new FileResource(
                        jars[i]);
                }
            }
        }
    }


    @ForPreparingMode
    public final void stop()
    {
        if (!started_) {
            return;
        }

        failedPluginIdsToStart_ = new String[0];
        extensionPointMap_ = null;
        extensionElementMap_ = null;
        pluginDependencies_ = null;

        Plugin<?>[] plugins = getPlugins();
        for (int i = plugins.length - 1; i >= 0; i--) {
            try {
                plugins[i].stop();
            } catch (Throwable t) {
                log_.warn("Can't stop plugin: pluginId=" + plugins[i].getId(),
                    t);
            }
        }
        enabledPlugins_ = null;

        enabledPluginMap_ = null;
        descriptors_ = null;
        descriptorMap_ = null;
        kvasir_ = null;

        started_ = false;
    }


    public final boolean isStarted()
    {
        return started_;
    }


    @ForPreparingMode
    void scan(Resource pluginsDir, Resource[] additionalPlugins)
    {
        // plugin.xmlとpatch.xmlを読み込む。
        Map<String, PluginDescriptor> descriptorMap = new TreeMap<String, PluginDescriptor>();
        Map<String, Map<Version, PluginDescriptor>> descriptorsMap = new HashMap<String, Map<Version, PluginDescriptor>>();
        Map<Identifier, List<Patch>> patchesMap = new HashMap<Identifier, List<Patch>>();
        readPluginSettings(pluginsDir, additionalPlugins, descriptorMap,
            descriptorsMap, patchesMap);

        // プラグインにパッチをあてる。また、継承関係を解決する。
        for (Iterator<Map.Entry<String, PluginDescriptor>> itr = descriptorMap
            .entrySet().iterator(); itr.hasNext();) {
            Map.Entry<String, PluginDescriptor> entry = itr.next();
            String id = entry.getKey();
            PluginDescriptor descriptor = entry.getValue();
            if (!descriptor.isResolved()) {
                descriptor = resolveInheritance(descriptor, patchesMap,
                    descriptorsMap, new HashSet<String>());
                if (descriptor.isResolved()) {
                    descriptorMap_.put(id, descriptor);
                    descriptorsMap.get(id).put(descriptor.getVersion(),
                        descriptor);
                }
            }
        }
    }


    @ForPreparingMode
    void constructPluginGraph()
    {
        List<Plugin<?>> pluginList = new ArrayList<Plugin<?>>();

        // プラグインの依存関係を元にグラフを作成する。
        // また依存関係に基づいてenable/disableを決定する。
        descriptors_ = sortPluginDescriptorsByDependencies(descriptorMap_);

        // Pluginインスタンスを構築する。また、ExtensionPointを収集する。
        Map<String, ClassLoaderPair> classLoaderPairMap = new HashMap<String, ClassLoaderPair>();
        Map<String, ComponentContainer> containerMap = new HashMap<String, ComponentContainer>();
        for (int i = 0; i < descriptors_.length; i++) {
            PluginDescriptor descriptor = descriptors_[i];
            if (descriptor.isDisabled()) {
                continue;
            }

            pluginList.add(buildPluginInstance(descriptor, classLoaderPairMap,
                containerMap));

            gatherExtensionPoints(descriptor.getExtensionPoints(),
                extensionPointMap_);
        }

        // ExtensionPoint毎にExtensionElementを収集する。
        extensionElementMap_ = gatherExtensionElements(descriptors_,
            extensionPointMap_);

        // コアプラグインは先に開始されるようにしておく。
        CorePluginElement[] elements = getExtensionElements(
            CorePluginElement.class, Globals.PLUGINID, true);
        for (int i = 0; i < elements.length; i++) {
            Plugin<?> plugin = elements[i].getPlugin();
            enabledPluginMap_.put(plugin.getId(), plugin);
        }
        for (Iterator<Plugin<?>> itr = pluginList.iterator(); itr.hasNext();) {
            Plugin<?> plugin = itr.next();
            enabledPluginMap_.put(plugin.getId(), plugin);
        }
    }


    @ForPreparingMode
    void startPlugins()
    {
        // pluginをstartさせる。
        if (log_.isDebugEnabled()) {
            log_.debug("start plugins...");
        }

        List<Plugin<?>> enabledPluginList = new ArrayList<Plugin<?>>(
            enabledPluginMap_.values());

        // start()中にPluginAlfr#getPlugin(プラグインインタフェース.class)が正しく
        // 動作するように、start()前にインタフェースの登録をするようにしている。
        for (Iterator<Plugin<?>> itr = enabledPluginList.iterator(); itr
            .hasNext();) {
            Plugin<?> plugin = itr.next();
            Class<?> pluginIf = ClassUtils.getSubInterface(plugin.getClass(),
                Plugin.class);
            if (pluginIf != null) {
                enabledPluginMap_.put(pluginIf, plugin);
            }
        }

        for (Iterator<Plugin<?>> itr = enabledPluginList.iterator(); itr
            .hasNext();) {
            Plugin<?> plugin = itr.next();
            String id = plugin.getId();
            if (plugin.isDisabled()) {
                // 依存するプラグインが起動に失敗してdisabledになった
                // 場合はこうなり得る。
                log_.info("[SKIP] Plugin is disabled: plugin-id=" + id);
                enabledPluginMap_.remove(id);
                Class<?> pluginIf = ClassUtils.getSubInterface(plugin
                    .getClass(), Plugin.class);
                if (pluginIf != null) {
                    enabledPluginMap_.remove(pluginIf);
                }
                continue;
            }

            if (startPlugin(plugin)) {
                if (log_.isDebugEnabled()) {
                    log_.debug("successfully started: " + id);
                }
            } else {
                enabledPluginMap_.remove(id);
                Class<?> pluginIf = ClassUtils.getSubInterface(plugin
                    .getClass(), Plugin.class);
                if (pluginIf != null) {
                    enabledPluginMap_.remove(pluginIf);
                }
            }
        }

        enabledPlugins_ = enabledPluginList.toArray(new Plugin[0]);
    }


    @ForPreparingMode
    boolean startPlugin(Plugin<?> plugin)
    {
        boolean started = false;
        Throwable throwable = null;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Plugin '").append(plugin.getId())
                .append("' is starting");
            if (plugin.isUnderDevelopment()) {
                sb.append(" UNDER DEVELOPMENT MODE");
            }
            log_.info(sb.toString());

            started = plugin.start();
        } catch (Throwable t) {
            throwable = t;
        }
        if (started) {
            log_.info("Plugin '" + plugin.getId()
                + "' has been successfully started.");
        } else {
            log_.error("Can't start plugin: plugin-id=" + plugin.getId(),
                throwable);
            failedPluginIdsToStart_ = (String[])ArrayUtil.add(
                failedPluginIdsToStart_, plugin.getId());
            try {
                plugin.stop();
            } catch (Throwable t) {
                log_.warn("Can't stop plugin: id=" + plugin.getId(), t);
            }
            plugin.getDescriptor().setAsDisabled();
            disableDependentPlugins(plugin.getId(), pluginDependencies_);
        }
        return started;
    }


    // plugin.xmlとpatch.xmlを読み込む。
    @ForPreparingMode
    void readPluginSettings(Resource pluginsDir, Resource[] additionalPlugins,
        Map<String, PluginDescriptor> descriptorMap,
        Map<String, Map<Version, PluginDescriptor>> descriptorsMap,
        Map<Identifier, List<Patch>> patchesMap)
    {
        List<Resource> dirList = new ArrayList<Resource>();
        Resource[] dirs = pluginsDir.listResources();
        if (dirs != null) {
            dirList.addAll(Arrays.asList(dirs));
        }
        if (additionalPlugins != null) {
            dirList.addAll(Arrays.asList(additionalPlugins));
        }
        dirs = dirList.toArray(new Resource[0]);
        if (dirs.length == 0) {
            return;
        }

        Resource pluginsConfigurationDirectory = getPluginsConfigurationDirectory();
        Resource pluginsRuntimeWorkDirectory = getPluginsRuntimeWorkDirectory();

        Map<String, Patch> patchMap = new TreeMap<String, Patch>();
        for (int i = 0; i < dirs.length; i++) {
            if (!dirs[i].isDirectory()) {
                log_.info("Ignore: is not a directory: " + dirs[i]);
                continue;
            }

            Resource xml = dirs[i].getChildResource(PLUGIN_XML);
            if (xml.exists()) {
                // plugin.xml。
                PluginDescriptor descriptor;
                try {
                    descriptor = readXMLResourceAsBean(xml,
                        PluginDescriptor.class);
                } catch (ResourceNotFoundException ex) {
                    log_.error("Can't find " + PLUGIN_XML + ": " + xml);
                    continue;
                } catch (Throwable t) {
                    log_.error("Can't read " + PLUGIN_XML + " (1): " + xml, t);
                    continue;
                }

                descriptor.setSystemDirectories(dirs[i],
                    pluginsConfigurationDirectory, pluginsRuntimeWorkDirectory);
                descriptor.cleanupSystemDirectories();

                String id = descriptor.getId();
                Map<Version, PluginDescriptor> map = descriptorsMap.get(id);
                if (map == null) {
                    map = new TreeMap<Version, PluginDescriptor>(
                        new Comparator<Version>() {
                            public int compare(Version o1, Version o2)
                            {
                                // Versionの新しい順に並べるようにする。
                                return -1 * o1.compareTo(o2);
                            }
                        });
                    descriptorsMap.put(id, map);
                }
                map.put(descriptor.getVersion(), descriptor);

                // 同じIDのプラグインが複数ある場合はバージョンの古いものを無視するようにする。
                PluginDescriptor d = descriptorMap.get(id);
                if (d == null
                    || descriptor.getVersion().compareTo(d.getVersion()) > 0) {
                    descriptorMap.put(id, descriptor);
                }
            } else if ((xml = dirs[i].getChildResource(PATCH_XML)).exists()) {
                // patch.xml。
                Patch patch;
                try {
                    patch = readXMLResourceAsBean(xml, Patch.class);
                } catch (ResourceNotFoundException ex) {
                    log_.error("Can't find " + PATCH_XML + ": " + xml);
                    continue;
                } catch (Throwable t) {
                    log_.error("Can't read " + PATCH_XML + " (1): " + xml, t);
                    continue;
                }

                String id = patch.getId();
                patch.getPlugin().setSystemDirectories(id, dirs[i],
                    pluginsConfigurationDirectory, pluginsRuntimeWorkDirectory);

                Patch p = patchMap.get(id);
                // 同じIDのパッチが複数ある場合はバージョンの古いものを無視するようにする。
                if (p == null
                    || patch.getVersion().compareTo(p.getVersion()) > 0) {
                    patchMap.put(id, patch);
                }
            } else {
                log_.warn("[SKIP] Can't find " + PLUGIN_XML + " nor "
                    + PATCH_XML + ": directory=" + dirs[i]);
            }
        }

        for (Iterator<Patch> itr = patchMap.values().iterator(); itr.hasNext();) {
            Patch patch = itr.next();
            Identifier key = patch.getPluginIdentifier();
            List<Patch> list = patchesMap.get(key);
            if (list == null) {
                list = new ArrayList<Patch>();
                patchesMap.put(key, list);
            }
            list.add(patch);
        }
    }


    @ForPreparingMode
    Resource getPluginsRuntimeWorkDirectory()
    {
        return kvasir_.getRuntimeWorkDirectory().getChildResource(
            Globals.PLUGINS_DIR);
    }


    @ForPreparingMode
    Resource getPluginsConfigurationDirectory()
    {
        return kvasir_.getConfigurationDirectory().getChildResource(
            Globals.PLUGINS_DIR);
    }


    /**
     * 指定されたプラグインにパッチを適用した後、継承関係を解決します。
     * <p>継承関係を解決できた場合は返されるPluginDescriptorのisResolved()の値がtrueになります。
     * 解決できなかった場合はfalseになります。
     * </p>
     *
     * @param descriptor プラグイン。
     * @param patchMap プラグインに適用するPatchが格納されたMap。
     * @param descriptorsMap 継承関係を解決するための元となるPluginDescriptorが格納されたMap。
     * @param idSet ループを検出するためのSet。
     * @return 解決した後のPluginDescriptor。
     */
    @ForPreparingMode
    PluginDescriptor resolveInheritance(PluginDescriptor descriptor,
        Map<Identifier, List<Patch>> patchMap,
        Map<String, Map<Version, PluginDescriptor>> descriptorsMap,
        Set<String> idSet)
    {
        if (descriptor.isResolved()) {
            return descriptor;
        }

        String id = descriptor.getId();
        if (idSet.contains(id)) {
            StringBuffer sb = new StringBuffer();
            sb.append("Refer to circulation has been detected: ");
            for (Iterator<String> itr = idSet.iterator(); itr.hasNext();) {
                sb.append(itr.next()).append(" -> ");
            }
            sb.append(id);
            log_.error(sb.toString());
            return descriptor;
        }

        XOMapper mapper = XOMapperFactory.newInstance().setBeanAccessorFactory(
            new AnnotationBeanAccessorFactory());

        idSet.add(id);
        try {
            Resource resolvedResourcesDirectory = null;

            Resource pluginsConfigurationDirectory = getPluginsConfigurationDirectory();
            Resource pluginsRuntimeWorkDirectory = getPluginsRuntimeWorkDirectory();

            // 継承関係を解決する。
            Base base = descriptor.getBase();
            if (base != null) {
                try {
                    Map<Version, PluginDescriptor> map = descriptorsMap
                        .get(base.getPlugin());
                    PluginDescriptor parentDescriptor = findMatchedPluginDescriptor(
                        base, map);
                    if (parentDescriptor == null) {
                        log_.error("Base not found: id=" + id + ", parent-id="
                            + base.getPlugin());
                        return descriptor;
                    }

                    parentDescriptor = resolveInheritance(parentDescriptor,
                        patchMap, descriptorsMap, idSet);
                    if (!parentDescriptor.isResolved()) {
                        log_.error("Can't resolve parent: id=" + id
                            + ", parent-id=" + base.getPlugin());
                        return descriptor;
                    }

                    // plugin.xmlをマージする。
                    PluginDescriptor merged = (PluginDescriptor)mapper.merge(
                        parentDescriptor, descriptor);
                    merged.setSystemDirectories(descriptor
                        .getRawResourcesDirectory(),
                        pluginsConfigurationDirectory,
                        pluginsRuntimeWorkDirectory);
                    descriptor = merged;

                    // *.diconなどのリソースをマージしてコピーする。*.diconをコピーするのは、
                    // URLが同一の*.diconをS2Containerに別のコンテナとして扱わせることが面倒
                    // だからということもある。
                    mergeResources(descriptor
                        .getResolvedDeltaResourcesDirectory(), parentDescriptor
                        .getResolvedResourcesDirectory(), descriptor);
                    mergeResources(descriptor
                        .getResolvedDeltaResourcesDirectory(), descriptor
                        .getRawResourcesDirectory(), descriptor);

                    resolvedResourcesDirectory = new OverriddenResource(
                        parentDescriptor.getResolvedResourcesDirectory(),
                        descriptor.getRawResourcesDirectory());
                } catch (Throwable t) {
                    log_.error("Can't resolve: id=" + id + ", parent-id="
                        + base.getPlugin(), t);
                    return descriptor;
                }
            }

            // パッチを適用する。
            List<Patch> patchList = patchMap.get(descriptor.getIdentifier());
            if (patchList != null) {
                if (resolvedResourcesDirectory == null) {
                    // *.diconなどのリソースをマージしてコピーする。*.diconをコピーするのは、
                    // URLが同一の*.diconをS2Containerに別のコンテナとして扱わせることが面倒
                    // だからということもある。
                    mergeResources(descriptor
                        .getResolvedDeltaResourcesDirectory(), descriptor
                        .getRawResourcesDirectory(), descriptor);
                    resolvedResourcesDirectory = descriptor
                        .getRawResourcesDirectory();
                }

                for (Iterator<Patch> itr = patchList.iterator(); itr.hasNext();) {
                    Patch patch = itr.next();

                    // plugin.xmlをマージする。
                    PluginDescriptor merged = (PluginDescriptor)mapper.merge(
                        descriptor, patch.getPlugin());
                    merged.setSystemDirectories(descriptor
                        .getRawResourcesDirectory(),
                        pluginsConfigurationDirectory,
                        pluginsRuntimeWorkDirectory);
                    descriptor = merged;

                    // *.diconなどのリソースをマージしてコピーする。*.diconをコピーするのは、
                    // URLが同一の*.diconをS2Containerに別のコンテナとして扱わせることが面倒
                    // だからということもある。
                    Resource subResource = patch.getPlugin()
                        .getRawResourcesDirectory();
                    mergeResources(descriptor
                        .getResolvedDeltaResourcesDirectory(), subResource,
                        descriptor);
                    resolvedResourcesDirectory = new OverriddenResource(
                        resolvedResourcesDirectory, subResource);
                }
            }

            if (resolvedResourcesDirectory == null) {
                resolvedResourcesDirectory = descriptor
                    .getRawResourcesDirectory();
            } else {
                resolvedResourcesDirectory = new OverriddenResource(
                    resolvedResourcesDirectory, descriptor
                        .getResolvedDeltaResourcesDirectory());
            }
            descriptor
                .setResolvedResourcesDirectory(resolvedResourcesDirectory);
            descriptor.setAsResolved();
            return descriptor;
        } finally {
            idSet.remove(id);
        }
    }


    @ForPreparingMode
    PluginDescriptor findMatchedPluginDescriptor(Base base,
        Map<Version, PluginDescriptor> descriptorMap)
    {
        if (descriptorMap == null) {
            return null;
        }
        for (Iterator<PluginDescriptor> itr = descriptorMap.values().iterator(); itr
            .hasNext();) {
            PluginDescriptor descriptor = itr.next();
            if (base.isMatched(descriptor.getVersion())) {
                return descriptor;
            }
        }
        return null;
    }


    @ForPreparingMode
    void mergeResources(Resource baseDirectory, Resource subDirectory,
        PluginDescriptor descriptor)
    {
        // plugin.xpropertiesをコピーする。
        mergePluginProperties(baseDirectory, subDirectory);

        // *.diconをコピーする。
        mergeDicons(baseDirectory, subDirectory);

        // libraryタグで指定されているフォルダの内容をコピーする。
        // これはクラスパスを構築した場合にもれなくフォルダ以下のリソースがクラスパスから見えるように
        // するため。
        mergeClasspathResources(baseDirectory, subDirectory, descriptor);
    }


    @ForPreparingMode
    void mergePluginProperties(Resource baseDirectory, Resource subDirectory)
    {
        Resource[] children = subDirectory.listResources();
        for (int i = 0; i < children.length; i++) {
            String name = children[i].getName();
            if (!name.startsWith(PROPERTIES_BASENAME)) {
                continue;
            } else if (!name.endsWith(PROPERTIES_SUFFIX)) {
                continue;
            }

            Resource baseResource = baseDirectory.getChildResource(name);
            if (!baseResource.exists()) {
                ResourceUtils.copy(children[i], baseResource);
            } else {
                MapProperties prop = new MapProperties(
                    new LinkedHashMap<String, String>());
                try {
                    prop.load(baseResource.getInputStream(), "UTF-8");
                } catch (Throwable t) {
                    throw new IORuntimeException("Can't load base resource: "
                        + baseResource, t);
                }
                try {
                    prop.load(children[i].getInputStream(), "UTF-8");
                } catch (Throwable t) {
                    throw new IORuntimeException("Can't load resource: "
                        + children[i], t);
                }

                OutputStream os = null;
                try {
                    os = baseResource.getOutputStream();
                    prop.store(os, "UTF-8");
                } catch (Throwable t) {
                    throw new IORuntimeException("Can't store resource: "
                        + baseResource, t);
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        }
    }


    @ForPreparingMode
    void mergeDicons(Resource baseDirectory, Resource subDirectory)
    {
        XOMapper mapper = XOMapperFactory.newInstance().setBeanAccessorFactory(
            new AnnotationBeanAccessorFactory());

        Resource[] children = subDirectory.listResources();
        for (int i = 0; i < children.length; i++) {
            String name = children[i].getName();
            Resource baseResource = baseDirectory.getChildResource(name);
            if (children[i].isDirectory()) {
                mergeDicons(baseResource, children[i]);
            } else {
                if (!name.endsWith(".dicon")) {
                    continue;
                }

                if (!baseResource.exists()) {
                    ResourceUtils.copy(children[i], baseResource);
                } else {
                    Components parentComponents;
                    try {
                        parentComponents = readDicon(baseResource);
                    } catch (Throwable t) {
                        throw new IORuntimeException("Can't read "
                            + baseResource, t);
                    }
                    Components components;
                    try {
                        components = readDicon(children[i]);
                    } catch (Throwable t) {
                        throw new IORuntimeException("Can't read "
                            + children[i], t);
                    }
                    Components mergedComponents = (Components)mapper.merge(
                        parentComponents, components);
                    // FIXME XMLヘッダをつけたりエンコーディングを適切にしたりしよう。
                    OutputStream os = null;
                    try {
                        os = baseResource.getOutputStream();
                        mapper.toXML(mergedComponents, new OutputStreamWriter(
                            os, "UTF-8"));
                    } catch (Throwable t) {
                        throw new IORuntimeException("Can't merge: parent="
                            + baseResource + ", target=" + children[i]
                            + ", output=" + baseResource, t);
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException ignore) {
                            }
                        }
                    }
                }
            }
        }
    }


    // *.diconを読み込む。
    @ForPreparingMode
    Components readDicon(Resource dicon)
        throws IllegalSyntaxException, IOException, ResourceNotFoundException,
        ValidationException
    {
        return XOMUtils.toBean(dicon.getInputStream(), Components.class);
    }


    // XMLファイルをBeanとして読み込む。
    @ForPreparingMode
    <T> T readXMLResourceAsBean(Resource xmlResource, Class<T> clazz)
        throws IllegalSyntaxException, IOException, ResourceNotFoundException,
        ValidationException
    {
        try {
            return XOMUtils.toBean(xmlResource.getInputStream(), clazz);
        } catch (ValidationException ex) {
            ex.setResourceName(xmlResource.getURL().toExternalForm());
            throw ex;
        }
    }


    @ForPreparingMode
    void mergeClasspathResources(Resource baseDirectory, Resource subDirectory,
        PluginDescriptor descriptor)
    {
        Runtime runtime = descriptor.getRuntime();
        if (runtime == null) {
            return;
        }
        Library[] libraries = runtime.getLibraries();
        for (int i = 0; i < libraries.length; i++) {
            String name = libraries[i].getName();
            Resource resource = subDirectory.getChildResource(name);
            if (resource.exists() && resource.isDirectory()
                && !libraries[i].isExpand()) {
                ResourceUtils.copy(resource, baseDirectory
                    .getChildResource(name));
            }
        }
    }


    @ForPreparingMode
    PluginDescriptor[] sortPluginDescriptorsByDependencies(
        Map<String, PluginDescriptor> descriptorMap)
    {
        List<Dependant> dependantList = new ArrayList<Dependant>(descriptorMap
            .size());
        for (Iterator<PluginDescriptor> itr = descriptorMap.values().iterator(); itr
            .hasNext();) {
            dependantList.add(new PluginDependant(itr.next()));
        }
        try {
            pluginDependencies_ = new Dependencies(dependantList);
        } catch (LoopDetectedException ex) {
            throw new IllegalArgumentException(
                "Plugin loop detection: plugin (id="
                    + ex.getDependant().getId() + ") requires plugin (id="
                    + ex.getRequirement().getId() + ")");
        }
        Dependency[] ds = pluginDependencies_.getDependencies();
        PluginDescriptor[] descriptors = new PluginDescriptor[ds.length];
        for (int i = 0; i < ds.length; i++) {
            Dependency d = ds[i];
            PluginDescriptor pd = ((PluginDependant)d.getSource())
                .getDescriptor();
            pd.setDependency(d);
            if (d.isDisabled()) {
                StringBuffer sb = new StringBuffer();
                sb.append("Plugin set disabled (id=").append(pd.getId())
                    .append(")");
                if (d.getLog() != null) {
                    sb.append(" because ").append(d.getLog());
                }
                log_.error(sb.toString());
                pd.setAsDisabled();
            }
            descriptors[i] = pd;
        }

        return descriptors;
    }


    // Pluginインスタンスを構築する。
    @ForPreparingMode
    Plugin<?> buildPluginInstance(PluginDescriptor descriptor,
        Map<String, ClassLoaderPair> classLoaderPairMap,
        Map<String, ComponentContainer> containerMap)
    {
        String id = descriptor.getId();
        try {
            // plugin.xpropertiesを読み込む。
            PluginProperties prop = descriptor.loadPluginProperties();

            // リソースのフィルタ処理を行い、ワークディレクトリにコピーする。
            filterResources(descriptor, prop);

            // 明示的にdisableになっているプラグインをdisableにする。
            if (!PropertyUtils.valueOf(prop.getProperty(PROP_ENABLED), true)) {
                descriptor.setAsDisabled();
            }

            // クラスローダを生成する。
            // （リソースのフィルタ処理よりも後に行なうこと！）
            ClassLoaderPair classLoaderPair = createClassLoader(descriptor,
                classLoaderPairMap);
            classLoaderPairMap.put(id, classLoaderPair);

            // プラグイン用のComponentContainerを生成する。
            ComponentContainer container = createComponentContainer(descriptor,
                containerMap, classLoaderPair);
            containerMap.put(id, container);

            // Pluginインスタンスを生成する。
            Plugin<?> plugin;
            if (!container.hasLocalComponent(Plugin.class)) {
                if (log_.isDebugEnabled()) {
                    log_
                        .debug("Plugin instance does not exist in componentContainer. Default instance will be used.");
                }
                plugin = newPluginInstance();
            } else {
                plugin = container.getLocalComponent(Plugin.class);
            }
            plugin.setDescriptor(descriptor);
            plugin.setProperties(prop);
            plugin.setInnerClassLoader(classLoaderPair.getInnerClassLoader());
            plugin.setOuterClassLoader(classLoaderPair.getOuterClassLoader());
            plugin.setComponentContainer(container);

            // ステータスを設定する。
            if (kvasir_.isUnderDevelopment()
                && id.equals(kvasir_
                    .getProperty(Globals.PROP_SYSTEM_DEVELOPEDPLUGINID))) {
                plugin.setUnderDevelopment(true);
                plugin.setProjectDirectory(developedPluginProjectDirectory_);
                pluginUnderDevelopment_ = plugin;
            }

            return plugin;
        } catch (RuntimeException ex) {
            descriptor.setAsDisabled();
            disableDependentPlugins(id, pluginDependencies_);
            throw ex;
        }
    }


    Plugin<?> newPluginInstance()
    {
        return kvasir_.getComponentContainer().getComponent(Plugin.class);
    }


    static class ClassLoaderPair
    {
        private ClassLoader innerClassLoader_;

        private ClassLoader outerClassLoader_;

        private ClassLoader parentClassLoader_;


        public ClassLoaderPair(ClassLoader innerClassLoader,
            ClassLoader outerClassLoader, ClassLoader parentClassLoader)
        {
            innerClassLoader_ = innerClassLoader;
            outerClassLoader_ = outerClassLoader;
            parentClassLoader_ = parentClassLoader;
        }


        public ClassLoader getInnerClassLoader()
        {
            return innerClassLoader_;
        }


        public ClassLoader getOuterClassLoader()
        {
            return outerClassLoader_;
        }


        public ClassLoader getParentClassLoader()
        {
            return parentClassLoader_;
        }
    }


    @ForPreparingMode
    Map<String, ExtensionElement[]> gatherExtensionElements(
        PluginDescriptor[] descriptors,
        Map<PluginLocalKey, ExtensionPoint> extensionPointMap)
    {
        Map<String, List<ElementPair>> elementPairMap = new HashMap<String, List<ElementPair>>();

        for (int i = 0; i < descriptors.length; i++) {
            if (descriptors[i].isDisabled()) {
                continue;
            }
            Extension[] extensions = descriptors[i].getExtensions();
            for (int j = 0; j < extensions.length; j++) {
                Extension extension = extensions[j];
                String point = extension.getPoint();
                if (extensionPointMap.get(generateKey(point)) == null) {
                    log_.error("Extension"
                        + " for unknown extension-point found: point=" + point);
                    continue;
                }
                List<ElementPair> list = elementPairMap.get(point);
                if (list == null) {
                    list = new ArrayList<ElementPair>();
                    elementPairMap.put(point.intern(), list);
                }
                Element[] elements = extension.getElements();
                for (int k = 0; k < elements.length; k++) {
                    list.add(new ElementPair(elements[k], extension));
                }
            }
        }

        Map<String, ExtensionElement[]> eesMap = new HashMap<String, ExtensionElement[]>();
        for (Iterator<Map.Entry<String, List<ElementPair>>> itr = elementPairMap
            .entrySet().iterator(); itr.hasNext();) {
            Map.Entry<String, List<ElementPair>> entry = itr.next();
            String pointId = entry.getKey();
            List<ElementPair> elementPairList = entry.getValue();
            ExtensionPoint extensionPoint = getExtensionPoint(
                extensionPointMap, generateKey(pointId));
            ExtensionElement[] eeps = gatherExtensionElements(extensionPoint,
                elementPairList);
            if (eeps != null) {
                eesMap.put(extensionPoint.getFullId(), eeps);
            }
        }

        return eesMap;
    }


    @ForPreparingMode
    ExtensionElement[] gatherExtensionElements(ExtensionPoint extensionPoint,
        List<ElementPair> elementPairList)
    {
        XOMapper mapper = XOMapperFactory.newInstance().setBeanAccessorFactory(
            new AnnotationBeanAccessorFactory());

        String pointId = extensionPoint.getFullId();
        Class<? extends ExtensionElement> elementClass = extensionPoint
            .getElementClass();
        ElementClassMetaData metaData = extensionPoint
            .getElementClassMetaData();

        List<ExtensionElement> extensionElementPairList = new ArrayList<ExtensionElement>();
        for (Iterator<ElementPair> itr = elementPairList.iterator(); itr
            .hasNext();) {
            ElementPair pair = itr.next();
            Plugin<?> plugin = pair.getExtension().getParent().getPlugin();
            ComponentContainer container = plugin.getComponentContainer();

            ExtensionElement extensionElement = null;
            Object component = null;
            Class<?> componentClass = null;

            Attribute idAttr = pair.getElement().getAttribute("id");
            if (idAttr != null) {
                // id属性がある場合。
                String componentKey = idAttr.getValue();
                if (metaData.getBinding() == BindingType.MUST) {
                    if (!container.hasComponent(componentKey)) {
                        throw new IllegalArgumentException("Component '"
                            + componentKey + "' does not exist: point="
                            + pointId + ", element="
                            + getElementString(pair.getElement(), mapper));
                    }
                    componentClass = container.getComponentClass(componentKey);
                    if (metaData.isReplace()) {
                        component = container.getComponent(componentKey);
                    }
                } else if (metaData.getBinding() == BindingType.MAY) {
                    if (container.hasComponent(componentKey)) {
                        componentClass = container
                            .getComponentClass(componentKey);
                        if (metaData.isReplace()) {
                            component = container.getComponent(componentKey);
                        }
                    }
                } else if (metaData.getBinding() == BindingType.NONE) {
                    ;
                } else {
                    throw new RuntimeException("Unknown bindingType: "
                        + metaData.getBinding());
                }
            } else {
                // id属性がない場合。
                if (metaData.getBinding() == BindingType.MUST) {
                    throw new IllegalArgumentException(
                        "'id' Attribute does not exist: point=" + pointId
                            + ", element="
                            + getElementString(pair.getElement(), mapper));
                } else if (metaData.getBinding() == BindingType.MAY
                    || metaData.getBinding() == BindingType.NONE) {
                    ;
                } else {
                    throw new RuntimeException("Unknown bindingType: "
                        + metaData.getBinding());
                }
            }

            if (componentClass != null) {
                if (metaData.getIsa() != null
                    && !metaData.getIsa().isAssignableFrom(componentClass)) {
                    // componentがisa指定で指定されたインタフェースを実装していない。
                    throw new IllegalArgumentException(
                        "Component  must be an instance of '"
                            + metaData.getIsa().getName() + "' class: point="
                            + pointId + ", element="
                            + getElementString(pair.getElement(), mapper));
                }
                if (metaData.isReplace()) {
                    if (!elementClass.isAssignableFrom(componentClass)) {
                        throw new IllegalArgumentException(
                            "Component  must be an instance of '"
                                + elementClass.getName() + "' class: point="
                                + pointId + ", element="
                                + getElementString(pair.getElement(), mapper));
                    }
                    extensionElement = (ExtensionElement)component;
                }
            }
            try {
                extensionElement = (ExtensionElement)mapper.toBean(pair
                    .getElement(), elementClass, extensionElement);
            } catch (ValidationException ex) {
                throw new IllegalArgumentException(
                    "Invalid extension element specified: point=" + pointId
                        + ", element="
                        + getElementString(pair.getElement(), mapper), ex);
            } catch (RuntimeException ex) {
                throw new RuntimeException(
                    "Can't read extension element: point=" + pointId
                        + ", element="
                        + getElementString(pair.getElement(), mapper), ex);
            }
            extensionElement.setParent(pair.getExtension());
            extensionElementPairList.add(extensionElement);
        }

        return normalizeEextensionElements(extensionElementPairList).toArray(
            (ExtensionElement[])Array.newInstance(elementClass, 0));
    }


    @ForPreparingMode
    String getElementString(Element element, XOMapper mapper)
    {
        StringWriter sw = new StringWriter();
        try {
            mapper.toXML(element, sw);
        } catch (IOException ex) {
            throw new RuntimeException("Can't happen!", ex);
        }
        return sw.toString();
    }


    @ForPreparingMode
    Collection<ExtensionElement> normalizeEextensionElements(
        List<ExtensionElement> extensionElementPairList)
    {
        int autoNumber = 0;

        // elementを収集する。
        Map<String, ExtensionElement> elementsByFullIdMap = new LinkedHashMap<String, ExtensionElement>();
        Map<String, ExtensionElement> replaceMap = new HashMap<String, ExtensionElement>();
        Set<String> removeSet = new HashSet<String>();
        for (Iterator<ExtensionElement> itr = extensionElementPairList
            .iterator(); itr.hasNext();) {
            ExtensionElement element = itr.next();

            // elementはidを持つ必要がある。
            // ない場合は自動的に設定する。
            if (element.getId() == null) {
                autoNumber = assignElementId(element, autoNumber);
                if (log_.isDebugEnabled()) {
                    log_.debug("Extension element has no id,"
                        + " so assigned automatically:" + " extension-point="
                        + element.getParent().getPoint() + ", plugin-id="
                        + element.getParent().getParent().getId()
                        + ", assigned id=" + element.getId());
                }
            }
            String fullId = element.getFullId();

            ActionType actionType = element.getActionType();
            // 置換指定ならその旨を記録する。
            if (actionType == ActionType.REPLACE) {
                ExtensionElement old = replaceMap.get(fullId);
                if (old != null) {
                    log_.info("Duplicate replace action: full-id=" + fullId
                        + ", old=" + old + ", new=" + element);
                }
                replaceMap.put(fullId, element);
                // 削除指定ならその旨を記録する。
            } else if (actionType == ActionType.REMOVE) {
                removeSet.add(fullId);
                continue;
            }

            ExtensionElement old = elementsByFullIdMap.get(fullId);
            if (old != null) {
                log_.warn("Duplicate entry. Skipped: full-id=" + fullId
                    + ", old=" + old + ", new=" + element);
            } else {
                elementsByFullIdMap.put(fullId, element);
            }
        }

        // elementをbefore、after指定に従って並べ替える。
        Map<String, ExtensionElement> sorted = sortExtensionElements(elementsByFullIdMap);

        // 置換指定されたelementを置換する。
        for (Iterator<Map.Entry<String, ExtensionElement>> itr2 = replaceMap
            .entrySet().iterator(); itr2.hasNext();) {
            Map.Entry<String, ExtensionElement> entry2 = itr2.next();
            String fullId = entry2.getKey();
            ExtensionElement element = entry2.getValue();
            if (!sorted.containsKey(fullId)) {
                log_.warn("Replace action replaces NOTHING: full-id=" + fullId
                    + ", element=" + element);
            } else {
                sorted.put(fullId, element);
            }
        }

        // 削除指定されたelementを除去する。
        for (Iterator<String> itr2 = removeSet.iterator(); itr2.hasNext();) {
            String fullId = itr2.next();
            if (!sorted.containsKey(fullId)) {
                log_.warn("Remove action removes NOTHING: full-id=" + fullId);
            } else {
                sorted.remove(fullId);
            }
        }

        return sorted.values();
    }


    @ForPreparingMode
    int assignElementId(ExtensionElement element, int autoNumber)
    {
        element.setId(String.valueOf(autoNumber++));
        return autoNumber;
    }


    @ForPreparingMode
    Map<String, ExtensionElement> sortExtensionElements(
        Map<String, ExtensionElement> elementMap)
    {
        Map<String, ExtensionElementDependant> dependantMap = new LinkedHashMap<String, ExtensionElementDependant>();

        // ダミーのDependantを追加しておく。
        ExtensionElementDependant first = new ExtensionElementDependant(
            ExtensionElement.ID_FIRST);
        dependantMap.put(ExtensionElement.ID_FIRST, first);
        ExtensionElementDependant last = new ExtensionElementDependant(
            ExtensionElement.ID_LAST);
        last.addRequirements(new ExtensionElementRequirement(
            ExtensionElement.ID_FIRST));
        dependantMap.put(ExtensionElement.ID_LAST, last);

        for (Iterator<ExtensionElement> itr = elementMap.values().iterator(); itr
            .hasNext();) {
            ExtensionElement element = itr.next();
            dependantMap.put(element.getFullId(),
                new ExtensionElementDependant(element));
        }
        for (Iterator<ExtensionElementDependant> itr = dependantMap.values()
            .iterator(); itr.hasNext();) {
            ExtensionElementDependant d = itr.next();
            ExtensionElement element = d.getExtensionElement();
            String before = (element != null) ? element.getBefore() : null;
            if (before != null) {
                String id = d.getId();
                StringTokenizer st = new StringTokenizer(before, ",");
                while (st.hasMoreTokens()) {
                    String beforeId = st.nextToken().trim();
                    ExtensionElementDependant beforeD = dependantMap
                        .get(beforeId);
                    if (beforeD == null) {
                        log_.error("Specified extension-element"
                            + " does not exist: id=" + beforeId + ", element="
                            + element);
                        continue;
                    }
                    beforeD
                        .addRequirements(new ExtensionElementRequirement(id));
                }
            }
        }

        Dependencies elementDependencies;
        try {
            elementDependencies = new Dependencies(dependantMap.values());
        } catch (LoopDetectedException ex) {
            throw new IllegalArgumentException(
                "Element loop detection: element (id="
                    + ex.getDependant().getId() + ") requires element (id="
                    + ex.getRequirement().getId() + ")");
        }
        Dependency[] ds = elementDependencies.getDependencies();
        Map<String, ExtensionElement> elemMap = new LinkedHashMap<String, ExtensionElement>();
        for (int i = 0; i < ds.length; i++) {
            ExtensionElementDependant ed = (ExtensionElementDependant)ds[i]
                .getSource();
            ExtensionElement e = ed.getExtensionElement();
            if (e != null) {
                elemMap.put(e.getFullId(), e);
            }
        }

        return elemMap;
    }


    @ForPreparingMode
    void gatherExtensionPoints(ExtensionPoint[] extensionPoints,
        Map<PluginLocalKey, ExtensionPoint> extensionPointMap)
    {
        for (int i = 0; i < extensionPoints.length; i++) {
            ExtensionPoint extensionPoint = extensionPoints[i];
            String pluginId = extensionPoint.getParent().getId();
            String id = extensionPoint.getId();
            if (id == null) {
                log_.error("id must be specified: " + extensionPoint
                    + ": plugin=" + pluginId);
                continue;
            }
            Class<?> elementClass = extensionPoint.getElementClass();
            if (elementClass == null) {
                log_.warn("element-class must be specified: " + extensionPoint
                    + ": plugin=" + pluginId);
                continue;
            }

            String fullId = extensionPoint.getFullId();
            if (extensionPointMap.containsKey(fullId)) {
                log_.warn("extension point is duplicated: id=" + fullId);
                continue;
            }
            extensionPointMap.put(generateKey(fullId), extensionPoint);

            bindClassToExtensionPoint(extensionPointMap, elementClass,
                pluginId, extensionPoint);

            bindClassToExtensionPoint(extensionPointMap, extensionPoint
                .getElementClassMetaData().getIsa(), pluginId, extensionPoint);
        }
    }


    PluginLocalKey generateKey(String pluginId, Object key)
    {
        return new PluginLocalKey(pluginId, key);
    }


    PluginLocalKey generateKey(String extensionPointId)
    {
        return new PluginLocalKey(null, extensionPointId);
    }


    @ForPreparingMode
    void bindClassToExtensionPoint(
        Map<PluginLocalKey, ExtensionPoint> extensionPointMap, Class<?> clazz,
        String pluginId, ExtensionPoint extensionPoint)
    {
        if (clazz == null) {
            return;
        }

        Class<?> c = clazz;
        while (c != null && c != Object.class) {
            bindSingleClassToExtensionPoint(extensionPointMap, c, pluginId,
                extensionPoint);
            c = c.getSuperclass();
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            bindSingleClassToExtensionPoint(extensionPointMap, interfaces[i],
                pluginId, extensionPoint);
        }
    }


    @ForPreparingMode
    void bindSingleClassToExtensionPoint(
        Map<PluginLocalKey, ExtensionPoint> extensionPointMap, Class<?> clazz,
        String pluginId, ExtensionPoint extensionPoint)
    {
        PluginLocalKey key = generateKey(pluginId, clazz);
        if (extensionPointMap.containsKey(key)) {
            ExtensionPoint registered = extensionPointMap.get(key);
            if (registered == extensionPoint) {
                // ExtensionElementがコンポーネントのインタフェースを実装している場合などに
                // registered == extensionPointとなるが、
                // これは重複登録ではないので何もしない。
            } else {
                // キーが既に登録されているので、後で重複エラーの検出ができるように
                // nullをputしておく。
                extensionPointMap.put(key, null);
            }
        } else {
            extensionPointMap.put(key, extensionPoint);
        }
    }


    @ForPreparingMode
    void filterResources(PluginDescriptor descriptor, PluginProperties prop)
    {
        Resource pluginDir = descriptor.getResolvedResourcesDirectory();
        Resource deployDir = descriptor.getRuntimeDeltaResourcesDirectory();
        TextTemplateEvaluator evaluator = new SimpleTextTemplateEvaluator(true);
        VariableResolver resolver = new PropertyHandlerVariableResolver(prop);

        Runtime runtime = descriptor.getRuntime();
        if (runtime != null) {
            Library[] libraries = runtime.getLibraries();
            for (int i = 0; i < libraries.length; i++) {
                Library library = libraries[i];
                String name = library.getName();
                if (library.isFilter()) {
                    Resource destination = deployDir.getChildResource(name);
                    ResourceUtils.delete(destination, true);
                    filterResource(pluginDir, name, deployDir, library
                        .getEncoding(), evaluator, resolver);
                }
            }
        }
    }


    @ForPreparingMode
    void filterResource(Resource pluginDir, String path, Resource deployDir,
        String encoding, TextTemplateEvaluator evaluator,
        VariableResolver resolver)
    {
        Resource resource = pluginDir.getChildResource(path);
        if (!resource.exists()) {
            return;
        } else if (resource.isDirectory()) {
            Resource dir = deployDir.getChildResource(path);
            dir.mkdirs();
            String[] names = resource.list();
            for (int i = 0; i < names.length; i++) {
                filterResource(pluginDir, path + "/" + names[i], deployDir,
                    encoding, evaluator, resolver);
            }
        } else {
            Resource destination = deployDir.getChildResource(path);
            KvasirUtils.filterResource(resource, destination, encoding,
                evaluator, resolver);
        }
    }


    @ForPreparingMode
    ClassLoaderPair createClassLoader(PluginDescriptor descriptor,
        Map<String, ClassLoaderPair> classLoaderPairMap)
    {
        if (log_.isDebugEnabled()) {
            log_.debug("createClassLoader: id=" + descriptor.getId());
        }

        // inner class loaderを生成する。

        boolean shouldReplacePluginLibrary = (developedPluginClassesDirectory_ != null && descriptor
            .getId().equals(developedPluginId_));
        boolean replaced = false;

        Map<URL, FilteredURL> furlMap = new LinkedHashMap<URL, FilteredURL>();

        Runtime runtime = descriptor.getRuntime();
        if (runtime != null) {
            Library[] libraries = runtime.getLibraries();
            for (int i = 0; i < libraries.length; i++) {
                String[] classPatterns = libraries[i].getExportClassPatterns();
                String[] resourcePatterns = (String[])ArrayUtil.add(
                    new String[] { RESOURCE_PATTERN_FOR_METAINF_EXCLUSION },
                    libraries[i].getExportResourcePatterns());
                URL[] urls = libraries[i].getURLsForURLClassLoader();
                for (int j = 0; j < urls.length; j++) {
                    if (shouldReplacePluginLibrary && !replaced
                        && isPluginLibrary(urls[j], descriptor.getId())) {
                        // 実行モードが統合テストなので、プラグインのライブラリ
                        // をビルドしたてのものと置き換える。
                        if (log_.isDebugEnabled()) {
                            log_.debug("Replace '" + urls[j] + "' into '"
                                + developedPluginClassesDirectory_ + "'");
                        }
                        urls[j] = ClassUtils
                            .getURLForURLClassLoader(developedPluginClassesDirectory_
                                .getURL());
                        replaced = true;
                    }

                    if (furlMap.containsKey(urls[j])) {
                        if (log_.isDebugEnabled()) {
                            log_.debug("[SKIP] Duplicate: " + urls[j]);
                        }
                        continue;
                    }
                    if (log_.isDebugEnabled()) {
                        log_.debug("Add to class loader: " + urls[j]);
                    }
                    furlMap.put(urls[j],
                        new FilteredURLClassLoader.FilteredURL(urls[j],
                            classPatterns, resourcePatterns));
                }
            }
        }
        if (descriptor.getId().equals(developedPluginId_)
            && developedPluginAdditinoalJars_ != null) {
            for (int i = 0; i < developedPluginAdditinoalJars_.length; i++) {
                URL url = ClassUtils
                    .getURLForURLClassLoader(developedPluginAdditinoalJars_[i]
                        .toFile());
                if (url == null) {
                    log_.warn("Jar not found: "
                        + developedPluginAdditinoalJars_[i]);
                }

                if (furlMap.containsKey(url)) {
                    if (log_.isDebugEnabled()) {
                        log_.debug("[SKIP] Duplicate: " + url);
                    }
                    continue;
                }
                if (log_.isDebugEnabled()) {
                    log_.debug("Add to class loader: " + url);
                }
                furlMap.put(url, new FilteredURLClassLoader.FilteredURL(url,
                    Library.PATTERNS_ALL, Library.PATTERNS_ALL));
            }
        }
        if (shouldReplacePluginLibrary && !replaced) {
            // Jarがない場合でもclassesをクラスローダに追加するようにする。
            // （+PLUSTを使った開発ではこういうことがあり得る。）
            URL classesURL = ClassUtils
                .getURLForURLClassLoader(developedPluginClassesDirectory_
                    .getURL());
            furlMap.put(classesURL, new FilteredURLClassLoader.FilteredURL(
                classesURL, Library.PATTERNS_ALL, Library.PATTERNS_ALL));
            if (log_.isDebugEnabled()) {
                log_.debug("Add classes directory to class loader: "
                    + developedPluginClassesDirectory_);
            }
        }

        List<ClassLoader> clList = new ArrayList<ClassLoader>();
        Dependency[] deps = descriptor.getDependency().getRequirements();
        for (int i = 0; i < deps.length; i++) {
            PluginDescriptor requirement = ((PluginDependant)deps[i]
                .getSource()).getDescriptor();
            ClassLoader requirementCl = classLoaderPairMap.get(
                requirement.getId()).getOuterClassLoader();
            if (log_.isDebugEnabled()) {
                log_.debug("Requirement: plugin-id=" + requirement.getId());
            }
            clList.add(requirementCl);
        }
        clList.add(kvasir_.getCommonClassLoader());
        ClassLoader parentCl = new CachedClassLoader(new CompositeClassLoader(
            clList.toArray(new ClassLoader[0])));

        ClassLoader innerCl;
        if (furlMap.size() == 0) {
            innerCl = parentCl;
        } else {
            //            innerCl = new ProxyClassLoader(new OverriddenURLClassLoader(furlMap
            //                .keySet().toArray(new URL[0]), parentCl));
            innerCl = new CachedClassLoader(new OverriddenURLClassLoader(
                furlMap.keySet().toArray(new URL[0]), parentCl));
        }

        // outer class loaderを生成する。

        ClassLoader outerCl;
        if (furlMap.size() == 0) {
            outerCl = kvasir_.getCommonClassLoader();
        } else {
            outerCl = new CachedClassLoader(new FilteredURLClassLoader(furlMap
                .values().toArray(new FilteredURLClassLoader.FilteredURL[0]),
                innerCl, kvasir_.getCommonClassLoader()));
        }

        return new ClassLoaderPair(innerCl, outerCl, parentCl);
    }


    @ForPreparingMode
    boolean isPluginLibrary(URL url, String pluginId)
    {
        String urlString = url.toExternalForm();
        if (!urlString.endsWith(JAR_URL_SUFFIX)) {
            return false;
        }
        int slash = urlString.lastIndexOf('/', urlString.length()
            - JAR_URL_SUFFIX.length());
        if (slash < 0) {
            return false;
        }
        return urlString.substring(slash + 1).equals(pluginId + JAR_URL_SUFFIX);
    }


    @ForPreparingMode
    ComponentContainer createComponentContainer(PluginDescriptor descriptor,
        Map<String, ComponentContainer> containerMap,
        ClassLoaderPair classLoaderPair)
    {
        if (log_.isDebugEnabled()) {
            log_.debug("createComponentContainer: id=" + descriptor.getId());
        }

        Dependency[] deps = descriptor.getDependency().getRequirements();
        Set<ComponentContainer> requirementSet = new LinkedHashSet<ComponentContainer>();
        ComponentContainer[] requirements = null;
        for (int i = 0; i < deps.length; i++) {
            PluginDescriptor requirement = ((PluginDependant)deps[i]
                .getSource()).getDescriptor();
            requirementSet.add(containerMap.get(requirement.getId()));
        }
        if (requirementSet.size() == 0) {
            // 依存するプラグインがないか、
            // 依存するどのプラグインもComponentContainerを持っていない
            // 場合はベースのComponentContainerを関連付けるようにする。
            ComponentContainer baseComponentContainer = kvasir_
                .getComponentContainer();
            // ベースのComponentContainerがnullになることは実際はないが、
            // テストの時にはあり得る。
            if (baseComponentContainer != null) {
                requirements = new ComponentContainer[] { baseComponentContainer };
            } else {
                requirements = new ComponentContainer[0];
            }
        } else {
            requirements = requirementSet.toArray(new ComponentContainer[0]);
        }
        String dicon = getDiconName(classLoaderPair);

        return ComponentContainerFactory.getInstance().createContainer(dicon,
            classLoaderPair.getInnerClassLoader(), requirements);
    }


    String getDiconName(ClassLoaderPair classLoaderPair)
    {
        // このプラグインで定義されたライブラリにdiconがない場合に依存プラグインのリソースを読まないようにしている。
        if (getResourceFromLocal(PLUGIN_DICON, classLoaderPair) != null) {
            return PLUGIN_DICON;
        } else if (getResourceFromLocal(PLUGIN_DICON_CLASS, classLoaderPair) != null) {
            return PLUGIN_DICON_CLASS;
        } else {
            return null;
        }
    }


    URL getResourceFromLocal(String path, ClassLoaderPair classLoaderPair)
    {
        ClassLoader cl = classLoaderPair.getInnerClassLoader();
        URL url = cl.getResource(path);
        if (url != null) {
            URL parentURL = classLoaderPair.getParentClassLoader().getResource(
                path);
            if (url.equals(parentURL)) {
                url = null;
            }
        }

        return url;
    }


    @ForPreparingMode
    void disableDependentPlugins(String pluginId, Dependencies deps)
    {
        Dependency dep = deps.getDependency(pluginId);
        dep.setDisabled();
        Dependency[] ads = dep.getAllDependants();
        for (int j = 0; j < ads.length; j++) {
            ((PluginDependant)ads[j].getSource()).getDescriptor()
                .setAsDisabled();
        }
    }


    @ForPreparingMode
    public void setPluginUpdater(final PluginUpdater pluginUpdater)
    {
        pluginUpdater_ = pluginUpdater;
    }


    public PluginUpdater getPluginUpdater()
    {
        return pluginUpdater_;
    }


    public PluginAlfrSettings getSettings()
    {
        return pluginAlfrSettings_;
    }


    public PluginAlfrSettings getSettingsForUpdate()
    {
        return readSettings();
    }


    public void storeSettings(final PluginAlfrSettings settings)
    {
        final Resource configurationDirectory = kvasir_
            .getConfigurationDirectory();
        final Resource systemDirectory = configurationDirectory
            .getChildResource(Globals.SYSTEM_DIR);
        if (!systemDirectory.exists()) {
            if (!systemDirectory.mkdirs()) {
                throw new RuntimeException("can't create directory ["
                    + systemDirectory.toFile() + "]");
            }
        }
        final Resource storeFile = systemDirectory
            .getChildResource(PluginAlfrImpl.SETTINGS_FILE_NAME);
        OutputStream os = null;
        try {
            os = storeFile.getOutputStream();
            XOMUtils.toXML(settings, os);
        } catch (final ResourceNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (final IOException ex) {
            throw new IORuntimeException(ex);
        } catch (ValidationException ex) {
            throw new RuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(os);
        }
        constructSettings();
    }


    public PluginDescriptor[] getAllPluginDescriptors()
    {
        return descriptors_;
    }


    public Plugin<?> getPluginUnderDevelopment()
    {
        return pluginUnderDevelopment_;
    }

}
