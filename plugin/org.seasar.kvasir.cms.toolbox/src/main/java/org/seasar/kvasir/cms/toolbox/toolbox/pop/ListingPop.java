package org.seasar.kvasir.cms.toolbox.toolbox.pop;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import org.seasar.kvasir.page.condition.Formula;
import org.seasar.kvasir.page.condition.Order;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.html.HTMLParser;
import org.seasar.kvasir.util.html.HTMLUtils;


/**
 * 一覧表示のためのPOPクラスです。
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

    public static final String PROP_DISPLAYONLYLISTED = "displayOnlyListed";

    public static final String PROP_RECURSIVE = "recursive";

    public static final String PROP_SORTKEY = "sortKey";

    public static final String PROP_SORTKEYFIELD = "sortKeyField";

    public static final String PROP_ASCENDING = "ascending";

    public static final String PROP_SUMMARYSOURCE = "summarySource";

    public static final String PROP_SUMMARYLENGTH = "summaryLength";

    public static final String PROP_CONTINUINGLABEL = "continuingLabel";

    public static final String PROP_OPTION = "option";

    public static final String PROP_PAGING = "paging";

    public static final String PROP_PAGENUMBERKEY = "pageNumberKey";

    public static final String BASEDIRECTORY_CURRENT = ".";

    public static final int NUMBEROFENTRIES_ALL = PageCondition.LENGTH_ALL;

    public static final int NUMBEROFENTRIES_DEFAULT = 10;

    public static final int SUMMARYLENGTH_DEFAULT = 128;

    private static final String SORTKEY_FIELD = "field";

    private static final String SORTKEY_RANDOM = "random";

    private static final String SUMMARYSOURCE_DESCRIPTION = "description";

    private static final int PAGENUMBER_FIRST = 1;

    private AuthPlugin authPlugin_;


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    @Override
    protected RenderedPop render(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        String error = null;
        do {
            Page thatPage = context.getThatPage();
            Locale locale = context.getLocale();

            int heimId = thatPage.getHeimId();
            Page baseDirectory = null;
            String baseDirectoryPath = getProperty(popScope, PROP_BASEDIRECTORY);
            int colon = baseDirectoryPath.indexOf(':');
            if (colon >= 0) {
                try {
                    heimId = Integer.parseInt(baseDirectoryPath.substring(0,
                        colon));
                } catch (NumberFormatException ex) {
                    error = MessageFormat.format(getPlugin().getProperty(
                        "pop.listingPop.error.baseDirectoryPath", locale),
                        baseDirectoryPath);
                    break;
                }
                baseDirectoryPath = baseDirectoryPath.substring(colon + 1);
            }
            if (baseDirectoryPath.equals(BASEDIRECTORY_CURRENT)) {
                if (thatPage != null) {
                    if (thatPage.isNode()) {
                        baseDirectoryPath = thatPage.getPathname();
                    } else {
                        baseDirectoryPath = thatPage.getParentPathname();
                    }
                }
            }
            baseDirectory = getPageAlfr().getPage(heimId, baseDirectoryPath);

            String summarySource = getProperty(popScope, PROP_SUMMARYSOURCE);
            int summaryLength = PropertyUtils.valueOf(getProperty(popScope,
                PROP_SUMMARYLENGTH), SUMMARYLENGTH_DEFAULT);
            String continuingLabel = getProperty(popScope, PROP_CONTINUINGLABEL);
            boolean paging = PropertyUtils.valueOf(getProperty(popScope,
                PROP_PAGING), false);

            Page[] pages;
            if (baseDirectory != null) {
                boolean displayOnlyViewable = PropertyUtils.valueOf(
                    getProperty(popScope, PROP_DISPLAYONLYVIEWABLE), false);
                boolean displayOnlyListed = PropertyUtils.valueOf(getProperty(
                    popScope, PROP_DISPLAYONLYLISTED), true);
                boolean recursive = PropertyUtils.valueOf(getProperty(popScope,
                    PROP_RECURSIVE), false);
                String sortKey = getProperty(popScope, PROP_SORTKEY);
                boolean random = SORTKEY_RANDOM.equals(sortKey);
                if (SORTKEY_FIELD.equals(sortKey)) {
                    sortKey = getProperty(popScope, PROP_SORTKEYFIELD);
                }
                boolean ascending = PropertyUtils.valueOf(getProperty(popScope,
                    PROP_ASCENDING), true);
                int numberOfItems = PropertyUtils.valueOf(getProperty(popScope,
                    PROP_NUMBEROFENTRIES), NUMBEROFENTRIES_DEFAULT);
                boolean gotAll = random
                    || numberOfItems == PageCondition.LENGTH_ALL;
                int pageNumber = getPageNumber(context, popScope);

                int pagesCount = 0;

                Formula option = null;
                String optionString = getProperty(popScope, PROP_OPTION);
                if (optionString != null && optionString.length() > 0) {
                    option = new Formula(optionString);
                }

                User actor = authPlugin_.getCurrentActor();
                PageCondition cond = createConditionForCount(actor,
                    displayOnlyViewable, displayOnlyListed, recursive, option);
                if (paging && !gotAll) {
                    if (baseDirectory.isRoot() && recursive) {
                        // 効率化のためこうしている。
                        pagesCount = getPageAlfr().getPagesCount(heimId, cond);
                    } else {
                        pagesCount = baseDirectory.getChildrenCount(cond);
                    }
                }
                cond = toConditionForPages(cond, paging ? pageNumber
                    : PAGENUMBER_FIRST, numberOfItems, sortKey, ascending,
                    random, option);
                if (baseDirectory.isRoot() && recursive) {
                    // 効率化のためこうしている。
                    pages = getPageAlfr().getPages(heimId, cond);
                } else {
                    pages = baseDirectory.getChildren(cond);
                }
                if (paging && gotAll) {
                    pagesCount = pages.length;
                }
                if (random) {
                    randomize(pages, numberOfItems);
                }

                if (paging && !gotAll && pagesCount > numberOfItems) {
                    popScope.put("pager", new Pager(pagesCount, numberOfItems,
                        pageNumber));
                }
            } else {
                pages = new Page[0];
            }

            ListingEntry[] entries = new ListingEntry[pages.length];
            for (int i = 0; i < pages.length; i++) {
                entries[i] = new ListingEntry(pages[i], locale, summarySource,
                    summaryLength, continuingLabel);
            }
            popScope.put("entries", entries);
        } while (false);

        if (error != null) {
            popScope.put("error", error);
        }

        return super.render(context, args, popScope);
    }


    int getPageNumber(PopContext context, Map<String, Object> popScope)
    {
        HttpServletRequest request = context.getRequest();
        if (request == null) {
            return PAGENUMBER_FIRST;
        }

        String pageNumber = request.getParameter(getProperty(popScope,
            PROP_PAGENUMBERKEY));
        if (pageNumber == null) {
            return PAGENUMBER_FIRST;
        }

        try {
            return Integer.parseInt(pageNumber);
        } catch (Throwable t) {
            return PAGENUMBER_FIRST;
        }
    }


    Page[] randomize(Page[] pages, int length)
    {
        List<Page> randomized = new ArrayList<Page>();
        LinkedList<Page> list = new LinkedList<Page>(Arrays.asList(pages));
        if (length < 0 || length > pages.length) {
            length = pages.length;
        }
        for (int i = 0; i < length; i++) {
            randomized.add(list.remove((int)(Math.random() * list.size())));
        }
        return randomized.toArray(new Page[0]);
    }


    PageCondition toConditionForPages(PageCondition cond, int pageNumber,
        int numberOfItems, String sortKey, boolean ascending, boolean random,
        Formula option)
    {
        if (!random) {
            cond.setOrder(new Order(sortKey, ascending)).setLength(
                numberOfItems);
            if (numberOfItems != PageCondition.LENGTH_ALL) {
                cond.setOffset(PageCondition.OFFSET_FIRST + (pageNumber - 1)
                    * numberOfItems);
            }
        }
        return cond;
    }


    protected PageCondition createConditionForCount(User actor,
        boolean displayOnlyViewable, boolean displayOnlyListed,
        boolean recursive, Formula option)
    {
        return new PageCondition().setIncludeConcealed(actor.isAdministrator())
            .setOnlyListed(displayOnlyListed).setRecursive(recursive).setUser(
                actor).setOption(new Formula(Page.FIELD_NODE + "=false"))
            .setPrivilege(
                displayOnlyViewable ? Privilege.ACCESS_VIEW
                    : Privilege.ACCESS_PEEK).addOption(option);
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

        private String summarySource_;

        private int summaryLength_;

        private String summary_;

        private Boolean continuing_;

        private String continuingLabel_;

        private String styleClass_;

        private String author_;

        private Date date_;


        public ListingEntry(Page page, Locale locale, String summarySource,
            int summaryLength, String continuingLabel)
        {
            page_ = page;
            locale_ = locale;
            summarySource_ = summarySource;
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
            String summarySource = null;
            if (SUMMARYSOURCE_DESCRIPTION.equals(summarySource_)) {
                summarySource = page_.getAbility(PropertyAbility.class)
                    .getProperty(PropertyAbility.PROP_DESCRIPTION, locale_);
            } else {
                if (!page_.isAsFile()) {
                    Content content = page_.getAbility(ContentAbility.class)
                        .getLatestContent(locale_);
                    if (content != null) {
                        HTMLParser parser = new HTMLParser(content
                            .getBodyHTMLString(VariableResolver.EMPTY));
                        summarySource = HTMLUtils.filter(parser.getSummary());
                    }
                }
            }
            if (summarySource == null) {
                summarySource = "";
            }

            if (summaryLength_ == SUMMARYLENGTH_ALL
                || summarySource.length() <= summaryLength_) {
                summary_ = summarySource;
                continuing_ = Boolean.FALSE;
            } else {
                summary_ = summarySource.substring(0, summaryLength_).concat(
                    SUFFIX_LEADING);
                continuing_ = Boolean.TRUE;
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

    public static class Pager
    {
        public static class Element
        {
            private int pageNumber;

            private boolean current;


            private Element(int pageNumber, boolean current)
            {
                this.pageNumber = pageNumber;
                this.current = current;
            }


            public int getPageNumber()
            {
                return pageNumber;
            }


            public boolean isCurrent()
            {
                return current;
            }
        }


        private int pagesCount;

        private int currentPageNumber;

        private List<Element> elements;


        public Pager(int itemsCount, int itemsPerPage, int currentPageNumber)
        {
            pagesCount = (itemsCount + itemsPerPage - 1) / itemsPerPage;
            this.currentPageNumber = currentPageNumber;

            List<Element> elements = new ArrayList<Element>();
            for (int pageNumber = PAGENUMBER_FIRST; pageNumber <= pagesCount; pageNumber++) {
                elements.add(new Element(pageNumber,
                    pageNumber == currentPageNumber));
            }
            this.elements = elements;
        }


        public boolean isFirst()
        {
            return currentPageNumber == PAGENUMBER_FIRST;
        }


        public boolean isLast()
        {
            return currentPageNumber == pagesCount;
        }


        public int getPreviousPageNumber()
        {
            return currentPageNumber - 1;
        }


        public int getNextPageNumber()
        {
            return currentPageNumber + 1;
        }


        public List<Element> getElements()
        {
            return elements;
        }
    }
}
