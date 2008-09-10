package org.seasar.kvasir.cms.manage.manage.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;

import org.seasar.framework.util.StringUtil;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.base.plugin.PluginAlfrSettings;
import org.seasar.kvasir.base.plugin.RemoteRepository;
import org.seasar.kvasir.cms.util.ServletUtils;


/**
 * @author manhole
 */
public class PluginRepositoryPage extends MainPanePage
{

    private PluginAlfr pluginAlfr_;

    private String repositoryId_;

    private String repositoryUrl_;

    private boolean repositoryEnabled_;

    private String targetRepositoryId_;

    private static final String SYSTEM_REPO_ID = "system-repo";


    // TODO レポジトリ変更

    public Object do_enterNewRepository()
    {
        return "/plugin-repository.new.html";
    }


    public Object do_addRepository()
    {
        final String urlString = getRepositoryUrl();
        final String repoId = getRepositoryId();
        // validate
        if (StringUtil.isBlank(repoId)) {
            setTransferredNotes(new Notes().add(new Note(
                "app.error.required.repositoryId", repoId)));
            return "/plugin.notes.html";
        }
        try {
            new URL(urlString);
        } catch (final MalformedURLException e) {
            setTransferredNotes(new Notes().add(new Note(
                "app.error.invalid.repositoryUrl", urlString)));
            return "/plugin.notes.html";
        }

        final PluginAlfr alfr = getPluginAlfr();
        final PluginAlfrSettings settings = alfr.getSettingsForUpdate();

        // 既にインストールされているID/URLの場合
        final List<RemoteRepository> remoteRepositories = getRepositories0(settings);
        for (final RemoteRepository repository : remoteRepositories) {
            if (repository.getUrl().equals(urlString)) {
                setTransferredNotes(new Notes().add(new Note(
                    "app.error.repositoryUrl.alreadyExists", urlString)));
                return "/plugin.notes.html";
            }
            if (repository.getRepositoryId().equals(repoId)) {
                setTransferredNotes(new Notes().add(new Note(
                    "app.error.repositoryId.alreadyExists", repoId)));
                return "/plugin.notes.html";
            }
        }

        final RemoteRepository remoteRepository = new RemoteRepository();
        remoteRepository.setRepositoryId(repoId);
        remoteRepository.setUrl(urlString);
        remoteRepository.setEnabled(true);
        settings.addRemoteRepository(remoteRepository);
        alfr.storeSettings(settings);
        return null;
    }


    public Object do_listRepositoties()
    {
        return "/plugin-repository.repositories.html";
    }


    public Object do_deleteRepository()
    {
        final String targetRepoId = getTargetRepositoryId();
        if (StringUtil.isBlank(targetRepoId)) {
            throw new IllegalStateException("targetRepositoryId is null");
        }
        final PluginAlfr alfr = getPluginAlfr();
        final PluginAlfrSettings settings = alfr.getSettingsForUpdate();
        final ArrayList<RemoteRepository> repos = new ArrayList<RemoteRepository>();
        final List<RemoteRepository> remoteRepositories = getRepositories0(settings);
        for (final RemoteRepository repository : remoteRepositories) {
            if (!targetRepoId.equals(repository.getRepositoryId())) {
                repos.add(repository);
            }
        }
        settings.setRemoteRepositories(repos.toArray(new RemoteRepository[repos
            .size()]));
        alfr.storeSettings(settings);
        return null;
    }


    public Object do_enterEditRepository()
    {
        final PluginAlfrSettings settings = getPluginAlfr().getSettings();
        final List<RemoteRepository> remoteRepositories = getRepositories0(settings);
        for (final RemoteRepository repository : remoteRepositories) {
            final String id = repository.getRepositoryId();
            if (id.equals(getRepositoryId())) {
                final String url = repository.getUrl();
                setRepositoryUrl(url);
                final boolean enabled = repository.isEnabled();
                setRepositoryEnabled(enabled);
                break;
            }
        }
        return "/plugin-repository.edit.html";
    }


    public Object do_updateRepository()
    {
        final String targetRepoId = getTargetRepositoryId();
        if (StringUtil.isBlank(targetRepoId)) {
            throw new IllegalStateException("targetRepositoryId is null");
        }
        final String repoId = getRepositoryId();
        if (StringUtil.isBlank(repoId)) {
            setTransferredNotes(new Notes().add(new Note(
                "app.error.required.repositoryId", repoId)));
            return "/plugin.notes.html";
        }
        final String repoUrl = getRepositoryUrl();
        try {
            new URL(repoUrl);
        } catch (final MalformedURLException e) {
            setTransferredNotes(new Notes().add(new Note(
                "app.error.invalid.repositoryUrl", repoUrl)));
            return "/plugin.notes.html";
        }
        final PluginAlfr alfr = getPluginAlfr();
        final PluginAlfrSettings settings = alfr.getSettingsForUpdate();
        final List<RemoteRepository> remoteRepositories = getRepositories0(settings);
        final ArrayList<RemoteRepository> repos = new ArrayList<RemoteRepository>();
        for (final RemoteRepository repository : remoteRepositories) {
            final String id = repository.getRepositoryId();
            if (id.equals(targetRepoId)) {
                repository.setRepositoryId(repoId);
                repository.setUrl(repoUrl);
                repository.setEnabled(isRepositoryEnabled());
            }
            repos.add(repository);
        }
        settings.setRemoteRepositories(repos.toArray(new RemoteRepository[repos
            .size()]));
        alfr.storeSettings(settings);
        return null;
    }


    public List<RemoteRepositotyDto> getRepositoties()
    {
        final PluginAlfrSettings settings = getPluginAlfr().getSettings();
        final List<RemoteRepositotyDto> list = new ArrayList<RemoteRepositotyDto>();
        for (final RemoteRepository repository : getRepositories0(settings)) {
            final String id = repository.getRepositoryId();
            final String url = repository.getUrl();
            final boolean enable = repository.isEnabled();
            final RemoteRepositotyDto dto = new RemoteRepositotyDto();
            dto.setId(id);
            dto.setUrl(url);
            dto.setEnabled(enable);
            list.add(dto);
        }
        return list;
    }


    private List<RemoteRepository> getRepositories0(
        final PluginAlfrSettings settings)
    {
        final String systemRepoUrl = constructSystemRepoUrl();

        final List<RemoteRepository> repos = new ArrayList<RemoteRepository>();
        boolean found = false;
        for (final RemoteRepository repo : settings.getRemoteRepositories()) {
            repos.add(repo);
            if (SYSTEM_REPO_ID.equals(repo.getRepositoryId())
                || systemRepoUrl.equals(repo.getUrl())) {
                found = true;
            }
        }
        if (!found) {
            final RemoteRepository systemRepo = new RemoteRepository();
            systemRepo.setEnabled(true);
            systemRepo.setRepositoryId(SYSTEM_REPO_ID);
            systemRepo.setUrl(systemRepoUrl);
            repos.add(systemRepo);
        }
        return repos;
    }


    private String constructSystemRepoUrl()
    {
        final String webappURL = ServletUtils.getWebappURL();
        final String systemRepoUrl = webappURL + "/system/maven2";
        return systemRepoUrl;
    }


    public PluginAlfr getPluginAlfr()
    {
        return pluginAlfr_;
    }


    public void setPluginAlfr(final PluginAlfr pluginAlfr)
    {
        pluginAlfr_ = pluginAlfr;
    }


    public String getRepositoryId()
    {
        return repositoryId_;
    }


    public void setRepositoryId(final String repositoryId)
    {
        repositoryId_ = repositoryId;
    }


    public String getRepositoryUrl()
    {
        return repositoryUrl_;
    }


    public void setRepositoryUrl(final String repositoryUrl)
    {
        repositoryUrl_ = repositoryUrl;
    }


    public boolean isRepositoryEnabled()
    {
        return repositoryEnabled_;
    }


    public void setRepositoryEnabled(boolean repositoryEnabled)
    {
        repositoryEnabled_ = repositoryEnabled;
    }


    public String getTargetRepositoryId()
    {
        return targetRepositoryId_;
    }


    public void setTargetRepositoryId(String beforeRepositoryId)
    {
        targetRepositoryId_ = beforeRepositoryId;
    }


    public boolean isSystemRepo()
    {
        return SYSTEM_REPO_ID.equals(repositoryId_);
    }


    public static class RemoteRepositotyDto
    {

        private String id_;

        private String url_;

        private boolean enabled_;


        public String getId()
        {
            return id_;
        }


        public void setId(final String id)
        {
            id_ = id;
        }


        public String getUrl()
        {
            return url_;
        }


        public void setUrl(final String name)
        {
            url_ = name;
        }


        public boolean isEnabled()
        {
            return enabled_;
        }


        public void setEnabled(final boolean enable)
        {
            enabled_ = enable;
        }

    }

}
