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
    /**
     * IDを返します。
     * <p>IDはGardを提供しているプラグイン内で一意の文字列です。
     * </p>
     * 
     * @return ID。
     */
    String getId();


    /**
     * フルIDを返します。
     * <p>フルIDは、Gardを提供しているプラグインのIDとGardのIDを「.」で連結したものです。
     * </p>
     * 
     * @return フルID。
     */
    String getFullId();


    /**
     * このGardを提供するプラグインを返します。
     * 
     * @return このGardを提供するプラグイン。
     */
    Plugin<?> getPlugin();


    /**
     * バージョンを返します。
     * 
     * @return バージョン。
     */
    String getVersion();


    /**
     * バージョンを設定します。
     * 
     * @param version バージョン。
     */
    void setVersion(String version);


    /**
     * このGardを構成するリソースのプラグインディレクトリ相対パスを返します。
     * 
     * @return このGardを構成するリソースのプラグインディレクトリ相対パス。
     */
    String getSource();


    /**
     * このGardを構成するリソースのプラグインディレクトリ相対パスを設定します。
     * 
     * @param source このGardを構成するリソースのプラグインディレクトリ相対パス。
     */
    void setSource(String source);


    /**
     * 同一バージョンのGardが既に存在していても、
     * Kvasir/Soraを再起動した際に再インポートするかどうかを返します。
     * 
     * @return 再インポートするかどうか。
     */
    boolean isReset();


    /**
     * 同一バージョンのGardが既に存在していても、
     * Kvasir/Soraを再起動した際に再インポートするかどうかを設定します。
     * 
     * @param reset 再インポートするかどうか。
     */
    void setReset(boolean reset);


    /**
     * シングルトンかどうかを返します。
     * <p>trueの場合、
     * このGardのインスタンスは同一Heim内にたかだか1つしか存在することができないことを表します。
     * </p>
     * 
     * @return シングルトンかどうか。
     */
    boolean isSingleton();


    /**
     * シングルトンかどうかを設定します。
     * 
     * @param singleton シングルトンかどうか。
     */
    void setSingleton(boolean singleton);


    /**
     * インストールする際のデフォルトのパス名を返します。
     * 
     * @return インストールする際のデフォルトのパス名。
     */
    String getDefaultPathname();


    /**
     * インストールする際のデフォルトのパス名を設定します。
     * 
     * @param defaultPathname インストールする際のデフォルトのパス名。
     */
    void setDefaultPathname(String defaultPathname);


    /**
     * このGardの表示名を返します。
     * 
     * @return 表示名。
     */
    String getDisplayName();


    /**
     * このGardの表示名を設定します。
     * 
     * @param displayName 表示名。
     */
    void setDisplayName(String displayName);


    /**
     * このGardの説明文を返します。
     * 
     * @return このGardの説明文。
     */
    String getDescription();


    /**
     * このGardの説明文を設定します。
     * 
     * @param description このGardの説明文。
     */
    void setDescription(String description);


    /**
     * このGardを構成するリソースのトップディレクトリ返します。
     * 
     * @return このGardを構成するリソースのトップディレクトリ。
     */
    Resource getSourceDirectory();


    /**
     * このGardをインストールする直前に呼び出されるメソッドです。
     * <p>このGardをインストールする前に実行したい処理があればここに書いて下さい。
     * </p>
     * 
     * @param gardRootPage Gardがインストールされるディレクトリ。
     * nullが渡されることはありません。
     * 
     * @return 通常はtrueを返して下さい。
     * falseを返した場合はインストール処理が中断されます。
     */
    boolean preInstallProcess(Page gardRootPage);


    /**
     * このGardをインストールした直後に呼び出されるメソッドです。
     * <p>このGardをインストールした直後に実行したい処理があればここに書いて下さい。
     * </p>
     * 
     * @param gardRootPage Gardがインストールされたディレクトリ。
     * nullが渡されることはありません。
     */
    void postInstallProcess(Page gardRootPage);


    /**
     * このGardをアップグレードする直前に呼び出されるメソッドです。
     * <p>このGardをアップグレードする前に実行したい処理があればここに書いて下さい。
     * </p>
     * 
     * @param gardRootPage Gardがアップグレードされるディレクトリ。
     * nullが渡されることはありません。
     * @param version アップグレードされるGardのバージョン。
     */
    void preUpgradeProcess(Page gardRootPage, Version version);


    /**
     * このGardをアップグレードした直後に呼び出されるメソッドです。
     * <p>このGardをアップグレードした直後に実行したい処理があればここに書いて下さい。
     * </p>
     * 
     * @param gardRootPage Gardがアップグレードされたディレクトリ。
     * nullが渡されることはありません。
     * @param version アップグレードされたGardのバージョン。
     */
    void postUpgradeProcess(Page gardRootPage, Version version);


    /**
     * このGardをアンインストールする直前に呼び出されるメソッドです。
     * <p>このGardをアンインストールする前に実行したい処理があればここに書いて下さい。
     * </p>
     * 
     * @param gardRootPage Gardがアンインストールされるディレクトリ。
     * nullが渡されることはありません。
     * @param version アンインストールされるGardのバージョン。
     */
    void preUninstallProcess(Page gardRootPage, Version version);


    /**
     * このGardをアンインストールした直後に呼び出されるメソッドです。
     * <p>このGardをアンインストールした直後に実行したい処理があればここに書いて下さい。
     * </p>
     * 
     * @param gardRootPage Gardがアンインストールされたディレクトリ。
     * nullが渡されることはありません。
     * @param version アンインストールされたGardのバージョン。
     */
    void postUninstallProcess(Page gardRootPage, Version version);
}
