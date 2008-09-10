package org.seasar.kvasir.base.xom;

import java.util.Locale;

import org.seasar.kvasir.util.collection.I18NPropertyHandler;

import net.skirnir.xom.I18NString;


public class I18NPropertyHandlerToI18NStringAdapter
    implements I18NString
{
    private I18NPropertyHandler handler_;

    private String name_;


    public I18NPropertyHandlerToI18NStringAdapter(I18NPropertyHandler prop,
        String name)
    {
        handler_ = prop;
        name_ = name;
    }


    public String getString(Locale locale)
    {
        return handler_.getProperty(name_, locale);
    }
}
