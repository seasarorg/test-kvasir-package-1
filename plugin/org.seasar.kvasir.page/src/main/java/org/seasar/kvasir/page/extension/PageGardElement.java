package org.seasar.kvasir.page.extension;

import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.gard.PageGard;
import org.seasar.kvasir.util.io.Resource;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Default;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
@Component(replace = true)
@Bean("page-gard")
public class PageGardElement extends AbstractElement
    implements PageGard
{
    private static final String DEFAULT_VERSION = "1.0.0";

    private String version_;

    private String source_;

    private boolean reset_ = false;

    private boolean singleton_ = false;

    private String defaultPathname_;

    private String displayName_;

    private String description_;


    public String getVersion()
    {
        if (version_ == null) {
            return DEFAULT_VERSION;
        } else {
            return version_;
        }
    }


    @Attribute
    @Default(DEFAULT_VERSION)
    public void setVersion(String version)
    {
        version_ = version;
    }


    public String getSource()
    {
        return source_;
    }


    public String findSource()
    {
        if (source_ == null) {
            return "gards/" + getId() + "/dynamic";
        } else {
            return source_;
        }
    }


    @Attribute
    public void setSource(String source)
    {
        if ((source != null) && source.startsWith("/")) {
            source = source.substring(1);
        }
        source_ = source;
    }


    public boolean isReset()
    {
        return reset_;
    }


    @Attribute
    @Default("false")
    public void setReset(boolean reset)
    {
        reset_ = reset;
    }


    public boolean isSingleton()
    {
        return singleton_;
    }


    @Attribute
    @Default("false")
    public void setSingleton(boolean singleton)
    {
        singleton_ = singleton;
    }


    public String getDefaultPathname()
    {
        return defaultPathname_;
    }


    @Attribute
    public void setDefaultPathname(String defaultPathname)
    {
        defaultPathname_ = defaultPathname;
    }


    public String getDisplayName()
    {
        return displayName_;
    }


    @Attribute
    public void setDisplayName(String displayName)
    {
        displayName_ = displayName;
    }


    public String getDescription()
    {
        return description_;
    }


    @Attribute
    public void setDescription(String description)
    {
        description_ = description;
    }


    public Resource getSourceDirectory()
    {
        return getParent().getParent().getPlugin().getHomeDirectory()
            .getChildResource(findSource());
    }


    public boolean preInstallProcess(Page gardRootPage)
    {
        return true;
    }


    public void postInstallProcess(Page gardRootPage)
    {
    }


    public void preUpgradeProcess(Page gardRootPage, Version version)
    {
    }


    public void postUpgradeProcess(Page gardRootPage, Version version)
    {
    }


    public void preUninstallProcess(Page gardRootPage, Version version)
    {
    }


    public void postUninstallProcess(Page gardRootPage, Version version)
    {
    }
}
