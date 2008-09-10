package org.seasar.kvasir.page;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class ConcealedPageEvent extends PageEvent
{
    private Page        page_;


    public ConcealedPageEvent(Page page)
    {
        super(PageEvent.CONCEALED);

        page_ = page;
    }


    public Page getPage()
    {
        return page_;
    }

}
