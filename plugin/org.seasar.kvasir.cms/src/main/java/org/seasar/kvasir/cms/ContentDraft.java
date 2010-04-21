package org.seasar.kvasir.cms;

import java.io.Serializable;
import java.util.Date;

import org.seasar.kvasir.cms.util.PresentationUtils;


/**
 * Pageのコンテントの下書きを表すクラスです。
 * 
 * @author yokota
 * @see CmsPlugin#getContentDraft(org.seasar.kvasir.page.Page, String)
 * @see CmsPlugin#setContentDraft(org.seasar.kvasir.page.Page, String, ContentDraft)
 * @see CmsPlugin#removeContentDraft(org.seasar.kvasir.page.Page, String)
 * @see PresentationUtils#getHTMLBodyString(org.seasar.kvasir.page.Page, java.util.Locale, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, boolean)
 */
public class ContentDraft
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String mediaType_;

    private String bodyString_;

    private Date createDate_;


    /**
     * このクラスのオブジェクトを構築します。
     */
    public ContentDraft()
    {
    }


    /**
     * このクラスのオブジェクトを構築します。
     * <p>メディアタイプを指定しない場合は通常現在のページのコンテントのメディアタイプが使用されます。
     * </p>
     * 
     * @param bodyString 本文。
     */
    public ContentDraft(String bodyString)
    {
        bodyString_ = bodyString;
    }


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param mediaType メディアタイプ。
     * @param bodyString 本文。
     */
    public ContentDraft(String mediaType, String bodyString)
    {
        mediaType_ = mediaType;
        bodyString_ = bodyString;
    }


    /**
     * メディアタイプを返します。
     * 
     * @return メディアタイプ。
     */
    public String getMediaType()
    {
        return mediaType_;
    }


    public void setMediaType(String mediaType)
    {
        mediaType_ = mediaType;
    }


    /**
     * 本文を返します。
     * 
     * @return 本文。
     */
    public String getBodyString()
    {
        return bodyString_;
    }


    public void setBodyString(String bodyString)
    {
        bodyString_ = bodyString;
    }


    /**
     * 作成日時を返します。
     * 
     * @return 作成日時。
     */
    public Date getCreateDate()
    {
        return createDate_;
    }


    public void setCreateDate(Date createDate)
    {
        createDate_ = createDate;
    }
}
