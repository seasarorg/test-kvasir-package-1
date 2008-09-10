package org.seasar.kvasir.webapp.extension;

import net.skirnir.xom.annotation.Attribute;


/**
 * @author YOKOTA Takehiko
 */
abstract public class AbstractPhasedElement extends AbstractWebappElement
{
    private String phase_;


    public String getPhase()
    {
        return phase_;
    }


    public String getPhase(String defaultPhase)
    {
        if (phase_ != null) {
            return phase_;
        } else {
            return defaultPhase;
        }
    }


    @Attribute
    public void setPhase(String phase)
    {
        phase_ = phase;
    }
}
