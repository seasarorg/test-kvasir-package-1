package org.seasar.kvasir.page.ability;


/**
 * 
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class AttributeFilter
{
    private boolean     compact_ = false;


    public boolean isCompact()
    {
        return compact_;
    }


    public void setCompact(boolean compact)
    {
        compact_ = compact;
    }
}
