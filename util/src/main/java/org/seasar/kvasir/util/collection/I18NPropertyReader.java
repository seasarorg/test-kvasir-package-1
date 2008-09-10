package org.seasar.kvasir.util.collection;

import java.util.Locale;


/**
 * @author YOKOTA Takehiko
 */
public interface I18NPropertyReader
    extends PropertyReader
{
    String getProperty(String name, Locale locale);
}
