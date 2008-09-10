package org.seasar.kvasir.page.ability.content.mock;

import java.io.InputStream;
import java.util.Date;

import org.seasar.kvasir.page.ability.content.Content;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.io.Resource;


public class MockContent
    implements Content
{
    private int id_;

    private int revisionNumber_;

    private String variant_;

    private Date createDate_;

    private Date modifyDate_;

    private String mediaType_;

    private String encoding_;

    private InputStream bodyInputStream_;

    private byte[] bodyBytes_;

    private String bodyString_;

    private Resource bodyResource_;

    private String bodyHTMLString_;


    public int getId()
    {
        return id_;
    }


    public int getRevisionNumber()
    {
        return revisionNumber_;
    }


    public String getVariant()
    {
        return variant_;
    }


    public Date getCreateDate()
    {
        return createDate_;
    }


    public Date getModifyDate()
    {
        return modifyDate_;
    }


    public String getMediaType()
    {
        return mediaType_;
    }


    public String getEncoding()
    {
        return encoding_;
    }


    public InputStream getBodyInputStream()
    {
        return bodyInputStream_;
    }


    public byte[] getBodyBytes()
    {
        return bodyBytes_;
    }


    public String getBodyString()
    {
        return bodyString_;
    }


    public Resource getBodyResource()
    {
        return bodyResource_;
    }


    public String getBodyHTMLString(VariableResolver resolver)
    {
        return bodyHTMLString_;
    }


    public String getBodyHTMLString()
    {
        return bodyHTMLString_;
    }


    public MockContent setBodyHTMLString(String bodyHTMLString)
    {
        bodyHTMLString_ = bodyHTMLString;
        return this;
    }


    public MockContent setBodyBytes(byte[] bodyBytes)
    {
        bodyBytes_ = bodyBytes;
        return this;
    }


    public MockContent setBodyInputStream(InputStream bodyInputStream)
    {
        bodyInputStream_ = bodyInputStream;
        return this;
    }


    public MockContent setBodyResource(Resource bodyResource)
    {
        bodyResource_ = bodyResource;
        return this;
    }


    public MockContent setBodyString(String bodyString)
    {
        bodyString_ = bodyString;
        return this;
    }


    public MockContent setCreateDate(Date createDate)
    {
        createDate_ = createDate;
        return this;
    }


    public MockContent setEncoding(String encoding)
    {
        encoding_ = encoding;
        return this;
    }


    public MockContent setId(int id)
    {
        id_ = id;
        return this;
    }


    public MockContent setMediaType(String mediaType)
    {
        mediaType_ = mediaType;
        return this;
    }


    public MockContent setModifyDate(Date modifyDate)
    {
        modifyDate_ = modifyDate;
        return this;
    }


    public MockContent setRevisionNumber(int revisionNumber)
    {
        revisionNumber_ = revisionNumber;
        return this;
    }


    public MockContent setVariant(String variant)
    {
        variant_ = variant;
        return this;
    }
}
