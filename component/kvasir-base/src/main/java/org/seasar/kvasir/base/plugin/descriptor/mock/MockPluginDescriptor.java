package org.seasar.kvasir.base.plugin.descriptor.mock;

import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.plugin.descriptor.impl.PluginDescriptorImpl;
import org.seasar.kvasir.util.io.Resource;


/**
 * @author manhole
 * @author YOKOTA Takehiko
 */
/*
 * 必要に応じて実装を加えるべし。プロパティとか。
 */
public class MockPluginDescriptor extends PluginDescriptorImpl
{
    public MockPluginDescriptor()
    {
    }


    public MockPluginDescriptor(Resource pluginDirectory,
        Resource configurationBaseDirectory)
    {
        setSystemDirectories(pluginDirectory, configurationBaseDirectory);
    }


    /**
     * システム用のディレクトリを設定します。
     *
     * @param pluginDirectory プラグインのインストールディレクトリ。
     * 通常は<code>KVASIR_HOME/plugins/PLUGIN_ID</code>ですが、
     * テスト用であれば<code>PROJECT_DIRECTORY/src/main/plugin</code>
     * のようになるでしょう。
     * @param configurationBaseDirectory 対象であるプラグインのためのconfigurationディレクトリ。
     * 通常は<code>KVASIR_HOME/configuration/plugins/PLUGIN_ID</code>ですが、
     * テスト用であれば<code>PROJECT_DIRECTORY/build/test-home/configuration/plugins/PLUGIN_ID</code>
     * のようになるでしょう。
     */
    public void setSystemDirectories(Resource pluginDirectory,
        Resource configurationBaseDirectory)
    {
        setRawResourcesDirectory(pluginDirectory);
        setResolvedDeltaResourcesDirectory(pluginDirectory);
        setRuntimeDeltaResourcesDirectory(pluginDirectory);
        setResolvedResourcesDirectory(pluginDirectory);
        setRuntimeResourcesDirectory(pluginDirectory);

        setConfigurationBaseDirectory(configurationBaseDirectory);
        Resource configurationDirectory = configurationBaseDirectory
            .getChildResource("default");
        setConfigurationDirectory(configurationDirectory);
        // ディレクトリがない場合は作る。
        configurationDirectory.mkdirs();
    }


    @Override
    public void setSystemDirectories(Resource rawResourcesDirectory,
        Resource pluginsConfigurationDirectory, Resource runtimeWorkDirectory)
    {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setSystemDirectories(String id, Resource rawResourcesDirectory,
        Resource pluginsConfigurationDirectory, Resource runtimeWorkDirectory)
    {
        throw new UnsupportedOperationException();
    }


    @Override
    public void cleanupSystemDirectories()
    {
        Resource configurationBaseDirectory = getConfigurationBaseDirectory();
        if (configurationBaseDirectory != null) {
            configurationBaseDirectory.delete();
            configurationBaseDirectory.mkdirs();
        }
    }


    public void setVersion(Version version)
    {
        setVersionString(version != null ? version.getString() : null);
    }
}
