package org.seasar.kvasir.page.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.util.ArrayUtils;

import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Child;


@Bean("name-restriction")
public class NameRestrictionElement extends AbstractElement
{
    private String[] invalidChars_ = new String[0];

    private String[] invalidNames_ = new String[0];


    public String[] getInvalidNames()
    {
        return invalidNames_;
    }


    @Child
    public void addInvalidName(String invalidName)
    {
        invalidNames_ = ArrayUtils.add(invalidNames_, invalidName);
    }


    public void setInvalidNames(String[] invalidNames)
    {
        invalidNames_ = invalidNames;
    }


    public String[] getInvalidChars()
    {
        return invalidChars_;
    }


    @Child
    public void addInvalidChar(String invalidChar)
    {
        invalidChars_ = ArrayUtils.add(invalidChars_, invalidChar);
    }


    public void setInvalidChars(String[] invalidChars)
    {
        invalidChars_ = invalidChars;
    }
}
