package org.seasar.kvasir.page;

/**
 * Pageオブジェクトが作成された場合に発生するPageEventです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class CreatedPageEvent extends PageEvent
{
    private Page page_;


    public CreatedPageEvent(Page page)
    {
        super(PageEvent.CREATED);

        page_ = page;
    }


    /**
     * 作成されたPageを返します。
     * 
     * @return 作成されたPage。
     */
    public Page getPage()
    {
        return page_;
    }

}
