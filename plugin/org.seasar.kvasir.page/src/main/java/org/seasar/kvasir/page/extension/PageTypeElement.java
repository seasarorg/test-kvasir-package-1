package org.seasar.kvasir.page.extension;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.page.type.PageType;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Required;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはオブジェクトの内部状態を変えない操作に関してスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
@Bean("page-type")
@Component(bindingType = BindingType.MUST, isa = PageType.class)
public class PageTypeElement extends AbstractElement
{
    private String iconResourcePath_;


    public String getIconResourcePath()
    {
        return iconResourcePath_;
    }


    @Attribute
    @Required
    public void setIconResourcePath(String iconResourcePath)
    {
        iconResourcePath_ = iconResourcePath;
    }
}
