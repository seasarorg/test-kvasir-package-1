package org.seasar.kvasir.cms.publish.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.skirnir.xom.annotation.Child;

public class PublishPluginSettings {
    private List<RepositoryElement> repositories = new ArrayList<RepositoryElement>();

    private List<ServerElement> servers = new ArrayList<ServerElement>();

    private List<DistributionGroupElement> distributionGroups = new ArrayList<DistributionGroupElement>();

    public RepositoryElement[] getRepositories() {
        return repositories.toArray(new RepositoryElement[0]);
    }

    @Child
    public void addRepository(RepositoryElement repository) {
        repositories.add(repository);
    }

    public void setRepositorys(RepositoryElement[] repositories) {
        this.repositories.clear();
        this.repositories.addAll(Arrays.asList(repositories));
    }

    public ServerElement[] getServers() {
        return servers.toArray(new ServerElement[0]);
    }

    @Child
    public void addServer(ServerElement server) {
        servers.add(server);
    }

    public void setServers(ServerElement[] servers) {
        this.servers.clear();
        this.servers.addAll(Arrays.asList(servers));
    }

    public DistributionGroupElement[] getDistributionGroups() {
        return distributionGroups.toArray(new DistributionGroupElement[0]);
    }

    @Child
    public void addDistributionGroup(DistributionGroupElement distributionGroup) {
        distributionGroups.add(distributionGroup);
    }

    public void setDistributionGroups(
            DistributionGroupElement[] distributionGroups) {
        this.distributionGroups.clear();
        this.distributionGroups.addAll(Arrays.asList(distributionGroups));
    }
}
