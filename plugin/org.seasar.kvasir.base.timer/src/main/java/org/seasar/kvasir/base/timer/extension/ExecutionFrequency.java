package org.seasar.kvasir.base.timer.extension;

/**
 * ジョブの実行頻度を表わす列挙型です。
 * 
 * @author skirnir
 */
public enum ExecutionFrequency
{
    /** 一度だけ実行されることを表わします。 */
    ONCE("once"),

    /** 1分に一度実行されることを表わします。 */
    PER_MINUTE("per-minute");

    public static ExecutionFrequency enumOf(String value)
    {
        for (ExecutionFrequency enm : values()) {
            if (enm.getValue().equals(value)) {
                return enm;
            }
        }
        return null;
    }


    private String value_;


    private ExecutionFrequency(String value)
    {
        value_ = value;
    }


    public String getValue()
    {
        return value_;
    }
}
