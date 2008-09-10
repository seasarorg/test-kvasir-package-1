package org.seasar.kvasir.page;

/**
 * Pageを一意に特定するための、Heimとパス名の組を表すクラスです。
 *
 * @author YOKOTA Takehiko
 */
public class PathId
    implements Comparable<PathId>
{
    /** デフォルトのページツリーに対応するHeim「Midgard」のIDです。 */
    public static final int HEIM_MIDGARD = 0;

    /** システム用のページツリーに対応するHeim「Alfheim」のIDです。 */
    public static final int HEIM_ALFHEIM = 1;

    private int heimId_;

    private String pathname_;


    /**
     * このクラスのオブジェクトを構築します。
     * 
     * @param heimId HeimのID。
     * @param pathname パス名。
     */
    public PathId(int heimId, String pathname)
    {
        heimId_ = heimId;
        pathname_ = pathname;
    }


    /**
     * このクラスのオブジェクトを構築します。
     * <p>指定されたPageオブジェクトを表すPathIdを構築します。
     * </p>
     * 
     * @param page Pageオブジェクト。nullを指定してはいけません。
     */
    public PathId(Page page)
    {
        this(page.getHeimId(), page.getPathname());
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        PathId other = (PathId)obj;
        if (other.heimId_ != heimId_) {
            return false;
        }
        if (other.pathname_ == null) {
            if (pathname_ != null) {
                return false;
            }
        } else if (!other.pathname_.equals(pathname_)) {
            return false;
        }
        return true;
    }


    @Override
    public int hashCode()
    {
        return heimId_ + (pathname_ != null ? pathname_.hashCode() : 0);
    }


    @Override
    public String toString()
    {
        return "{heimId=" + heimId_ + ", pathname=" + pathname_ + "}";
    }


    /**
     * HeimのIDを返します。
     * 
     * @return HeimのID。
     */
    public int getHeimId()
    {
        return heimId_;
    }


    /**
     * パス名を返します。
     * 
     * @return パス名。
     */
    public String getPathname()
    {
        return pathname_;
    }


    /**
     * PathId同士を比較します。
     * @param o 比較対象のPathId。nullを指定してはいけません。
     * @return 比較結果。指定されたPathIdよりもHeimのIDが大きい場合は1。
     * 小さい場合は-1。
     * 同じ場合はパス名を辞書順で比較し、指定されたPathIdよりもパス名が大きい場合は1。
     * 小さい場合は-1。同じ場合は0。
     */
    public int compareTo(PathId o)
    {
        int cmp = heimId_ - o.heimId_;
        if (cmp == 0) {
            cmp = pathname_.compareTo(o.pathname_);
        }
        return cmp;
    }
}
