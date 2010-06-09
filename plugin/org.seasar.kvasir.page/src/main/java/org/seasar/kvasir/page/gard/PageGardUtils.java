package org.seasar.kvasir.page.gard;

import static org.seasar.kvasir.page.ability.PropertyAbility.PROPPREFIX_PAGEGARD_INSTALLED;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


/**
 * @author YOKOTA Takehiko
 */
public class PageGardUtils
{
    private PageGardUtils()
    {
    }


    public static Version getLatestVersion(String gardId)
    {
        Page alfheimRoot = PageUtils.getPageAlfr()
            .getPage(Page.ID_ALFHEIM_ROOT);
        Page idPage = alfheimRoot.getChild(gardId);
        if (idPage == null) {
            return null;
        }
        String[] versionStrings = idPage.getChildNames();
        if (versionStrings.length == 0) {
            return null;
        }
        Version[] versions = new Version[versionStrings.length];
        for (int i = 0; i < versions.length; i++) {
            versions[i] = new Version(versionStrings[i]);
        }
        Arrays.sort(versions);
        return versions[versions.length - 1];
    }


    public static Page getLatestVersionPage(String gardId)
    {
        Page alfheimRoot = PageUtils.getPageAlfr()
            .getPage(Page.ID_ALFHEIM_ROOT);
        Version latest = getLatestVersion(gardId);
        if (latest == null) {
            return null;
        }
        return alfheimRoot.getChild(gardId + "/" + latest.getString());
    }


    public static Page getVersionPage(String gardId, Version version)
    {
        if ((gardId == null) || (version == null)) {
            return null;
        }

        Page alfheimRoot = PageUtils.getPageAlfr()
            .getPage(Page.ID_ALFHEIM_ROOT);
        return alfheimRoot.getChild(gardId + "/" + version.getString());
    }


    /**
     * 指定されたHeimに指定されたGardがインストール済みかどうかを返します。
     * <p>このメソッドで判定可能なのはシングルトンなGardだけです。
     * </p>
     * 
     * @param pageGardId GardのフルID。
     * @param heimId HeimのID。
     * @return 指定されたGardがインストール済みか。
     */
    public static boolean isInstalled(String pageGardId, int heimId)
    {
        return isInstalled(pageGardId, PageUtils.getPageAlfr().getRootPage(
            heimId));
    }


    /**
     * 指定されたPageに指定されたGardがインストール済みかを返します。
     * <p>指定されたPage自身に対してGardがインストール済みかを返します。
     * PageがどのGard配下なのかを調べたい場合は{@link Page#getNearestGardRoot()}
     * などでまずGardのルートを見つけてからそれをこのメソッドに指定して下さい。
     * </p>
     * 
     * @param pageGardId GardのフルID。
     * @param page Page。
     * nullを指定することもできます。
     * @return 指定されたGardがインストール済みか。
     */
    public static boolean isInstalled(String pageGardId, Page page)
    {
        if (page == null) {
            return false;
        }
        return PropertyUtils.valueOf(page.getAbility(PropertyAbility.class)
            .getProperty(PROPPREFIX_PAGEGARD_INSTALLED + pageGardId), false);
    }


    public static MapProperties loadProperties(Resource resource)
    {
        MapProperties prop = new MapProperties(new HashMap<String, String>());
        try {
            prop.load(resource.getInputStream(), "UTF-8");
        } catch (ResourceNotFoundException ex) {
            ;
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
        return prop;
    }
}
