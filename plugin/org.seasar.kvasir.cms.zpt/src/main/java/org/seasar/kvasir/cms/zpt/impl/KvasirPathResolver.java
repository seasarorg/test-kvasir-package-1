package org.seasar.kvasir.cms.zpt.impl;

import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.VariableResolver;
import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.zpt.tales.NotePathResolver;

import org.seasar.kvasir.base.Asgard;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.Privilege;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.page.auth.AuthPlugin;
import org.seasar.kvasir.page.condition.Order;
import org.seasar.kvasir.page.condition.PageCondition;
import org.seasar.kvasir.util.collection.AttributeReader;
import org.seasar.kvasir.util.collection.I18NPropertyReader;
import org.seasar.kvasir.util.collection.PropertyReader;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class KvasirPathResolver extends NotePathResolver
{
    private static final String NAME_BODYSTRING = "%bodyString";

    private static final String NAME_BODYHTMLSTRING = "%bodyHTMLString";

    private static final String BEGIN_BODYHTMLSTRING = NAME_BODYHTMLSTRING
        + "(";

    private static final String BEGIN_ABILITY = "ability(";

    private static final String END_PAREN = ")";

    private static final String NAME_LATESTCONTENT = "latestContent";

    private static final String NAME_L7D_LABEL = "%"
        + PropertyAbility.PROP_LABEL;

    private static final String NAME_L7D_DESCRIPTION = "%"
        + PropertyAbility.PROP_DESCRIPTION;

    private static final String NAME_VISIBLECHILDREN = "visibleChildren";

    private static final String NAME_VISIBLECHILDRENCOUNT = "visibleChildrenCount";

    private static final String NAME_VISIBLECHILDNAMES = "visibleChildNames";


    public KvasirPathResolver()
    {
        setNoteLocalizer(new KvasirNoteLocalizer());
    }


    /*
     * PathResolver
     */

    public boolean accept(TemplateContext context,
        VariableResolver varResolver, Object obj, String child)
    {
        return (obj instanceof Page && child.startsWith(BEGIN_ABILITY)
            && child.endsWith(END_PAREN) || obj instanceof PropertyAbility
            || obj instanceof ContentAbility || obj instanceof Content
            || obj instanceof Note || obj instanceof I18NPropertyReader
            || obj instanceof PropertyReader || obj instanceof AttributeReader);
    }


    public Object resolve(TemplateContext context,
        VariableResolver varResolver, Object obj, String child)
    {
        if (obj instanceof Page) {
            Page page = (Page)obj;
            if (child.startsWith(BEGIN_ABILITY) && child.endsWith(END_PAREN)) {
                String name = child.substring(BEGIN_ABILITY.length(), child
                    .length()
                    - END_PAREN.length());
                return page.getAbility(name);
            } else if (child.equals(PropertyAbility.PROP_LABEL)) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_LABEL);
            } else if (child.equals(PropertyAbility.PROP_DESCRIPTION)) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_DESCRIPTION);
            } else if (child.equals(PropertyAbility.PROP_SUBTYPE)) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_SUBTYPE);
            } else if (child.equals(NAME_L7D_LABEL)) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_LABEL,
                    getNoteLocalizer().findLocale(context, varResolver));
            } else if (child.equals(NAME_L7D_DESCRIPTION)) {
                return page.getAbility(PropertyAbility.class).getProperty(
                    PropertyAbility.PROP_DESCRIPTION,
                    getNoteLocalizer().findLocale(context, varResolver));
            } else if (child.equals(NAME_VISIBLECHILDREN)) {
                return page.getChildren(buildVisibleCondition());
            } else if (child.equals(NAME_VISIBLECHILDRENCOUNT)) {
                return page.getChildrenCount(buildVisibleCondition());
            } else if (child.equals(NAME_VISIBLECHILDNAMES)) {
                return page.getChildNames(buildVisibleCondition());
            } else {
                return null;
            }
        } else if (obj instanceof PropertyAbility) {
            PropertyAbility ability = (PropertyAbility)obj;
            if (child.startsWith("%")) {
                return ability.getProperty(child.substring(1),
                    getNoteLocalizer().findLocale(context, varResolver));
            } else {
                return ability.getProperty(child);
            }
        } else if (obj instanceof ContentAbility) {
            ContentAbility ability = (ContentAbility)obj;
            if (child.equals(NAME_LATESTCONTENT)) {
                return ability.getLatestContent(getNoteLocalizer().findLocale(
                    context, varResolver));
            }
        } else if (obj instanceof Content) {
            Content content = (Content)obj;
            if (child.equals(NAME_BODYSTRING)) {
                return content.getBodyString();
            } else if (child.equals(NAME_BODYHTMLSTRING)) {
                return content.getBodyHTMLString(null);
            } else if (child.startsWith(BEGIN_BODYHTMLSTRING)
                && child.endsWith(END_PAREN)) {
                String name = child.substring(BEGIN_BODYHTMLSTRING.length(),
                    child.length() - END_PAREN.length());
                Object vr = varResolver.getVariable(context, name);
                org.seasar.kvasir.util.el.VariableResolver evr = null;
                if (vr instanceof org.seasar.kvasir.util.el.VariableResolver) {
                    evr = (org.seasar.kvasir.util.el.VariableResolver)vr;
                }
                return content.getBodyHTMLString(evr);
            }
        } else if (obj instanceof Note) {
            Note note = (Note)obj;
            if (child.equals(NAME_VALUE)) {
                return getNoteLocalizer().getMessageResourceValue(context,
                    varResolver, note.getValue(), note.getParameters());
            }
        } else if (obj instanceof I18NPropertyReader) {
            I18NPropertyReader reader = (I18NPropertyReader)obj;
            if (child.startsWith("%")) {
                return reader.getProperty(child.substring(1),
                    getNoteLocalizer().findLocale(context, varResolver));
            } else {
                return reader.getProperty(child);
            }
        } else if (obj instanceof PropertyReader) {
            return ((PropertyReader)obj).getProperty(child);
        } else if (obj instanceof AttributeReader) {
            return ((AttributeReader)obj).getAttribute(child);
        }
        return null;
    }


    PageCondition buildVisibleCondition()
    {
        return new PageCondition().setIncludeConcealed(false).setOnlyListed(
            true).setUser(
            Asgard.getKvasir().getPluginAlfr().getPlugin(AuthPlugin.class)
                .getCurrentActor()).setPrivilege(Privilege.ACCESS_VIEW)
            .setOrder(new Order(PageCondition.FIELD_ORDERNUMBER));
    }
}
