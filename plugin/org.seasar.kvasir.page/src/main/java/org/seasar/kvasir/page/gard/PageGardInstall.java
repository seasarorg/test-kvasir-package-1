package org.seasar.kvasir.page.gard;

/**
 * @author YOKOTA Takehiko
 */
public interface PageGardInstall
{
    String getGardId();


    void setGardId(String pageGardId);


    String getPathname();


    void setPathname(String pathname);


    boolean isReset();


    void setReset(boolean reset);
}
