package org.seasar.kvasir.util.dependency;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface Dependant
{
    String getId();

    boolean isDisabled();

    /**
     * このDependantが依存するDependantに関する情報を持つRequirement
     * の配列を返します。
     * <p>依存するDependantが存在しない場合は空の配列を返します。
     * 
     * @return 依存するDependantに関する情報を持つRequirementの配列。
     */
    Requirement[] getRequirements();
}
