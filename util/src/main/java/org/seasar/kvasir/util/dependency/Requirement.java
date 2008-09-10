package org.seasar.kvasir.util.dependency;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface Requirement
{
    String getId();

    boolean isMatched(Dependant dependant);
}
