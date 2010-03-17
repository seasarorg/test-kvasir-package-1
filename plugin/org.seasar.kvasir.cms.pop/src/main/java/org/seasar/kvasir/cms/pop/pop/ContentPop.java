package org.seasar.kvasir.cms.pop.pop;

import static org.seasar.kvasir.cms.pop.ValidationResult.ERROR_ASIS;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.PopContextWrapper;
import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.cms.pop.PopPropertyEntry;
import org.seasar.kvasir.cms.pop.PopPropertyEntryBag;
import org.seasar.kvasir.cms.pop.PopPropertyMetaData;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.ValidationResult;
import org.seasar.kvasir.cms.pop.util.PresentationUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.ability.content.ContentMold;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.html.HTMLUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ContentPop extends GenericPop
{
    public static final String ID = PopPlugin.ID + ".contentPop";

    public static final String PROP_USE_PAGE_LABEL_AS_TITLE = "usePageLabelAsTitle";

    public static final String PROP_SHOW_INFORMATION = "showInformation";

    public static final String PROP_SHOW_DESCRIPTION = "showDescription";

    public static final String PROP_SHOW_CONTENT = "showContent";

    public static final String PROP_CONTENT_MEDIA_TYPE = "contentMediaType";

    public static final String PROP_CONTENT_LABEL = "contentLabel";

    public static final String PROP_CONTENT_DESCRIPTION = "contentDescription";

    public static final String PROP_CONTENT_BODY = "contentBody";

    public static final String PROP_AUTHOR = "author";

    public static final String PROP_DATE = "date";

    public static final String PROP_ICON = "icon";

    public static final String PROP_DEFAULT_CONTENT_MEDIA_TYPE = "defaultContentMediaType";

    public static final String PROP_PATHNAME = "pathname";

    public static final String ERROR_INVALIDVALUE = "pop.contentPop.error.invalidValue";


    @Override
    protected Map<String, Object> populatePropertiesTo(
        Map<String, Object> popScope, PopContext context)
    {
        Locale locale = context.getLocale();
        if (popScope != null) {
            PopPropertyMetaData[] metaDatas = getPropertyMetaDatas();
            for (int i = 0; i < metaDatas.length; i++) {
                String id = metaDatas[i].getId();
                if (PROP_CONTENT_BODY.equals(id)
                    || PROP_CONTENT_MEDIA_TYPE.equals(id)) {
                    // レンダリング時に直接取得するようにしているのでスキップする。
                    continue;
                }
                popScope.put(id, getProperty(context, id, locale));
            }
            putPropertyUnlessContained(popScope, context, PROP_TITLE, locale);
            putPropertyUnlessContained(popScope, context, PROP_BODY, locale);
            putPropertyUnlessContained(popScope, context, PROP_BODY_TYPE,
                locale);
        }
        return popScope;
    }


    @Override
    public String getProperty(PopContext context,
        Map<String, PopPropertyEntry> map, String id, String variant)
    {
        PopPropertyEntry entry = map.get(PROP_PATHNAME);
        Page page = getPage(context, entry != null ? entry.getValue() : null);

        if (PROP_CONTENT_MEDIA_TYPE.equals(id)) {
            if (page != null) {
                Content content = page.getAbility(ContentAbility.class)
                    .getLatestContent(variant);
                if (content != null) {
                    return content.getMediaType();
                }
            }
            return getProperty(context, map, PROP_DEFAULT_CONTENT_MEDIA_TYPE,
                variant);
        } else if (PROP_CONTENT_LABEL.equals(id)) {
            if (page != null) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_LABEL, variant);
            } else {
                return null;
            }
        } else if (PROP_AUTHOR.equals(id)) {
            if (page != null) {
                return getAuthor(page, variant);
            } else {
                return null;
            }
        } else if (PROP_DATE.equals(id)) {
            if (page != null) {
                return getDateString(page);
            } else {
                return null;
            }
        } else if (PROP_ICON.equals(id)) {
            if (page != null) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PresentationUtils.PROP_PAGE_ICON);
            } else {
                return null;
            }
        } else if (PROP_CONTENT_DESCRIPTION.equals(id)) {
            if (page != null) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_DESCRIPTION, variant);
            } else {
                return null;
            }
        } else if (PROP_CONTENT_BODY.equals(id)) {
            if (page != null) {
                Content content = page.getAbility(ContentAbility.class)
                    .getLatestContent(variant);
                if (content != null
                    && content.getMediaType().startsWith("text/")) {
                    return content.getBodyString();
                }
            }
            return null;
        }
        return super.getProperty(context, map, id, variant);
    }


    @Override
    protected RenderedPop render(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        String title = "";
        String body = "";

        Page page = getPage(context, getProperty(popScope, PROP_PATHNAME));
        Locale locale = context.getLocale();

        if (PropertyUtils.valueOf(getProperty(popScope,
            PROP_USE_PAGE_LABEL_AS_TITLE), true)) {
            if (page != null) {
                title = HTMLUtils.filter(getProperty(popScope,
                    PROP_CONTENT_LABEL));
            }
        } else {
            title = getElement().renderTitle(getProperty(popScope, PROP_TITLE),
                context);
        }

        if (page != null) {
            if (PropertyUtils.valueOf(getProperty(popScope,
                PROP_SHOW_INFORMATION), true)) {
                popScope.put(PROP_SHOW_INFORMATION, Boolean.TRUE);
                popScope.put(PROP_AUTHOR, getAuthor(page, locale));
                popScope.put(PROP_DATE, getDate(page));
            } else {
                popScope.remove(PROP_SHOW_INFORMATION);
            }
            if (PropertyUtils.valueOf(getProperty(popScope,
                PROP_SHOW_DESCRIPTION), true)) {
                String description = getProperty(popScope,
                    PROP_CONTENT_DESCRIPTION);
                if (description != null && description.length() > 0) {
                    popScope.put(PROP_SHOW_DESCRIPTION, Boolean.TRUE);
                    popScope.put(PROP_CONTENT_DESCRIPTION, description);
                } else {
                    popScope.remove(PROP_SHOW_DESCRIPTION);
                }
            } else {
                popScope.remove(PROP_SHOW_DESCRIPTION);
            }
            if (PropertyUtils.valueOf(getProperty(popScope, PROP_SHOW_CONTENT),
                true)) {
                String contentBody;
                if (context.isInPreviewMode()) {
                    contentBody = PresentationUtils.getHTMLBodyString(page,
                        getProperty(popScope, PROP_CONTENT_MEDIA_TYPE),
                        getProperty(popScope, PROP_CONTENT_BODY), context);
                } else {
                    // ページがコンパイル済みボディを持っている場合にそれがレンダリングに
                    // 利用されるよう、プレビューモードとは違うやり方でコンテンツを
                    // レンダリングしている。
                    Content content = page.getAbility(ContentAbility.class)
                        .getLatestContent(locale);
                    if (content != null) {
                        contentBody = content.getBodyHTMLString(null);
                    } else {
                        contentBody = "";
                    }
                }
                if (contentBody.length() > 0) {
                    popScope.put(PROP_SHOW_CONTENT, Boolean.TRUE);
                    popScope.put(PROP_CONTENT_BODY, contentBody);
                } else {
                    popScope.remove(PROP_SHOW_CONTENT);
                }
            } else {
                popScope.remove(PROP_SHOW_CONTENT);
            }

            body = getElement().renderBodyWithoutException(
                getProperty(popScope, PROP_BODY),
                getProperty(popScope, PROP_BODY_TYPE),
                new PopContextWrapper(context, popScope));
        }

        return new RenderedPop(getId(), getPopId(), title, body);
    }


    Page getPage(PopContext context)
    {
        return getPage(context, getProperty(context, PROP_PATHNAME,
            Page.VARIANT_DEFAULT));
    }


    Page getPage(PopContext context, String pathname)
    {
        Page thatPage = context.getThatPage();
        if (pathname == null) {
            return thatPage;
        } else if ("".equals(pathname)) {
            return context.getPageRequest().getRootPage();
        }
        Page page = null;
        if (thatPage != null) {
            if (".".equals(pathname)) {
                // 高速化のため。
                page = thatPage;
            } else {
                page = context.getPageRequest().getRootPage().getChild(
                    PageUtils.getAbsolutePathname(pathname, thatPage));
            }
        } else {
            PageDispatch dispatch = context.getThat();
            if (dispatch != null) {
                Page rootPage = context.getPageRequest().getRootPage();
                page = rootPage.getChild(PageUtils.getAbsolutePathname(
                    pathname, getPageAlfr().findNearestPage(
                        rootPage.getHeimId(), dispatch.getPathname())
                        .getLordPathname(), dispatch.getPathname()));
            }
        }
        return page;
    }


    Date getDate(Page page)
    {
        try {
            return parseDate(getDateString(page));
        } catch (ParseException ex) {
            throw new RuntimeException(ex);
        }
    }


    Date parseDate(String dateString)
        throws ParseException
    {
        return PageUtils.parseDate(dateString);
    }


    String getDateString(Page page)
    {
        String dateString = page.getAbility(PropertyAbility.class).getProperty(
            PROP_DATE);
        if (dateString == null) {
            dateString = page.getModifyDateString();
        }
        return dateString;
    }


    String getAuthor(Page page, Locale locale)
    {
        return page.getAbility(PropertyAbility.class).getProperty(PROP_AUTHOR,
            locale);
    }


    String getAuthor(Page page, String variant)
    {
        return page.getAbility(PropertyAbility.class).getProperty(PROP_AUTHOR,
            variant);
    }


    @Override
    public ValidationResult validateProperties(PopContext context,
        String variant, PopPropertyEntry[] entries)
    {
        ValidationResult result = new ValidationResult();
        PopPropertyEntryBag bag = new PopPropertyEntryBag(entries);

        String date = bag.getProperty(PROP_DATE);
        if (date != null) {
            try {
                parseDate(date);
            } catch (ParseException ex) {
                result.addEntry(new ValidationResult.Entry(PROP_DATE,
                    ERROR_INVALIDVALUE));
            }
        }

        String contentBody = bag.getProperty(PROP_CONTENT_BODY);
        if (contentBody != null) {
            String contentMediaType = getProperty(bag, context,
                PROP_CONTENT_MEDIA_TYPE, variant);
            try {
                PresentationUtils.getHTMLBodyString(getPage(context, bag
                    .getProperty(PROP_PATHNAME)), contentMediaType,
                    contentBody, context);
            } catch (Throwable t) {
                result.addEntry(new ValidationResult.Entry(PROP_CONTENT_BODY,
                    ERROR_ASIS, t.toString()));
            }
        }

        return result;
    }


    @Override
    public String getProperty(PopContext context, String id, String variant)
    {
        if (PROP_CONTENT_MEDIA_TYPE.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                Content content = page.getAbility(ContentAbility.class)
                    .getLatestContent(variant);
                if (content != null) {
                    return content.getMediaType();
                }
            }
            return getProperty(context, PROP_DEFAULT_CONTENT_MEDIA_TYPE,
                variant);
        } else if (PROP_CONTENT_LABEL.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_LABEL, variant);
            } else {
                return null;
            }
        } else if (PROP_AUTHOR.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                return getAuthor(page, variant);
            } else {
                return null;
            }
        } else if (PROP_DATE.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                return getDateString(page);
            } else {
                return null;
            }
        } else if (PROP_ICON.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PresentationUtils.PROP_PAGE_ICON);
            } else {
                return null;
            }
        } else if (PROP_CONTENT_DESCRIPTION.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_DESCRIPTION, variant);
            } else {
                return null;
            }
        } else if (PROP_CONTENT_BODY.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                Content content = page.getAbility(ContentAbility.class)
                    .getLatestContent(variant);
                if (content != null
                    && content.getMediaType().startsWith("text/")) {
                    return content.getBodyString();
                }
            }
            return null;
        }
        return super.getProperty(context, id, variant);
    }


    @Override
    public String getProperty(PopContext context, String id, Locale locale)
    {
        if (PROP_CONTENT_MEDIA_TYPE.equals(id)) {
            return getProperty(context, id, Page.VARIANT_DEFAULT);
        } else if (PROP_CONTENT_LABEL.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_LABEL, locale);
            } else {
                return null;
            }
        } else if (PROP_AUTHOR.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                return getAuthor(page, locale);
            } else {
                return null;
            }
        } else if (PROP_DATE.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                return getDateString(page);
            } else {
                return null;
            }
        } else if (PROP_ICON.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PresentationUtils.PROP_PAGE_ICON);
            } else {
                return null;
            }
        } else if (PROP_CONTENT_DESCRIPTION.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_DESCRIPTION, locale);
            } else {
                return null;
            }
        } else if (PROP_CONTENT_BODY.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                Content content = page.getAbility(ContentAbility.class)
                    .getLatestContent(locale);
                if (content != null
                    && content.getMediaType().startsWith("text/")) {
                    return content.getBodyString();
                }
            }
            return null;
        }
        return super.getProperty(context, id, locale);
    }


    @Override
    public void setProperty(PopContext context, String id, String variant,
        String value)
    {
        // contentMediaTypeは最初defaultMediaTypeの値がセットされるようになっているため、
        // そのまま確定させても値が変わっていないとみなされて保存されない。それを避けるためにこうしている。
        if (PROP_CONTENT_MEDIA_TYPE.equals(id)) {
            Page page = getPage(context, getProperty(context, PROP_PATHNAME,
                Page.VARIANT_DEFAULT));
            if (page != null) {
                ContentAbility ability = page.getAbility(ContentAbility.class);
                ContentMold mold = null;
                if (ability.getLatestContent(variant) == null) {
                    mold = new ContentMold().setMediaType(value).setBodyString(
                        "");
                } else {
                    if (isModified(context, id, variant, value)) {
                        mold = new ContentMold().setMediaType(value);
                    }
                }
                if (mold != null) {
                    ability.setContent(variant, mold);
                }
            }
            return;
        }
        super.setProperty(context, id, variant, value);
    }


    @Override
    protected void setProperty0(PopContext context, String id, String variant,
        String value)
    {
        if (PROP_CONTENT_LABEL.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                page.getAbility(PropertyAbility.class).setProperty(
                    PropertyAbility.PROP_LABEL, variant, value);
            }
            return;
        } else if (PROP_AUTHOR.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                page.getAbility(PropertyAbility.class).setProperty(PROP_AUTHOR,
                    variant, value);
            } else {
                return;
            }
        } else if (PROP_DATE.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                page.getAbility(PropertyAbility.class).setProperty(PROP_DATE,
                    value);
            } else {
                return;
            }
        } else if (PROP_ICON.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                page.getAbility(PropertyAbility.class).setProperty(
                    PresentationUtils.PROP_PAGE_ICON, value);
            } else {
                return;
            }
        } else if (PROP_CONTENT_DESCRIPTION.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                page.getAbility(PropertyAbility.class).setProperty(
                    PropertyAbility.PROP_DESCRIPTION, variant, value);
            }
            return;
        } else if (PROP_CONTENT_BODY.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                page.getAbility(ContentAbility.class).setContent(variant,
                    new ContentMold().setBodyString(value));
            }
            return;
        }
        super.setProperty0(context, id, variant, value);
    }


    @Override
    public void setProperties(PopContext context, String variant,
        PopPropertyEntry[] entries)
    {
        String pathname = null;
        for (int i = 0; i < entries.length; i++) {
            if (PROP_PATHNAME.equals(entries[i].getId())) {
                pathname = entries[i].getValue();
                break;
            }
        }
        // pathnameが変わった場合はページ関連のプロパティ値を更新しないようにしている。
        // そうしないと意図しない値がページに設定されてしまうため。
        boolean setPageDependentProperties = pathname.equals(getProperty(
            context, PROP_PATHNAME, Page.VARIANT_DEFAULT));

        Page page = getPage(context, pathname);
        if (page != null) {
            ContentMold mold = new ContentMold();
            boolean contentModified = false;
            List<PopPropertyEntry> entryList = new ArrayList<PopPropertyEntry>();
            for (int i = 0; i < entries.length; i++) {
                String id = entries[i].getId();
                String value = entries[i].getValue();

                if (PROP_CONTENT_MEDIA_TYPE.equals(id)) {
                    if (setPageDependentProperties) {
                        ContentAbility ability = page
                            .getAbility(ContentAbility.class);
                        // contentMediaTypeは最初defaultMediaTypeの値がセットされるようになっているため、
                        // そのまま確定させても値が変わっていないとみなされて保存されない。それを避けるためにこうしている。
                        mold.setMediaType(value);
                        if (ability.getLatestContent(variant) != null) {
                            contentModified = true;
                        }
                    }
                } else if (PROP_CONTENT_BODY.equals(id)) {
                    if (setPageDependentProperties) {
                        if (isModified(context, id, variant, value)) {
                            mold.setBodyString(value);
                            contentModified = true;
                        }
                    }
                } else if (PROP_CONTENT_DESCRIPTION.equals(id)
                    || PROP_CONTENT_LABEL.equals(id) || PROP_AUTHOR.equals(id)
                    || PROP_DATE.equals(id) || PROP_ICON.equals(id)) {
                    if (setPageDependentProperties) {
                        entryList.add(entries[i]);
                    }
                } else {
                    entryList.add(entries[i]);
                }
            }

            if (contentModified) {
                page.getAbility(ContentAbility.class).setContent(variant, mold);
            }

            entries = entryList.toArray(new PopPropertyEntry[0]);
        }

        super.setProperties(context, variant, entries);

        if (page != null) {
            page.touch();
        }
    }


    @Override
    public void removeProperty0(PopContext context, String id, String variant)
    {
        if (PROP_CONTENT_MEDIA_TYPE.equals(id)) {
            return;
        } else if (PROP_CONTENT_LABEL.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                page.getAbility(PropertyAbility.class).removeProperty(
                    PropertyAbility.PROP_LABEL, variant);
            }
            return;
        } else if (PROP_AUTHOR.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                page.getAbility(PropertyAbility.class).removeProperty(
                    PROP_AUTHOR, variant);
            } else {
                return;
            }
        } else if (PROP_DATE.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                page.getAbility(PropertyAbility.class)
                    .removeProperty(PROP_DATE);
            } else {
                return;
            }
        } else if (PROP_ICON.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                page.getAbility(PropertyAbility.class).removeProperty(
                    PresentationUtils.PROP_PAGE_ICON);
            } else {
                return;
            }
        } else if (PROP_CONTENT_DESCRIPTION.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                page.getAbility(PropertyAbility.class).removeProperty(
                    PropertyAbility.PROP_DESCRIPTION, variant);
            }
            return;
        } else if (PROP_CONTENT_BODY.equals(id)) {
            Page page = getPage(context);
            if (page != null) {
                page.getAbility(ContentAbility.class).clearContents(variant);
            }
            return;
        }
        super.removeProperty0(context, id, variant);
    }


    @Override
    protected PopPropertyEntryBag newPopPropertyEntryBag(PopContext context,
        String variant, PopPropertyEntry[] entries)
    {
        PopPropertyEntryBag bag = super.newPopPropertyEntryBag(context,
            variant, entries);

        String pathname = null;
        for (int i = 0; i < entries.length; i++) {
            if (PROP_PATHNAME.equals(entries[i].getId())) {
                pathname = entries[i].getValue();
                break;
            }
        }
        if (!pathname.equals(getProperty(context, PROP_PATHNAME,
            Page.VARIANT_DEFAULT))) {
            // pathnameが変わった場合はページ関連のプロパティ値を変更先ページから取得するようにする。
            Page page = getPage(context, pathname);
            bag.putProperty(PROP_CONTENT_LABEL, page != null ? page.getAbility(
                PropertyAbility.class).getProperty(PropertyAbility.PROP_LABEL,
                variant) : "");
            bag.putProperty(PROP_AUTHOR, page != null ? page.getAbility(
                PropertyAbility.class).getProperty(PROP_AUTHOR, variant) : "");
            bag.putProperty(PROP_DATE, page != null ? page.getAbility(
                PropertyAbility.class).getProperty(PROP_DATE, variant) : "");
            bag.putProperty(PROP_ICON, page != null ? page.getAbility(
                PropertyAbility.class).getProperty(
                PresentationUtils.PROP_PAGE_ICON, variant) : "");
            bag.putProperty(PROP_CONTENT_DESCRIPTION, page != null ? page
                .getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_DESCRIPTION, variant) : "");

            String contentMediaType = "";
            String contentBody = "";
            if (page != null) {
                Content content = page.getAbility(ContentAbility.class)
                    .getLatestContent(variant);
                if (content != null
                    && content.getMediaType().startsWith("text/")) {
                    contentMediaType = content.getMediaType();
                    contentBody = content.getBodyString();
                }
            }
            if (contentMediaType == null) {
                contentMediaType = bag
                    .getProperty(PROP_DEFAULT_CONTENT_MEDIA_TYPE);
            }
            bag.putProperty(PROP_CONTENT_MEDIA_TYPE, contentMediaType);
            bag.putProperty(PROP_CONTENT_BODY, contentBody);
        }
        return bag;
    }
}
