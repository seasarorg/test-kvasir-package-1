package org.seasar.kvasir.base;

import java.util.Enumeration;

import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface Kvasir
{
    /**
     * Kvasirが持つコンポーネントコンテナ中での、
     * Kvasirコンポーネント自身の名前です。
     */
    String COMPONENT_KVASIR = "kvasir";

    String ID = "org.seasar.kvasir.base";


    boolean start(KvasirProperties prop, ComponentContainer container,
        ClassLoader commonClassLoader, ClassLoader systemClassLoader);


    void stop(int timeoutSeconds);


    boolean isStarted();


    boolean beginSession();


    long endSession();


    boolean isInSession();


    /**
     * 現在のスレッドに関連付けるコンテキストクラスローダとして使用すべきクラスローダを返します。
     * 
     * @return クラスローダ。
     */
    ClassLoader getCurrentClassLoader();


    Version getVersion();


    long getBuildNumber();


    /**
     * Kvasir/Soraのホームディレクトリを返します。
     *
     * @return ホームディレクトリ。
     */
    Resource getHomeDirectory();


    Resource getConfigurationDirectory();


    Resource getSystemDirectory();


    Resource getRuntimeWorkDirectory();


    Resource getPluginsDirectory();


    Resource getStagingDirectory();


    ClassLoader getCommonClassLoader();


    ClassLoader getClassLoader();


    boolean isUnderDevelopment();


    boolean isStandalone();


    String getProperty(String name);


    String getProperty(String name, String defaultValue);


    int getProperty(String name, int defaultValue);


    void setProperty(String name, String value);


    void removeProperty(String name);


    Enumeration<String> propertyNames();


    void storeProperties();


    ComponentContainer getComponentContainer();


    ComponentContainer getRootComponentContainer();


    PluginAlfr getPluginAlfr();


    Object getAttribute(String name);


    void setAttribute(String name, Object value);


    void removeAttribute(String name);


    Enumeration<String> getAttributeNames();


    /*
     ClassLoader getOriginalContextClassLoader();
     */

    /**
     * 指定されたnameに対応する構造化プロパティを返します。
     * <p>構造化プロパティは指定したクラスのインスタンスにアンマーシャルされて返されます。
     * 構造化プロパティの保存形式が指定したクラスにマッピングできない形式である場合は
     * RuntimeExceptionがスローされます。
     * </p>
     * <p>指定されたnameに対応するメタデータが存在しない場合はnullを返します。
     * </p>
     * @param name 名前。
     * @param structureClass 構造化プロパティを規定するクラス。メソッドの返り値はこのクラスのインスタンスになります。
     *
     * @return 構造化プロパティト。
     */
    <T> T getStructuredProperty(String name, Class<T> structureClass);


    /**
     * 指定された構造化プロパティを、nameに対応づけて保存します。
     * @param name 構造化プロパティの名前。
     * @param structure 保存する構造化プロパティ。nullを指定すると現在保存されているデータが消去されます。
     *
     * @see #getStructuredProperty(String, Class)
     */
    void setStructuredProperty(String name, Object structure);
}
