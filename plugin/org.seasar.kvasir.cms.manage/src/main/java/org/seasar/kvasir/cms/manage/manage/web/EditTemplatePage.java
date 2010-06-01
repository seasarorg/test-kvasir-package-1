package org.seasar.kvasir.cms.manage.manage.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.render.Notes;
import net.skirnir.freyja.render.html.OptionTag;

import org.seasar.kvasir.cms.manage.tab.impl.PageTab;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.template.Template;
import org.seasar.kvasir.page.ability.template.TemplateAbility;
import org.seasar.kvasir.page.ability.template.TemplateAbilityPlugin;
import org.seasar.kvasir.page.ability.template.extension.TemplateHandlerElement;


public class EditTemplatePage extends MainPanePage
{
    /*
     * set by framework
     */

    private TemplateAbilityPlugin templatePlugin_;

    private String bodyString_;

    private String command_;

    private String responseContentType_;

    private int revision_;

    private String type_;

    private String variant_ = Page.VARIANT_DEFAULT;

    /*
     * for presentation tier
     */

    private boolean custom_;

    private OptionTag[] definedTypes_;

    private String[] definedVariants_;

    private boolean exists_;

    private boolean undefinedVariant_;

    private boolean unknownType_;


    /*
     * public scope methods
     */

    public Object do_execute()
    {
        if ("POST".equalsIgnoreCase(getYmirRequest().getMethod())) {
            if (updateTemplate()) {
                setNotes(new Notes().add(new Note(
                    "app.note.editTemplate.succeed")));
                Map<String, String[]> paramMap = new HashMap<String, String[]>();
                paramMap.put("variant", new String[] { variant_ });
                return getRedirection("/edit-template.do" + getPathname(),
                    paramMap);
            }
        }

        return render();
    }


    /*
     * private scope methods
     */

    private boolean updateTemplate()
    {
        Page page = getPage();
        TemplateAbility ability = page.getAbility(TemplateAbility.class);

        if ("delete".equals(command_)) {
            // テンプレートを削除する。
            ability.clearAllTemplates();
            page.touch();
        } else {
            if (bodyString_ != null) {
                ability.setTemplate(variant_, bodyString_);
            }
            if (type_ != null) {
                if (templatePlugin_.getTemplateHandlerElement(type_) == null) {
                    // typeで指定されたテンプレートタイプが
                    // 登録されていない。
                    setNotes(new Notes().add(new Note(
                        "app.error.editTemplate.unknownTemplateType", type_)));
                    return false;
                }
                ability.setType(type_);
            }
            if (responseContentType_ != null) {
                ability.setResponseContentType(responseContentType_);
            }
            page.touch();
        }
        return true;
    }


    private String render()
    {
        enableTab(PageTab.NAME_TEMPLATE);
        enableLocationBar(true);

        TemplateAbility ability = getPage().getAbility(TemplateAbility.class);
        custom_ = (ability.getTemplate() != null);
        Template template;
        if (custom_) {
            template = ability.getTemplate(variant_);
        } else {
            TemplateAbility defaultAbility = findDefaultTemplateAbility();
            if (defaultAbility != null) {
                ability = defaultAbility;
                template = ability.getTemplate(variant_);
            } else {
                template = null;
            }
        }
        definedVariants_ = ability.getVariants();
        undefinedVariant_ = true;
        for (int i = 0; i < definedVariants_.length; i++) {
            if (variant_.equals(definedVariants_[i])) {
                undefinedVariant_ = false;
                break;
            }
        }

        exists_ = (custom_ && template != null);
        if (type_ == null) {
            type_ = ability.getType();
        }
        TemplateHandlerElement[] elements = templatePlugin_
            .getTemplateHandlerElements();
        Locale locale = getLocale();
        definedTypes_ = new OptionTag[elements.length];
        boolean setSelected = false;
        OptionTag tagForAll = null;
        for (int i = 0; i < elements.length; i++) {
            String type = elements[i].getType();
            definedTypes_[i] = new OptionTag(type, elements[i]
                .resolveDisplayName(locale));
            if (TemplateHandlerElement.TYPE_ALL.equals(type)) {
                tagForAll = definedTypes_[i];
            }
            if (type.equals(type_)) {
                setSelected = true;
                definedTypes_[i].setSelected(true);
            }
        }
        if (!setSelected && tagForAll != null) {
            tagForAll.setSelected(true);
        }
        if (responseContentType_ == null) {
            responseContentType_ = ability.getResponseContentType();
        }
        if (bodyString_ == null) {
            bodyString_ = (template != null ? template.getBody() : "");
        }

        return "/edit-template.html";
    }


    private TemplateAbility findDefaultTemplateAbility()
    {
        Page templatePage = null;

        Page page = getPage();
        String subType = page.getAbility(PropertyAbility.class).getProperty(
            PropertyAbility.PROP_SUBTYPE);
        Page[] lords = page.getLords();
        for (int i = 1; i < lords.length; i++) {
            if ((templatePage = findTemplatePage(page.getType(), subType,
                lords[i])) != null) {
                break;
            }
        }
        if (templatePage == null) {
            templatePage = findTemplatePage(page.getType(), subType, page
                .getRoot());
        }
        if (templatePage != null) {
            return templatePage.getAbility(TemplateAbility.class);
        } else {
            return null;
        }
    }


    Page findTemplatePage(String type, String subType, Page lord)
    {
        Page templatePage = null;
        if (subType != null) {
            templatePage = lord.getChild(Page.PATHNAME_TEMPLATES + "/" + type
                + "." + subType);
        }
        if (templatePage == null) {
            templatePage = lord.getChild(Page.PATHNAME_TEMPLATES + "/" + type);
        }
        if (templatePage == null) {
            templatePage = lord.getChild(Page.PATHNAME_TEMPLATES + "/default");
        }
        // 後方互換性のため。
        if (templatePage == null && !Page.TYPE.equals(type)) {
            templatePage = lord.getChild(Page.PATHNAME_TEMPLATES + "/"
                + Page.TYPE);
        }
        return templatePage;
    }


    /*
     * for framework / presentation tier
     */

    public String getBodyString()
    {
        return bodyString_;
    }


    public void setBodyString(String bodyString)
    {
        bodyString_ = bodyString;
    }


    public String getCommand()
    {
        return command_;
    }


    public void setCommand(String command)
    {
        command_ = command;
    }


    public String getResponseContentType()
    {
        return responseContentType_;
    }


    public void setResponseContentType(String responseContentType)
    {
        responseContentType_ = responseContentType;
    }


    public int getRevision()
    {
        return revision_;
    }


    public void setRevision(int revision)
    {
        revision_ = revision;
    }


    public String getType()
    {
        return type_;
    }


    public void setType(String type)
    {
        type_ = type;
    }


    public String getVariant()
    {
        return variant_;
    }


    public void setVariant(String variant)
    {
        variant_ = variant;
    }


    public void setVariants(String[] variants)
    {
        if (variants.length == 1 || !variants[0].equals(VARIANT_UNDEFINED)) {
            variant_ = variants[0];
        } else {
            variant_ = variants[1];
        }
    }


    /*
     * for presentation tier
     */

    public OptionTag[] getDefinedTypes()
    {
        return definedTypes_;
    }


    public String[] getDefinedVariants()
    {
        return definedVariants_;
    }


    public boolean isExists()
    {
        return exists_;
    }


    public boolean isUndefinedVariant()
    {
        return undefinedVariant_;
    }


    public boolean isUnknownType()
    {
        return unknownType_;
    }


    /*
     * for framework
     */

    public void setTemplateAbilityPlugin(TemplateAbilityPlugin templatePlugin)
    {
        templatePlugin_ = templatePlugin;
    }


    public boolean isCustom()
    {
        return custom_;
    }


    public boolean isCanAdd()
    {
        // テンプレートを持たないページについては、デフォルトバリアントが表示されている
        // 時に追加可能。
        // テンプレートを持っているページについては、バリアントが「新しいバリアントを追加…」
        // の時に追加可能。
        return !custom_ && Page.VARIANT_DEFAULT.equals(variant_) || custom_
            && VARIANT_UNDEFINED.equals(variant_);
    }


    public boolean isCanRevert()
    {
        return custom_ && Page.VARIANT_DEFAULT.equals(variant_);
    }
}
