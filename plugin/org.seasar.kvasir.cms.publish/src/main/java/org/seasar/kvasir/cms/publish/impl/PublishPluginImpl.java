package org.seasar.kvasir.cms.publish.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.maven.wagon.CommandExecutor;
import org.apache.maven.wagon.ConnectionException;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.WagonException;
import org.apache.maven.wagon.authentication.AuthenticationException;
import org.apache.maven.wagon.authentication.AuthenticationInfo;
import org.apache.maven.wagon.providers.ssh.SshWagon;
import org.apache.maven.wagon.providers.ssh.knownhost.SingleKnownHostProvider;
import org.apache.maven.wagon.repository.Repository;
import org.codehaus.plexus.util.IOUtil;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.plugin.SettingsEvent;
import org.seasar.kvasir.base.plugin.SettingsListener;
import org.seasar.kvasir.cms.publish.PublishPlugin;
import org.seasar.kvasir.cms.publish.PublishRuntimeException;
import org.seasar.kvasir.cms.publish.setting.DistributionGroupElement;
import org.seasar.kvasir.cms.publish.setting.PageElement;
import org.seasar.kvasir.cms.publish.setting.PublishPluginSettings;
import org.seasar.kvasir.cms.publish.setting.RepositoryElement;
import org.seasar.kvasir.cms.publish.setting.ServerElement;
import org.seasar.kvasir.cms.util.ServletUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.ability.PermissionAbility;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.webapp.util.Response;

public class PublishPluginImpl extends AbstractPlugin<PublishPluginSettings>
        implements PublishPlugin {
    private static final String SUFFIX_WAGON = "Wagon";

    private static final String NAME_INDEX = "index.html";

    private Map<String, RepositoryElement> repositoryMap = new LinkedHashMap<String, RepositoryElement>();

    private Map<String, Repository> wagonRepositoryMap = new LinkedHashMap<String, Repository>();

    private Map<String, DistributionGroupElement> distributionGroupMap = new LinkedHashMap<String, DistributionGroupElement>();

    @Binding(bindingType = BindingType.MUST)
    protected PagePlugin pagePlugin;

    protected KvasirLog log = KvasirLogFactory.getLog(getClass());

    @Override
    public Class<PublishPluginSettings> getSettingsClass() {
        return PublishPluginSettings.class;
    }

    protected boolean doStart() {
        addSettingsListener(new SettingsListener<PublishPluginSettings>() {
            public void notifyUpdated(SettingsEvent<PublishPluginSettings> event) {
                prepareForRepositories();
            }
        });

        return true;
    }

    void prepareForRepositories() {
        repositoryMap.clear();
        wagonRepositoryMap.clear();
        distributionGroupMap.clear();

        PublishPluginSettings settings = getSettings();

        for (RepositoryElement repository : settings.getRepositories()) {
            repositoryMap.put(repository.getId(), repository);
            wagonRepositoryMap.put(repository.getId(), new Repository(
                    repository.getId(), repository.getUrl()));
        }

        for (DistributionGroupElement distributionGroup : settings
                .getDistributionGroups()) {
            distributionGroupMap.put(distributionGroup.getId(),
                    distributionGroup);
        }
    }

    protected void doStop() {
        repositoryMap.clear();
        wagonRepositoryMap.clear();
        distributionGroupMap.clear();
    }

    public RepositoryElement getRepository(String id) {
        return repositoryMap.get(id);
    }

    public List<RepositoryElement> getRepositories() {
        return new ArrayList<RepositoryElement>(repositoryMap.values());
    }

    Repository getWagonRepository(String id) {
        return wagonRepositoryMap.get(id);
    }

    public DistributionGroupElement getDistributionGroup(String id) {
        return distributionGroupMap.get(id);
    }

    public List<DistributionGroupElement> getDistributionGroups() {
        return new ArrayList<DistributionGroupElement>(distributionGroupMap
                .values());
    }

    public void publish(String distributionGroupId, boolean optimize) {
        DistributionGroupElement distributionGroup = getDistributionGroup(distributionGroupId);
        if (distributionGroup == null) {
            throw new PublishRuntimeException("Unknown distributionGroupId: "
                    + distributionGroupId);
        }

        PageAlfr pageAlfr = pagePlugin.getPageAlfr();
        User anonymousUser = pageAlfr.getPage(User.class,
                User.ID_ANONYMOUS_USER);

        List<Page> pages = new ArrayList<Page>();
        Set<Integer> recursives = new HashSet<Integer>();
        for (PageElement element : distributionGroup.getPages()) {
            Page page = pageAlfr.getPage(element.getHeimId(), element
                    .getPathname());
            if (page == null) {
                throw new PublishRuntimeException("Page not found: heimId="
                        + element.getHeimId() + ", pathname="
                        + element.getPathname());
            }
            if (!page.getAbility(PermissionAbility.class).permits(
                    anonymousUser, Privilege.ACCESS_VIEW)) {
                continue;
            }

            pages.add(page);
            if (element.isRecursive()) {
                recursives.add(page.getId());
            }
        }

        publish(pages, recursives, distributionGroup.getRepositoryId(),
                optimize);
    }

    public void publish(Page page, boolean recursive, String repositoryId,
            boolean optimize) {
        publish(Arrays.asList(new Page[] { page }), new HashSet<Integer>(Arrays
                .asList(new Integer[] { page.getId() })), repositoryId,
                optimize);
    }

    public void publish(Collection<Page> pages, Set<Integer> recursives,
            String repositoryId, boolean optimize) {
        if (pages.isEmpty()) {
            return;
        }

        Repository repository = getWagonRepository(repositoryId);
        if (repository == null) {
            throw new PublishRuntimeException("Unknown repositoryId: "
                    + repositoryId);
        }

        Wagon wagon = getWagon(repository);
        if (wagon == null) {
            throw new PublishRuntimeException("Unsupported repository: "
                    + repository.getUrl());
        }

        try {
            wagon.connect(repository, getAuthenticationInfo(repository));
        } catch (ConnectionException ex) {
            throw new PublishRuntimeException(ex);
        } catch (AuthenticationException ex) {
            throw new PublishRuntimeException(ex);
        }

        try {
            publish(wagon, pages, recursives, optimize);
        } catch (WagonException ex) {
            throw new PublishRuntimeException(ex);
        } finally {
            try {
                wagon.disconnect();
            } catch (ConnectionException ignore) {
            }
        }
    }

    AuthenticationInfo getAuthenticationInfo(Repository repository) {
        String hostName = repository.getHost();
        for (ServerElement server : getSettings().getServers()) {
            if (hostName.equals(server.getHostName())) {
                AuthenticationInfo authenticationInfo = new AuthenticationInfo();
                authenticationInfo.setUserName(server.getUserName());
                authenticationInfo.setPassword(server.getPassword());
                authenticationInfo.setPassphrase(server.getPassphrase());
                authenticationInfo.setPrivateKey(server.getPrivateKey());
                return authenticationInfo;
            }
        }
        return null;
    }

    Wagon getWagon(Repository repository) {
        String url = repository.getUrl();
        int colon = url.indexOf(':');
        if (colon < 0) {
            return null;
        }
        String key = url.substring(0, colon) + SUFFIX_WAGON;
        try {
            Wagon wagon = (Wagon) getComponentContainer().getComponent(key);
            if (wagon instanceof SshWagon) {
                SshWagon sshWagon = (SshWagon) wagon;
                SingleKnownHostProvider knownHostsProvider = new SingleKnownHostProvider();
                knownHostsProvider.setHostKeyChecking("no");
                sshWagon.setKnownHostsProvider(knownHostsProvider);
            }
            return wagon;
        } catch (Throwable t) {
            log.warn("Cannot get Wagon component: " + key, t);
            return null;
        }
    }

    void publish(Wagon wagon, Page page, boolean recursive)
            throws WagonException {
        // TODO concealedされている時下層をpublishしないようにしたほうが合理的かもしれない…。
        if (!page.isConcealed()) {
            do {
                String relativeDestPath = page.getPathname();

                if (page.isNode()) {
                    if (page.getChild(NAME_INDEX) == null) {
                        relativeDestPath += "/" + NAME_INDEX;
                    } else {
                        break;
                    }
                }

                relativeDestPath = relativeDestPath
                        .substring(1/* = "/".length() */);

                // FIXME ディレクトリでなく子ページを持つ、HTMLなページについては
                // hrefとsrcの中のパスが子ページである場合にディレクトリをずらすようにしよう。
                // この対応が終わったら子ページをpublishするようにすること。
                Response response = ServletUtils.getResponse(ServletUtils
                        .getURL(page));
                File source = null;
                try {
                    source = File.createTempFile("kvasir", ".bin");

                    IOUtils.pipe(response.getInputStream(),
                            new FileOutputStream(source));

                    wagon.put(source, relativeDestPath);
                } catch (IOException ex) {
                    throw new PublishRuntimeException(ex);
                } finally {
                    if (source != null) {
                        source.delete();
                    }
                }
            } while (false);
        }

        if (recursive && page.isNode()) {
            User anonymousUser = pagePlugin.getPageAlfr().getPage(User.class,
                    User.ID_ANONYMOUS_USER);
            for (Page child : page.getChildren(new PageCondition().setUser(
                    anonymousUser).setPrivilege(Privilege.ACCESS_VIEW))) {
                publish(wagon, child, recursive);
            }
        }
    }

    void publish(Wagon wagon, Collection<Page> pages, Set<Integer> recursives,
            boolean optimize) throws WagonException {
        if (!optimize || !(wagon instanceof CommandExecutor)) {
            for (Page page : pages) {
                publish(wagon, page, recursives.contains(page.getId()));
            }
            return;
        }

        File zipFile = null;
        try {
            zipFile = File.createTempFile("wagon", ".zip");
            String remoteFileName = zipFile.getName();

            createZip(zipFile, pages, recursives);

            wagon.put(zipFile, remoteFileName);

            String targetRepoBaseDirectory = wagon.getRepository().getBasedir();

            CommandExecutor executor = (CommandExecutor) wagon;
            // We use the super quiet option here as all the noise seems to kill/stall the connection
            String command = "unzip -o -qq -d " + targetRepoBaseDirectory + " "
                    + targetRepoBaseDirectory + "/" + remoteFileName;
            if (log.isDebugEnabled()) {
                log.debug("Command: " + command);
            }
            try {
                executor.executeCommand(command);
            } finally {
                command = "rm -f " + targetRepoBaseDirectory + "/"
                        + remoteFileName;
                if (log.isDebugEnabled()) {
                    log.debug("Command: " + command);
                }

                executor.executeCommand(command);
            }
        } catch (IOException ex) {
            throw new PublishRuntimeException(ex);
        } finally {
            if (zipFile != null) {
                zipFile.delete();
            }
        }
    }

    void createZip(File zipName, Collection<Page> pages, Set<Integer> recursives)
            throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipName));

        try {
            for (Page page : pages) {
                writeZipEntry(zos, page, recursives.contains(page.getId()));
            }
        } finally {
            IOUtil.close(zos);
        }
    }

    void writeZipEntry(ZipOutputStream jar, Page page, boolean recursive)
            throws IOException {
        // TODO concealedされている時下層をpublishしないようにしたほうが合理的かもしれない…。
        if (!page.isConcealed()) {
            do {
                String entryName = page.getPathname();

                if (page.isNode()) {
                    if (page.getChild(NAME_INDEX) == null) {
                        entryName += "/" + NAME_INDEX;
                    } else {
                        break;
                    }
                }

                entryName = entryName.substring(1/* = "/".length() */);

                // FIXME ディレクトリでなく子ページを持つ、HTMLなページについては
                // hrefとsrcの中のパスが子ページである場合にディレクトリをずらすようにしよう。
                // この対応が終わったら子ページをpublishするようにすること。
                Response response = ServletUtils.getResponse(ServletUtils
                        .getURL(page));
                InputStream is = response.getInputStream();
                try {
                    jar.putNextEntry(new ZipEntry(entryName));

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        jar.write(buffer, 0, bytesRead);
                    }
                } finally {
                    IOUtils.closeQuietly(is);
                }
            } while (false);
        }

        if (recursive && page.isNode()) {
            User anonymousUser = pagePlugin.getPageAlfr().getPage(User.class,
                    User.ID_ANONYMOUS_USER);
            for (Page child : page.getChildren(new PageCondition().setUser(
                    anonymousUser).setPrivilege(Privilege.ACCESS_VIEW))) {
                writeZipEntry(jar, child, recursive);
            }
        }
    }
}
