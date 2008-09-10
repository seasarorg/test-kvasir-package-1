package org.seasar.kvasir.util.el;

/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface VariableResolver
{
    VariableResolver EMPTY = new VariableResolver() {
        public Object getValue(Object key)
        {
            return null;
        }
    };


    Object getValue(Object key);
}
