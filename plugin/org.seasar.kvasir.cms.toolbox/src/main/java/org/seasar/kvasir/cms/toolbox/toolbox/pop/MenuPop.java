package org.seasar.kvasir.cms.toolbox.toolbox.pop;

import static org.seasar.kvasir.page.Page.PROTOCOL_PAGE;
import static org.seasar.kvasir.page.ability.PropertyAbility.PROP_DESCRIPTION;
import static org.seasar.kvasir.page.ability.PropertyAbility.PROP_LABEL;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.pop.GenericPop;
import org.seasar.kvasir.cms.toolbox.ToolboxPlugin;
import org.seasar.kvasir.cms.util.PresentationUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.page.type.Directory;
import org.seasar.kvasir.page.type.User;
import org.seasar.kvasir.util.PropertyUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class MenuPop extends GenericPop
{
    public static final String ID = ToolboxPlugin.ID + ".menuPop";

    private static final int COLS = 2;

    public static final String PROP_ONLYDIRECTORY = "onlyDirectory";

    public static final String PROP_DISPLAYDESCRIPTION = "displayDescription";

    public static final String PROP_DISPLAYONLYVIEWABLE = "displayOnlyViewable";

    public static final String PROP_COLS = "cols";

    private static final String CLASS_CONCEALED = "concealed";

    private static final String CLASS_ENTRY = "entry";

    private AuthPlugin authPlugin_;


    public void setAuthPlugin(AuthPlugin authPlugin)
    {
        authPlugin_ = authPlugin;
    }


    @Override
    protected RenderedPop render(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        Page page = context.getThatPage();
        if (page == null) {
            return new RenderedPop(this, "", "");
        }

        User actor = authPlugin_.getCurrentActor();
        int cols = getCols(context);
        boolean onlyDirectory = PropertyUtils.valueOf(getProperty(popScope,
            PROP_ONLYDIRECTORY), false);
        boolean displayDescription = PropertyUtils.valueOf(getProperty(
            popScope, PROP_DISPLAYDESCRIPTION), false);
        boolean displayOnlyViewable = PropertyUtils.valueOf(getProperty(
            popScope, PROP_DISPLAYONLYVIEWABLE), false);

        Page[] pages;
        if (page != null) {
            PageCondition condition = new PageCondition().setIncludeConcealed(
                actor.isAdministrator()).setOnlyListed(true).setUser(actor)
                .setPrivilege(
                    displayOnlyViewable ? Privilege.ACCESS_VIEW
                        : Privilege.ACCESS_PEEK);
            if (onlyDirectory) {
                condition.setType(Directory.TYPE);
            }
            pages = page.getChildren(condition);
        } else {
            pages = new Page[0];
        }
        List<Directory> directoryList = new ArrayList<Directory>();
        List<Page> documentList = new ArrayList<Page>();
        for (int i = 0; i < pages.length; i++) {
            String type = pages[i].getType();
            if (Directory.TYPE.equals(type)) {
                directoryList.add((Directory)pages[i]);
            } else if (Page.TYPE.equals(type)) {
                documentList.add(pages[i]);
            }
        }

        Locale locale = context.getLocale();
        int directorySize = directoryList.size();
        int rows = (directorySize + cols - 1) / cols;
        MenuEntry[][] directoryEntries = new MenuEntry[rows][cols];
        int idx = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Directory directory = directoryList.get(idx++);
                directoryEntries[r][c] = createMenuEntry(directory, locale,
                    cols, displayDescription,
                    directory.isConcealed() ? CLASS_ENTRY + " "
                        + CLASS_CONCEALED : CLASS_ENTRY);
                if (idx == directorySize) {
                    break;
                }
            }
        }

        int documentSize = documentList.size();
        MenuEntry[] documentEntries = new MenuEntry[documentSize];
        for (int i = 0; i < documentSize; i++) {
            Page document = documentList.get(i);
            documentEntries[i] = createMenuEntry(document, locale, cols,
                displayDescription, document.isConcealed() ? CLASS_CONCEALED
                    : null);
        }

        popScope.put("directory.entries", directoryEntries);
        popScope.put("document.entries", documentEntries);
        popScope.put("onlyDirectory", Boolean.valueOf(onlyDirectory));
        return super.render(context, args, popScope);
    }


    int getCols(PopContext context)
    {
        return PropertyUtils.valueOf(getProperty(context, PROP_COLS,
            Page.VARIANT_DEFAULT), COLS);
    }


    MenuEntry createMenuEntry(Page page, Locale locale, int cols,
        boolean displayDescription, String styleClass)
    {
        PropertyAbility prop = page.getAbility(PropertyAbility.class);
        String description = (displayDescription ? prop.getProperty(
            PROP_DESCRIPTION, locale) : null);
        String label = prop.getProperty(PROP_LABEL, locale);
        if (label == null) {
            label = page.getName();
        }
        return new MenuEntry(description, PresentationUtils.getIconURL(page,
            PROTOCOL_PAGE), label, page.getPathname(), String
            .valueOf(100 / cols)
            + "%", styleClass);
    }


    public static class MenuEntry
    {
        private String description_;

        private String iconURL_;

        private String label_;

        private String pathname_;

        private String width_;

        private String styleClass_;


        public MenuEntry(String description, String iconURL, String label,
            String pathname, String width, String styleClass)
        {
            description_ = description;
            iconURL_ = iconURL;
            label_ = label;
            pathname_ = pathname;
            width_ = width;
            styleClass_ = styleClass;
        }


        public String getDescription()
        {
            return description_;
        }


        public String getIconURL()
        {
            return iconURL_;
        }


        public String getLabel()
        {
            return label_;
        }


        public String getPathname()
        {
            return pathname_;
        }


        public String getWidth()
        {
            return width_;
        }


        public String getStyleClass()
        {
            return styleClass_;
        }
    }
}
