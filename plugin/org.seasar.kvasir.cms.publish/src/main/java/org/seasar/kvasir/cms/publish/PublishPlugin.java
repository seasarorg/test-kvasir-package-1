package org.seasar.kvasir.cms.publish;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.publish.setting.DistributionGroupElement;
import org.seasar.kvasir.cms.publish.setting.PublishPluginSettings;
import org.seasar.kvasir.cms.publish.setting.RepositoryElement;
import org.seasar.kvasir.page.Page;

public interface PublishPlugin extends Plugin<PublishPluginSettings> {
    String ID = "org.seasar.kvasir.cms.publish";

    String ID_PATH = ID.replace('.', '/');

    List<DistributionGroupElement> getDistributionGroups();

    DistributionGroupElement getDistributionGroup(String id);

    List<RepositoryElement> getRepositories();

    RepositoryElement getRepository(String id);

    void publish(String distributionGroupId, boolean optimize);

    void publish(Page page, boolean recursive, String repositoryId,
            boolean optimize);

    void publish(Collection<Page> pages, Set<Integer> recursives,
            String repositoryId, boolean optimize);
}
