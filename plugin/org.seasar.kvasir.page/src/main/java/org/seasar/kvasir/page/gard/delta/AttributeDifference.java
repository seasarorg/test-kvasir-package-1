package org.seasar.kvasir.page.gard.delta;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface AttributeDifference
{
    Delta[] getStringDeltas(int type);

    Delta[] getStreamDeltas(int type);
}
