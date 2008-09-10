package org.seasar.kvasir.webapp.impl;

import java.util.HashMap;
import java.util.Map;

import org.seasar.kvasir.webapp.Phases;


public class PhasesImpl
    implements Phases
{
    private Map<String, Integer> phaseMap_ = new HashMap<String, Integer>();


    public PhasesImpl(String[] phases)
    {
        for (int i = 0; i < phases.length; i++) {
            phaseMap_.put(phases[i], i);
        }
    }


    public int getOrdinal(String phase)
        throws IllegalArgumentException
    {
        Integer ordinal = phaseMap_.get(phase);
        if (ordinal != null) {
            return ordinal.intValue();
        } else {
            throw new IllegalArgumentException("Unknown phase: " + phase);
        }
    }
}
