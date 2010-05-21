package org.seasar.kvasir.cms.manage.manage.web;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.seasar.cms.ymir.Request;
import org.seasar.cms.ymir.Response;
import org.seasar.cms.ymir.response.SelfContainedResponse;
import org.seasar.kvasir.cms.manage.ManagePlugin;
import org.seasar.kvasir.cms.manage.manage.dto.DefaultGroupDto;
import org.seasar.kvasir.cms.manage.manage.dto.FormUnitDto;
import org.seasar.kvasir.cms.manage.manage.dto.FormUnitGroupDto;
import org.seasar.kvasir.cms.manage.manage.dto.NewPopsDto;
import org.seasar.kvasir.cms.manage.manage.dto.PopDto;
import org.seasar.kvasir.cms.pop.Pane;
import org.seasar.kvasir.cms.pop.Pop;
import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.pop.PopPlugin;
import org.seasar.kvasir.cms.pop.PopPropertyEntry;
import org.seasar.kvasir.cms.pop.PopPropertyMetaData;
import org.seasar.kvasir.cms.pop.RenderedPop;
import org.seasar.kvasir.cms.pop.ValidationResult;
import org.seasar.kvasir.cms.pop.ValidationResult.Entry;
import org.seasar.kvasir.cms.pop.extension.FormUnitElement;
import org.seasar.kvasir.cms.pop.extension.PopElement;
import org.seasar.kvasir.cms.util.CmsUtils;
import org.seasar.kvasir.cms.util.ServletUtils;
import org.seasar.kvasir.cms.zpt.util.HTMLUtils;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.PageUtils;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.util.PropertyUtils;
import org.seasar.kvasir.util.io.ResourceNotFoundException;


public class LayoutPage extends LayoutPageBase
{
    private static final String PREFIX_PANE = "pane.";

    private static final String PREFIX_PROPERTY = "property.";

    private static final String PROP_POPEDITMODE = ManagePlugin.ID
        + ".popEditMode";

    private static final String PROPVALUE_INPLACE = "inPlace";

    private static final String PROPVALUE_DIALOG = "dialog";

    private static final String STYLECLASS_GROUP = "formUnitGroup";

    private static final String STYLECLASS_TABCONTENT = "tabcontent";

    private static final String SCHEME_PAGE = "page:";

    private PopPlugin popPlugin_;

    private String popId_;

    private String variant_;

    private boolean remove_;

    private String pathname_;

    private RenderedPop renderedPop_;

    private NewPopsDto newPops_;

    private PopDto pop_;

    private FormUnitGroupDto[] formUnitGroups_;

    private String errorMessage_;

    private boolean formTabified_;

    private String groupStyleClass_;

    private int offset_;

    private Boolean popEditModeInPlace_;

    private String[] candidates_;


    public void setPopPlugin(PopPlugin popPlugin)
    {
        popPlugin_ = popPlugin;
    }


    public String getPopId()
    {
        return popId_;
    }


    public String getVariant()
    {
        return variant_;
    }


    public void setVariant(String variant)
    {
        variant_ = variant;
    }


    public void setRemove(boolean remove)
    {
        remove_ = remove;
    }


    public void setPathname(String pathname)
    {
        pathname_ = pathname;
    }


    public String getPathname()
    {
        return pathname_;
    }


    public RenderedPop getRenderedPop()
    {
        return renderedPop_;
    }


    public String getErrorMessage()
    {
        return errorMessage_;
    }


    public FormUnitGroupDto[] getFormUnitGroups()
    {
        return formUnitGroups_;
    }


    public boolean isFormTabified()
    {
        return formTabified_;
    }


    public String getTabifiedFormHtmlId()
    {
        if (formTabified_) {
            return popId_ + "_editProperties_tabified";
        } else {
            return null;
        }
    }


    public String getGroupStyleClass()
    {
        return groupStyleClass_;
    }


    public void setPopId(String popId)
    {
        popId_ = popId;
    }


    public void setOffset(int offset)
    {
        offset_ = offset;
    }


    public NewPopsDto getNewPops()
    {
        return newPops_;
    }


    public Boolean getPopEditModeInPlace()
    {
        return popEditModeInPlace_;
    }


    public void setPopEditModeInPlace(Boolean popEditModeInPlace)
    {
        popEditModeInPlace_ = popEditModeInPlace;
    }


    public String[] getCandidates()
    {
        return candidates_;
    }


    public PopDto getPop()
    {
        return pop_;
    }


    public Object do_execute()
    {
        String url;
        String site = getCmsPlugin().getSite(getCurrentHeimId());
        if (site != null) {
            url = site + getPageRequest().getContextPath();
        } else {
            url = "!";
        }
        return getRedirection(url);
    }


    public Object do_console()
    {
        return "/layout.console.html";
    }


    public Object do_popEditMode()
    {
        PropertyAbility prop = getPageAlfr().getRootPage(getCurrentHeimId())
            .getAbility(PropertyAbility.class);
        if (popEditModeInPlace_ != null) {
            prop.setProperty(PROP_POPEDITMODE, (popEditModeInPlace_
                .booleanValue() ? PROPVALUE_INPLACE : PROPVALUE_DIALOG));
        } else {
            popEditModeInPlace_ = PROPVALUE_INPLACE.equals(prop
                .getProperty(PROP_POPEDITMODE));
        }

        return "/layout.popEditMode.html";
    }


    public Response do_viewImage()
    {
        if (popId_ == null) {
            return null;
        }
        PopElement element = popPlugin_.getPopElement(popId_);
        if (element == null) {
            return null;
        }
        try {
            return new SelfContainedResponse(element.getImageResource()
                .getInputStream(), "image/jpeg");
        } catch (ResourceNotFoundException ex) {
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    public Object do_update()
    {
        Request request = getYmirRequest();
        int heimId = getRequestedHeimId();
        boolean newPop = (popId_ != null && popId_.endsWith("-1_") && !remove_);

        for (Iterator<String> itr = request.getParameterNames(); itr.hasNext();) {
            String name = itr.next();
            if (name.startsWith(PREFIX_PANE)) {
                Pane pane = popPlugin_.getPane(heimId, name
                    .substring(PREFIX_PANE.length()));
                List<Pop> popList = new ArrayList<Pop>();
                String[] ids = PropertyUtils
                    .toLines(request.getParameter(name));
                for (int i = 0; i < ids.length; i++) {
                    String id = ids[i];
                    boolean newPopTarget = (newPop && id.equals(popId_));
                    if (newPopTarget) {
                        id = id.substring(0, id.length() - 1/*= "_" */);
                    }
                    Pop pop = popPlugin_.getPop(heimId, id);
                    if (newPopTarget
                        && pop.getInstanceId() == Pop.INSTANCEID_DEFAULT) {
                        // インスタンスIDが0のものは一時利用に使われるため、ペインには埋め込まない
                        // ようにしている。
                        pop = popPlugin_.getPop(heimId, id);
                    }
                    if (pop != null) {
                        popList.add(pop);
                        if (newPopTarget) {
                            popId_ = pop.getId();
                        }
                    }
                }
                pane.setPops(popList.toArray(new Pop[0]));
            }
        }
        if (popId_ != null && remove_) {
            popPlugin_.removePop(popPlugin_.getPop(heimId, popId_));
        }
        try {
            String response = getPopHTML(popId_, findStartPathname());
            return "html:" + (response != null ? response : "");
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }


    public String do_editProperties()
    {
        if (popId_ == null) {
            return null;
        }
        Pop pop = popPlugin_.getPop(getRequestedHeimId(), popId_);
        if (pop == null) {
            return null;
        }

        if (variant_ == null) {
            variant_ = "";
        }

        PopContext context = newPopContext();
        PopPropertyMetaData[] metaDatas = pop.getPropertyMetaDatas();

        if (remove_) {
            for (int i = 0; i < metaDatas.length; i++) {
                pop.removeProperty(context, metaDatas[i].getId(), variant_);
            }
        }

        pop_ = new PopDto(pop, getLocale());

        return renderEditPropertiesForm(context, pop, null, null);
    }


    String renderEditPropertiesForm(PopContext context, Pop pop,
        Entry[] errorEntries, PopPropertyEntry[] propertyEntries)
    {
        FormUnitElement[] formUnits = pop.getElement().getFormUnits();
        List<FormUnitGroupDto> groupList = new ArrayList<FormUnitGroupDto>();

        Map<String, String> errorMap = new HashMap<String, String>();
        if (errorEntries != null && errorEntries.length > 0) {
            errorMessage_ = getResource("app.error.layout.illegalPopPropertyValue");
            Locale locale = context.getLocale();
            for (int i = 0; i < errorEntries.length; i++) {
                errorMap.put(errorEntries[i].getId(), getLocalMessage(
                    errorEntries[i], locale, pop));
            }
        }

        Map<String, String> valueMap = new HashMap<String, String>();
        if (propertyEntries != null) {
            for (int i = 0; i < propertyEntries.length; i++) {
                valueMap.put(propertyEntries[i].getId(), propertyEntries[i]
                    .getValue());
            }
        }

        FormUnitGroupDto group = new FormUnitGroupDto(new DefaultGroupDto(pop,
            popPlugin_, getLocale()));
        for (int i = 0; i < formUnits.length; i++) {
            String error = errorMap.get(formUnits[i].getId());
            FormUnitDto formUnit = new FormUnitDto(context, pop, formUnits[i],
                getLocale(), variant_, valueMap.get(formUnits[i].getId()),
                error);
            if (error != null) {
                group.setError(true);
            }
            if (formUnit.isKindIsGroup()) {
                if (group.getFormUnits().size() > 0) {
                    groupList.add(group);
                }
                group = new FormUnitGroupDto(formUnit);
            } else {
                group.add(formUnit);
            }
        }
        groupList.add(group);
        formUnitGroups_ = groupList.toArray(new FormUnitGroupDto[0]);

        formTabified_ = !PROPVALUE_INPLACE.equals(getPageAlfr().getRootPage(
            getCurrentHeimId()).getAbility(PropertyAbility.class).getProperty(
            PROP_POPEDITMODE));
        groupStyleClass_ = formTabified_ ? STYLECLASS_TABCONTENT
            : STYLECLASS_GROUP;

        return "/layout.editProperties.html";
    }


    String getLocalMessage(Entry entry, Locale locale, Pop pop)
    {
        if (ValidationResult.ERROR_ASIS.equals(entry.getMessageKey())) {
            return entry.getArgs()[0];
        } else {
            return new MessageFormat(pop.getElement().getPlugin().getProperty(
                entry.getMessageKey(), locale), locale).format(entry.getArgs(),
                new StringBuffer(), null).toString();
        }
    }


    public String do_validateProperties()
    {
        if (popId_ == null) {
            return "content:ERROR";
        }
        Pop pop = popPlugin_.getPop(getRequestedHeimId(), popId_);
        if (pop == null) {
            return "content:ERROR";
        }

        if (variant_ == null) {
            variant_ = Page.VARIANT_DEFAULT;
        }

        PopPropertyEntry[] propertyEntries = constructPopPropertyEntries();
        ValidationResult result = pop.validateProperties(newPopContext(),
            variant_, propertyEntries);

        if (result.getEntries().length > 0) {
            return renderEditPropertiesForm(newPopContext(), pop, result
                .getEntries(), propertyEntries);
        } else {
            return "content:";
        }
    }


    public String do_previewPop()
    {
        if (popId_ == null) {
            return null;
        }
        Pop pop = popPlugin_.getPop(getRequestedHeimId(), popId_);
        if (pop == null) {
            return null;
        }

        if (variant_ == null) {
            variant_ = Page.VARIANT_DEFAULT;
        }

        renderedPop_ = pop.preview(newPopContext(), new String[0], variant_,
            constructPopPropertyEntries());

        return "/layout.updateProperties.html";
    }


    @SuppressWarnings("unchecked")
    PopPropertyEntry[] constructPopPropertyEntries()
    {
        Request request = getYmirRequest();
        List<PopPropertyEntry> entryList = new ArrayList<PopPropertyEntry>();
        for (Iterator<String> itr = request.getParameterNames(); itr.hasNext();) {
            String name = itr.next();
            if (!name.startsWith(PREFIX_PROPERTY)) {
                continue;
            }
            entryList.add(new PopPropertyEntry(name.substring(PREFIX_PROPERTY
                .length()), request.getParameter(name)));
        }
        return entryList.toArray(new PopPropertyEntry[0]);
    }


    public String do_updateProperties()
    {
        if (popId_ == null) {
            return null;
        }
        Pop pop = popPlugin_.getPop(getRequestedHeimId(), popId_);
        if (pop == null) {
            return null;
        }

        if (variant_ == null) {
            variant_ = Page.VARIANT_DEFAULT;
        }

        pop.setProperties(newPopContext(), variant_,
            constructPopPropertyEntries());

        try {
            String response = getPopHTML(popId_, findStartPathname());
            if (response != null) {
                return "html:" + response;
            } else {
                renderedPop_ = pop.render(newPopContext(), new String[0]);
                return "/layout.updateProperties.html";
            }
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }


    public Object do_newPops()
    {
        newPops_ = new NewPopsDto(popPlugin_, getPageAlfr().findNearestPage(
            getCurrentHeimId(), findStartPathname()).getGardIds(), offset_,
            getLocale());

        return "/layout.newPops.html";
    }


    String getPopHTML(String popId, String containerPathname)
    {
        if (popId == null) {
            return null;
        }
        if (log_.isDebugEnabled()) {
            log_.debug("getPopHTML(" + popId + ", " + containerPathname + ")");
        }
        String pageURL = ServletUtils.getURL(containerPathname, httpRequest_);
        String responseText = ServletUtils.getResponseText(pageURL,
            httpRequest_, httpResponse_);
        String popHTML = HTMLUtils.toString(HTMLUtils.getElementById(
            responseText, popId));
        if (log_.isDebugEnabled()) {
            log_.debug("pageURL=" + pageURL);
            log_.debug("responseText=" + responseText);
            log_.debug("popHTML=" + popHTML);
        }
        return popHTML;
    }


    PopContext newPopContext()
    {
        return popPlugin_.newContext(null, httpRequest_, httpResponse_,
            CmsUtils.newPageRequest(httpRequest_, getPageRequest().getThat()
                .getDispatcher(), findStartPathname()));
    }


    public String do_loadPageCandidates()
    {
        if (pathname_ == null) {
            return "content:";
        }
        Page page = getStartPage();
        if (page == null) {
            return "content:";
        }

        String scheme = "";
        String pathname;
        if (pathname_.startsWith(SCHEME_PAGE)) {
            scheme = SCHEME_PAGE;
            pathname = pathname_.substring(SCHEME_PAGE.length());
        } else if (pathname_.indexOf(':') < 0) {
            pathname = pathname_;
        } else {
            return "content:";
        }

        String basePathname = "";
        int slash = pathname.lastIndexOf('/');
        if (slash >= 0) {
            page = getPageAlfr().getPage(
                getCurrentHeimId(),
                PageUtils.getAbsolutePathname(pathname.substring(0, slash),
                    page));
            if (page == null) {
                return "content:";
            }
            basePathname = pathname.substring(0, slash + 1);
            pathname = pathname.substring(slash + 1);
        }

        String[] childNames = page.getChildNames();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < childNames.length; i++) {
            if (childNames[i].startsWith(pathname)) {
                list.add(scheme + basePathname + childNames[i]);
            }
        }
        candidates_ = list.toArray(new String[0]);
        return "/layout.loadPageCandidates.html";
    }


    public String do_showPagePreview()
    {
        if (pathname_ == null) {
            return "content:";
        }
        Page page = getStartPage();
        if (page == null) {
            return "content:";
        }

        if (pathname_.startsWith(SCHEME_PAGE)) {
            pathname_ = pathname_.substring(SCHEME_PAGE.length());
        } else if (pathname_.indexOf(':') >= 0) {
            return "content:";
        }

        page = getPageAlfr().getPage(getCurrentHeimId(),
            PageUtils.getAbsolutePathname(pathname_, page));
        if (page == null) {
            return "content:";
        }
        pathname_ = page.getPathname();

        ContentAbility ability = page.getAbility(ContentAbility.class);
        Content content = ability.getLatestContent(Page.VARIANT_DEFAULT);
        if (content == null || !content.getMediaType().startsWith("image/")) {
            return "content:";
        }

        return "/layout.showPagePreview.html";
    }
}
