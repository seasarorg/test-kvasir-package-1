package org.seasar.kvasir.cms.publish.setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Required;

public class DistributionGroupElement {
    private String id;

    private String repositoryId;

    private List<PageElement> pages = new ArrayList<PageElement>();

    public String getId() {
        return id;
    }

    @Attribute
    @Required
    public void setId(String id) {
        this.id = id;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    @Attribute
    @Required
    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public PageElement[] getPages() {
        return pages.toArray(new PageElement[0]);
    }

    @Child
    public void addPage(PageElement page) {
        pages.add(page);
    }

    public void setPages(PageElement[] pages) {
        this.pages.clear();
        this.pages.addAll(Arrays.asList(pages));
    }
}
