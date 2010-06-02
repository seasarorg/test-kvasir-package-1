package org.seasar.kvasir.cms.java;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.cms.PageDispatch;
import org.seasar.kvasir.cms.PageRequest;


/**
 * Javaテンプレートに記述するクラスのベースクラスです。
 * Javaテンプレートとして設定するには、このクラスのサブクラスとして処理クラスを記述し、
 * 記述したクラスの中身だけをテンプレートとして設定して下さい
 * （class宣言をコメントアウトすると良いでしょう）。
 * <p><b>同期化：</b>
 * このクラスのサブクラスはスレッドセーフである必要はありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
abstract public class Base
{
    protected HttpServletRequest request;

    protected HttpServletResponse response;

    protected PageRequest pageRequest;

    protected PageDispatch my;

    protected PageDispatch that;

    private boolean finish_;


    /**
     * 処理を行ないます。
     * <p>Javaテンプレートを持つページにアクセスされた場合、
     * フレームワークはJavaテンプレートが持つ{@link #process(HttpServletRequest, HttpServletResponse, PageRequest)}
     * メソッドを呼び出します。
     * </p>
     * <p>processメソッドがfalseを返すと以降のレンダリング処理が呼び出されますが、
     * trueを返すと以降の処理が呼び出されなくなります。
     * 明示的にレスポンスを{@link HttpServletResponse}に設定した場合など、
     * 以降の処理を呼び出す必要がない場合はtrueを返すようにして下さい。
     * </p>
     * 
     * @param request HttpServletRequest。
     * @param response HttpServletResponse。
     * @param pageRequest PageRequest。
     * @return 以降のレンダリング処理を中断するかどうか。
     * @throws Exception 例外が発生した場合。
     * @see #finish()
     */
    public final boolean process(HttpServletRequest request,
        HttpServletResponse response, PageRequest pageRequest)
        throws Exception
    {
        this.request = request;
        this.response = response;
        this.pageRequest = pageRequest;
        this.my = pageRequest.getMy();
        this.that = pageRequest.getThat();

        finish_ = false;
        execute();
        return finish_;
    }


    /**
     * 実際の処理を行ないます。
     * <p>Javaテンプレートではこのメソッドをオーバライドして処理を記述するようにして下さい。
     * </p>
     * <p>このメソッド内で明示的にレスポンスを{@link HttpServletResponse}に設定した場合など、
     * フレームワークが以降の処理を呼び出す必要がない場合は{@link #finish()}を呼び出して下さい。
     * </p>
     * 
     * @throws Exception 例外が発生した場合。
     * @see #finish()
     */
    public void execute()
        throws Exception
    {
    }


    /**
     * フレームワークが以降の処理を呼び出さないようにします。
     * <p>{@link #execute()}内で明示的にレスポンスを{@link HttpServletResponse}に設定した場合など、
     * フレームワークが以降の処理を呼び出す必要がない場合はこのメソッドを呼び出して下さい。
     * </p>
     * 
     * @see #execute()
     */
    public final void finish()
    {
        finish_ = true;
    }
}
