package org.seasar.kvasir.cms.toolbox.toolbox.pop;

import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.cms.pop.PopContext;
import org.seasar.kvasir.cms.toolbox.ToolboxPlugin;
import org.seasar.kvasir.util.LocaleUtils;
import org.seasar.kvasir.util.PropertyUtils;

import net.skirnir.freyja.render.html.OptionTag;


public class LanguageSelectorPop extends CustomPop
{
    public static final String ID = ToolboxPlugin.ID + ".languageSelectorPop";

    public static final String PROP_LANGUAGES = "languages";

    public static final String PROP_STYLECLASS = "styleClass";

    public static final String LANGUAGES_ALL = "*";


    @Override
    public void process(PopContext context, String[] args,
        Map<String, Object> popScope)
    {
        Locale[] locales;
        String[] languages = PropertyUtils.toLines(getProperty(popScope,
            PROP_LANGUAGES));
        if (languages.length == 1 && LANGUAGES_ALL.equals(languages[0])) {
            locales = Locale.getAvailableLocales();
        } else {
            locales = new Locale[languages.length];
            for (int i = 0; i < languages.length; i++) {
                locales[i] = LocaleUtils.getLocale(languages[i]);
            }
        }
        OptionTag[] optionTags = new OptionTag[locales.length];
        for (int i = 0; i < locales.length; i++) {
            optionTags[i] = new OptionTag(locales[i].toString(), locales[i]
                .getDisplayName(context.getLocale())).setSelected(locales[i]
                .equals(context.getLocale()));
        }
        popScope.put("languages", optionTags);
        popScope.put("here", context.getThat().getPathname());
    }
}
