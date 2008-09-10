package org.seasar.kvasir.base.mime.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.base.mime.MimeMapping;

import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Required;


@Component(bindingType = BindingType.NONE)
@Bean("mime-mapping")
public class MimeMappingElement extends AbstractElement
    implements MimeMapping
{
    private String extension_;

    private String mimeType_;


    public MimeMappingElement()
    {
    }


    public MimeMappingElement(String extension, String mimeType)
    {
        setExtension(extension);
        setMimeType(mimeType);
    }


    public String getExtension()
    {
        return extension_;
    }


    @Child
    @Required
    public void setExtension(String extension)
    {
        extension_ = extension;
    }


    public String getMimeType()
    {
        return mimeType_;
    }


    @Child
    @Required
    public void setMimeType(String mimeType)
    {
        mimeType_ = mimeType;
    }
}
