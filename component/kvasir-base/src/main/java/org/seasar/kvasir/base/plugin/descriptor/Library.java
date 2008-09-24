package org.seasar.kvasir.base.plugin.descriptor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.seasar.kvasir.util.ClassUtils;
import org.seasar.kvasir.util.io.Resource;

import net.skirnir.xom.annotation.Attribute;
import net.skirnir.xom.annotation.Child;
import net.skirnir.xom.annotation.Default;
import net.skirnir.xom.annotation.Id;
import net.skirnir.xom.annotation.Parent;
import net.skirnir.xom.annotation.Required;


/**
 * プラグインのクラスローダにロードさせるライブラリを指定するためのタグに対応するクラスです。
 * <p>指定されたライブラリに含まれるクラスやリソースはプラグイン内から参照できるようになります。
 * </p>
 * <p>クラスやリソースをプラグイン外にも公開したい場合は、子要素として{@link Export}要素を設定
 * することで公開するクラスやリソースを指定することができます。
 * デフォルトでは外部には何も公開されません。
 * </p>
 * 
 * @author yokota
 *
 */
public class Library
{
    public static final String[] PATTERNS_ALL = new String[] { Export.NAME_ALL };

    private Runtime parent_;

    private String name_;

    private List<Export> exportList_ = new ArrayList<Export>();

    private boolean filter_ = false;

    private String encoding_ = null;

    private boolean expand_ = false;


    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("<library");
        if (name_ != null) {
            sb.append(" name=\"").append(name_).append("\"");
        }
        if (filter_ != false) {
            sb.append(" filter=\"").append(filter_).append("\"");
        }
        if (encoding_ != null) {
            sb.append(" encoding=\"").append(encoding_).append("\"");
        }
        if (expand_ != false) {
            sb.append(" expand=\"").append(expand_).append("\"");
        }
        sb.append(">");
        for (Iterator<Export> itr = exportList_.iterator(); itr.hasNext();) {
            sb.append(itr.next());
        }
        sb.append("</library>");
        return sb.toString();
    }


    public Runtime getParent()
    {
        return parent_;
    }


    @Parent
    public void setParent(Runtime parent)
    {
        parent_ = parent;
    }


    /**
     * フィルタ処理を行なう際のリソースの文字エンコーディングを返します。
     * <p>このライブラリに対応するリソースに対してフィルタ処理を行なう際には
     * リソースをテキストとして読み込み、フィルタ処理を行なった後再びリソースとして書き出します。
     * このプロパティはその際の読み込み・書き出し用文字エンコーディングを返します。
     * </p>
     * <p>デフォルトの文字エンコーディングはUTF-8です。
     * </p>
     *   
     * @return 文字エンコーディング。
     * @see #isFilter()
     */
    public String getEncoding()
    {
        return (encoding_ == null ? "UTF-8" : encoding_);
    }


    @Attribute
    @Default("UTF-8")
    public void setEncoding(String encoding)
    {
        encoding_ = encoding;
    }


    /**
     * nameプロパティで表されるディレクトリを展開するかどうかを返します。
     * <p>このプロパティがtrueである場合は、nameプロパティで表されるディレクトリ
     * 以下のJARファイル全てがこのライブラリに含まれると解釈されます
     * （ディレクトリ自体は含まれないようになります）。
     * falseである場合は、nameプロパティが指す対象自体がこのライブラリに含まれると
     * 解釈されます。
     * </p>
     * <p>ディレクトリの展開は再帰的には行なわれません。
     * すなわちディレクトリ直下のJARだけがライブラリに含まれると解釈されます。
     * </p>
     * 
     * @return ディレクトリを展開するかどうか。
     */
    public boolean isExpand()
    {
        return expand_;
    }


    @Attribute
    @Default("false")
    public void setExpand(boolean expand)
    {
        expand_ = expand;
    }


    /**
     * プラグインの外部に公開するエントリを表す{@link Export}の配列を返します。
     * 
     * @return {@link Export}の配列。nullが返されることはありません。
     */
    public Export[] getExports()
    {
        return exportList_.toArray(new Export[0]);
    }


    @Child
    public void addExport(Export export)
    {
        exportList_.add(export);
    }


    public void setExports(Export[] exports)
    {
        exportList_.clear();
        exportList_.addAll(Arrays.asList(exports));
    }


    /**
     * このライブラリにフィルタ処理を行なうかどうかを返します。
     * <p>このプロパティがtrueである場合、nameプロパティに対応するリソースに
     * フィルタ処理が施されます。
     * nameプロパティに対応するリソースがディレクトリである場合は
     * ディレクトリに含まれる全てのリソース（再帰的に全て）にフィルタ処理が施されます。
     * </p>
     * <p>ここでのフィルタ処理とは、リソース中の「<code>${xxx}</code>」のような指定を
     * plugin.xproperties中のxxxプロパティの値で置き換えることを表します。
     * </p>
     * <p>このプロパティがfalseである場合はフィルタ処理は施されません。
     * </p>
     * <p>デフォルトの状態ではフィルタ処理は行なわれません。
     * </p>
     * 
     * @return フィルタ処理を行なうかどうか。
     * @see #getEncoding()
     */
    public boolean isFilter()
    {
        return filter_;
    }


    @Attribute
    @Default("false")
    public void setFilter(boolean filter)
    {
        filter_ = filter;
    }


    /**
     * ライブラリの名前を返します。
     * <p>名前はplugin.xmlの置いてあるディレクトリ相対のパスとして表されます。
     * ディレクトリを返すこともあります。
     * </p>
     * 
     * @return ライブラリの名前。
     */
    public String getName()
    {
        return name_;
    }


    @Attribute
    @Required
    @Id
    public void setName(String name)
    {
        name_ = name;
    }


    /**
     * このライブラリに対応するクラスパス要素を表すURLの配列を返します。
     * 基本的にはnameプロパティに対応するJARファイルまたはディレクトリが返されますが、
     * nameがディレクトリを指していてかつexpandプロパティの値がtrueである場合は
     * ディレクトリに含まれる全てのJARファイルが返されます。
     * 
     * @return URLの配列。nullが返されることはありません。
     * @see #isExpand()
     */
    public URL[] getURLsForURLClassLoader()
    {
        if (name_ != null) {
            Resource directory = getParent().getParent()
                .getRuntimeResourcesDirectory();
            Resource resource = directory.getChildResource(name_);
            List<URL> urlList = new ArrayList<URL>();
            if (expand_) {
                // ディレクトリ内のJARファイルを対象にする。
                Resource[] children = resource.listResources();
                if (children != null) {
                    for (int i = 0; i < children.length; i++) {
                        if (children[i].isDirectory()
                            || !children[i].getName().endsWith(".jar")) {
                            continue;
                        }
                        URL url = ClassUtils
                            .getURLForURLClassLoader(children[i].toFile());
                        if (url != null) {
                            urlList.add(url);
                        }
                    }
                }
            } else {
                URL url = ClassUtils.getURLForURLClassLoader(resource.toFile());
                if (url != null) {
                    urlList.add(url);
                }
            }
            return urlList.toArray(new URL[0]);
        }

        return new URL[0];
    }


    /**
     * ライブラリに含まれるクラスのうちプラグインの外部に公開されるクラスを表すパターンを返します。
     * <p>このメソッドが返すパターンに名前がマッチするクラスはプラグインの外部に公開されます。
     * </p>
     * <p>リソースは対象外です。
     * </p>
     * 
     * @return クラス名のパターンの配列。nullが返されることはありません。
     */
    public String[] getExportClassPatterns()
    {
        return getExportPatterns(false);
    }


    /**
     * ライブラリに含まれるリソースのうちプラグインの外部に公開されるリソースを表すパターンを返します。
     * <p>このメソッドが返すパターンに名前がマッチするリソースはプラグインの外部に公開されます。
     * </p>
     * <p>クラスは対象外です。
     * </p>
     * 
     * @return リソース名のパターンの配列。nullが返されることはありません。
     */
    public String[] getExportResourcePatterns()
    {
        return getExportPatterns(true);
    }


    String[] getExportPatterns(boolean resource)
    {
        String[] exportPatterns;
        boolean all = false;
        List<String> list = new ArrayList<String>();
        int size = exportList_.size();
        for (int i = 0; i < size; i++) {
            Export export = exportList_.get(i);
            if (export.isResource() != resource) {
                continue;
            }
            String name = export.getName();
            if (Export.NAME_ALL.equals(name)) {
                all = true;
                break;
            } else if (name != null) {
                list.add(name);
            }
        }
        if (all) {
            exportPatterns = PATTERNS_ALL;
        } else {
            exportPatterns = list.toArray(new String[0]);
        }
        return exportPatterns;
    }
}
