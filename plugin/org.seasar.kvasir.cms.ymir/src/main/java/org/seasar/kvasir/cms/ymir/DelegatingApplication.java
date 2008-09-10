package org.seasar.kvasir.cms.ymir;


public interface DelegatingApplication
    extends YmirApplication
{
    void setApplication(YmirApplication application);
}
