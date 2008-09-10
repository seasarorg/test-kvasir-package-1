package org.seasar.kvasir.page.search;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface SearchPlugin
    extends Plugin<EmptySettings>
{
    String ID = "org.seasar.kvasir.page.search";

    String ID_PATH = ID.replace('.', '/');

    String DEFAULT_SEARCHSYSTEMID = ID + ".defaultSearchSystem";


    SearchSystem getDefaultSearchSystem();


    SearchSystem getSearchSystem(Object key);


    SearchSystem[] getSearchSystems();
}
