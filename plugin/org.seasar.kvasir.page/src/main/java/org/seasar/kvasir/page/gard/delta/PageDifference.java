package org.seasar.kvasir.page.gard.delta;

import org.seasar.kvasir.page.ability.PageAbilityAlfr;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface PageDifference
{
    int getType();

    Delta[] getFieldDeltas(int type);

    /**
     * このオブジェクトが持つAttributeに関するDeltaの名前を返します。
     * <p>Attributeが存在しない場合は空の配列を返します。</p>
     * 
     * @param abilityAlfrClass PageAbilityAlfrのクラス。
     * @return Attributeの名前の配列。
     */
    String[] getAbilityDeltaNames(Class<? extends PageAbilityAlfr> pageAbilityAlfrClass);

    String[] getAbilityDeltaVariants(Class<? extends PageAbilityAlfr> pageAbilityAlfrClass, String name);

    Delta getAbilityDelta(Class<? extends PageAbilityAlfr> pageAbilityAlfrClass, String name,
        String variant);
}
