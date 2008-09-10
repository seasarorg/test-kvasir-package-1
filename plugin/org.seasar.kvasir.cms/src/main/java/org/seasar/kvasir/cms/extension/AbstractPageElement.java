package org.seasar.kvasir.cms.extension;

import org.seasar.kvasir.cms.GardIdProvider;
import org.seasar.kvasir.webapp.extension.AbstractPhasedElement;

import net.skirnir.xom.annotation.Attribute;


/**
 * @author YOKOTA Takehiko
 */
abstract public class AbstractPageElement extends AbstractPhasedElement
{
    public static final String WHAT_TEMPLATETYPE = "templateType";

    public static final String WHAT_PATH = "path";

    public static final String WHAT_PAGETYPE = "pageType";

    private String gardId_;

    private String gardIdProviderId_;

    private GardIdProvider gardIdProvider_;

    private String what_;

    private String how_;

    private boolean not_;

    private String except_;

    private boolean regex_ = false;


    public String getGardId()
    {
        return gardId_;
    }


    @Attribute
    public void setGardId(String gardId)
    {
        gardId_ = gardId;
    }


    public synchronized GardIdProvider getGardIdProvider()
    {
        if (gardIdProvider_ == null && gardIdProviderId_ != null) {
            gardIdProvider_ = (GardIdProvider)getPlugin()
                .getComponentContainer().getComponent(gardIdProviderId_);
        }

        return gardIdProvider_;
    }


    public String getGardIdProviderId()
    {
        return gardIdProviderId_;
    }


    @Attribute
    public void setGardIdProviderId(String gardIdProviderId)
    {
        gardIdProviderId_ = gardIdProviderId;
    }


    public String getWhat()
    {
        return what_;
    }


    @Attribute
    public void setWhat(String what)
    {
        what_ = what;
    }


    public String getHow()
    {
        return how_;
    }


    @Attribute
    public void setHow(String how)
    {
        how_ = how;
    }


    public boolean isNot()
    {
        return not_;
    }


    @Attribute
    public void setNot(boolean not)
    {
        not_ = not;
    }


    public String getExcept()
    {
        return except_;
    }


    @Attribute
    public void setExcept(String except)
    {
        except_ = except;
    }


    public boolean isRegex()
    {
        return regex_;
    }


    @Attribute
    public void setRegex(boolean regex)
    {
        regex_ = regex;
    }
}
