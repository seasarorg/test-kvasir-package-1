package org.seasar.kvasir.cms.processor;

import java.net.URL;
import java.util.Set;

import javax.servlet.ServletConfig;

import org.seasar.kvasir.page.Page;


/**
 * gard root page相対のURIに対応するファイルパスを取得するための
 * インタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスは{@link #init(Page,ServletConfig)}
 * メソッドの呼び出し以降はスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface LocalPathResolver
{
    void init(Page gardRootPage, ServletConfig config);


    String getRealPath(String path);


    URL getResource(String path);


    Set<String> getResourcePaths(String path);
}
