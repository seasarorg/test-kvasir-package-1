package org.seasar.kvasir.base.dao;

import java.util.Map;

import org.seasar.cms.beantable.Pair;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface QueryHandler
{
    String getQuery(String name);


    Pair constructPair(String name, String[] blocks,
        Map<String, Object> namedParamMap, Object[] params);
}
