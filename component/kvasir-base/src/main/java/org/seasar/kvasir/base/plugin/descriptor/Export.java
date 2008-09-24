package org.seasar.kvasir.base.plugin.descriptor;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Default;
import net.skirnir.xom.annotation.Parent;
import net.skirnir.xom.annotation.Required;


/**
 * ライブラリに含まれるエントリのうちプラグイン外部に公開するものを指定するためのタグに対応するクラスです。
 * <p>デフォルトでは、nameプロパティで示される名前パターンにマッチするクラスが外部に公開されます。
 * リソースを公開したい場合は{@link #setResource(boolean)}でtrueを設定して下さい。
 * </p>
 * 
 * @author yokota
 *
 */
public class Export
{
    /**
     * 「全て」を表す名前パターンです。
     */
    public static final String NAME_ALL = "**";

    private Library parent_;

    private String name_;

    private boolean resource_;


    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<export");
        if (name_ != null) {
            sb.append(" name=\"").append(name_).append("\"");
        }
        if (resource_ != false) {
            sb.append(" resource=\"").append(resource_).append("\"");
        }
        sb.append(" />");
        return sb.toString();
    }


    public Library getParent()
    {
        return parent_;
    }


    @Parent
    public void setParent(Library parent)
    {
        parent_ = parent;
    }


    /**
     * プラグインの外部に公開するエントリの名前パターンを返します。
     * <p>ライブラリに含まれるエントリのうちこのパターンにマッチするものが
     * プラグインの外部に公開されます。
     * </p>
     * 
     * @return Ant形式のパターン。
     */
    public String getName()
    {
        return name_;
    }


    @Attribute
    @Required
    public void setName(String name)
    {
        name_ = name;
    }


    /**
     * 公開する対象がリソースかどうかを返します。
     * <p>trueを返す場合、ライブラリに含まれるエントリのうち
     * {@link #getName()}で得られるパターンにマッチするリソースが
     * プラグインの外部に公開されます。
     * falseを返す場合は名前パターンにマッチするクラスがプラグインの外部に公開されます。
     * 
     * @return リソースかどうか。
     */
    public boolean isResource()
    {
        return resource_;
    }


    @Attribute
    @Default("false")
    public void setResource(boolean resource)
    {
        resource_ = resource;
    }
}
