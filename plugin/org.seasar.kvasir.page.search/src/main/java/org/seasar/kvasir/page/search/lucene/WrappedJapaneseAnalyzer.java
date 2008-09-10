package org.seasar.kvasir.page.search.lucene;

import java.io.File;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.seasar.kvasir.base.log.KvasirLog;
import org.seasar.kvasir.base.log.KvasirLogFactory;


/**
 * デフォルトコンストラクタを使ってJapaneseAnalyzerを生成すると
 * Webアプリケーション毎に
 * Senの辞書設定ファイルを設定できない問題を解決するためのラッパークラスです。
 * <p>Sen1.1以降とlucene-ja1.4.2以降の組み合わせでは、
 * 同一VM上で動作するそれぞれの
 * Webアプリケーション毎に辞書設定ファイルを設定することができますが、
 * 辞書設定ファイルのパスはJapaneseAnalyzer
 * のコンストラクタの引数として与えるようになっているため、
 * JapaneseAnalyzerを<code>Class.forName().newInstance()</code>
 * のようにデフォルトコンストラクタによって生成すると、
 * JapaneseAnalyzerに適切な辞書ファイルを知らせることができません。
 * このクラスを使うことで、
 * デフォルトコンストラクタによっても適切な辞書ファイルを利用するような
 * JapaneseAnalyzerクラスを生成することができるようになります。</p>
 *
 * @author YOKOTA Takehiko
 */
public class WrappedJapaneseAnalyzer extends Analyzer
{
    private static final String RELATIVE_CONFIG_PATH = File.separator + "conf"
        + File.separator + "sen.xml";

    private static final String ANALYZER_CLASS_NAME = "org.apache.lucene.analysis.ja.JapaneseAnalyzer";

    private static Class<?> analyzerClass_;

    private static Constructor<?> analyzerConstructor_;

    private static boolean enabled_ = false;

    private static String configPath_ = null;

    private static KvasirLog log_ = KvasirLogFactory
        .getLog(WrappedJapaneseAnalyzer.class);

    private Analyzer analyzer_;

    /*
     * static initializers
     */

    static {
        try {
            analyzerClass_ = Class.forName(ANALYZER_CLASS_NAME);
            try {
                analyzerConstructor_ = analyzerClass_
                    .getConstructor(new Class[] { String.class });
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException("JapaneseAnalyzer may be odd: "
                    + ANALYZER_CLASS_NAME);
            }
            enabled_ = true;
        } catch (ClassNotFoundException ex) {
            // クラスが見つからなかった場合は有効化しないようにする。
            log_.error("Can't resolve class: " + ANALYZER_CLASS_NAME, ex);
        }
    }


    /*
     * constructors
     */

    public WrappedJapaneseAnalyzer()
    {
        if (!enabled_) {
            throw new IllegalStateException("Can't find JapaneseAnalyzer: "
                + ANALYZER_CLASS_NAME);
        }

        try {
            if (configPath_ == null) {
                analyzer_ = (Analyzer)analyzerClass_.newInstance();
                if (log_.isDebugEnabled()) {
                    log_.debug("Created analyzer with default constructor");
                }
            } else {
                analyzer_ = (Analyzer)analyzerConstructor_
                    .newInstance(new Object[] { configPath_ });
                if (log_.isDebugEnabled()) {
                    log_.debug("Created analyzer with configPath: "
                        + configPath_);
                }
            }
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }


    /*
     * static methods
     */

    public static void setSenHome(String senHome)
    {
        if (senHome.endsWith(File.separator)) {
            senHome = senHome.substring(0, senHome.length() - 1);
        }
        setConfigPath(senHome + RELATIVE_CONFIG_PATH);
    }


    public static void setConfigPath(String configPath)
    {
        configPath_ = configPath;
    }


    /*
     * public scope methods
     */

    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        return analyzer_.tokenStream(fieldName, reader);
    }
}
