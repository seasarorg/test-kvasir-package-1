package org.seasar.kvasir.page.auth.protocol;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface AuthProtocol
{
    boolean authenticate(String password, String challenge);

    String getInnerExpression(String password);
}
