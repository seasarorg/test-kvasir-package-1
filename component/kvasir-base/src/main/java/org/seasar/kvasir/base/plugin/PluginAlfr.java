package org.seasar.kvasir.base.plugin;

import org.seasar.kvasir.base.Lifecycle;
import org.seasar.kvasir.base.annotation.ForPreparingMode;
import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface PluginAlfr
    extends Lifecycle
{
    String PLUGIN_XML = "plugin.xml";

    String PLUGIN_DICON = "plugin.dicon";

    String PLUGIN_DICON_CLASS = "PluginPreparer.class";

    String PATCH_XML = "patch.xml";


    Plugin<?> getPlugin(String id);


    <T extends Plugin<?>> T getPlugin(Class<T> clazz);


    /**
     * 現在有効であるプラグインの配列を返します。
     * <p>プラグインの有効・無効の定義については
     * {@link PluginDescriptor#isDisabled()}を参照して下さい。
     * </p>
     * <p>返される配列はプラグインの依存関係順になっています。
     * 言い換えると、
     * 配列中のあるプラグインが依存しているすべてのプラグインはそのプラグインよりも前方にあります。
     * </p>
     * <p>有効なプラグインが存在しない場合は空の配列を返します。</p>
     *
     * @return 有効であるプラグインの配列。nullを返すことはありません。
     */
    Plugin<?>[] getPlugins();


    /**
     * Kvasir/Soraが読み込んだ全てのプラグイン記述子の配列を返します。
     * <p>プラグインが有効であるかどうかに関わらず、
     * 全てのプラグインのPluginDescriptorを返します。
     * ただし、同一IDのプラグインについては最もバージョンが新しいものだけを返します。
     * </p>
     *
     * @return プラグイン記述子の配列。nullを返すことはありません。
     */
    PluginDescriptor[] getAllPluginDescriptors();


    ExtensionPoint getExtensionPoint(String point);


    ExtensionPoint getExtensionPoint(Object key, String pluginId);


    ExtensionElement[] getExtensionElements(String point);


    ExtensionElement[] getExtensionElements(String point, boolean ascending);


    <T> T[] getExtensionElements(Class<T> elementClass, String pluginId);


    <T> T[] getExtensionElements(Class<T> elementClass, String pluginId,
        boolean ascending);


    Object[] getExtensionComponents(String point);


    Object[] getExtensionComponents(String point, boolean ascending);


    /**
     * 指定されたプラグインが持つ拡張ポイントにプラグインされた拡張に対応するコンポーネントの配列を返します。
     * <p>このメソッドは<code>getExtensionComponents(componentClass, pluginId, true)</code>
     * と同じです。
     * </p>
     * 
     * @param <T> コンポーネントの型。
     * @param componentClass コンポーネントの型を表わすClassオブジェクト。
     * @param pluginId プラグインID。
     * @return コンポーネントの配列。
     * nullが返されることはありません。
     */
    <T> T[] getExtensionComponents(Class<T> componentClass, String pluginId);


    /**
     * 指定されたプラグインが持つ拡張ポイントにプラグインされた拡張に対応するコンポーネントの配列を返します。
     * <p>プラグインが複数の拡張ポイントを持つ場合はcomponentClassから拡張ポイントが決定されます。
     * componentClassとしては拡張を表わすExtensionElementクラス、その祖先クラス、実装インタフェース、
     * 拡張を表わすExtensionElementに付与された{@link Component}アノテーションのisaプロパティ
     * で指定されたコンポーネントクラス、その祖先クラス、実装インタフェースを指定することができます。
     * なおcomponentClassから拡張ポイントが特定できなかった場合は使用される拡張ポイントは不定となるため、
     * 拡張ポイントを特定できるようなるべく具体的なクラスを指定するようにして下さい。
     * </p>
     * 
     * @param <T> コンポーネントの型。
     * @param componentClass コンポーネントの型を表わすClassオブジェクト。
     * @param pluginId プラグインID。
     * @param ascending コンポーネントを昇順で返すかどうか。falseの場合降順で返されます。
     * @return コンポーネントの配列。
     * nullが返されることはありません。
     */
    <T> T[] getExtensionComponents(Class<T> componentClass, String pluginId,
        boolean ascending);


    String[] getFailedPluginIdsToStart();


    PluginUpdater getPluginUpdater();


    PluginAlfrSettings getSettings();


    PluginAlfrSettings getSettingsForUpdate();


    void storeSettings(PluginAlfrSettings settings);


    @ForPreparingMode
    boolean notifyKvasirStarted();


    /**
     * 現在開発中のプラグインのPluginインスタンスを返します。
     * <p>Kvasir/Soraが開発モードでない場合や、開発モードのプラグインが存在しない場合は
     * nullを返します。
     * </p>
     *
     * @return 現在開発中のプラグインインスタンス。
     */
    Plugin<?> getPluginUnderDevelopment();
}
