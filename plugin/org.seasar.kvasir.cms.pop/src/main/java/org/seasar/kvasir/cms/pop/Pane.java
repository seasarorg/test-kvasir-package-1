package org.seasar.kvasir.cms.pop;

public interface Pane
{
    String getId();


    Pop[] getPops();


    void addPop(Pop pop);


    void setPops(Pop[] pops);
}
