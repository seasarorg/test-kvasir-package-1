package org.seasar.kvasir.cms.ymir.extension;

/**
 * @author YOKOTA Takehiko
 */
public interface ExtensionPoints
{
    /**
     * Application登録のための拡張ポイントのIDです。
     */
    String APPLICATIONS = "applications";

    /**
     * Ymirに自動生成元のテンプレートとみなさせるテンプレートを登録するための拡張ポイントのIDです。
     */
    String EXTERNALTEMPLATES = "externalTemplates";

    /**
     * ApplicationMapping登録のための拡張ポイントのIDです。
     */
    String APPLICATIONMAPPINGS = "applicationMappings";
}
