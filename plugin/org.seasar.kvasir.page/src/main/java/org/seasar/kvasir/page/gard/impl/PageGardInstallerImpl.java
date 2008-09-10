package org.seasar.kvasir.page.gard.impl;

import static org.seasar.kvasir.page.ability.PropertyAbility.PROPPREFIX_PAGEGARD_INSTALLED;
import static org.seasar.kvasir.page.ability.PropertyAbility.PROPPREFIX_PAGEGARD_VERSION;
import static org.seasar.kvasir.page.ability.PropertyAbility.PROP_PAGEGARD_ID;
import static org.seasar.kvasir.page.ability.PropertyAbility.PROP_PAGEGARD_VERSION;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.LoopDetectedException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.gard.PageGard;
import org.seasar.kvasir.page.gard.PageGardInstaller;
import org.seasar.kvasir.page.gard.PageGardNotFoundRuntimeException;
import org.seasar.kvasir.page.gard.PageGardUtils;
import org.seasar.kvasir.page.gard.delta.Delta;
import org.seasar.kvasir.page.gard.delta.PageDifference;
import org.seasar.kvasir.page.gard.delta.PageDifferer;
import org.seasar.kvasir.page.type.User;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageGardInstallerImpl
    implements PageGardInstaller
{
    private PagePlugin plugin_;


    public PageGardInstallerImpl()
    {
    }


    public Page install(Page parent, String name, String gardId)
        throws DuplicatePageException
    {
        // 最新バージョンを求める。
        Page gard = PageGardUtils.getLatestVersionPage(gardId);
        if (gard == null) {
            // 存在しない。
            throw new PageGardNotFoundRuntimeException("No entry")
                .setId(gardId);
        }

        return install(createPage(parent, name, gard), gard, gardId);
    }


    public boolean install(Page dest, String gardId)
        throws DuplicatePageException
    {
        Page gard = PageGardUtils.getLatestVersionPage(gardId);
        if (gard == null) {
            // 存在しない。
            throw new PageGardNotFoundRuntimeException("No entry")
                .setId(gardId);
        }

        return (install(dest, gard, gardId) != null);
    }


    public void upgrade(Page page)
        throws DuplicatePageException
    {
        String gardId = page.getGardId();
        Version oldVersion = page.getGardVersion();
        if (gardId == null) {
            throw new IllegalArgumentException(
                "Specified page is not an instance of an registered gard: page="
                    + page);
        }
        if (oldVersion == null) {
            throw new IllegalArgumentException("Can't find version: page="
                + page + ", gardId=" + gardId);
        }
        Page oldGard = PageGardUtils.getVersionPage(gardId, oldVersion);
        if (oldGard == null) {
            // アップグレードするには以前のバージョンのPageGardが必要。
            throw new PageGardNotFoundRuntimeException("No entry")
                .setId(gardId).setVersion(oldVersion);
        }

        Version newVersion = PageGardUtils.getLatestVersion(gardId);
        if (newVersion == null) {
            // 存在しない。
            throw new PageGardNotFoundRuntimeException("No entry")
                .setId(gardId);
        }
        Page newGard = PageGardUtils.getVersionPage(gardId, newVersion);
        PageGard pageGard = plugin_.getPageGard(gardId);

        PageDifferer differer = null;
        if (pageGard != null) {
            pageGard.preUpgradeProcess(page, oldVersion);
        }

        upgrade(page, newGard, oldGard, differer);

        if (pageGard != null) {
            pageGard.postUpgradeProcess(page, oldVersion);
        }

        touch(page);
    }


    public void uninstall(Page page)
    {
        String gardId = page.getGardId();
        Version oldVersion = page.getGardVersion();
        if (gardId == null) {
            // 単にページツリーを削除する。
            page.delete();
            return;
        }

        if (oldVersion == null) {
            throw new IllegalArgumentException("Can't find version: page="
                + page + ", gardId=" + gardId);
        }

        Version newVersion = PageGardUtils.getLatestVersion(gardId);
        if (newVersion == null) {
            // 存在しない。
            throw new PageGardNotFoundRuntimeException("No entry")
                .setId(gardId);
        }
        PageGard pageGard = plugin_.getPageGard(gardId);

        if (pageGard != null) {
            pageGard.preUninstallProcess(page, oldVersion);
        }
        page.delete();
        if (pageGard != null) {
            pageGard.postUninstallProcess(page, oldVersion);
        }
    }


    public Page copy(final Page parent, final String name, final Page source,
        final boolean adjustName)
        throws DuplicatePageException, LoopDetectedException
    {
        if (PageUtils.isLooped(source, parent)) {
            throw new LoopDetectedException("Can't copy " + source + " into "
                + parent);
        }

        Page dest;
        try {
            dest = parent.runWithLocking(new Processable<Page>() {
                public Page process()
                    throws ProcessableRuntimeException
                {
                    String actualName;
                    if (adjustName) {
                        actualName = parent.getNonExistentChildName(name);
                    } else {
                        actualName = name;
                    }

                    try {
                        return createPage(parent, actualName, source);
                    } catch (DuplicatePageException ex) {
                        throw new ProcessableRuntimeException(ex);
                    }
                }
            });
        } catch (ProcessableRuntimeException ex) {
            throw (DuplicatePageException)ex.getCause();
        }

        createPages(dest, source);
        populate(dest, source, false);
        touch(dest);

        return dest;
    }


    /*
     * private scope methods
     */

    private Page createPages(Page dest, Page source)
        throws DuplicatePageException
    {
        Page[] sourceChildlen = source.getChildren();
        for (int i = 0; i < sourceChildlen.length; i++) {
            Page sourceChild = sourceChildlen[i];
            String name = sourceChild.getName();
            Page destChild = dest.getChild(name);
            if (destChild == null) {
                destChild = createPage(dest, name, sourceChild);
            }
            createPages(destChild, sourceChild);
        }

        return dest;
    }


    private Page createPage(Page parent, String name, Page source)
        throws DuplicatePageException
    {
        return parent.createChild(newPageMold(source, name)
            .setOmitCreationProcessForAbilityAlfr(true));
    }


    private PageMold newPageMold(Page source, String name)
    {
        return plugin_.getPageType(source.getType()).newPageMold()
            .setName(name);
    }


    private Page install(Page dest, Page source, String gardId)
        throws DuplicatePageException
    {
        PageGard pageGard = plugin_.getPageGard(gardId);

        if (pageGard != null) {
            if (!pageGard.preInstallProcess(dest)) {
                return null;
            }
        }
        createPages(dest, source);
        populate(dest, source, false);

        dest.setAsLord(true);
        PropertyAbility prop = dest.getAbility(PropertyAbility.class);
        prop.setProperty(PROP_PAGEGARD_ID, gardId);
        String versionString = PageGardUtils.getLatestVersion(gardId)
            .getString();
        prop.setProperty(PROP_PAGEGARD_VERSION, versionString);
        prop.setProperty(PROPPREFIX_PAGEGARD_INSTALLED + gardId, String
            .valueOf(true));
        prop.setProperty(PROPPREFIX_PAGEGARD_VERSION + gardId, versionString);

        if (pageGard != null) {
            if (pageGard.isSingleton()) {
                Page rootPage = plugin_.getPageAlfr().getRootPage(
                    dest.getHeimId());
                prop = rootPage.getAbility(PropertyAbility.class);
                prop.setProperty(PROPPREFIX_PAGEGARD_INSTALLED + gardId, String
                    .valueOf(true));
                prop.setProperty(PROPPREFIX_PAGEGARD_VERSION + gardId,
                    versionString);
            }

            pageGard.postInstallProcess(dest);
        }

        touch(dest);

        return dest;
    }


    private void populate(final Page dest, final Page source,
        final boolean importOrderNumber)
    {
        if (dest == null) {
            return;
        }

        Page[] dests = plugin_.getPageAlfr().runWithLocking(
            new Page[] { dest, source }, new Processable<Page[]>() {
                public Page[] process()
                    throws ProcessableRuntimeException
                {
                    // fieldの情報をインポートする。
                    populateFields(dest, source, importOrderNumber);

                    // PageAbility毎の情報をインポートする。
                    PageAbilityAlfr[] alfrs = plugin_.getPageAbilityAlfrs();
                    for (int i = 0; i < alfrs.length; i++) {
                        populateAbility(dest, alfrs[i], source);
                    }

                    return source.getChildren();
                }
            });

        for (int i = 0; i < dests.length; i++) {
            populate(dest.getChild(dests[i].getName()), dests[i], true);
        }
    }


    private void populateFields(Page dest, Page source,
        boolean importOrderNumber)
    {
        // node
        dest.setNode(source.isNode());

        // lord
        if (source.isLord()) {
            dest.setAsLord(true);
        }

        // orderNumber
        if (importOrderNumber) {
            dest.setOrderNumber(source.getOrderNumber());
        }

        // createDate
        dest.setCreateDate(source.getCreateDate());

        // modifyDate
        dest.setModifyDate(source.getModifyDate());

        // revealDate
        dest.setRevealDate(source.getRevealDate());

        // concealDate
        dest.setConcealDate(source.getConcealDate());

        // ownerUser
        String path = PageUtils.encodePathname(source, source.getOwnerUser()
            .getPathname());
        User ownerUser = (User)PageUtils.decodeToPage(User.class, dest, path);
        if (ownerUser != null) {
            dest.setOwnerUser(ownerUser);
        }

        // asFile
        dest.setAsFile(source.isAsFile());

        // listing
        dest.setListing(source.isListing());
    }


    private void populateAbility(Page dest, PageAbilityAlfr abilityAlfr,
        Page source)
    {
        String[] variants = abilityAlfr.getVariants(source);
        for (int i = 0; i < variants.length; i++) {
            String variant = variants[i];

            for (Iterator<String> itr = abilityAlfr.attributeNames(source,
                variant); itr.hasNext();) {
                String name = itr.next();
                Attribute attr = abilityAlfr
                    .getAttribute(source, name, variant);
                abilityAlfr.setAttribute(dest, name, variant, attr);
            }
        }
    }


    private void upgrade(Page page, Page toGard, Page fromGard,
        PageDifferer differer)
        throws DuplicatePageException
    {
        if (page == null) {
            return;
        }

        PageDifference pd = differer.diff(toGard, fromGard);
        int type = pd.getType();
        if (type == Delta.TYPE_ADD) {
            // 元々なかったところに新バージョンからページが
            // 追加されたが、そこにはもうページがある。
            // そういう場合は何もしない。
            return;
        } else if (type == Delta.TYPE_REMOVE) {
            // 新バージョンでページが消された。
            // そういう場合は、ページが修正されていなければ
            // ページを消すようにする。
            if (differer.diff(page, fromGard).getType() == Delta.TYPE_NONE) {
                page.delete();
            }
            return;
        }

        differer.apply(page, pd);

        Set<String> set = new HashSet<String>();
        if (toGard != null) {
            String[] toNames = toGard.getChildNames();
            for (int i = 0; i < toNames.length; i++) {
                set.add(toNames[i]);
            }
        }
        if (fromGard != null) {
            String[] fromNames = fromGard.getChildNames();
            for (int i = 0; i < fromNames.length; i++) {
                set.add(fromNames[i]);
            }
        }

        for (Iterator<String> itr = set.iterator(); itr.hasNext();) {
            String name = itr.next();
            Page child = page.getChild(name);
            Page toChild = toGard.getChild(name);
            Page fromChild = fromGard.getChild(name);

            if (child != null) {
                upgrade(child, toChild, fromChild, differer);
            } else {
                // setの作り方から、toChildかfromChildかいずれかは非null。
                if (fromChild == null) {
                    // 元々なかったページが新バージョンから追加された。
                    // 今もページはないので、単にページを追加する。
                    child = createPage(page, name, toChild);
                    populate(child, toChild, false);
                } else if (toChild == null) {
                    // 元々あったページが新バージョンでなくなった。
                    // すでにページがないので何もしない。
                    ;
                } else {
                    // ページはそのままか新バージョンで修正されている。
                    // ただ、ページは明示的に削除されているので、
                    // 何もしない。
                    ;
                }
            }
        }
    }


    private void touch(Page page)
    {
        page.touch(false);
        Page[] children = page.getChildren();
        for (int i = 0; i < children.length; i++) {
            touch(children[i]);
        }
    }


    /*
     * for framework
     */

    public void setPlugin(PagePlugin pagePlugin)
    {
        plugin_ = pagePlugin;
    }
}
