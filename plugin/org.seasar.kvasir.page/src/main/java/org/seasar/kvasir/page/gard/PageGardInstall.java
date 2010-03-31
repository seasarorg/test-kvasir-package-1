package org.seasar.kvasir.page.gard;

/**
 * Gardを自動インストールするための情報を表すインタフェースです。
 * 
 * @author YOKOTA Takehiko
 */
public interface PageGardInstall
{
    /**
     * インストールするGardのフルIDを返します。
     * 
     * @return インストールするGardのフルID。
     */
    String getGardId();


    /**
     * インストールするGardのフルIDを設定します。
     * 
     * @param pageGardId インストールするGardのフルID。
     */
    void setGardId(String pageGardId);


    /**
     * インストールパスを返します。
     * 
     * @return インストールパス。
     */
    String getPathname();


    /**
     * インストールパスを設定します。
     * 
     * @param pathname インストールパス。
     */
    void setPathname(String pathname);


    /**
     * 既にインストール済みである場合に再インストールするかどうかを返します。
     * <p>このメソッドがtrueを返す場合、
     * Kvasir/Soraの起動時に既にインストール済みであるGardはアンインストールされて再度インストールされます。
     * </p>
     * 
     * @return 再インストールするかどうか。
     */
    boolean isReset();


    /**
     * 再インストールするかどうかを設定します。
     * 
     * @param reset 再インストールするかどうか。
     */
    void setReset(boolean reset);
}
