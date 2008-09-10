package org.seasar.kvasir.cms.ymir.extension;

import static org.seasar.cms.ymir.Globals.LANDMARK_CLASSNAME;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.cms.ymir.DelegatingApplication;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Required;


/**
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.MUST, isa = DelegatingApplication.class)
@Bean("application")
public class ApplicationElement extends AbstractElement
{
    private static final String APP_ROOT = "gards";

    private String gardId_;

    private String rootPath_;

    private String landmark_;


    public String getGardId()
    {
        return gardId_;
    }


    @Attribute
    @Required
    public void setGardId(String gardId)
    {
        gardId_ = gardId;
    }


    public String getRootPath()
    {
        if (rootPath_ == null) {
            return APP_ROOT + "/" + getShortId(gardId_) + "/static";
        } else {
            return rootPath_;
        }
    }


    String getShortId(String gardId)
    {
        if (gardId == null) {
            return null;
        }
        int dot = gardId.lastIndexOf('.');
        if (dot < 0) {
            return gardId;
        } else {
            return gardId.substring(dot + 1);
        }
    }


    @Attribute
    public void setRootPath(String rootPath)
    {
        rootPath_ = rootPath;
    }


    public DelegatingApplication getApplication()
    {
        return (DelegatingApplication)getComponent();
    }


    public String getLandmark()
    {
        if (landmark_ == null) {
            return LANDMARK_CLASSNAME;
        }
        return landmark_;
    }


    @Attribute
    public void setLandmark(String landmark)
    {
        landmark_ = landmark;
    }
}
