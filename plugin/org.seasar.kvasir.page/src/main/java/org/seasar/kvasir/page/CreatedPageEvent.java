package org.seasar.kvasir.page;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class CreatedPageEvent extends PageEvent
{
    private Page        page_;


    public CreatedPageEvent(Page page)
    {
        super(PageEvent.CREATED);

        page_ = page;
    }


    public Page getPage()
    {
        return page_;
    }

}
