package org.seasar.kvasir.cms.toolbox.toolbox.web;

import java.util.Locale;

import org.seasar.cms.ymir.extension.annotation.Out;
import org.seasar.cms.ymir.scope.impl.SessionScope;
import org.seasar.kvasir.util.LocaleUtils;
import org.seasar.kvasir.webapp.Globals;


public class SelectLanguagePage extends SelectLanguagePageBase
{
    private Locale currentLocale_;


    @Override
    public String _post()
    {
        currentLocale_ = LocaleUtils.getLocale(language_);

        return "redirect:!" + (here_ != null ? here_ : "");
    }


    @Out(name = Globals.ATTR_LOCALE, scopeClass = SessionScope.class)
    public Locale getCurrentLocale()
    {
        return currentLocale_;
    }
}
