package org.seasar.kvasir.page.condition;

/**
 * 検索結果の並び順を指定するためのクラスです。
 * <p><b>同期化：</b>
 * このクラスは不変クラスです。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class Order
{
    /** フィールド名です。 */
    private String fieldName_;

    /** 並べる順番が昇順かどうかです。 */
    private boolean ascend_;


    /*
     * constructors
     */

    /**
     * このクラスのオブジェクトを構築します。
     * <p>指定したフィールドに関して昇順に並べることを表すオブジェクトを構築します。
     * </p>
     * 
     * @param fieldName フィールド名。フィールド名としては{@link PageCondition}の
     * <code>FIELD_*</code>定数を指定します。
     */
    public Order(String fieldName)
    {
        this(fieldName, true);
    }


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param fieldName フィールド名。フィールド名としては{@link PageCondition}の
     * <code>FIELD_*</code>定数を指定します。
     * @param ascend 昇順かどうか。
     */
    public Order(String fieldName, boolean ascend)
    {
        fieldName_ = fieldName;
        ascend_ = ascend;
    }


    /*
     * public scope methods
     */

    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (ascend_) {
            sb.append("+");
        } else {
            sb.append("-");
        }
        sb.append(fieldName_);
        return sb.toString();
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (ascend_ ? 1231 : 1237);
        result = prime * result
            + ((fieldName_ == null) ? 0 : fieldName_.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Order other = (Order)obj;
        if (ascend_ != other.ascend_)
            return false;
        if (fieldName_ == null) {
            if (other.fieldName_ != null)
                return false;
        } else if (!fieldName_.equals(other.fieldName_))
            return false;
        return true;
    }


    /**
     * フィールド名を返します。
     *
     * @return フィールド名。
     */
    public String getFieldName()
    {
        return fieldName_;
    }


    /**
     * 並べる順番が昇順かどうかを返します。
     *
     * @return 並べる順番が昇順かどうか。
     */
    public boolean isAscending()
    {
        return ascend_;
    }
}
