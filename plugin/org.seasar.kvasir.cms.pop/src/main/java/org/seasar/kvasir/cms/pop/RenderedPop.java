package org.seasar.kvasir.cms.pop;

import org.seasar.kvasir.cms.pop.util.PopUtils;


/**
 * POPをレンダリングした結果を表すクラスです。
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 * @see Pop
 */
public class RenderedPop
{
    private String id_;

    private String popId_;

    private String title_;

    private String body_;


    public RenderedPop(Pop pop, String title, String body)
    {
        this(pop.getId(), pop.getPopId(), title, body);
    }


    public RenderedPop(String id, String popId, String title, String body)
    {
        id_ = id;
        popId_ = popId;
        title_ = title;
        body_ = body;
    }


    @Override
    public String toString()
    {
        return "id=" + id_ + ", popId=" + popId_ + ", title=" + title_
            + ", body=" + body_;
    }


    /**
     * POPのIDを返します。
     * <p>この値はPOPを表示するためのHTMLブロックのid属性の値として利用できます。
     * </p>
     *
     * @return ID。
     * @see Pop#getId()
     */
    public String getId()
    {
        return id_;
    }


    public void setId(String id)
    {
        id_ = id;
    }


    /**
     * POPの種別の識別子を返します。
     * <p>この値はPOPを表示するためのHTMLブロックのclass属性の値として利用できます。
     * </p>
     *
     * @return ID。
     * @see Pop#getPopId()
     */
    public String getPopId()
    {
        return popId_;
    }


    public void setPopId(String popId)
    {
        popId_ = popId;
    }


    /**
     * POPのHTML形式の題名を返します。
     *
     * @return 題名。
     */
    public String getTitle()
    {
        return title_;
    }


    /**
     * POPのHTML形式の題名を設定します。
     *
     * @param title 題名。
     */
    public void setTitle(String title)
    {
        title_ = title;
    }


    /**
     * POPのHTML形式の本文を返します。
     *
     * @return 本文。
     */
    public String getBody()
    {
        return body_;
    }


    /**
     * POPのHTML形式の本文を設定します。
     *
     * @param body 本文。
     */
    public void setBody(String body)
    {
        body_ = body;
    }


    /**
     * POPが空かどうかを返します。
     * <p>POPの表示内容が何もないかどうかを返します。
     * 通常フレームワークはこのプロパティを見ることによって空のPOPのレンダリング結果が空になるようにしていますが、
     * この挙動を変えたい場合はこのメソッドをオーバライドしたクラスを作成して
     * {@link Pop#render(PopContext, String[])}の返り値として返すようにして下さい。
     * </p>
     *
     * @return POPが空かどうか。
     * @see Pop#render(PopContext, String[])
     * @see PopUtils#renderPane(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.seasar.kvasir.cms.PageRequest, String)
     */
    public boolean isEmpty()
    {
        return ((title_ == null || title_.length() == 0) && (body_ == null || body_
            .length() == 0));
    }
}
