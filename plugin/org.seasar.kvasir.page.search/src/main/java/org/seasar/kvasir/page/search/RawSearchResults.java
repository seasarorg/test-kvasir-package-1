package org.seasar.kvasir.page.search;

import java.util.HashMap;
import java.util.Map;


/**
 * 検索エンジンから返された検索結果を表わすクラスです。
 *
 * @author YOKOTA Takehiko
 */
public class RawSearchResults
{
    private Object              rawResult_;

    private Map<String, Object> map_ = new HashMap<String, Object>();


    /*
     * constructors
     */

    /**
     * このクラスのオブジェクトを生成します。
     *
     * @param rawResult 検索エンジンから返された生の検索結果。
     */
    public RawSearchResults(Object rawResult)
    {
        rawResult_ = rawResult;
    }


    /**
     * 検索エンジンから返された生の検索結果を返します。
     *
     * @return 検索エンジンから返された生の検索結果。
     */
    public Object getRawResult()
    {
        return rawResult_;
    }


    /**
     * 属性を返します。
     * <p>指定された属性が存在しない場合はnullを返します。</p>
     *
     * @param name 属性名。
     * @return 属性値。
     */
    public Object getAttribute(String name)
    {
        return map_.get(name);
    }


    /**
     * 属性を設定します。
     * <p>属性値としてnullを指定すると<code>removeAttribute()</code>
     * と同じ動作をします。</p>
     *
     * @param name 属性名。
     * @param value 属性値。
     */
    public void setAttribute(String name, Object value)
    {
        if (value == null) {
            removeAttribute(name);
        } else {
            map_.put(name, value);
        }
    }


    /**
     * 属性を削除します。
     *
     * @param name 属性名。
     */
    public void removeAttribute(String name)
    {
        map_.remove(name);
    }
}
