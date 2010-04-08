package org.seasar.kvasir.cms;

import java.io.Serializable;
import java.util.Date;

import org.seasar.kvasir.cms.util.PresentationUtils;


/**
 * Pageの一時的なコンテントを表すクラスです。
 * 
 * @author yokota
 * @see CmsPlugin#getTemporaryContent(org.seasar.kvasir.page.Page, String)
 * @see CmsPlugin#setTemporaryContent(org.seasar.kvasir.page.Page, String, TemporaryContent)
 * @see CmsPlugin#removeTemporaryContent(org.seasar.kvasir.page.Page, String)
 * @see PresentationUtils#getHTMLBodyString(org.seasar.kvasir.page.Page, java.util.Locale, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, boolean)
 */
public class TemporaryContent
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String mediaType_;

    private String bodyString_;

    private Date createDate_;


    /**
     * このクラスのオブジェクトを構築します。
     */
    public TemporaryContent()
    {
    }


    /**
     * このクラスのオブジェクトを構築します。
     * <p>メディアタイプを指定しない場合は通常現在のページのコンテントのメディアタイプが使用されます。
     * </p>
     * 
     * @param bodyString 本文。
     */
    public TemporaryContent(String bodyString)
    {
        bodyString_ = bodyString;
    }


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param mediaType メディアタイプ。
     * @param bodyString 本文。
     */
    public TemporaryContent(String mediaType, String bodyString)
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
