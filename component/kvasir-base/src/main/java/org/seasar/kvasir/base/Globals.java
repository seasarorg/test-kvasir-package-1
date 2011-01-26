package org.seasar.kvasir.base;

/**
 * @author YOKOTA Takehiko
 */
public interface Globals
{
    /**
     * 設定ファイルの格納ディレクトリのホームディレクトリからの相対パスです。
     */
    String CONFIGURATION_DIR = "configuration";

    /**
     * 実行時用のワークディレクトリのホームディレクトリからの相対パスです。
     */
    String RUNTIMEWORK_DIR = "rtwork";

    /**
     * プラグインが格納されているディレクトリのホームディレクトリからの相対パスです。
     */
    String PLUGINS_DIR = "plugins";

    /**
     * Kvasirへインストールするファイルが格納されているディレクトリの
     * ホームディレクトリからの相対パスです。
     */
    String STAGING_DIR = "staging";

    /**
     * 共通リソースが格納されているディレクトリへのホームからの相対パスです。
     */
    String COMMON_DIR = "common";

    /**
     * 共通のクラスファイルが格納されているディレクトリへのホームからの相対パスです。
     */
    String COMMON_CLASSES_DIR = COMMON_DIR + "/classes";

    /**
     * 共通のJARファイルが格納されているディレクトリへのホームからの相対パスです。
     */
    String COMMON_LIB_DIR = COMMON_DIR + "/lib";

    /**
     * システム用のディレクトリへのホームからの相対パスです。
     */
    String SYSTEM_DIR = "system";

    /**
     * システム用のクラスファイルが格納されているディレクトリへのホームからの相対パスです。
     */
    String SYSTEM_CLASSES_DIR = SYSTEM_DIR + "/classes";

    /**
     * システム用のJARファイルが格納されているディレクトリへのホームからの相対パスです。
     */
    String SYSTEM_LIB_DIR = SYSTEM_DIR + "/lib";

    /**
     * Kvasir/Soraのホームディレクトリを表すプロパティのキーです。
     */
    String PROP_SYSTEM_HOME_DIR = "system.home.dir";

    String PLUGINID = "org.seasar.kvasir.base";

    String PLUGINID_PATH = PLUGINID.replace('.', '/');

    /**
     * Kvasir/Soraで用いるComponentContainerを生成するための
     * ComponentContainerFactoryのクラス名を表すプロパティのキーです。
     */
    String PROP_SYSTEM_COMPONENTCONTAINERFACTORY_CLASSNAME = "system.componentContainerFactory.className";

    /**
     * ComponentContainerの設定リソースのパスを表すプロパティのキーです。
     */
    String PROP_SYSTEM_COMPONENTCONTAINER_CONFIG_RESOURCE = "system.componentContainer.config.resource";

    /**
     * ファイル中に含まれる式言語指定を置換するファイルの名前を表すプロパティのキーです。
     * <p>ファイルはホームディレクトリからの相対ディレクトリとして解釈されます。
     * </p>
     * <p>ファイルを複数指定する場合はカンマで区切ってください。</p>
     */
    String PROP_SYSTEM_FILTER_RESOURCES = "system.filter.resources";

    /**
     * ComponentContainer中のKvasirコンポーネントの名前です。
     */
    String COMPONENT_KVASIR = "kvasir";

    /**
     * ComponentContainer中のPluginAlfrコンポーネントの名前です。
     */
    String COMPONENT_PLUGINALFR = "pluginAlfr";

    String RESOURCE_PATTERN_FOR_METAINF_EXCLUSION = "!META-INF/s2container/components.dicon";

    /**
     * 現在開発中のプラグインのIDを表すプロパティのキーです。
     */
    String PROP_SYSTEM_DEVELOPEDPLUGINID = "system.developedPluginId";

    /**
     * 現在開発中のプラグインのプロジェクトディレクトリの絶対パスを表すプロパティのキーです。
     */
    String PROP_SYSTEM_DEVELOPEDPLUGIN_PROJECT_DIR = "system.developedPlugin.project.dir";

    /**
     * 現在開発中のプラグインのビルドされたクラスファイルが格納されるディレクトリの絶対パスを表すプロパティのキーです。
     */
    String PROP_SYSTEM_DEVELOPEDPLUGIN_CLASSES_DIR = "system.developedPlugin.classes.dir";

    /**
     * 現在開発中のプラグインのホームディレクトリの絶対パスを表すプロパティのキーです。
     */
    String PROP_SYSTEM_DEVELOPEDPLUGIN_HOME_DIR = "system.developedPlugin.home.dir";

    /**
     * 現在開発中のプラグインの動作時のクラスローダに追加したいJARファイルの絶対パスを表すプロパティのキーです。
     * 複数のエントリを記述する場合はパスを「,」で区切って下さい。
     */
    String PROP_SYSTEM_DEVELOPEDPLUGIN_ADDITIONALJARS = "system.developedPlugin.additionalJars";

    /**
     * 複数のKvasir/Soraインスタンスで単一のデータストレージを共有させるかどうかを表すプロパティのキーです。
     */
    String PROP_SYSTEM_ENABLECLUSTERING = "system.enableClustering";

    /**
     * Kvasir/Soraのバージョンを保持しているプロパティリソースのパスです。
     */
    String KVASIR_POM_PROPERTIES = "META-INF/maven/org.seasar.kvasir/kvasir-base/pom.properties";

    /**
     * Kvasir/Soraのバージョンを保持しているプロパティリソースのパスです。
     */
    String PROP_VERSION = "version";

    /**
     * ディストリビューションのビルド番号を保持しているプロパティリソースのパスです。
     */
    String BUILD_NUMBER = "build.number";

    /**
     * ビルド番号を保持しているプロパティリソースが持つ、ビルド番号を表すプロパティのキーです。
     */
    String PROP_BUILD_NUMBER = "build.number";
}
