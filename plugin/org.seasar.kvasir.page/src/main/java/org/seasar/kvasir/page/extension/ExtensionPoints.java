package org.seasar.kvasir.page.extension;

/**
 * @author YOKOTA Takehiko
 */
public interface ExtensionPoints
{
    /**
     * PageListener登録のための拡張ポイントのIDです。
     */
    String PAGELISTENERS = "pageListeners";

    /**
     * PageType登録のための拡張ポイントのIDです。
     */
    String PAGETYPES = "pageTypes";

    /**
     * PageAbilityAlfr登録のための拡張ポイントのIDです。
     */
    String PAGEABILITYALFRS = "pageAbilityAlfrs";

    /**
     * PageGard登録のための拡張ポイントのIDです。
     */
    String PAGEGARDS = "pageGards";

    /**
     * PageGardInstall登録のための拡張ポイントのIDです。
     */
    String PAGEGARDINSTALLS = "pageGardInstalls";

    /**
     * NameRestriction登録のための拡張ポイントのIDです。
     */
    String NAMERESTRICTIONS = "nameRestrictions";
}
