package org.seasar.kvasir.base.timer;
import org.seasar.kvasir.base.timer.extension.JobElement;


/**
 * {@link Job}の実装クラスを作成するためのベースとして使用可能な抽象クラスです。
 * 
 * @author skirnir
 * @see Job
 */
abstract public class AbstractJob
    implements Job
{
    private JobElement element_;


    public void setElement(JobElement element)
    {
        element_ = element;
    }


    public JobElement getElement()
    {
        return element_;
    }
}
