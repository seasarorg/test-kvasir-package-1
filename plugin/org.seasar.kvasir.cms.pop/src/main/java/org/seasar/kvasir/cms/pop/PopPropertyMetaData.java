package org.seasar.kvasir.cms.pop;

import java.util.Locale;


public interface PopPropertyMetaData
{
    String getId();


    String getName();


    String getName(Locale locale);


    String getDescription();


    String getDescription(Locale locale);


    boolean isHumanReadable();


    Type getType();


    String getDefault();
}
