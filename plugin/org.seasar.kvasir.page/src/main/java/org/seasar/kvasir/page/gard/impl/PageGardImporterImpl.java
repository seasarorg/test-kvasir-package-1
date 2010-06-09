package org.seasar.kvasir.page.gard.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.seasar.kvasir.page.DuplicatePageException;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageMold;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;
import org.seasar.kvasir.page.gard.PageGardImporter;
import org.seasar.kvasir.page.gard.PageGardUtils;
import org.seasar.kvasir.page.type.DirectoryMold;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.collection.I18NProperties;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.impl.ResourceInputStreamFactory;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageGardImporterImpl
    implements PageGardImporter
{
    static final String FIELD_XPROPERTIES = "field.xproperties";

    static final String RNAME_PREFIX_ATTRIBUTE = "attribute";

    static final String RNAME_SUFFIX_XPROPERTIES = ".xproperties";

    static final String RNAME_PREFIX_STREAM = "stream";

    private PagePlugin pagePlugin_;


    public PageGardImporterImpl()
    {
    }


    public Page imports(Page parent, String name, Resource dir)
        throws DuplicatePageException
    {
        if (!dir.exists()) {
            throw new IllegalArgumentException(
                "Can't import pageGard since specified directory does not exist: "
                    + dir);
        }
        Page page = createPages(parent, name, new ResourceBag(dir),
            new DirectoryMold());
        imports(page, dir, false);
        touch(page);
        return page;
    }


    Page createPages(Page parent, String name, ResourceBag resourceBag,
        PageMold defaultMold)
        throws DuplicatePageException
    {
        PageMold mold = newPageMold(defaultMold, resourceBag, name)
            .setOmitCreationProcessForAbilityAlfr(true);
        Page page = parent.createChild(mold);

        Resource[] resources = resourceBag.getResource().listResources();
        if (resources != null) {
            List<ResourceBag> bags = new ArrayList<ResourceBag>();
            for (int i = 0; i < resources.length; i++) {
                String n = resources[i].getName();
                if (shouldIgnoreFileName(n) || n.equals(".kv")) {
                    continue;
                }

                bags.add(new ResourceBag(resources[i]));
            }

            Collections.sort(bags);

            for (ResourceBag bag : bags) {
                createPages(page, bag.getResource().getName(), bag, null);
            }
        }

        return page;
    }


    public void imports(Page page, Resource dir)
    {
        if (!dir.exists()) {
            throw new IllegalArgumentException(
                "Can't import pageGard since specified directory does not exist: "
                    + dir);
        }
        createPages(page, new ResourceBag(dir));
        imports(page, dir, true);
        touch(page);
    }


    Page createPages(Page page, ResourceBag resourceBag)
    {
        Resource[] resources = resourceBag.getResource().listResources();
        if (resources != null) {
            List<ResourceBag> bags = new ArrayList<ResourceBag>();
            for (int i = 0; i < resources.length; i++) {
                String n = resources[i].getName();
                if (shouldIgnoreFileName(n) || n.equals(".kv")) {
                    continue;
                }

                bags.add(new ResourceBag(resources[i]));
            }

            Collections.sort(bags);

            for (ResourceBag bag : bags) {
                String name = bag.getResource().getName();
                Page child = page.getChild(name);
                if (child == null) {
                    PageMold mold = newPageMold(null, bag, name)
                        .setOmitCreationProcessForAbilityAlfr(true);
                    try {
                        child = page.createChild(mold);
                    } catch (DuplicatePageException ex) {
                        throw new RuntimeException("Can't create page: "
                            + page.getPathname() + "/" + name, ex);
                    }
                }

                createPages(child, bag);
            }
        }

        return page;
    }


    boolean shouldIgnoreFileName(String name)
    {
        if (name == null || name.equals(".svn") || name.equals("_svn")) {
            return true;
        } else {
            return false;
        }
    }


    PageMold newPageMold(PageMold defaultMold, ResourceBag resourceBag,
        String name)
    {
        PageMold mold;
        String type = resourceBag.getFieldProperties().getProperty("type");
        if (type != null) {
            mold = pagePlugin_.getPageType(type).newPageMold();
        } else {
            mold = (defaultMold != null ? defaultMold : new PageMold());
        }

        // name
        mold.setName(name);

        return mold;
    }


    void imports(final Page page, Resource dir, final boolean replace)
    {
        if (page == null) {
            return;
        }

        final Resource contentDir = dir.getChildResource(".kv");

        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                // fieldの情報をインポートする。
                importsFields(page, contentDir
                    .getChildResource(FIELD_XPROPERTIES), false);

                // PageAbilityAlfr毎の情報をインポートする。
                PageAbilityAlfr[] alfrs = pagePlugin_.getPageAbilityAlfrs();
                for (int i = 0; i < alfrs.length; i++) {
                    importsAbility(page, alfrs[i], contentDir
                        .getChildResource("ability." + alfrs[i].getShortId()),
                        replace);
                }
                return null;
            }
        });

        Resource[] resources = dir.listResources();
        if (resources != null) {
            for (int i = 0; i < resources.length; i++) {
                String n = resources[i].getName();
                if (shouldIgnoreFileName(n) || n.equals(".kv")) {
                    continue;
                }
                imports(page.getChild(n), resources[i], replace);
            }
        }
    }


    void importsFields(Page page, Resource resource, boolean importOrderNumber)
    {
        MapProperties prop = PageGardUtils.loadProperties(resource);

        // node
        String value = prop.getProperty(Page.FIELD_NODE);
        if (value != null) {
            page.setNode(PropertyUtils.valueOf(value, false));
        }

        // lord
        Page lord = PageUtils.decodeToPage(page, prop
            .getProperty(Page.FIELD_LORD));
        if (lord != null) {
            boolean pageIsLord = lord.equals(page);
            if (pageIsLord && !page.isNode()) {
                // lordの時は無条件にnodeにする。
                page.setNode(true);
            }
            page.setAsLord(pageIsLord);
        }

        // orderNumber
        if (importOrderNumber) {
            value = prop.getProperty(Page.FIELD_ORDERNUMBER);
            if (value != null) {
                page.setOrderNumber(PropertyUtils.valueOf(value, 0));
            }
        }

        // createDate
        String createDate = prop.getProperty(Page.FIELD_CREATEDATE);
        if (createDate != null) {
            page.setCreateDate(new Date(PropertyUtils.valueOf(createDate, 0L)));
        }

        // modifyDate
        value = prop.getProperty(Page.FIELD_MODIFYDATE);
        if (value == null) {
            value = createDate;
        }
        if (value != null) {
            page.setModifyDate(new Date(PropertyUtils.valueOf(value, 0L)));
        }

        // revealDate
        value = prop.getProperty(Page.FIELD_REVEALDATE);
        if (value == null) {
            value = createDate;
        }
        if (value != null) {
            page.setRevealDate(new Date(PropertyUtils.valueOf(value, 0L)));
        }

        // concealDate
        value = prop.getProperty(Page.FIELD_CONCEALDATE);
        if (value != null) {
            page.setConcealDate(new Date(PropertyUtils.valueOf(value, 0L)));
        }

        // ownerUser
        User ownerUser = (User)PageUtils.decodeToPage(User.class, page, prop
            .getProperty(Page.FIELD_OWNERUSER));
        if (ownerUser != null) {
            page.setOwnerUser(ownerUser);
        }

        // asFile
        value = prop.getProperty(Page.FIELD_ASFILE);
        if (value != null) {
            page.setAsFile(PropertyUtils.valueOf(value, false));
        }

        // listing
        value = prop.getProperty(Page.FIELD_LISTING);
        if (value != null) {
            page.setListing(PropertyUtils.valueOf(value, true));
        }
    }


    @SuppressWarnings("unchecked")
    void importsAbility(Page page, PageAbilityAlfr abilityAlfr, Resource dir,
        boolean replace)
    {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        if (replace) {
            abilityAlfr.clearAttributes(page);
        }

        // 属性名順でsetAttributeするためにSortedMapにしている。
        SortedMap<String, SortedMap<String, Attribute>> attrMap = new TreeMap<String, SortedMap<String, Attribute>>();
        I18NProperties prop = new I18NProperties(dir, RNAME_PREFIX_ATTRIBUTE,
            RNAME_SUFFIX_XPROPERTIES);
        String[] variants = prop.getVariants();
        for (int i = 0; i < variants.length; i++) {
            String variant = variants[i];
            for (Enumeration<String> enm = prop.propertyNames(variant); enm
                .hasMoreElements();) {
                String encoded = (String)enm.nextElement();
                String name;
                String key;
                int dollar = encoded.indexOf('$');
                if (dollar < 0) {
                    name = PageUtils.decodeName(encoded);
                    key = "";
                } else {
                    name = PageUtils.decodeName(encoded.substring(0, dollar));
                    key = PageUtils.decodeName(encoded.substring(dollar + 1));
                }
                Attribute attr = getAttribute(attrMap, name, variant);
                attr.setString(key, prop.getProperty(encoded, variant));
            }
        }

        String[] rnames = dir.list();
        for (int i = 0; i < rnames.length; i++) {
            String rname = rnames[i];
            if (rname.startsWith(RNAME_PREFIX_STREAM)) {
                int dot = rname.indexOf('.');
                if (dot < 0) {
                    continue;
                }
                String variant = rname.substring(RNAME_PREFIX_STREAM.length(),
                    dot);
                if (variant.startsWith("_")) {
                    variant = variant.substring(1);
                }
                String encoded = rname.substring(dot + 1);
                String name;
                String key;
                int dollar = encoded.indexOf('$');
                if (dollar < 0) {
                    name = PageUtils.decodeName(encoded);
                    key = "";
                } else {
                    name = PageUtils.decodeName(encoded.substring(0, dollar));
                    key = PageUtils.decodeName(encoded.substring(dollar + 1));
                }

                Attribute attr = getAttribute(attrMap, name, variant);
                attr.setStream(key, new ResourceInputStreamFactory(dir
                    .getChildResource(rname)));
            }
        }
        for (Iterator itr = attrMap.entrySet().iterator(); itr.hasNext();) {
            Map.Entry entry = (Map.Entry)itr.next();
            String name = (String)entry.getKey();
            SortedMap map = (SortedMap)entry.getValue();
            for (Iterator itr2 = map.entrySet().iterator(); itr2.hasNext();) {
                Map.Entry entry2 = (Map.Entry)itr2.next();
                String variant = (String)entry2.getKey();
                Attribute attr = (Attribute)entry2.getValue();

                abilityAlfr.setAttribute(page, name, variant, attr);
            }
        }
    }


    Attribute getAttribute(Map<String, SortedMap<String, Attribute>> attrMap,
        String name, String variant)
    {
        SortedMap<String, Attribute> map = attrMap.get(name);
        if (map == null) {
            map = new TreeMap<String, Attribute>();
            attrMap.put(name.intern(), map);
        }
        Attribute attr = map.get(variant);
        if (attr == null) {
            attr = new Attribute();
            map.put(variant.intern(), attr);
        }
        return attr;
    }


    void touch(Page page)
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

    public void setPagePlugin(PagePlugin pagePlugin)
    {
        pagePlugin_ = pagePlugin;
    }
}
