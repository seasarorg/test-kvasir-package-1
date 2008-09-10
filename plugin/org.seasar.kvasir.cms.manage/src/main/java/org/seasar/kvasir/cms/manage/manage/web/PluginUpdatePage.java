package org.seasar.kvasir.cms.manage.manage.web;

import java.util.ArrayList;
import java.util.List;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;

import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.base.plugin.PluginUpdater;
import org.seasar.kvasir.base.plugin.PluginVersions;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;


/**
 * @author manhole
 */
public class PluginUpdatePage extends MainPanePage
{

    private PluginAlfr pluginAlfr_;

    private PluginUpdater pluginUpdater_;

    private List<PluginDto> plugins_;

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
        return "/plugin-update.html";
    }


    public List<PluginDto> getPlugins()
    {
        return plugins_;
    }


    public Object do_listPlugins()
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
            //dto.setStatus("");
            list.add(dto);
        }
        plugins_ = list;
        return "/plugin-update.plugins.html";
    }


    public Object do_searchPluginForUpdate()
    {
        final List<PluginDto> list = findUpdatedPlugins();
        plugins_ = list;
        if (list.isEmpty()) {
            setTransferredNotes(new Notes().add(new Note(
                "app.note.plugin.noUpdateFound")));
        }
        return "/plugin-update.updatePlugins.html";
    }


    private List<PluginDto> findUpdatedPlugins()
    {
        final PluginVersions[] updatedPlugins = getPluginUpdater()
            .getUpdatedPlugins(isExcludeSnapshot());
        final List<PluginDto> list = new ArrayList<PluginDto>();
        for (final Plugin<?> plugin : getPluginAlfr().getPlugins()) {
            final PluginDto dto = new PluginDto();
            final PluginDescriptor descriptor = plugin.getDescriptor();
            final String name = descriptor.getName();
            final String resolvedName = plugin.resolveString(name);
            dto.setId(plugin.getId());
            dto.setName(resolvedName);
            final PluginVersions matches = getMatches(updatedPlugins, plugin);
            if (matches != null) {
                final Version[] versions = matches.getVersions();
                dto.setVersion(versions[versions.length - 1].getString());
                list.add(dto);
            }
        }
        return list;
    }


    public Object do_updatePlugin()
    {
        final List<PluginDto> list = findUpdatedPlugins();
        final PluginUpdater updater = getPluginUpdater();
        for (final PluginDto dto : list) {
            updater.installPlugin(dto.getId(), dto.getVersion(),
                isExcludeSnapshot());
        }

        setTransferredNotes(new Notes().add(new Note(
            "app.plugin.update.succeed")));
        return "/plugin.notes.html";
    }


    private PluginVersions getMatches(final PluginVersions[] updatedPlugins,
        final Plugin<?> plugin)
    {
        for (final PluginVersions pluginVersions : updatedPlugins) {
            if (plugin.getId().equals(pluginVersions.getId())) {
                return pluginVersions;
            }
        }
        return null;
    }


    public PluginUpdater getPluginUpdater()
    {
        return pluginUpdater_;
    }


    public void setPluginUpdater(final PluginUpdater pluginUpdater)
    {
        pluginUpdater_ = pluginUpdater;
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
