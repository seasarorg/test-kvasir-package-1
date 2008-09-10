package org.seasar.kvasir.base.plugin.descriptor.impl;

import static org.seasar.kvasir.base.plugin.Plugin.PROP_PLUGIN_CONFIGURATION_DIR;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.kvasir.base.Identifier;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.descriptor.Base;
import org.seasar.kvasir.base.plugin.descriptor.Extension;
import org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.descriptor.Requires;
import org.seasar.kvasir.base.plugin.descriptor.Runtime;
import org.seasar.kvasir.base.plugin.impl.PluginProperties;
import org.seasar.kvasir.base.util.StructuredPropertyUtils;
import org.seasar.kvasir.util.collection.I18NProperties;
import org.seasar.kvasir.util.collection.I18NPropertyHandler;
import org.seasar.kvasir.util.dependency.Dependency;
import org.seasar.kvasir.util.io.FileUtils;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceUtils;
import org.seasar.kvasir.util.io.impl.OverriddenResource;

import net.skirnir.xom.XOMapper;


public class PluginDescriptorImpl
    implements PluginDescriptor
{
    private static final String SETTINGS_NAME = "plugin-settings.xml";

    /**
     * 構造化プロパティを格納するためのディレクトリの相対パスです。
     */
    private static final String PATH_STRUCTURED_PROPERTY = "structures";

    /**
     * 構造化プロパティを保存する際のリソース名のサフィックスです。
     */
    private static final String SUFFIX_STRUCTURED_PROPERTY = ".xml";

    private Plugin<?> plugin_;

    private String xmlns_;

    private String xmlns_xsi_;

    private String xsi_schemaLocation_;

    private String id_;

    private String name_;

    private String versionString_;

    private Version version_;

    private String providerName_;

    private String className_;

    private Base base_;

    private Runtime runtime_;

    private Requires requires_;

    private List<Extension> extensionList_ = new ArrayList<Extension>();

    private List<ExtensionPoint> extensionPointList_ = new ArrayList<ExtensionPoint>();

    private Resource rawResourcesDirectory_;

    private Resource resolvedDeltaResourcesDirectory_;

    private Resource runtimeDeltaResourcesDirectory_;

    private Resource configurationBaseDirectory_;

    private Resource configurationDirectory_;

    private Resource resolvedResourcesDirectory_;

    private Resource runtimeResourcesDirectory_;

    private boolean disabled_;

    private boolean resolved_;

    private Dependency dependency_;

    private Identifier identifier_;


    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<plugin");
        if (id_ != null) {
            sb.append(" id=\"").append(id_).append("\"");
        }
        if (xmlns_ != null) {
            sb.append(" xmlns=\"").append(xmlns_).append("\"");
        }
        if (xmlns_xsi_ != null) {
            sb.append(" xmlns:xsi=\"").append(xmlns_xsi_).append("\"");
        }
        if (xsi_schemaLocation_ != null) {
            sb.append(" xsi:schemaLocation=\"").append(xsi_schemaLocation_)
                .append("\"");
        }
        if (name_ != null) {
            sb.append(" name=\"").append(name_).append("\"");
        }
        if (versionString_ != null) {
            sb.append(" version=\"").append(versionString_).append("\"");
        }
        if (providerName_ != null) {
            sb.append(" provider-name=\"").append(providerName_).append("\"");
        }
        if (className_ != null) {
            sb.append(" class-name=\"").append(className_).append("\"");
        }
        sb.append(">");
        if (base_ != null) {
            sb.append(base_);
        }
        if (runtime_ != null) {
            sb.append(runtime_);
        }
        if (requires_ != null) {
            sb.append(requires_);
        }
        for (Iterator<Extension> itr = extensionList_.iterator(); itr.hasNext();) {
            sb.append(itr.next());
        }
        for (Iterator<ExtensionPoint> itr = extensionPointList_.iterator(); itr
            .hasNext();) {
            sb.append(itr.next());
        }
        sb.append("</plugin>");
        return sb.toString();
    }


    public Plugin<?> getPlugin()
    {
        return plugin_;
    }


    public void setPlugin(Plugin<?> parent)
    {
        plugin_ = parent;
    }


    public String getXmlns()
    {
        return xmlns_;
    }


    public void setXmlns(String xmlns)
    {
        xmlns_ = xmlns;
    }


    public String getXmlns_xsi()
    {
        return xmlns_xsi_;
    }


    public void setXmlns_xsi(String xmlns_xsi)
    {
        xmlns_xsi_ = xmlns_xsi;
    }


    public String getXsi_schemaLocation()
    {
        return xsi_schemaLocation_;
    }


    public void setXsi_schemaLocation(String xsi_schemaLocation)
    {
        xsi_schemaLocation_ = xsi_schemaLocation;
    }


    public Extension[] getExtensions()
    {
        return extensionList_.toArray(new Extension[0]);
    }


    public void addExtension(Extension extension)
    {
        extensionList_.add(extension);
    }


    public ExtensionPoint[] getExtensionPoints()
    {
        return extensionPointList_.toArray(new ExtensionPoint[0]);
    }


    public void addExtensionPoint(ExtensionPoint extensionPoint)
    {
        extensionPointList_.add(extensionPoint);
    }


    public String getId()
    {
        return id_;
    }


    public void setId(String id)
    {
        id_ = id;
        if (version_ != null) {
            identifier_ = new Identifier(id_, version_);
        }
    }


    public String getName()
    {
        return name_;
    }


    public void setName(String name)
    {
        name_ = name;
    }


    public String getProviderName()
    {
        return providerName_;
    }


    public void setProviderName(String providerName)
    {
        providerName_ = providerName;
    }


    public Base getBase()
    {
        return base_;
    }


    public void setBase(Base base)
    {
        base_ = base;
    }


    public Requires getRequires()
    {
        return requires_;
    }


    public void setRequires(Requires requires)
    {
        requires_ = requires;
    }


    public Runtime getRuntime()
    {
        return runtime_;
    }


    public void setRuntime(Runtime runtime)
    {
        runtime_ = runtime;
    }


    public String getVersionString()
    {
        return versionString_;
    }


    public void setVersionString(String versionString)
    {
        versionString_ = versionString;
        version_ = new Version(versionString);
        if (id_ != null) {
            identifier_ = new Identifier(id_, version_);
        }
    }


    public Version getVersion()
    {
        return version_;
    }


    public void setSystemDirectories(Resource rawResourcesDirectory,
        Resource pluginsConfigurationDirectory, Resource runtimeWorkDirectory)
    {
        setSystemDirectories(id_, rawResourcesDirectory,
            pluginsConfigurationDirectory, runtimeWorkDirectory);
    }


    public void setSystemDirectories(String id, Resource rawResourcesDirectory,
        Resource pluginsConfigurationDirectory, Resource runtimeWorkDirectory)
    {
        setRawResourcesDirectory(rawResourcesDirectory);
        setResolvedDeltaResourcesDirectory(runtimeWorkDirectory
            .getChildResource(id_ + "/static"));
        setRuntimeDeltaResourcesDirectory(runtimeWorkDirectory
            .getChildResource(id_ + "/runtime"));
        setConfigurationBaseDirectory(pluginsConfigurationDirectory
            .getChildResource(id_));
        configurationBaseDirectory_.mkdirs();
        setConfigurationDirectory(configurationBaseDirectory_
            .getChildResource("default"));
        configurationDirectory_.mkdir();
    }


    protected void setConfigurationDirectory(Resource configurationDirectory)
    {
        configurationDirectory_ = configurationDirectory;
    }


    protected void setConfigurationBaseDirectory(
        Resource configurationBaseDirectory)
    {
        configurationBaseDirectory_ = configurationBaseDirectory;
    }


    protected void setRuntimeDeltaResourcesDirectory(
        Resource runtimeDeltaResourcesDirectory)
    {
        runtimeDeltaResourcesDirectory_ = runtimeDeltaResourcesDirectory;
    }


    protected void setResolvedDeltaResourcesDirectory(
        Resource resolvedDeltaResourcesDirectory)
    {
        resolvedDeltaResourcesDirectory_ = resolvedDeltaResourcesDirectory;
    }


    protected void setRawResourcesDirectory(Resource rawResourcesDirectory)
    {
        rawResourcesDirectory_ = rawResourcesDirectory;
    }


    public void cleanupSystemDirectories()
    {
        resolvedDeltaResourcesDirectory_.mkdirs();
        ResourceUtils.deleteChildren(resolvedDeltaResourcesDirectory_);
        runtimeDeltaResourcesDirectory_.mkdirs();
        ResourceUtils.deleteChildren(runtimeDeltaResourcesDirectory_);
    }


    public Resource getConfigurationBaseDirectory()
    {
        return configurationBaseDirectory_;
    }


    public Resource getConfigurationDirectory()
    {
        return configurationDirectory_;
    }


    public Resource getRawResourcesDirectory()
    {
        return rawResourcesDirectory_;
    }


    public Resource getResolvedDeltaResourcesDirectory()
    {
        return resolvedDeltaResourcesDirectory_;
    }


    public Resource getRuntimeDeltaResourcesDirectory()
    {
        return runtimeDeltaResourcesDirectory_;
    }


    public Resource getResolvedResourcesDirectory()
    {
        return resolvedResourcesDirectory_;
    }


    public void setResolvedResourcesDirectory(
        Resource resolvedResourcesDirectory)
    {
        resolvedResourcesDirectory_ = resolvedResourcesDirectory;
        setRuntimeResourcesDirectory(new OverriddenResource(
            resolvedResourcesDirectory, runtimeDeltaResourcesDirectory_));
    }


    protected void setRuntimeResourcesDirectory(
        Resource runtimeResourcesDirectory)
    {
        runtimeResourcesDirectory_ = runtimeResourcesDirectory;
    }


    public Resource getRuntimeResourcesDirectory()
    {
        return runtimeResourcesDirectory_;
    }


    /**
     * プラグインが無効であるかどうかを返します。
     * <p>「プラグインが有効である」というのは、具体的には次の全ての条件を満たすことを言います。
     * <ul>
     * <li>認識可能な同一IDのプラグインのうち、バージョン番号が最も新しい。</li>
     * <li>プロパティ「enabled」の値が「false」ではない。</li>
     * <li>Pluginインスタンスを正常に作成できた。</li>
     * <li>依存している全てのプラグインが有効である。</li>
     * <li>プラグインの開始処理が正常に終了した。</li>
     * <li>全てのプラグインの開始処理が終了した後のPlugin#notifyKvasirStarted()の呼び出しが正常に終了した。</li>
     * </ul>
     * 上の条件を1つでも満たさない場合は、そのプラグインは無効です。
     * </p>
     *
     * @return 無効かどうか。
     */
    public boolean isDisabled()
    {
        return disabled_;
    }


    public void setAsDisabled()
    {
        disabled_ = true;
    }


    public Dependency getDependency()
    {
        return dependency_;
    }


    public void setDependency(Dependency dependency)
    {
        dependency_ = dependency;
    }


    public Identifier getIdentifier()
    {
        return identifier_;
    }


    public boolean isResolved()
    {
        return resolved_;
    }


    public void setAsResolved()
    {
        resolved_ = true;
    }


    public PluginProperties loadPluginProperties()
    {
        I18NPropertyHandler ph = new I18NProperties(
            resolvedResourcesDirectory_, PROPERTIES_BASENAME, PROPERTIES_SUFFIX);
        ph.setProperty(PROP_PLUGIN_CONFIGURATION_DIR, FileUtils
            .toAbstractPath(configurationDirectory_.toFile().getPath()));
        return new PluginProperties(getId(), new I18NProperties(
            getConfigurationBaseDirectory(), PROPERTIES_BASENAME,
            PROPERTIES_SUFFIX), ph);
    }


    public <T> T loadSettings(Class<T> settingsClass)
    {
        T settings = getStructuredProperty(getCustomSettingsResource(),
            settingsClass);
        if (settings == null) {
            settings = getStructuredProperty(getResolvedResourcesDirectory()
                .getChildResource(SETTINGS_NAME), settingsClass);
        }
        return settings;
    }


    public <T> T getStructuredProperty(String name, Class<T> structureClass)
    {
        return getStructuredProperty(getStructuredPropertyResource(name),
            structureClass);
    }


    public synchronized <T> T getStructuredProperty(
        Resource structuredResource, Class<T> structureClass)
    {
        return StructuredPropertyUtils.getStructuredProperty(
            structuredResource, structureClass);
    }


    Resource getStructuredPropertyResource(String name)
    {
        return configurationBaseDirectory_
            .getChildResource(PATH_STRUCTURED_PROPERTY + "/" + name
                + SUFFIX_STRUCTURED_PROPERTY);
    }


    public void storeSettings(Object settings)
    {
        setStructuredProperty(getCustomSettingsResource(), settings);
    }


    Resource getCustomSettingsResource()
    {
        return configurationBaseDirectory_.getChildResource(SETTINGS_NAME);
    }


    public synchronized void resetSettings()
    {
        Resource settingsResource = getCustomSettingsResource();
        if (settingsResource.exists()) {
            settingsResource.delete();
        }
    }


    public void setStructuredProperty(String name, Object structure)
    {
        setStructuredProperty(getStructuredPropertyResource(name), structure);
    }


    public synchronized void setStructuredProperty(Resource structuredResource,
        Object structure)
    {
        StructuredPropertyUtils.setStructuredProperty(structuredResource,
            structure);
    }


    /*
     * Merger
     */

    public void merge(Object bean, Object merged, XOMapper mapper)
    {
        // baseを空にする以外は通常のmergeと同じ。
        mapper.mergeAttributes(this, bean, merged);
        mapper.mergeChildren(this, bean, merged);
        ((PluginDescriptor)merged).setBase(null);
    }
}
