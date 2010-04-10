package org.seasar.kvasir.webapp.processor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 複数のRequestProcessorをつなげたチェインを表すインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public interface RequestProcessorChain
{
    /**
     * 現在のRequestProcessorの次のRequestProcessorの処理を呼び出します。
     * 
     * @param request HTTPリクエスト。
     * @param response HTTPレスポンス。
     * @throws ServletException サーブレットとしての処理でエラーが発生した場合。
     * @throws IOException I/Oエラーが発生した場合。
     */
    public void doProcess(HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException;
}
