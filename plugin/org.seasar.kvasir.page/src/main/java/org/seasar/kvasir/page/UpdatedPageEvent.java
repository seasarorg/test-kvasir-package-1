package org.seasar.kvasir.page;


/**
 * Pageオブジェクトが更新された場合に発生するPageEventです。
 * <p>このイベントが発生するのは、次のいずれかの場合です。</p>
 * <ul>
 *   <li>{@link Page#touch()}または{@link Page#touch(boolean)}
 *       が呼び出された場合。</li>
 *   <li>{@link PageAlfr#touch(Page[])}または
 *       {@link PageAlfr#touch(Page[], boolean)}が呼び出された場合。</li>
 * </ul>
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class UpdatedPageEvent extends PageEvent
{
    private Page[]      pages_;


    public UpdatedPageEvent(Page page)
    {
        this(new Page[]{ page });
    }


    public UpdatedPageEvent(Page[] pages)
    {
        super(PageEvent.UPDATED);

        pages_ = pages;
    }


    /**
     * <code>touch()</code>が呼び出されたPageオブジェクトを返します。
     * <p>このメソッドが返すPageの配列が要素を1つだけ持っている場合は、
     * そのPageは排他ロックされた状態であることが保証されています。</p>
     * 
     * @return 長さ1以上のPageオブジェクトの配列。nullが返ることはありません。
     */
    public Page[] getPages()
    {
        return pages_;
    }

}
