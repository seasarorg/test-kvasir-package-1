package org.seasar.kvasir.util.wiki;

import java.io.PrintWriter;


/**
 * このインタフェースの実装クラスはステートレスである必要があります。
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 */
public interface LineEvaluator
{
    /**
     * このLinkEvaluatorが処理する部分文字列の開始文字列を返します。
     *
     * @return このLinkEvaluatorが処理する部分文字列の開始文字列。
     */
    String getBegin();

    /**
     * このLinkEvaluatorが処理する部分文字列の終了文字列を返します。
     *
     * @return このLinkEvaluatorが処理する部分文字列の終了文字列。
     */
    String getEnd();

    /**
     * 指定された文字列を評価します。
     * <p><code>content</code>はHTMLエスケープされていませんので、
     * <code>content</code>
     * そのものまたは一部を出力する時には必ず
     * {@link net.skirnir.kvasir.util.HTMLUtils#filter(String)}
     * などを用いてHTMLエスケープするようにして下さい。</p>
     * <p>このメソッドはフレームワークから呼び出されます。</p>
     * <p>評価結果は<code>context#getWriter()</code>ではなく
     * <code>writer</code>に出力するようにして下さい。</p>
     *
     * @param context 現在のコンテキスト。
     * @param writer 評価結果の出力先PrintWriter。
     * @param content 処理対象の文字列。
     * 行の中の、{@link #getBegin()}が返す文字列から
     * {@link #getEnd()}が返す文字列までの範囲の文字列です。
     */ 
    void evaluate(Context context, PrintWriter writer, String content);
}
