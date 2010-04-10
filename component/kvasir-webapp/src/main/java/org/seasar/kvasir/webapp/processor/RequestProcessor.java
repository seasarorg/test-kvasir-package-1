package org.seasar.kvasir.webapp.processor;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * HTTPリクエストを処理するRequestProcessorを定義するインタフェースです。
 * <p>RequestProcessorはKvasir/Soraの世界でWebアプリケーションのサーブレットに相当する概念です。
 * Kvasir/Soraではサーブレットの代わりにRequestProcessorを定義して
 * <code>org.seasar.kvasir.base.webapp.requestProcessors</code>
 * 拡張ポイントにプラグインすることでHTTPリクエストの処理をカスタマイズすることができます。
 * </p>
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface RequestProcessor
{
    /**
     * このオブジェクトの初期化を行います。
     * 
     * @param config ServletConfigオブジェクト。
     */
    void init(ServletConfig config);


    /**
     * このオブジェクトを破棄します。
     */
    void destroy();


    /**
     * 指定されたHTTPリクエストを処理します。
     * 
     * @param request HTTPリクエスト。
     * @param response HTTPレスポンス。
     * @param chain RequestProcessorのチェイン。
     * リクエストをこのRequestProcessorで処理しない場合は{@link RequestProcessorChain#doProcess(HttpServletRequest, HttpServletResponse)}
     * を呼び出して下さい。
     * @throws ServletException サーブレットとしての処理でエラーが発生した場合。
     * @throws IOException I/Oエラーが発生した場合。
     */
    void doProcess(HttpServletRequest request, HttpServletResponse response,
        RequestProcessorChain chain)
        throws ServletException, IOException;
}
