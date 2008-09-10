package org.seasar.kvasir.base.plugin;

import java.util.List;

import org.seasar.kvasir.base.Version;


/**
 * @author manhole
 */
public interface PluginUpdater
{

    void installPlugin(String pluginId, String version, boolean excludeSnapshot);


    List<Version> getAvailableVersions(String pluginId, boolean excludeSnapshot);


    /**
     * プラグインが既にKvasirへインストールされているかを判定します。
     */
    boolean containsPlugin(String pluginId);


    PluginCandidate getInstallCandidate(String pluginId, String version,
        boolean excludeSnapshot);


    PluginVersions[] getUpdatedPlugins(boolean ignoreSnapshot);

}
