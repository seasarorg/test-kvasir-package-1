package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.List;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;

import org.seasar.framework.util.StringUtil;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.base.plugin.PluginCandidate;
import org.seasar.kvasir.base.plugin.PluginUpdater;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;


/**
 * @author manhole
 */
public class PluginInstallPage extends MainPanePage
{

    private PluginAlfr pluginAlfr_;

    private PluginUpdater pluginUpdater_;

    private String pluginId_;

    private String pluginVersion_;

    private PluginCandidate installCandidate_;

    private boolean excludeSnapshot_;


    public PluginAlfr getPluginAlfr()
    {
        return pluginAlfr_;
    }


    public void setPluginAlfr(final PluginAlfr pluginAlfr)
    {
        pluginAlfr_ = pluginAlfr;
    }


    public Object do_execute()
    {
        return "/plugin-install.search.html";
    }


    public Object do_installPlugin()
    {
        final String id = getPluginId();
        if (StringUtil.isBlank(id)) {
            return null;
        }
        final String version = getPluginVersion();
        if (StringUtil.isBlank(version)) {
            return null;
        }
        final PluginUpdater updater = getPluginUpdater();
        updater.installPlugin(id, version, isExcludeSnapshot());
        setTransferredNotes(new Notes().add(new Note(
            "app.plugin.install.succeed", id, version)));
        return "/plugin.notes.html";
    }


    public Object do_searchPluginForInstall()
    {
        final String id = getPluginId();
        if (StringUtil.isBlank(id)) {
            setTransferredNotes(new Notes().add(new Note(
                "app.error.required.pluginId", id)));
            return "/plugin-install.search.html";
        }
        final PluginUpdater updater = getPluginUpdater();

        // インストール済みな場合
        if (updater.containsPlugin(id)) {
            setTransferredNotes(new Notes().add(new Note(
                "app.error.plugin.alreadyInstalled", id)));
            return "/plugin-install.search.html";
        }

        final boolean excludeSnapshot = isExcludeSnapshot();
        final List<Version> versions = updater.getAvailableVersions(id,
            excludeSnapshot);
        if (versions.isEmpty()) {
            // そんなプラグインありません
            setTransferredNotes(new Notes().add(new Note(
                "app.error.plugin.notFoundInRepositories", id)));
            return "/plugin-install.search.html";
        }

        final Version installVersion = versions.get(versions.size() - 1);
        //updater.installPlugin(id, installVersion.getString());
        //setTransferredNotes(new Notes().add(new Note(
        //    "app.plugin.install.confirm", id, installVersion.getString())));
        final String v = installVersion.getString();
        setPluginVersion(v);

        installCandidate_ = updater.getInstallCandidate(id, v, excludeSnapshot);

        return "/plugin-install.confirm.html";
    }


    public List<PluginDto> getPlugins()
    {
        final List<PluginDto> list = new ArrayList<PluginDto>();
        for (final Plugin<?> plugin : getPluginAlfr().getPlugins()) {
            final PluginDto dto = new PluginDto();
            final PluginDescriptor descriptor = plugin.getDescriptor();
            final String name = descriptor.getName();
            final String resolvedName = plugin.resolveString(name);
            dto.setId(plugin.getId());
            dto.setName(resolvedName);
            dto.setVersion(descriptor.getVersionString());
            list.add(dto);
        }
        return list;
    }


    public PluginUpdater getPluginUpdater()
    {
        return pluginUpdater_;
    }


    public void setPluginUpdater(final PluginUpdater pluginUpdater)
    {
        pluginUpdater_ = pluginUpdater;
    }


    public String getPluginId()
    {
        return pluginId_;
    }


    public void setPluginId(final String pluginId)
    {
        pluginId_ = pluginId;
    }


    public String getPluginVersion()
    {
        return pluginVersion_;
    }


    public void setPluginVersion(final String pluginVersion)
    {
        pluginVersion_ = pluginVersion;
    }


    public PluginCandidate getInstallCandidate()
    {
        return installCandidate_;
    }


    public boolean isExcludeSnapshot()
    {
        return excludeSnapshot_;
    }


    public void setExcludeSnapshot(final boolean ignoreSnapshot)
    {
        excludeSnapshot_ = ignoreSnapshot;
    }


    public static class PluginDto
    {

        private String id;

        private String name;

        private String version;


        public String getId()
        {
            return id;
        }


        public void setId(final String id)
        {
            this.id = id;
        }


        public String getName()
        {
            return name;
        }


        public void setName(final String name)
        {
            this.name = name;
        }


        public String getVersion()
        {
            return version;
        }


        public void setVersion(final String version)
        {
            this.version = version;
        }

    }

}
