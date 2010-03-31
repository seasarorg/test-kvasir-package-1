package org.seasar.kvasir.page;

/**
 * Pageの状態が変化した際に実行する処理を表すインタフェースです。
 * 
 * @author YOKOTA Takehiko
 */
public interface PageListener
{
    /**
     * Pageの状態が変更された際に呼び出されるメソッドです。
     * <p>渡されるイベントオブジェクトの型は、発生したイベントの種類によって異なります。
     * <dl>
     *   <dt>{@link PageEvent#CREATED}</dt><dd>{@link CreatedPageEvent}</dd>
     *   <dt>{@link PageEvent#MOVED}</dt><dd>{@link MovedPageEvent}</dd>
     *   <dt>{@link PageEvent#CONCEALED}</dt><dd>{@link ConcealedPageEvent}</dd>
     *   <dt>{@link PageEvent#DELETED}</dt><dd>{@link DeletedPageEvent}</dd>
     *   <dt>{@link PageEvent#LORD_CHANGED}</dt><dd>{@link LordChangedPageEvent}</dd>
     *   <dt>{@link PageEvent#UPDATED}</dt><dd>{@link UpdatedPageEvent}</dd>
     * </dl>
     * 
     * @param event 発生したイベント。
     */
    void notifyChanged(PageEvent event);
}
