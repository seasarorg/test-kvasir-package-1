package org.seasar.kvasir.page.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;


/**
 * Pageを排他ロックした上でメソッドを呼び出すためのInterceptorです。
 * <p>第1引数で指定されたPageオブジェクトについて、
 * Page#exclusiveLock(false)を呼び出して排他ロックを行なった状態で
 * メソッド呼び出しを行なます。
 * そのため、このInterceptorを適用するメソッドは
 * 第1引数がPageオブジェクトである必要があります。</p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class PageLockingInterceptor
    implements MethodInterceptor
{
    public Object invoke(final MethodInvocation invocation)
        throws Throwable
    {
        Object[] arguments = invocation.getArguments();
        Page page = (Page)arguments[0];
        if (page == null) {
            return invocation.proceed();
        } else {
            try {
                return page.runWithLocking(new Processable<Object>() {
                    public Object process()
                        throws ProcessableRuntimeException
                    {
                        try {
                            return invocation.proceed();
                        } catch (Throwable t) {
                            throw new ProcessableRuntimeException(t);
                        }
                    }
                });
            } catch (ProcessableRuntimeException ex) {
                throw ex.getCause();
            }
        }
    }
}
