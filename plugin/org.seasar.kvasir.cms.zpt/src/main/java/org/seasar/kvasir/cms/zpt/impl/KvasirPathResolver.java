package org.seasar.kvasir.cms.zpt.impl;

import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.ability.PropertyAbility;
import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.page.ability.content.ContentAbility;
import org.seasar.kvasir.util.collection.AttributeReader;
import org.seasar.kvasir.util.collection.I18NPropertyReader;
import org.seasar.kvasir.util.collection.PropertyReader;

import net.skirnir.freyja.TemplateContext;
import net.skirnir.freyja.VariableResolver;
import net.skirnir.freyja.render.Note;
import net.skirnir.freyja.zpt.tales.NotePathResolver;


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

    private static final Object NAME_LATESTCONTENT = "latestContent";


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
            } else {
                throw new RuntimeException("Logic error");
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
}
