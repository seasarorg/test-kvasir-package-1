package org.seasar.kvasir.cms.publish;

import java.util.List;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.cms.publish.setting.DistributionGroupElement;
import org.seasar.kvasir.cms.publish.setting.PublishPluginSettings;

public interface PublishPlugin extends Plugin<PublishPluginSettings> {
    String ID = "org.seasar.kvasir.cms.publish";

    String ID_PATH = ID.replace('.', '/');

    List<DistributionGroupElement> getDistributionGroups();

    DistributionGroupElement getDistributionGroup(String id);

    void publish(String distributionGroupId, boolean optimize);
}
