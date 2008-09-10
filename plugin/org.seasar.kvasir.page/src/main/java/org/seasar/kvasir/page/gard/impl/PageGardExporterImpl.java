package org.seasar.kvasir.page.gard.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.TreeMap;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PagePlugin;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.PageAbilityAlfr;
import org.seasar.kvasir.page.gard.PageGardExporter;
import org.seasar.kvasir.util.collection.I18NProperties;
import org.seasar.kvasir.util.collection.MapProperties;
import org.seasar.kvasir.util.collection.PropertyHandler;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.Resource;

/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PageGardExporterImpl implements PageGardExporter {
    static final String FIELD_XPROPERTIES = PageGardImporterImpl.FIELD_XPROPERTIES;

    static final String RNAME_PREFIX_ATTRIBUTE = PageGardImporterImpl.RNAME_PREFIX_ATTRIBUTE;

    static final String RNAME_SUFFIX_XPROPERTIES = PageGardImporterImpl.RNAME_SUFFIX_XPROPERTIES;

    static final String RNAME_PREFIX_STREAM = PageGardImporterImpl.RNAME_PREFIX_STREAM;

    private PagePlugin pagePlugin_;

    /*
     * PageGardExporter
     */

    public void exports(Resource dir, final Page page) {
        final Resource contentDir = dir.getChildResource(".kv");
        contentDir.mkdirs();

        Page[] pages = page.runWithLocking(new Processable<Page[]>() {
            public Page[] process() throws ProcessableRuntimeException {
                // fieldの情報をエクスポートする。
                exportsFields(contentDir.getChildResource(FIELD_XPROPERTIES),
                        page);

                // PageAbilityAlfr毎の情報をエクスポートする。
                PageAbilityAlfr[] alfrs = pagePlugin_.getPageAbilityAlfrs();
                for (int i = 0; i < alfrs.length; i++) {
                    exportsAbility(contentDir.getChildResource("ability."
                            + alfrs[i].getShortId()), page, alfrs[i]);
                }

                return page.getChildren();
            }
        });

        for (int i = 0; i < pages.length; i++) {
            exports(dir.getChildResource(pages[i].getName()), pages[i]);
        }
    }

    private void exportsFields(Resource resource, Page page) {
        PropertyHandler prop = new MapProperties(new TreeMap<String, String>());

        // type
        prop.setProperty(Page.FIELD_TYPE, page.getType());

        // node
        prop.setProperty(Page.FIELD_NODE, String.valueOf(page.isNode()));

        // lord
        prop.setProperty(Page.FIELD_LORD, PageUtils.encodePathname(page, page
                .getLordPathname()));

        // orderNumber
        prop.setProperty(Page.FIELD_ORDERNUMBER, String.valueOf(page
                .getOrderNumber()));

        // createDate
        prop.setProperty(Page.FIELD_CREATEDATE, String.valueOf(page
                .getCreateDate().getTime()));

        // modifyDate
        prop.setProperty(Page.FIELD_MODIFYDATE, String.valueOf(page
                .getModifyDate().getTime()));

        // revealDate
        prop.setProperty(Page.FIELD_REVEALDATE, String.valueOf(page
                .getRevealDate().getTime()));

        // concealDate
        prop.setProperty(Page.FIELD_CONCEALDATE, String.valueOf(page
                .getConcealDate().getTime()));

        // ownerUser
        prop.setProperty(Page.FIELD_OWNERUSER, PageUtils.encodePathname(page,
                page.getOwnerUser().getPathname()));

        // asFile
        prop.setProperty(Page.FIELD_ASFILE, String.valueOf(page.isAsFile()));

        // listing
        prop.setProperty(Page.FIELD_LISTING, String.valueOf(page.isListing()));

        OutputStream os = null;
        Writer out = null;
        try {
            os = resource.getOutputStream();
            out = new OutputStreamWriter(os, "UTF-8");
            os = null;
            prop.store(out);
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(os);
        }
    }

    private void exportsAbility(Resource dir, Page page,
            PageAbilityAlfr abilityAlfr) {
        boolean exported = false;
        dir.mkdirs();
        I18NProperties prop = new I18NProperties(dir, RNAME_PREFIX_ATTRIBUTE,
                RNAME_SUFFIX_XPROPERTIES);
        String[] variants = abilityAlfr.getVariants(page);
        for (int i = 0; i < variants.length; i++) {
            String variant = variants[i];
            for (Iterator<String> itr = abilityAlfr.attributeNames(page,
                    variant); itr.hasNext();) {
                String name = itr.next();
                Attribute attr = abilityAlfr.getAttribute(page, name, variant);

                String encodedNameString = PageUtils.encodeName(name,
                        PageUtils.NER_PROPNAME);

                for (Iterator<String> itr2 = attr.stringNames(); itr2.hasNext();) {
                    String key = itr2.next();
                    String encoded;
                    if (key.length() == 0) {
                        encoded = encodedNameString;
                    } else {
                        encoded = encodedNameString
                                + "$"
                                + PageUtils.encodeName(key,
                                        PageUtils.NER_PROPNAME);
                    }
                    prop.setProperty(encoded, variant, attr.getString(key));
                    exported = true;
                }

                String encodedNameStream = PageUtils.encodeName(name,
                        PageUtils.NER_FILENAME);
                StringBuffer sb = new StringBuffer()
                        .append(RNAME_PREFIX_STREAM);
                if (variant.length() > 0) {
                    sb.append('_').append(variant);
                }
                sb.append('.');
                String streamNamePrefix = sb.toString();

                for (Iterator<String> itr2 = attr.streamNames(); itr2.hasNext();) {
                    String key = itr2.next();
                    String encoded;
                    if (key.length() == 0) {
                        encoded = streamNamePrefix + encodedNameStream;
                    } else {
                        encoded = streamNamePrefix
                                + encodedNameStream
                                + "$"
                                + PageUtils.encodeName(key,
                                        PageUtils.NER_FILENAME);
                    }
                    Resource stream = dir.getChildResource(encoded);
                    try {
                        IOUtils.pipe(attr.getStream(key).getInputStream(),
                                stream.getOutputStream());
                    } catch (IOException ex) {
                        throw new IORuntimeException(ex);
                    }
                    exported = true;
                }
            }
        }
        try {
            prop.store();
        } catch (IOException ex) {
            throw new IORuntimeException(ex);
        }
        if (!exported) {
            dir.delete();
        }
    }

    /*
     * for framework
     */

    public void setPagePlugin(PagePlugin pagePlugin) {
        pagePlugin_ = pagePlugin;
    }
}
