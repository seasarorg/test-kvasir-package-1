package org.seasar.kvasir.cms.filter.impl;

import java.io.IOException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.kvasir.page.PageNotFoundRuntimeException;
import org.seasar.kvasir.cms.PageRequest;
import org.seasar.kvasir.cms.filter.PageFilter;
import org.seasar.kvasir.cms.filter.PageFilterChain;
import org.seasar.kvasir.webapp.Dispatcher;


/**
 * 常にPageNotFoundRuntimeExceptionをスローするFilterです。
 * <p>あるパスへの直接のアクセスを許可したくない場合、このFilterを使うことができます。
 * 例えばpageFilters拡張ポイントに以下のエントリをプラグインすることで
 * 「*.html」というパスへの直接アクセスを禁止することができます：
 * </p>
 * <pre>
 * &lt;page-filter id="phantomPageFilter"
 *   phase="initialization" what="path" how="^.+\.html$"
 *   regex="true" /&gt;
 * </pre>
 * <p>（このクラスのインスタンスがphantomPageFilterという名前で
 * org.seasar.kvasir.cmsプラグインのplugin.diconに登録されているので、
 * <code>id="phantomPageFilter"</code>と指定した場合はコンポーネントの登録は不要です。）
 * </p>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class PhantomPageFilter
    implements PageFilter
{
    public void init(FilterConfig config)
    {
    }


    public void destroy()
    {
    }


    public void doFilter(HttpServletRequest request,
        HttpServletResponse response, Dispatcher dispatcher,
        PageRequest pageRequest, PageFilterChain chain)
        throws ServletException, IOException
    {
        throw new PageNotFoundRuntimeException().setPathname(pageRequest
            .getMy().getPathname());
    }
}
