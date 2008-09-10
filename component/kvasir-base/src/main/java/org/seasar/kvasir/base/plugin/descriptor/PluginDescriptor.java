package org.seasar.kvasir.base.plugin.descriptor;

import org.seasar.kvasir.base.Identifier;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.descriptor.impl.PluginDescriptorImpl;
import org.seasar.kvasir.base.plugin.impl.PluginProperties;
import org.seasar.kvasir.util.dependency.Dependency;
import org.seasar.kvasir.util.io.Resource;

import net.skirnir.xom.Merger;
import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Required;


@Bean(name = "plugin", implementation = PluginDescriptorImpl.class)
public interface PluginDescriptor
    extends Merger
{
    String PROPERTIES_BASENAME = "plugin";

    String PROPERTIES_SUFFIX = ".xproperties";


    Plugin<?> getPlugin();


    void setPlugin(Plugin<?> parent);


    String getXmlns();


    @Attribute("xmlns")
    void setXmlns(String xmlns);


    String getXmlns_xsi();


    @Attribute("xmlns:xsi")
    void setXmlns_xsi(String xmlns_xsi);


    String getXsi_schemaLocation();


    @Attribute("xsi:schemaLocation")
    void setXsi_schemaLocation(String xsi_schemaLocation);


    Extension[] getExtensions();


    @Child
    void addExtension(Extension extension);


    ExtensionPoint[] getExtensionPoints();


    @Child
    void addExtensionPoint(ExtensionPoint extensionPoint);


    String getId();


    @Attribute
    @Required
    void setId(String id);


    String getName();


    @Attribute
    void setName(String name);


    String getProviderName();


    @Attribute
    void setProviderName(String providerName);


    Base getBase();


    @Child
    void setBase(Base base);


    Requires getRequires();


    @Child
    void setRequires(Requires requires);


    Runtime getRuntime();


    @Child
    void setRuntime(Runtime runtime);


    String getVersionString();


    @Attribute("version")
    @Required
    void setVersionString(String versionString);


    Version getVersion();


    void setSystemDirectories(Resource rawResourcesDirectory,
        Resource pluginsConfigurationDirectory, Resource runtimeWorkDirectory);


    void setSystemDirectories(String id, Resource rawResourcesDirectory,
        Resource pluginsConfigurationDirectory, Resource runtimeWorkDirectory);


    void cleanupSystemDirectories();


    Resource getConfigurationBaseDirectory();


    Resource getConfigurationDirectory();


    Resource getRawResourcesDirectory();


    Resource getResolvedDeltaResourcesDirectory();


    Resource getRuntimeDeltaResourcesDirectory();


    Resource getResolvedResourcesDirectory();


    void setResolvedResourcesDirectory(Resource resolvedResourcesDirectory);


    Resource getRuntimeResourcesDirectory();


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
    boolean isDisabled();


    void setAsDisabled();


    Dependency getDependency();


    void setDependency(Dependency dependency);


    Identifier getIdentifier();


    boolean isResolved();


    void setAsResolved();


    PluginProperties loadPluginProperties();


    <T> T loadSettings(Class<T> settingsClass);


    <T> T getStructuredProperty(String name, Class<T> structureClass);


    <T> T getStructuredProperty(Resource structuredResource,
        Class<T> structureClass);


    void storeSettings(Object settings);


    void resetSettings();


    void setStructuredProperty(String name, Object structure);


    void setStructuredProperty(Resource structuredResource, Object structure);
}