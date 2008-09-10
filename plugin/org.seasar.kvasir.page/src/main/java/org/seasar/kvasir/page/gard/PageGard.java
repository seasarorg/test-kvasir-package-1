package org.seasar.kvasir.page.gard;

import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.util.io.Resource;


/**
 * Gardを表すインタフェースです。
 * <p>Gardとは、1つのアプリケーションとして意味のある集まりを定義するための概念です。
 * GardにはLordをルートとするサブページツリーの他、
 * アプリケーションを構成するためのその他のリソースが含まれます。
 * </p>
 * <p>Gardは文字列IDを持ちます。またGardは
 * <code>org.seasar.kvasir.page</code>プラグインの
 * <code>org.seasar.kvasir.page.pageGards</code>拡張ポイントを
 * 拡張することで追加定義することができます。
 * </p>
 * <p>Gardが持つサブページツリーのルートを「Gardルートページ」と言います。
 * GardルートはLordである必要があります。
 * </p>
 * <p>Gardルートページの下にはLordや他のGardのルートページを持たないことが多いですが、
 * Lordや他のGardルートページを持つこともできます。
 * </p>
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface PageGard
{
    String getId();


    String getFullId();


    Plugin<?> getPlugin();


    String getVersion();


    void setVersion(String version);


    String getSource();


    void setSource(String source);


    boolean isReset();


    void setReset(boolean reset);


    boolean isSingleton();


    void setSingleton(boolean singleton);


    String getDefaultPathname();


    void setDefaultPathname(String defaultPathname);


    String getDisplayName();


    void setDisplayName(String displayName);


    String getDescription();


    void setDescription(String description);


    Resource getSourceDirectory();


    boolean preInstallProcess(Page gardRootPage);


    void postInstallProcess(Page gardRootPage);


    void preUpgradeProcess(Page gardRootPage, Version version);


    void postUpgradeProcess(Page gardRootPage, Version version);


    void preUninstallProcess(Page gardRootPage, Version version);


    void postUninstallProcess(Page gardRootPage, Version version);
}
