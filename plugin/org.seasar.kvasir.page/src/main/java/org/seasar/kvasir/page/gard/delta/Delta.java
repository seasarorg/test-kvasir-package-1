package org.seasar.kvasir.page.gard.delta;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface Delta
{
    int     TYPE_NONE = 0;
    int     TYPE_ADD = 1;
    int     TYPE_REMOVE = 2;
    int     TYPE_CHANGE = 3;
    int     TYPES_COUNT = 4;


    int getType();

    String getName();

    Object getTo();

    Object getFrom();

    Object getDifference();
}
