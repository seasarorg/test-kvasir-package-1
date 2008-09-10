package org.seasar.kvasir.cms.ymir.extension;

import java.util.regex.Pattern;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.cms.ymir.ApplicationMapping;
import org.seasar.kvasir.cms.ymir.YmirApplication;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Default;
import net.skirnir.xom.annotation.Required;


/**
 * アプリケーションを拡張する場合、特定のパスへのリクエストを別アプリケーションに処理させたいことがあります。
 * このクラスは、これを行なうために用意されているapplicationMappings拡張ポイントのための拡張要素クラスです。
 * 
 * @author YOKOTA Takehiko
 */
@Component(bindingType = BindingType.NONE)
@Bean("application-mapping")
public class ApplicationMappingElement extends AbstractElement
    implements ApplicationMapping
{
    private String forwardedId_;

    private String path_;

    private String targetId_;

    private boolean regex_;

    private Pattern pathPattern_;

    private YmirApplication forwardedApplication_;


    /**
     * リクエストを処理させたいアプリケーションのIDを返します。
     * 
     * @return リクエストを処理させたいアプリケーションのID。
     */
    public String getForwardedId()
    {
        return forwardedId_;
    }


    @Attribute
    @Required
    public void setForwardedId(String forwardedId)
    {
        forwardedId_ = forwardedId;
    }


    /**
     * パスを返します。
     * <p><code>isRegex()</code>がtrueの場合はパスの正規表現を表します。</p>
     * 
     * @return パス。
     */
    public String getPath()
    {
        return path_;
    }


    @Attribute
    @Required
    public void setPath(String path)
    {
        path_ = path;
        if (path != null) {
            pathPattern_ = Pattern.compile(path);
        } else {
            pathPattern_ = null;
        }
    }


    /**
     * 元々リクエストを処理するはずのアプリケーションのIDを返します。
     * 
     * @return 元々リクエストを処理するはずのアプリケーションのID。
     */
    public String getTargetId()
    {
        return targetId_;
    }


    @Attribute
    @Required
    public void setTargetId(String targetId)
    {
        targetId_ = targetId;
    }


    /**
     * パスを正規表現として解釈するかどうかを返します。
     * 
     * @return パスを正規表現として解釈するかどうか。
     */
    public boolean isRegex()
    {
        return regex_;
    }


    @Attribute
    @Default("false")
    public void setRegex(boolean regex)
    {
        regex_ = regex;
    }


    public boolean isMatched(String path)
    {
        if (regex_) {
            return pathPattern_.matcher(path).find();
        } else {
            return path_.equals(path);
        }
    }


    /**
     * 実際にリクエストの処理を行なうアプリケーションオブジェクトを返します。
     * 
     * @return アプリケーションオブジェクト。
     */
    public YmirApplication getForwardedApplication()
    {
        return forwardedApplication_;
    }


    public void setForwardedApplication(YmirApplication forwardedApplication)
    {
        forwardedApplication_ = forwardedApplication;
    }
}
