package org.seasar.kvasir.page.ability.content.impl;

import org.seasar.kvasir.util.wiki.LinkEvaluator;


public class KvasirLinkEvaluator extends LinkEvaluator
{
    private static final String PROTOCOL_PAGE = "page";


    @Override
    protected boolean isAcceptableProtocol(String protocol)
    {
        return PROTOCOL_PAGE.equals(protocol)
            || super.isAcceptableProtocol(protocol);
    }
}
