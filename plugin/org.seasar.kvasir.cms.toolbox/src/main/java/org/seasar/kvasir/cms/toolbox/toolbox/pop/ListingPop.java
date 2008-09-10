package org.seasar.kvasir.cms.toolbox.toolbox.pop;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.pop.GenericPop;
import org.seasar.kvasir.cms.pop.util.PresentationUtils;
import org.seasar.kvasir.cms.toolbox.ToolboxPlugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.condition.Order;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.html.HTMLParser;
import org.seasar.kvasir.util.html.HTMLUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ListingPop extends GenericPop
{
    public static final String ID = ToolboxPlugin.ID + ".listingPop";

    public static final String PROP_BASEDIRECTORY = "baseDirectory";

    public static final String PROP_NUMBEROFENTRIES = "numberOfEntries";

    public static final String PROP_DISPLAYONLYVIEWABLE = "displayOnlyViewable";

    public static final String PROP_SORTKEY = "sortKey";

    public static final String PROP_ASCENDING = "ascending";

    public static final String PROP_SUMMARYLENGTH = "summaryLength";

    public static final String PROP_CONTINUINGLABEL = "continuingLabel";

    public static final String BASEDIRECTORY_CURRENT = ".";

    public static final int NUMBEROFENTRIES_ALL = PageCondition.LENGTH_ALL;

    public static final int NUMBEROFENTRIES_DEFAULT = 10;

    public static final int SUMMARYLENGTH_DEFAULT = 128;

    private AuthPlugin authPlugin_;


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    @Override
    protected RenderedPop render(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        Page thatPage = context.getThatPage();

        Page baseDirectory = null;
        String baseDirectoryPath = getProperty(popScope, PROP_BASEDIRECTORY);
        if (baseDirectoryPath.equals(BASEDIRECTORY_CURRENT)) {
            if (thatPage != null) {
                if (thatPage.isNode()) {
                    baseDirectory = thatPage;
                } else {
                    baseDirectory = thatPage.getParent();
                }
            }
        } else {
            if (thatPage != null) {
                baseDirectory = getPageAlfr().getPage(thatPage.getHeimId(),
                    baseDirectoryPath);
            }
        }

        Locale locale = context.getLocale();
        int summaryLength = PropertyUtils.valueOf(getProperty(popScope,
            PROP_SUMMARYLENGTH), SUMMARYLENGTH_DEFAULT);
        String continuingLabel = getProperty(popScope, PROP_CONTINUINGLABEL);
        boolean displayOnlyViewable = PropertyUtils.valueOf(getProperty(
            popScope, PROP_DISPLAYONLYVIEWABLE), false);

        Page[] pages;
        if (baseDirectory != null) {
            String sortKey = getProperty(popScope, PROP_SORTKEY);
            boolean ascending = PropertyUtils.valueOf(getProperty(popScope,
                PROP_ASCENDING), true);
            int numberOfItems = PropertyUtils.valueOf(getProperty(popScope,
                PROP_NUMBEROFENTRIES), NUMBEROFENTRIES_DEFAULT);

            User actor = authPlugin_.getCurrentActor();
            pages = baseDirectory.getChildren(new PageCondition().setType(
                Page.TYPE).setIncludeConcealed(actor.isAdministrator())
                .setOnlyListed(true).setUser(actor).setPrivilege(
                    displayOnlyViewable ? Privilege.ACCESS_VIEW
                        : Privilege.ACCESS_PEEK).setOrder(
                    new Order(sortKey, ascending)).setLength(numberOfItems));
        } else {
            pages = new Page[0];
        }

        ListingEntry[] entries = new ListingEntry[pages.length];
        for (int i = 0; i < pages.length; i++) {
            entries[i] = new ListingEntry(pages[i], locale, summaryLength,
                continuingLabel);
        }

        popScope.put("entries", entries);
        return super.render(context, args, popScope);
    }


    public static class ListingEntry
    {
        private static final String SUFFIX_LEADING = "...";

        private static final int SUMMARYLENGTH_ALL = -1;

        private static final String CLASS_CONCEALED = "concealed";

        private static final String PROP_DATE = "date";

        private static final String PROP_AUTHOR = "author";

        private Page page_;

        private Locale locale_;

        private int summaryLength_;

        private String summary_;

        private Boolean continuing_;

        private String continuingLabel_;

        private String styleClass_;

        private String author_;

        private Date date_;


        public ListingEntry(Page page, Locale locale, int summaryLength,
            String continuingLabel)
        {
            page_ = page;
            locale_ = locale;
            summaryLength_ = summaryLength;
            continuingLabel_ = continuingLabel;
            styleClass_ = page.isConcealed() ? CLASS_CONCEALED : null;
            author_ = getAuthor(page, locale);
            date_ = getDate(page);
        }


        String getAuthor(Page page, Locale locale)
        {
            return page.getAbility(PropertyAbility.class).getProperty(
                PROP_AUTHOR, locale);
        }


        Date getDate(Page page)
        {
            String dateString = page.getAbility(PropertyAbility.class)
                .getProperty(PROP_DATE);
            try {
                return PageUtils.parseDate(dateString);
            } catch (Throwable t) {
                return null;
            }
        }


        public Page getPage()
        {
            return page_;
        }


        public String getLabel()
        {
            String label = page_.getAbility(PropertyAbility.class).getProperty(
                PropertyAbility.PROP_LABEL, locale_);
            if (label == null) {
                label = page_.getName();
            }
            return label;
        }


        public String getDescription()
        {
            return page_.getAbility(PropertyAbility.class).getProperty(
                PropertyAbility.PROP_DESCRIPTION, locale_);
        }


        public String getIconURL()
        {
            return PresentationUtils.getIconURL(page_);
        }


        public String getSummary()
        {
            if (summary_ == null) {
                fetchSummary();
            }
            return summary_;
        }


        void fetchSummary()
        {
            summary_ = "";
            continuing_ = Boolean.FALSE;

            if (!page_.isAsFile()) {
                Content content = page_.getAbility(ContentAbility.class)
                    .getLatestContent(locale_);
                if (content != null) {
                    String bodyHTMLString = content
                        .getBodyHTMLString(VariableResolver.EMPTY);
                    if (summaryLength_ == SUMMARYLENGTH_ALL) {
                        summary_ = bodyHTMLString;
                        continuing_ = Boolean.FALSE;
                    } else {
                        HTMLParser parser = new HTMLParser(bodyHTMLString);
                        String body = parser.getSummary();
                        if (body.length() <= summaryLength_) {
                            summary_ = HTMLUtils.filter(body);
                            continuing_ = Boolean.FALSE;
                        } else {
                            summary_ = HTMLUtils.filter(body.substring(0,
                                summaryLength_).concat(SUFFIX_LEADING));
                            continuing_ = Boolean.TRUE;
                        }
                    }
                }
            }
        }


        public boolean isContinuing()
        {
            if (continuing_ == null) {
                fetchSummary();
            }
            return continuing_.booleanValue();
        }


        public String getContinuingLabel()
        {
            return continuingLabel_;
        }


        public String getStyleClass()
        {
            return styleClass_;
        }


        public String getAuthor()
        {
            return author_;
        }


        public Date getDate()
        {
            return date_;
        }
    }
}
