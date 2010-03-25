package org.seasar.kvasir.base.descriptor;

import org.seasar.kvasir.base.plugin.Plugin;
import org.seasar.kvasir.base.plugin.descriptor.Extension;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Default;


/**
 * 拡張ポイントにプラグインする拡張の各要素を表すインタフェースです。
 * <p><b>注意：</b>
 * getterメソッドが返すオブジェクトは変更しないで下さい。</p>
 * <p><b>同期化：</b>
 * このクラスが持つsetterメソッドは、
 * フレームワークによって非同期に呼び出さなれないことが保証されています。
 * getterメソッドは非同期に呼び出されることがあります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface ExtensionElement
{
    /** 最初の位置を表すIDです。 */
    String ID_FIRST = "_first";

    /** 最後の位置を表すIDです。 */
    String ID_LAST = "_last";


    /**
     * この拡張要素を提供しているプラグインを返します。
     * 
     * @return この拡張を提供しているプラグイン。
     */
    Plugin<?> getPlugin();


    /**
     * この拡張要素を束ねている拡張オブジェクトを返します。
     * 
     * @return この拡張要素を束ねている拡張オブジェクト。
     */
    Extension getParent();


    /**
     * この要素のIDを返します。
     * <p>IDはこの要素を提供しているプラグインの範囲で一意な識別子です。
     * </p>
     * 
     * @return ID。
     */
    String getId();


    /**
     * この要素のフルIDを返します。
     * <p>フルIDはKvasir/Sora全体で一意な識別子です。
     * </p>
     * 
     * @return フルID。
     */
    String getFullId();


    String getAction();


    ActionType getActionType();


    /**
     * この要素を拡張ポイントにプラグインする際にどの要素の前に登録するかを返します。
     * <p>返す値はプラグイン位置の要素のIDです。
     * </p>
     * 
     * @return 登録位置の要素のID。
     */
    String getBefore();


    /**
     * この要素を拡張ポイントにプラグインする際にどの要素の後に登録するかを返します。
     * <p>返す値はプラグイン位置の要素のIDです。
     * </p>
     * 
     * @return 登録位置の要素のID。
     */
    String getAfter();


    /**
     * この要素に対応するコンポーネントを返します。
     * <p>対応するコンポーネントが存在しない場合はnullを返します。
     * </p>
     * 
     * @return この要素に対応するコンポーネント。
     */
    Object getComponent();


    /*
     * for framework
     */

    void setParent(Extension parent);


    @Attribute
    void setId(String id);


    @Attribute
    void setAction(String action);


    @Attribute
    @Default(ID_LAST)
    void setBefore(String before);


    @Attribute
    @Default(ID_FIRST)
    void setAfter(String after);
}
