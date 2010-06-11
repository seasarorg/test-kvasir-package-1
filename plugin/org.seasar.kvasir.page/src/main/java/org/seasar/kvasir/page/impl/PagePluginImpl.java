package org.seasar.kvasir.page.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.KvasirUtils;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.LoopDetectedException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageAlfr;
import org.seasar.kvasir.page.PageEvent;
import org.seasar.kvasir.page.PageListener;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PathId;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;
import org.seasar.kvasir.page.extension.NameRestrictionElement;
import org.seasar.kvasir.page.gard.PageGard;
import org.seasar.kvasir.page.gard.PageGardExporter;
import org.seasar.kvasir.page.gard.PageGardImporter;
import org.seasar.kvasir.page.gard.PageGardInstall;
import org.seasar.kvasir.page.gard.PageGardInstaller;
import org.seasar.kvasir.page.gard.PageGardUtils;
import org.seasar.kvasir.page.type.DirectoryMold;
import org.seasar.kvasir.page.type.PageType;
import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PagePluginImpl extends AbstractPlugin<EmptySettings>
    implements PagePlugin
{
    private PageAlfr pageAlfr_;

    private PageListener[] pageListeners_;

    private PageType[] pageTypes_;

    private Map<Object, PageType> pageTypeMap_;

    private PageAbilityAlfr[] pageAbilityAlfrs_;

    private Map<Serializable, PageAbilityAlfr> pageAbilityAlfrMap_;

    private PageGard[] pageGards_;

    private Map<String, PageGard> pageGardMap_;

    private Map<String, Plugin<?>> pluginMap_;

    private PageGardInstall[] pageGardInstalls_;

    private PageGardInstaller installer_;

    private PageGardImporter importer_;

    private PageGardExporter exporter_;

    private Set<String> invalidNameSet_;

    private String invalidNameChars_;


    /*
     * constructors
     */

    public PagePluginImpl()
    {
    }


    /*
     * PagePlugin
     */

    public PageAlfr getPageAlfr()
    {
        return pageAlfr_;
    }


    public PageType getPageType(Object key)
    {
        PageType pageType = pageTypeMap_.get(key);
        if (pageType == null) {
            pageType = pageTypeMap_.get(Page.TYPE);
        }
        return pageType;
    }


    @SuppressWarnings("unchecked")
    public <T extends PageType> T getPageType(Class<T> key)
    {
        return (T)pageTypeMap_.get(key);
    }


    public PageType[] getPageTypes()
    {
        return pageTypes_;
    }


    public PageListener[] getPageListeners()
    {
        return pageListeners_;
    }


    public void notifyPageListeners(PageEvent pageEvent)
    {
        for (int i = 0; i < pageListeners_.length; i++) {
            pageListeners_[i].notifyChanged(pageEvent);
        }
    }


    public PageAbilityAlfr getPageAbilityAlfr(Object key)
    {
        return pageAbilityAlfrMap_.get(key);
    }


    @SuppressWarnings("unchecked")
    public <T extends PageAbilityAlfr> T getPageAbilityAlfr(Class<T> key)
    {
        return (T)pageAbilityAlfrMap_.get(key);
    }


    public PageAbilityAlfr[] getPageAbilityAlfrs()
    {
        return pageAbilityAlfrs_;
    }


    public boolean isValidName(String name)
    {
        if (name == null) {
            return false;
        }
        if (invalidNameSet_.contains(name)) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            char ch = name.charAt(i);
            if (invalidNameChars_.indexOf(ch) >= 0) {
                return false;
            }
        }

        return true;
    }


    public Plugin<?> getPlugin(String gardFullId)
    {
        return pluginMap_.get(gardFullId);
    }


    public PageGard getPageGard(String gardFullId)
    {
        return pageGardMap_.get(gardFullId);
    }


    public PageGard[] getPageGards()
    {
        return pageGards_;
    }


    public Page imports(Page parent, String name, Resource dir)
        throws DuplicatePageException
    {
        return importer_.imports(parent, name, dir);
    }


    public void imports(Page page, Resource dir, boolean replace)
    {
        importer_.imports(page, dir, replace);
    }


    public void exports(Resource dir, Page page)
    {
        exporter_.exports(dir, page);
    }


    public Page install(Page parent, String name, String gardFullId)
        throws DuplicatePageException
    {
        return installer_.install(parent, name, gardFullId);
    }


    public boolean install(Page page, String gardFullId)
        throws DuplicatePageException
    {
        return installer_.install(page, gardFullId);
    }


    public void upgrade(Page page)
        throws DuplicatePageException
    {
        installer_.upgrade(page);
    }


    public void uninstall(Page page)
    {
        installer_.uninstall(page);
    }


    public Page copy(Page parent, String name, Page source, boolean adjustName)
        throws DuplicatePageException, LoopDetectedException
    {
        return installer_.copy(parent, name, source, adjustName);
    }


    /*
     * AbstractPlugin
     */

    @Override
    public Object getAdapter(Object key)
    {
        if (PageAlfr.class.equals(key)) {
            return pageAlfr_;
        } else {
            return null;
        }
    }


    @Override
    protected boolean doStart()
    {
        pageListeners_ = getExtensionComponents(PageListener.class);

        pageTypes_ = getExtensionComponents(PageType.class);
        pageTypeMap_ = new HashMap<Object, PageType>();
        for (int i = 0; i < pageTypes_.length; i++) {
            PageType type = pageTypes_[i];
            pageTypeMap_.put(type.getClass(), type);
            pageTypeMap_.put(type.getId(), type);
            pageTypeMap_.put(type.getInterface(), type);
        }
        pageAbilityAlfrs_ = getExtensionComponents(PageAbilityAlfr.class);
        pageAbilityAlfrMap_ = new HashMap<Serializable, PageAbilityAlfr>();
        for (int i = 0; i < pageAbilityAlfrs_.length; i++) {
            PageAbilityAlfr alfr = pageAbilityAlfrs_[i];
            pageAbilityAlfrMap_.put(ClassUtils.getSubInterface(alfr.getClass(),
                PageAbilityAlfr.class), alfr);
            pageAbilityAlfrMap_.put(alfr.getAbilityInterfaceClass(), alfr);
            pageAbilityAlfrMap_.put(alfr.getShortId(), alfr);
        }

        if (!KvasirUtils.start(pageAlfr_)) {
            return false;
        }

        if (!KvasirUtils.start(pageListeners_)) {
            return false;
        }

        if (!KvasirUtils.start(pageTypes_)) {
            return false;
        }

        if (!KvasirUtils.start(pageAbilityAlfrs_)) {
            return false;
        }

        pageGards_ = getExtensionElements(PageGard.class);
        if (!KvasirUtils.start(pageGards_)) {
            return false;
        }

        pageGardMap_ = new HashMap<String, PageGard>();
        pluginMap_ = new HashMap<String, Plugin<?>>();
        for (int i = 0; i < pageGards_.length; i++) {
            String fullId = pageGards_[i].getFullId();
            pageGardMap_.put(fullId, pageGards_[i]);
            pluginMap_.put(fullId, pageGards_[i].getPlugin());
        }

        pageGardInstalls_ = getExtensionElements(PageGardInstall.class);

        invalidNameSet_ = new HashSet<String>();
        StringBuilder sb = new StringBuilder();
        NameRestrictionElement[] nameRestrictionElements = getExtensionElements(NameRestrictionElement.class);
        for (int i = 0; i < nameRestrictionElements.length; i++) {
            invalidNameSet_.addAll(Arrays.asList(nameRestrictionElements[i]
                .getInvalidNames()));
            String[] invalidChars = nameRestrictionElements[i]
                .getInvalidChars();
            for (int j = 0; j < invalidChars.length; j++) {
                sb.append(invalidChars[j]);
            }
        }
        invalidNameChars_ = sb.toString();

        return true;
    }


    @Override
    protected void doStop()
    {
        invalidNameChars_ = null;
        invalidNameSet_ = null;

        importer_ = null;
        exporter_ = null;
        installer_ = null;

        pageGardInstalls_ = null;

        KvasirUtils.stop(pageGards_);
        pageGards_ = null;
        pageGardMap_ = null;
        pluginMap_ = null;

        KvasirUtils.stop(pageAlfr_);
        pageAlfr_ = null;

        KvasirUtils.stop(pageListeners_);
        pageListeners_ = null;

        KvasirUtils.stop(pageTypes_);
        pageTypes_ = null;
        pageTypeMap_ = null;

        KvasirUtils.stop(pageAbilityAlfrs_);
        pageAbilityAlfrs_ = null;
        pageAbilityAlfrMap_ = null;
    }


    /*
     * Plugin
     */

    @Override
    public void notifyKvasirStarted()
    {
        importPageGards(pageGards_);

        installPageGards(pageGardInstalls_, PathId.HEIM_MIDGARD);
    }


    /*
     * private scope methods
     */

    private void importPageGards(PageGard[] pageGards)
    {
        Page alfheimRoot = pageAlfr_.getRootPage(PathId.HEIM_ALFHEIM);

        for (int i = 0; i < pageGards.length; i++) {
            PageGard pageGard = pageGards[i];
            String gardId = pageGard.getFullId();
            String version = pageGard.getVersion();

            Page idPage = alfheimRoot.getChild(gardId);
            Page versionPage = null;
            if (idPage != null) {
                versionPage = idPage.getChild(version);
                if (versionPage != null) {
                    // 既にインポート済み。
                    if (pageGard.isReset()) {
                        // reset="true"なのでインポートし直す。
                        versionPage.delete();
                        versionPage = null;
                    } else {
                        // 何もしない。
                        continue;
                    }
                }
            } else {
                try {
                    idPage = alfheimRoot.createChild(new DirectoryMold()
                        .setName(gardId));
                } catch (DuplicatePageException ex) {
                    throw new RuntimeException("Can't happen!", ex);
                }
            }

            try {
                imports(idPage, version, pageGard.getSourceDirectory());
            } catch (DuplicatePageException ex) {
                throw new RuntimeException("Can't happen!", ex);
            }
        }
    }


    private void installPageGards(PageGardInstall[] pageGardInstalls, int heimId)
    {
        for (int i = 0; i < pageGardInstalls.length; i++) {
            PageGardInstall pageGardInstall = pageGardInstalls[i];
            PageGard pageGard = getPageGard(pageGardInstall.getGardId());
            if (pageGard == null) {
                log_
                    .warn("[SKIP] Corresponding page-gard does not exist: gard-id="
                        + pageGardInstall.getGardId() + ": " + pageGardInstall);
                continue;
            }

            String pageGardId = pageGardInstall.getGardId();
            String pathname = null;
            if (pageGard.isSingleton()) {
                pathname = pageGard.getDefaultPathname();
            }
            if (pathname == null) {
                pathname = pageGardInstall.getPathname();
            }
            if (pathname == null) {
                pathname = Page.PATHNAME_PLUGINS + "/" + pageGard.getId();
            }

            if (log_.isDebugEnabled()) {
                log_.debug("Install page-gard (" + pageGard.getFullId()
                    + ") to path " + pathname);
            }

            Page page = pageAlfr_.getPage(heimId, pathname);
            if (page != null) {
                // ページが既に存在する。
                if (page.isRoot()) {
                    // ルートにだけはインストールできるようにしている。
                    // そうできた方がうれしい気がするので。
                    // ただしインストールは一度だけ。
                    if (PageGardUtils.isInstalled(pageGardId, page)) {
                        // インストール済みなのでインストールしない。
                        continue;
                    }
                } else if (pageGardInstall.isReset()) {
                    uninstall(page);
                    page = null;
                } else {
                    // ページが存在する＆reset="false"なので
                    // インストールしない。
                    continue;
                }
            }
            if (page != null) {
                try {
                    install(page, pageGardId);
                } catch (Throwable t) {
                    log_.error("Install failed: pathname=" + pathname
                        + ", gard-id=" + pageGardId);
                }
            } else {
                // ページが存在しない。
                int slash = pathname.lastIndexOf('/');
                page = pageAlfr_.getPage(heimId, pathname.substring(0, slash));
                String name = pathname.substring(slash + 1);
                if (page == null) {
                    // 親ページが存在しない。
                    log_.error("Parent page does not exist: pathname="
                        + pathname + ", gard-id=" + pageGardId);
                    continue;
                }
                try {
                    install(page, name, pageGardId);
                } catch (DuplicatePageException ex) {
                    log_.error("Can't happen! target page already exists: "
                        + "page=" + page + ", gard-id=" + pageGardId);
                } catch (Throwable t) {
                    log_.error("Install failed: parent page=" + page
                        + ", name=" + name + ", gard-id=" + pageGardId, t);
                }
            }
        }
    }


    public boolean isAlreadyIntalled(String gardId, int heimId)
    {
        return isAlreadyIntalled(getPageGard(gardId), heimId);
    }


    public boolean isAlreadyIntalled(PageGard gard, int heimId)
    {
        if (gard == null) {
            return false;
        }
        if (!gard.isSingleton()) {
            return false;
        }
        return PageGardUtils.isInstalled(gard.getFullId(), heimId);
    }


    /*
     * for framework
     */

    public void setPageAlfr(PageAlfr pageAlfr)
    {
        pageAlfr_ = pageAlfr;
    }


    public void setPageGardInstaller(PageGardInstaller installer)
    {
        installer_ = installer;
    }


    public void setPageGardImporter(PageGardImporter importer)
    {
        importer_ = importer;
    }


    public void setPageGardExporter(PageGardExporter exporter)
    {
        exporter_ = exporter;
    }
}
