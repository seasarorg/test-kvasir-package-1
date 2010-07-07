package org.seasar.kvasir.cms.publish.setting;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Required;

public class RepositoryElement {
    private String id;

    private String url;

    public String getId() {
        return id;
    }

    @Attribute
    @Required
    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    @Attribute
    @Required
    public void setUrl(String url) {
        this.url = url;
    }
}
