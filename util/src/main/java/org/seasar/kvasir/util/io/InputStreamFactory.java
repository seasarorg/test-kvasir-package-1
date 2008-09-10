package org.seasar.kvasir.util.io;

import java.io.InputStream;


/**
 * InputStreamを生成するためのインタフェースです。
 * <p><b>実装上の注意：</b>
 * このインタフェースの実装クラスのequals()メソッドは、
 * 実装クラスの種類によらず、
 * 内包するInputStreamが指す内容を比較した結果を返す必要があります。
 * また、{@link #getInputStream()}が呼び出されるまではInputStream
 * を生成したり保持していたりしてはいけません。
 * これは、事前に生成したり保持していたりしているInputStreamをclose
 * することができないからです。
 * </p>
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface InputStreamFactory
{
    InputStream getInputStream();
}
