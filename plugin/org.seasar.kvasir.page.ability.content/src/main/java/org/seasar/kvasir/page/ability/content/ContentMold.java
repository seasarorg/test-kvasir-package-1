package org.seasar.kvasir.page.ability.content;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class ContentMold
{
    private static final String ENCODING_DEFAULT = "UTF-8";

    private Integer revisionNumber_;

    private Date createDate_;

    private Date modifyDate_;

    private String mediaType_;

    private String encoding_;

    private InputStream bodyInputStream_;

    private String bodyString_;

    private Integer version_;


    public Integer getRevisionNumber()
    {
        return revisionNumber_;
    }


    public void setRevisionNumber(Integer revisionNumber)
    {
        revisionNumber_ = revisionNumber;
    }


    public Date getCreateDate()
    {
        return createDate_;
    }


    public ContentMold setCreateDate(Date createDate)
    {
        createDate_ = createDate;
        return this;
    }


    public String getEncoding()
    {
        return encoding_;
    }


    public ContentMold setEncoding(String encoding)
    {
        encoding_ = encoding;
        return this;
    }


    public String getMediaType()
    {
        return mediaType_;
    }


    public ContentMold setMediaType(String mediaType)
    {
        mediaType_ = mediaType;
        return this;
    }


    public Date getModifyDate()
    {
        return modifyDate_;
    }


    public ContentMold setModifyDate(Date modifyDate)
    {
        modifyDate_ = modifyDate;
        return this;
    }


    public ContentMold setBodyInputStream(InputStream bodyInputStream)
    {
        setBodyInputStream0(bodyInputStream);
        setBodyString0(null);
        return this;
    }


    void setBodyInputStream0(InputStream bodyInputStream)
    {
        IOUtils.closeQuietly(bodyInputStream_);
        bodyInputStream_ = bodyInputStream;
    }


    void setBodyString0(String bodyString)
    {
        bodyString_ = bodyString;
    }


    public InputStream getBodyInputStream()
    {
        InputStream bodyInputStream;
        if (bodyInputStream_ != null) {
            bodyInputStream = bodyInputStream_;
            bodyInputStream_ = null;
        } else if (bodyString_ != null) {
            if (encoding_ == null) {
                encoding_ = ENCODING_DEFAULT;
            }
            try {
                bodyInputStream = new ByteArrayInputStream(bodyString_
                    .getBytes(encoding_));
            } catch (UnsupportedEncodingException ex) {
                throw new IORuntimeException(ex);
            }
        } else {
            bodyInputStream = null;
        }
        return bodyInputStream;
    }


    public ContentMold setBodyBytes(byte[] bodyBytes)
    {
        if (bodyBytes == null) {
            setBodyInputStream(null);
        } else {
            setBodyInputStream(new ByteArrayInputStream(bodyBytes));
        }
        return this;
    }


    public ContentMold setBodyString(String bodyString)
    {
        setBodyInputStream0(null);
        setBodyString0(bodyString);
        return this;
    }


    public Integer getVersion()
    {
        return version_;
    }


    public ContentMold setVersion(Integer version)
    {
        version_ = version;
        return this;
    }


    public boolean bodyExists()
    {
        return (bodyInputStream_ != null || bodyString_ != null);
    }
}
