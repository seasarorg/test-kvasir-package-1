package org.seasar.kvasir.webapp.impl;

import java.util.Comparator;

import org.seasar.kvasir.webapp.Phases;
import org.seasar.kvasir.webapp.extension.AbstractPhasedElement;


public class PhasedElementComparator
    implements Comparator<AbstractPhasedElement>
{
    private Phases phases_;

    private String defaultphase_;


    public PhasedElementComparator(String[] phases, String defaultphase)
    {
        phases_ = new PhasesImpl(phases);
        defaultphase_ = defaultphase;
    }


    public int compare(AbstractPhasedElement o1, AbstractPhasedElement o2)
    {
        return phases_.getOrdinal(o1.getPhase(defaultphase_))
            - phases_.getOrdinal(o2.getPhase(defaultphase_));
    }
}
