package org.seasar.kvasir.cms;

/**
 * フィルタ等の条件にgardIdを指定したい場合で、実行時までgardIdが決定できないものについて、
 * gardIdの取得を遅延させるためのクラスです。
 * <p><b>注意：</b>このクラスが持つメソッドが返す値は毎回同じである必要があります。</p>
 *
 * @author YOKOTA Takehiko
 */
public interface GardIdProvider
{
    boolean containsGardId(String gardId);

    String[] getGardIds();
}
