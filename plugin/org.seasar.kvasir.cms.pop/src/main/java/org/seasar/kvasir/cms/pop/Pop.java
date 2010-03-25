package org.seasar.kvasir.cms.pop;

import java.util.Locale;
import java.util.Map;

import org.seasar.kvasir.cms.pop.extension.PopElement;


/**
 * ペインに配置したりコンテンツに埋め込んだりできるWeb部品であるPOPを表すインタフェースです。
 * <p>PopインスタンスはIDとインスタンスIDの組毎にSingletonとしてシステム内で生成され、
 * リクエスト毎にevaluate()が呼び出されます。
 * </p>
 * <p>PopElementを引数とするようなSetterメソッドを持たせることで、
 * このPopを定義しているPopElementオブジェクトを取得することができます。
 * </p>
 * <p>このインタフェースの実装クラスがLifecycleインタフェースを実装している場合、
 * PopElementとインスタンスIDの設定が終わった後にstart()メソッドが呼び出されます。
 * （stop()メソッドは呼び出されません。）
 * </p>
 *
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface Pop
{
    char INSTANCE_DELIMITERCHAR = '-';

    String INSTANCE_DELIMITER = "-";

    int INSTANCEID_NEW = -1;

    int INSTANCEID_DEFAULT = 0;


    /**
     * このPOPインスタンスの識別子を返します。
     * <p>通常このメソッドが返すのは、POP種別の識別子とインスタンスIDを「<code>-</code>」で
     * 連結した文字列
     *  （例：<code>org.seasar.kvasir.cms.toolbox.loginPop-1</code>）です。
     * </p>
     *
     * @return このPOPインスタンスの識別子。
     * @see #getPopId()
     * @see #getInstanceId()
     */
    String getId();


    /**
     * このPOPの種別の識別子を返します。
     * <p>POPの種別の識別子とは、
     * POPを提供するプラグインのIDとPOPのプラグイン内のIDを「<code>.</code>」で連結したもの
     * （例：<code>org.seasar.kvasir.cms.toolbox.loginPop</code>）です。
     * </p>
     *
     * @return このPOPの種別の識別子。
     * @see #getId()
     * @see #getInstanceId()
     */
    String getPopId();


    /**
     * このPOPインスタンスのインスタンスIDを返します。
     * <p>インスタンスIDはPOP種別に関して一意です。
     * </p>
     *
     * @return インスタンスID。
     * @see #getId()
     * @see #getPopId()
     */
    int getInstanceId();


    void setInstanceId(int instanceId);


    /**
     * このPOPインスタンスが所属するHeimのIDを返します。
     *
     * @return このPOPインスタンスが所属するHeimのID。
     */
    int getHeimId();


    /**
     * このインスタンスが所属するHeimを設定します。
     *
     * @param heim このインスタンスが所属するHeimのID。
     */
    void setHeimId(int heimId);


    /**
     * 指定されたコンテキストにおいてこのPOPをレンダリングした結果を返します。
     *
     * @param context コンテキスト。
     * @param args 引数。
     * @return レンダリングした結果。
     */
    RenderedPop render(PopContext context, String[] args);


    PopPropertyMetaData[] getPropertyMetaDatas();


    /**
     * 指定されたIDに対応するプロパティの値を返します。
     * <p>humanReadableでないプロパティについては、
     * バリアントがデフォルトバリアントである場合のみ値を返します。
     * バリアントとしてデフォルトバリアント以外を指定した場合はnullを返します。
     * </p>
     *
     * @param context コンテキスト。
     * @param id プロパティのID。
     * @param variant バリアント。
     * @return 値。値が見つからなかった場合はnullを返します。
     */
    String getProperty(PopContext context, String id, String variant);


    String getProperty(PopContext context, String id, Locale locale);


    /**
     * 指定されたIDに対応するプロパティの値を返します。
     * <p>このメソッドはプレビューのために用いられます。
     * プレビュー用のプロパティ値によって値が変化するタイプのプロパティを持つPOPは、
     * プレビュー用の値のMapである<code>map</code>を使って適切なプロパティ値を返すように
     * このメソッドをオーバライドする必要があります。
     * </p>
     * 
     * @param context コンテキスト。
     * @param map プレビュー用のプロパティ値が入っているMap。
     * @param id プロパティのID。
     * @param variant バリアント。
     * @return 値。値が見つからなかった場合はnullを返します。
     */
    String getProperty(PopContext context, Map<String, PopPropertyEntry> map,
        String id, String variant);


    /**
     * 指定されたIDに対応するプロパティの値を設定します。
     * <p>humanReadableでないプロパティについては、
     * バリアントの値によらずデフォルトバリアントが指定された場合と同じようにふるまいます。
     * </p>
     *
     * @param context コンテキスト。
     * @param id プロパティのID。
     * @param variant バリアント。
     * @param value 設定する値。
     */
    void setProperty(PopContext context, String id, String variant, String value);


    /**
     * 指定されたプロパティの値を一括設定します。
     * <p>通常は{@link #setProperty(PopContext, String, String, String)}を順次呼び出すのと同じですが、
     * POPによっては値をまとめて永続化した場合と個別に永続化した場合で結果が異なる場合がありますので、
     * 一括で値を設定するケースではなるべくこのメソッドを使うようにして下さい。
     * </p>
     *
     * @param context コンテキスト。
     * @param variant バリアント。
     * @param entries プロパティのIDと値の組を表すオブジェクトの配列。nullを指定してはいけません。
     */
    void setProperties(PopContext context, String variant,
        PopPropertyEntry[] entries);


    /**
     * 指定されたプロパティの値のバリデーションを行ないます。
     * <p>{@link #setProperties(PopContext, String, PopPropertyEntry[])}
     * や{@link #preview(PopContext, String[], String, PopPropertyEntry[])
     * を呼び出す前にこのメソッドを使って値の正当性を検証することをお勧めします。
     * </p>
     * 
     * @param context コンテキスト。
     * @param variant バリアント。
     * @param entries プロパティのIDと値の組を表すオブジェクトの配列。nullを指定してはいけません。
     * @return バリデーションの結果。nullが返ることはありません。
     * @see ValidationResult
     */
    ValidationResult validateProperties(PopContext context, String variant,
        PopPropertyEntry[] entries);


    /**
     * 指定されたプロパティの値を使ってPOPをレンダリングします。
     * 
     * @param context コンテキスト。
     * @param args 引数。
     * @param variant バリアント。
     * @param entries プロパティのIDと値の組を表すオブジェクトの配列。nullを指定してはいけません。
     * @return レンダリングした結果。
     */
    RenderedPop preview(PopContext context, String[] args, String variant,
        PopPropertyEntry[] entries);


    /**
     * 指定されたIDに対応するプロパティの値を削除します。
     * <p>humanReadableでないプロパティについては、
     * バリアントの値によらずデフォルトバリアントが指定された場合と同じようにふるまいます。
     * </p>
     *
     * @param context コンテキスト。
     * @param id プロパティのID。
     * @param variant バリアント。
     */
    void removeProperty(PopContext context, String id, String variant);


    PopElement getElement();


    void setElement(PopElement element);


    /**
     * POPインスタンスが削除される前に行なうべき処理を行ないます。
     * <p>このメソッドはPOPインスタンスが削除される際に呼び出されます。
     * 永続化されたプロパティを削除するなど、
     * POPインスタンスが削除される前に行なうべき処理が行なわれます。
     * </p>
     */
    void notifyRemoving();
}
