package org.seasar.kvasir.page.ability.content.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.seasar.kvasir.base.cache.Cache;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.AbstractCachedPageAbilityAlfr;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.AttributeFilter;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.ContentAbilityAlfr;
import org.seasar.kvasir.page.ability.content.ContentAbilityPlugin;
import org.seasar.kvasir.page.ability.content.ContentMold;
import org.seasar.kvasir.util.LocaleUtils;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.io.IOUtils;
import org.seasar.kvasir.util.io.InputStreamFactory;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ContentAbilityAlfrImpl extends AbstractCachedPageAbilityAlfr
    implements ContentAbilityAlfr
{
    private ContentAbilityPlugin plugin_;

    private ContentCache cache_;


    public void setPlugin(ContentAbilityPlugin plugin)
    {
        plugin_ = plugin;
    }


    public void setCache(ContentCache cache)
    {
        cache_ = cache;
    }


    @Override
    protected Cache<?, ?> getCache()
    {
        return cache_;
    }


    @Override
    protected String getCacheId()
    {
        return ContentAbilityPlugin.ID + ".contentAbilityAlfr";
    }


    protected boolean doStart()
    {
        return true;
    }


    protected void doStop()
    {
        plugin_ = null;
        cache_ = null;
    }


    public void clearAllContents(final Page page)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                synchronized (ContentAbilityAlfrImpl.this) {
                    cache_.clearAllContents(page.getDto().getId());
                }
                return null;
            }
        });
    }


    public void removeContentsBefore(final Page page, final String variant,
        final int revisionNumber)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                synchronized (ContentAbilityAlfrImpl.this) {
                    cache_.removeContentsBefore(page.getDto().getId(), variant,
                        revisionNumber);
                }
                return null;
            }
        });
    }


    public void clearContents(final Page page, final String variant)
    {
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                synchronized (ContentAbilityAlfrImpl.this) {
                    cache_.clearContents(page.getDto().getId(), variant);
                }
                return null;
            }
        });
    }


    public synchronized Content getContent(Page page, Locale locale, Date date)
    {
        String[] suffixes = LocaleUtils.getSuffixes(locale, true);
        for (int i = 0; i < suffixes.length; i++) {
            Content content = getContent(page, suffixes[i], getRevisionNumber(
                page, suffixes[i], date));
            if (content != null) {
                return content;
            }
        }
        return null;
    }


    ContentKey newKey(Page page, Locale locale)
    {
        return new ContentKey(page.getDto().getId(), locale);
    }


    public synchronized Content getContent(Page page, String variant,
        int revisionNumber)
    {
        return cache_
            .getContent(page.getDto().getId(), variant, revisionNumber);
    }


    public synchronized int getEarliestRevisionNumber(Page page, String variant)
    {
        return cache_.getEarliestRevisionNumber(page.getDto().getId(), variant);
    }


    public synchronized Content getLatestContent(Page page, String variant)
    {
        return cache_.getContent(page.getDto().getId(), variant,
            getLatestRevisionNumber(page, variant));
    }


    public synchronized Content getLatestContent(Page page, Locale locale)
    {
        return cache_.get(newKey(page, locale));
    }


    public synchronized int getLatestRevisionNumber(Page page, String variant)
    {
        return cache_.getLatestRevisionNumber(page.getDto().getId(), variant);
    }


    public synchronized Date getModifyDate(Page page)
    {
        return cache_.getModifyDate(page.getDto().getId());
    }


    public synchronized int getRevisionNumber(Page page, String variant,
        Date date)
    {
        return cache_.getRevisionNumber(page.getDto().getId(), variant, date);
    }


    public void setContent(Page page, String variant, ContentMold mold)
    {
        updateContent(page, variant, mold, true);
    }


    public void updateContent(Page page, String variant, ContentMold mold)
    {
        updateContent(page, variant, mold, false);
    }


    void updateContent(final Page page, final String variant,
        final ContentMold mold, final boolean overwrite)
    {
        try {
            page.runWithLocking(new Processable<Object>() {
                public Object process()
                    throws ProcessableRuntimeException
                {
                    synchronized (ContentAbilityAlfrImpl.this) {
                        cache_.updateContent(page.getDto().getId(), variant,
                            mold, overwrite);
                    }
                    return null;
                }
            });
        } finally {
            IOUtils.closeQuietly(mold.getBodyInputStream());
        }
    }


    synchronized boolean hasAnyContents(Page page)
    {
        return cache_.hasAnyContents(page.getDto().getId());
    }


    /*
     * PageAbilityAlfr
     */

    public PageAbility getAbility(Page page)
    {
        return new ContentAbilityImpl(this, page);
    }


    public Class<? extends PageAbility> getAbilityInterfaceClass()
    {
        return ContentAbility.class;
    }


    public String getShortId()
    {
        return SHORTID;
    }


    public void create(Page page)
    {
        // 特に何もしない。
    }


    public void delete(Page page)
    {
        clearAllContents(page);
    }


    public String[] getVariants(Page page)
    {
        return cache_.getVariants(page.getDto().getId());
    }


    public Attribute getAttribute(Page page, String name, String variant)
    {
        Attribute attr = null;
        if (ATTR_MODIFYDATE.equals(name)) {
            if (Page.VARIANT_DEFAULT.equals(variant)) {
                attr = new Attribute();
                attr.setString(SUBNAME_DEFAULT, String.valueOf(getModifyDate(
                    page).getTime()));
            }
        } else if (ATTR_EARLIESTREVISIONNUMBER.equals(name)) {
            attr = new Attribute();
            attr.setString(SUBNAME_DEFAULT, String
                .valueOf(getEarliestRevisionNumber(page, variant)));
        } else if (ATTR_LATESTREVISIONNUMBER.equals(name)) {
            attr = new Attribute();
            attr.setString(SUBNAME_DEFAULT, String
                .valueOf(getLatestRevisionNumber(page, variant)));
        } else {
            Content content;
            Integer revisionNumber = parseAsInteger(name);
            if (revisionNumber == null) {
                return null;
            }
            content = getContent(page, variant, revisionNumber);
            if (content == null) {
                return null;
            }

            attr = new Attribute();
            attr.setString(SUBNAME_CREATEDATE, String.valueOf(content
                .getCreateDate().getTime()));
            attr.setString(SUBNAME_MODIFYDATE, String.valueOf(content
                .getModifyDate()));
            attr.setString(SUBNAME_MEDIATYPE, content.getMediaType());
            attr.setString(SUBNAME_ENCODING, content.getEncoding());
            attr.setStream(SUBNAME_DEFAULT, new ContentInputStreamFactory(
                content));
        }
        return attr;
    }


    /*
     * name順にsetAttribute()を呼び出すことで、
     * リビジョンについてもうまく設定していけるようにしている。
     */
    public void setAttribute(Page page, String name, String variant,
        Attribute attr)
    {
        Integer revisionNumber = parseAsInteger(name);
        if (revisionNumber != null) {
            ContentMold mold = new ContentMold();
            mold.setRevisionNumber(revisionNumber);
            String createDate = attr.getString(SUBNAME_CREATEDATE);
            if (createDate != null) {
                mold.setCreateDate(new Date(PropertyUtils.valueOf(createDate,
                    0L)));
            }
            String modifyDate = attr.getString(SUBNAME_MODIFYDATE);
            if (modifyDate != null) {
                mold.setModifyDate(new Date(PropertyUtils.valueOf(modifyDate,
                    0L)));
            }
            String mediaType = attr.getString(SUBNAME_MEDIATYPE);
            if (mediaType != null) {
                mold.setMediaType(mediaType);
            }
            String encoding = attr.getString(SUBNAME_ENCODING);
            if (encoding != null) {
                mold.setEncoding(encoding);
            }
            InputStreamFactory isf = attr.getStream(SUBNAME_DEFAULT);
            if (isf != null) {
                mold.setBodyInputStream(isf.getInputStream());
            }

            updateContent(page, variant, mold);
        }
    }


    public void removeAttribute(final Page page, final String name,
        final String variant)
    {
        // 消せるものは何もない。
    }


    public void clearAttributes(final Page page)
    {
        clearAllContents(page);
    }


    public boolean containsAttribute(Page page, String name, String variant)
    {
        if (ATTR_MODIFYDATE.equals(name)) {
            return Page.VARIANT_DEFAULT.equals(variant);
        } else if (ATTR_EARLIESTREVISIONNUMBER.equals(name)
            || ATTR_LATESTREVISIONNUMBER.equals(name)) {
            return true;
        } else {
            Integer revisionNumber = parseAsInteger(name);
            if (revisionNumber == null) {
                return false;
            }
            return (getContent(page, variant, revisionNumber) != null);
        }
    }


    public Iterator<String> attributeNames(Page page, String variant,
        AttributeFilter filter)
    {
        List<String> list = new ArrayList<String>();
        if (hasAnyContents(page)) {
            if (Page.VARIANT_DEFAULT.equals(variant)) {
                list.add(ATTR_MODIFYDATE);
            }
            list.add(ATTR_EARLIESTREVISIONNUMBER);
            list.add(ATTR_LATESTREVISIONNUMBER);
            if (!filter.isCompact()) {
                int earliest = getEarliestRevisionNumber(page, variant);
                int latest = getLatestRevisionNumber(page, variant);
                String earliestStr = String.valueOf(earliest);
                String latestStr = String.valueOf(latest);
                int earliestWidth = earliestStr.length();
                int latestWidth = latestStr.length();
                int delta = latestWidth - earliestWidth;
                String padding;
                if (delta != 0) {
                    // 桁を揃える。
                    StringBuilder sb = new StringBuilder(delta);
                    for (int i = 0; i < delta; i++) {
                        sb.append('0');
                    }
                    padding = sb.toString();
                } else {
                    padding = "";
                }
                for (int r = earliest; r <= latest; r++) {
                    Content content = getContent(page, variant, r);
                    if (content == null) {
                        continue;
                    }
                    String rStr = String.valueOf(r);
                    list.add(padding.substring(rStr.length() - earliestWidth)
                        + rStr);
                }
            } else {
                Content content = getLatestContent(page, variant);
                if (content != null) {
                    list.add(String.valueOf(content.getRevisionNumber()));
                }
            }
        }
        return list.iterator();
    }


    private Integer parseAsInteger(String attrName)
    {
        try {
            return new Integer(attrName);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
