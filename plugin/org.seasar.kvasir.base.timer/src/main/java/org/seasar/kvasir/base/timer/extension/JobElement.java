package org.seasar.kvasir.base.timer.extension;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Bean;
import net.skirnir.xom.annotation.Default;

import org.seasar.kvasir.base.descriptor.AbstractElement;
import org.seasar.kvasir.base.descriptor.annotation.BindingType;
import org.seasar.kvasir.base.descriptor.annotation.Component;
import org.seasar.kvasir.base.timer.Job;


/**
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはオブジェクトの内部状態を変えない操作に関してスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
@Bean("job")
@Component(bindingType = BindingType.MUST, isa = Job.class)
public class JobElement extends AbstractElement
{
    private ExecutionFrequency executionFrequency_ = ExecutionFrequency.ONCE;


    public String getExecutionFrequency()
    {
        return executionFrequency_.getValue();
    }


    public ExecutionFrequency getExecutionFrequencyEnum()
    {
        return ExecutionFrequency.enumOf(getExecutionFrequency());
    }


    @Attribute
    @Default("once")
    public void setExecutionFrequency(String executionFrequency)
    {
        executionFrequency_ = ExecutionFrequency.enumOf(executionFrequency);
        if (executionFrequency_ == null) {
            throw new IllegalArgumentException("Unknown execution-frequency: "
                + executionFrequency);
        }
    }
}
