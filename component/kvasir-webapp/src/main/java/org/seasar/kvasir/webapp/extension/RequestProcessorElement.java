package org.seasar.kvasir.webapp.extension;

import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.webapp.processor.RequestProcessor;
import org.seasar.kvasir.webapp.processor.impl.FilteredRequestProcessor;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
@Bean("request-processor")
@Component(bindingType = BindingType.MUST, isa = RequestProcessor.class)
public class RequestProcessorElement extends AbstractPhasedElement
{
    public static final String METHOD_ALL = "*";

    public static final String[] METHODS_DEFAULT = new String[] { "GET", "POST" };

    private String method_;


    public String[] getMethods()
    {
        if (method_ == null) {
            return METHODS_DEFAULT;
        } else {
            return method_.split(",");
        }
    }


    public String getMethod()
    {
        return method_;
    }


    @Attribute
    public void setMethod(String method)
    {
        method_ = method;
    }


    public RequestProcessor getRequestProcessor()
    {
        String[] methods;
        if (METHOD_ALL.equals(method_)) {
            methods = null;
        } else {
            methods = getMethods();
        }
        return new FilteredRequestProcessor(((RequestProcessor)getComponent()),
            methods);
    }
}
