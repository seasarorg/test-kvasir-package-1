package org.seasar.kvasir.cms.publish.setting;

import org.seasar.kvasir.page.PathId;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Default;
import net.skirnir.xom.annotation.Required;

public class PageElement {
    private static final String DEFAULT_HEIMID = "" + PathId.HEIM_MIDGARD;

    private int heimId = PathId.HEIM_MIDGARD;

    private String pathname;

    private boolean recursive;

    public int getHeimId() {
        return heimId;
    }

    @Attribute
    @Default(DEFAULT_HEIMID)
    public void setHeimId(int heimId) {
        this.heimId = heimId;
    }

    public String getPathname() {
        return pathname;
    }

    @Attribute
    @Required
    public void setPathname(String pathname) {
        this.pathname = pathname;
    };

    public boolean isRecursive() {
        return recursive;
    }

    @Attribute
    @Default("false")
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
}
