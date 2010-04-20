package org.seasar.kvasir.page.type;

import org.seasar.kvasir.page.Page;


/**
 * ディレクトリを表すインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface Directory
    extends Page
{
    String TYPE = "directory";
}
