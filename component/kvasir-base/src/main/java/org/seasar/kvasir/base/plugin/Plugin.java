package org.seasar.kvasir.base.plugin;

import java.util.Locale;

import org.seasar.kvasir.base.Adaptable;
import org.seasar.kvasir.base.Kvasir;
import org.seasar.kvasir.base.Lifecycle;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.annotation.ForPreparingMode;
import org.seasar.kvasir.base.container.ComponentContainer;
import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.impl.PluginProperties;
import org.seasar.kvasir.util.collection.I18NPropertyReader;
import org.seasar.kvasir.util.io.Resource;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface Plugin<S>
    extends Adaptable, Lifecycle, I18NPropertyReader
{
    String PROP_ENABLED = "enabled";

    /**
     * プラグインの設定ファイルの格納ディレクトリの抽象パスを表すプロパティのキーです。
     */
    String PROP_PLUGIN_CONFIGURATION_DIR = "plugin.configuration.dir";

    /**
     * プラグインが持つコンポーネントコンテナ中での、
     * プラグイン自身の名前です。
     */
    String COMPONENT_PLUGIN = "plugin";


    String getId();


    Version getVersion();


    boolean isDisabled();


    boolean isUnderDevelopment();


    void setUnderDevelopment(boolean underDevelopment);


    /**
     * プラグインディレクトリを返します。
     *
     * @return プラグインディレクトリ。
     */
    //    Resource getHomeDirectory();
    /**
     * プラグインの変更可能な設定を保持しているディレクトリを返します。
     * <p>プラグイン開発者の方へ。
     * プラグインに関する変更可能な設定は
     * このディレクトリの下に保持するようにし、
     * homeDirectoryの下には置かないようにして下さい。
     * また、ファイルやディレクトリを作成する場合は、
     * resourcesという名前以外の名前をつけるようにして下さい
     * （resourcesという名前はフレームワークが使用します）。
     * </p>
     *
     * @return 変更可能な設定を保持しているディレクトリ。
     */
    //    Resource getConfigurationDirectory();
    /**
     * プラグインディレクトリ以下にあるリソースをオーバライドするような、
     * 変更可能なリソースを保持しているディレクトリを返します。
     * <p>プラグインディレクトリ以下にある読み込み専用リソースの内容を
     * オーバライドしたい場合はこのディレクトリにリソースを置きます。
     * 例えばプラグインディレクトリ以下に変数置換指定（${...}のようなもの）
     * を含むリソースがある場合、
     * フレームワークはそれを実際の値で置き換えた上でこのディレクトリ以下に
     * リソースを置きます。
     * これによって、変数の値が埋め込まれたリソースが利用されることになります。
     * </p>
     * <p>このディレクトリはフレームワークによって使用されます。</p>
     *
     * @return オーバライド用のプラグインディレクトリ。
     */
    //    Resource getConfigurableResourcesDirectory();
    /**
     * オーバライド用のプラグインディレクトリとプラグインディレクトリを
     * 重ねあわせたディレクトリリソースを返します。
     * <p>プラグインディレクトリ以下のリソースは
     * オーバライドされることがあるため、
     * リソースを参照する場合は{@link #getHomeDirectory()}
     * の返すディレクトリを使わず、
     * このメソッドが返すディレクトリを使うようにして下さい。
     * </p>
     *
     * @return 重ねあわされたプラグインディレクトリ。
     */
    //    Resource getResourcesDirectory();
    PluginDescriptor getDescriptor();


    ComponentContainer getComponentContainer();


    ClassLoader getInnerClassLoader();


    ClassLoader getOuterClassLoader();


    String getProperty(String name);


    String getProperty(String name, String variant);


    String getProperty(String name, Locale locale);


    String resolveString(String str);


    String resolveString(String str, Locale locale);


    String resolveString(String str, Locale locale,
        boolean returnNullIfNotExists);


    ExtensionElement[] getExtensionElements(String point);


    ExtensionElement[] getExtensionElements(String point, boolean ascending);


    <T> T[] getExtensionElements(Class<T> elementClass);


    <T> T[] getExtensionElements(Class<T> elementClass, boolean ascending);


    Object[] getExtensionComponents(String point);


    Object[] getExtensionComponents(String point, boolean ascending);


    <T> T[] getExtensionComponents(Class<T> componentClass);


    <T> T[] getExtensionComponents(Class<T> componentClass, boolean ascending);


    /*
     * for framework
     */

    PluginProperties getProperties();


    @ForPreparingMode
    void setProperties(PluginProperties prop);


    void storeProperties();


    /**
     * プロパティファイルを再読み込みします。
     * </P>このメソッドは、開発モードでこのメソッドを呼び出すことで
     * plugin.xpropertiesファイルへの変更をシステムに動的に反映させるために用意されています。</p>
     */
    void reloadProperties();


    @ForPreparingMode
    void setDescriptor(PluginDescriptor descriptor);


    Resource getHomeDirectory();


    Resource getConfigurationDirectory();


    @ForPreparingMode
    void setInnerClassLoader(ClassLoader innerClassLoader);


    @ForPreparingMode
    void setOuterClassLoader(ClassLoader outerClassLoader);


    @ForPreparingMode
    void setComponentContainer(ComponentContainer container);


    @ForPreparingMode
    void setKvasir(Kvasir kvasir);


    @ForPreparingMode
    void setPluginAlfr(PluginAlfr pluginAlfr);


    /**
     * 全てのプラグインが開始され、
     * Kvasir/Soraがユーザからのリクエストを受け付ける直前に呼び出されます。
     */
    @ForPreparingMode
    void notifyKvasirStarted();


    /**
     * プラグインの設定が変更された通知を受け取るためのリスナを追加します。
     * <p>追加されたリスナには、次の場合に通知がなされます。</p>
     * <ul>
     *   <li>{@link #notifyKvasirStarted()}が呼び出された直後。</li>
     *   <li>{@link #storeSettings(S)}が呼び出された直後。</li>
     * </ul>
     * <p>このメソッドはKvasir/Soraの起動処理の終了後には呼び出さないで下さい。
     * </p>
     * 
     * @param listener リスナ。
     */
    @ForPreparingMode
    void addSettingsListener(SettingsListener<S> listener);


    /**
     * プラグインの設定が変更されたことをリスナに通知します。
     * <p><b>実装上の注意：</b>このメソッドはスレッドセーフである必要があります。
     * </p>
     *
     */
    void notifySettingsUpdated(SettingsEvent<S> eveent);


    /**
     * このプラグインのためのKvasirLogインスタンスを返します。
     *
     * @return このプラグインのためのKvasirLogインスタンス。
     */
    KvasirLog getLog();


    /**
     * 指定されたオブジェクトをこのプラグインのライフサイクルに組み込みます。
     * <p>指定されたオブジェクトがLifecycleインタフェースを実装している場合、
     * このプラグインの開始処理の終了直後にオブジェクトのstart()メソッドを呼び出すようにします
     * （このメソッドの呼び出し時にプラグインが既に開始されている場合はすぐにstart()メソッドが呼び出されます）。
     * またこのプラグインの終了処理の開始直後にオブジェクトのstop()メソッドを呼び出すようにします。
     * </p>
     * <p>オブジェクトがLifecycleインタフェースを実装していなかったり引数がnullであったりした場合は
     * 何もしません。</p>
     * <p>このメソッドを呼び出せるのはKvasir#start()以前だけです。</p>
     *
     * @param object ライフサイクルに組み込むオブジェクト。
     * @throws IllegalStateException Kvasir#start()が呼び出された後にこのメソッドが呼び出された場合。
     */
    void addToLifecycle(Object object)
        throws IllegalStateException;


    /**
     * このプラグインが既に開始されているかを返します。
     *
     * @return このプラグインが既に開始されているか。
     */
    boolean isStarted();


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
     * @return 構造化プロパティ。
     */
    <T> T getStructuredProperty(String name, Class<T> structureClass);


    /**
     * 指定された構造化プロパティを、nameに対応づけて保存します。
     *
     * @param name 構造化プロパティの名前。
     * @param structure 保存する構造化プロパティ。nullを指定すると現在保存されているデータが消去されます。
     *
     * @see #getStructuredProperty(String, Class)
     */
    void setStructuredProperty(String name, Object structure);


    /**
     * このプラグインの設定を返します。
     * <p>設定を表す構造化プロパティクラスが定義されていないプラグインの場合、
     * このメソッドの返り値はnullになります。
     * 逆に、設定を表す構造化プロパティクラスが定義されているプラグインの場合は、
     * 必ず非nullを返します。
     * </p>
     * <p><b>注意：</b>このメソッドが返すオブジェクトは参照専用です。
     * setterメソッドを呼び出してはいけません。
     * プラグインの設定を変更したい場合は、必ず{@link #getSettingsForUpdate()}
     * を使って取得したオブジェクトを変更し、{@link #storeSettings(Object)}
     * に渡すようにして下さい。
     * </p>
     *
     * @return 構造化プロパティ。
     */
    S getSettings();


    S getSettingsForUpdate();


    S newSettings();


    /**
     * このプラグインの設定を保存します。
     * <p>設定としてnullを指定することはできません。
     * </p>
     *
     * @param settings 保存する設定。
     * @throws IllegalArgumentException 設定としてnullを指定した場合。
     *
     * @see #getStructuredProperty(String, Class)
     */
    void storeSettings(S settings);


    /**
     * このプラグインの設定を初期状態に戻します。
     */
    void resetSettings();


    /**
     * プラグインの設定を表す構造化プロパティのクラスを返します。
     * <p>このプラグインに関して、設定を表す構造化プロパティが定義されていない場合はnullを返します。
     * </p>
     * <p>このメソッドが返すクラスは、必ずpublicなデフォルトコンストラクタを持っている必要があります。
     * </p>
     *
     * @return 設定を表す構造化プロパティのクラス。
     */
    Class<S> getSettingsClass();


    /**
     * このプラグインの開発プロジェクトのプロジェクトディレクトリを返します。
     * <p>{@link #isUnderDevelopment()}がtrueである場合のみ意味を持ちます。
     * そうでない場合はnullを返します。
     * </p>
     *
     * @return プロジェクトディレクトリ。
     */
    Resource getProjectDirectory();


    /**
     * このプラグインの開発プロジェクトの、プラグインリソースを保持するディレクトリを返します。
     * <p>プラグインの開発プロジェクトのディレクトリを<it>HOME</it>とする場合、
     * 通常<it>HOME</it><code>/src/main/plugin/</code>を返します。
     * </p>
     * <p>{@link #isUnderDevelopment()}がtrueである場合のみ意味を持ちます。
     * そうでない場合はnullを返します。
     * </p>
     *
     * @return プラグインリソースを保持するディレクトリ。
     */
    Resource getHomeSourceDirectory();


    /**
     * このプラグインの開発プロジェクトのプロジェクトディレクトリを設定します。
     *
     * @param projectDirectory プロジェクトディレクトリ。
     */
    @ForPreparingMode
    void setProjectDirectory(Resource projectDirectory);


    /**
     * このプラグインの状態を最新にします。
     * <p>このメソッド呼び出しにより、例えばplugin.xpropertiesが読み込み直されます。
     * </p>
     *
     */
    void refresh();
}
