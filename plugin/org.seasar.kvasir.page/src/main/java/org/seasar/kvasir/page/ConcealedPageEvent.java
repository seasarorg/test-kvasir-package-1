package org.seasar.kvasir.page;

/**
 * Pageが非表示状態になった場合に発生するPageEventです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class ConcealedPageEvent extends PageEvent
{
    private Page page_;


    public ConcealedPageEvent(Page page)
    {
        super(PageEvent.CONCEALED);

        page_ = page;
    }


    /**
     * 非表示になったPageを返します。
     * 
     * @return 非表示になったPage。
     */
    public Page getPage()
    {
        return page_;
    }

}
