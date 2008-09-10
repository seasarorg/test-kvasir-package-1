package org.seasar.kvasir.util.wiki;


/**
 * このインタフェースの実装クラスはステートレスである必要があります。
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 */
public interface BlockEvaluator
{
    /**
     * このBlockEvaluatorが処理する行ブロックが、
     * 他の種類の行ブロックを入れ子にすることができるかどうかを返します。
     * <p>このメソッドは、
     * 行ブロックの最終行の処理が終わった後にフレームワークから呼び出されます。
     * </p>
     * <p>このBlockEvaluator
     * が処理する行ブロックが他の種類の行ブロックを入れ子に持つ場合は
     * trueを返すようにして下さい。
     * 他の種類の行ブロックを入れ子に持たない場合は
     * falseを返すようにして下さい。
     *
     * @return 他の種類の行ブロックを入れ子にすることができるかどうか。
     */ 
    boolean canNest();


    /**
     * このBlockEvaluatorが処理する行ブロックが、
     * 他の種類の行ブロックの入れ子になることができるかどうかを返します。
     * <p>このメソッドは、
     * 行ブロックの最終行の処理が終わった後にフレームワークから呼び出されます。
     * </p>
     * <p>このBlockEvaluator
     * が処理する行ブロックが他の種類の行ブロックの入れ子になり得る場合は
     * trueを返すようにして下さい。
     * 他の種類の行ブロックを入れ子になり得ない場合は
     * falseを返すようにして下さい。
     *
     * @return 他の種類の行ブロックを入れ子にすることができるかどうか。
     */ 
    boolean canBeNested();


    /**
     * 現在の行ブロックをこのEvaluatorが処理すべきかどうかを判断します。
     * <p>このメソッドは、
     * 行ブロックの開始時にフレームワークから呼び出されます。</p>
     * <p>行ブロックの先頭行<code>line</code>から、
     * 現在の行ブロックをこのEvaluatorが処理すべきかを判断して下さい。</p>
     * <p><code>line</code>がこのEvaluatorの処理対処でない場合は、
     * 返り値としてfalseを返すようにして下さい。</p>
     * <p><code>line</code>がこのEvaluatorの処理対処である場合は、
     * 返り値としてtrueを返すようにして下さい。</p>
     * <p><b>注意：</b>このメソッドの中では{@link Context#getWriter()}
     * が返す<code>PrintWriter</code>に何も出力してはいけません。</p>
     *
     * @param context 現在のコンテキスト。
     * @param line 行ブロックの先頭文字列。
     * @return 行ブロックが処理対象かどうか。
     */
    boolean shouldEvaluate(Context context, String line);


    /**
     * 行ブロックのそれぞれの行を処理します。
     * <p>このメソッドは、
     * 行ブロックを構成する行毎にフレームワークから呼び出されます。</p>
     * <p>行ブロックが終了した場合は、
     * 返り値としてfalseを返すようにして下さい。</p>
     * <p>行ブロックが終了していない場合は、
     * 返り値としてtrueを返すようにして下さい。</p>
     *
     * @param context 現在のコンテキスト。
     * @param line 処理対象の文字列。
     * @param first 行ブロックの先頭行かどうか。
     * @return 行ブロックが継続しているかどうか。
     */
    boolean evaluate(Context context, String line, boolean first);


    /**
     * 行ブロックの終了時に行なうべき処理を行ないます。
     * <p>このメソッドは、
     * 行ブロックの最終行の処理が終わった後にフレームワークから呼び出されます。
     * </p>
     * <p>必要に応じて文字列を{@link Context#getWriter()}
     * で得られる<code>PrintWriter</code>に出力しても構いません。</p>
     *
     * @param context 現在のコンテキスト。
     */ 
    void terminate(Context context);


    /**
     * 行ブロックの処理中に入れ子として他の種類の行ブロックが現れた時の処理を行ないます。
     * <p>このメソッドは、
     * 行ブロックの処理中に入れ子として他の種類の行ブロックが現れた時にフレームワークから呼び出されます。
     * 次の行ブロックが他の行ブロックの入れ子にならないブロックであったり、
     * このBlockEvaluatorが他の行ブロックを入れ子にできない場合は、
     * このメソッドは呼び出されません。</p>
     * <p>必要に応じて文字列を{@link Context#getWriter()}
     * で得られる<code>PrintWriter</code>に出力しても構いません。</p>
     *
     * @param context 現在のコンテキスト。
     * @param line 入れ子となる行ブロックの先頭の文字列。
     */
    void beginNestedBlock(Context context, String line);


    /**
     * 行ブロックの処理中に他の種類の行ブロックが入れ子として現れた後、
     * その入れ子ブロックの処理が終了した時の処理を行ないます。
     * <p>このメソッドは、
     * 行ブロックの処理中に他の種類の行ブロックが入れ子として現れた後、
     * その入れ子ブロックの処理が終了した時にフレームワークから呼び出されます。
     * このBlockEvaluatorが他の行ブロックを入れ子にできない場合は、
     * このメソッドは呼び出されることはありません。</p>
     * <p>必要に応じて文字列を{@link Context#getWriter()}
     * で得られる<code>PrintWriter</code>に出力しても構いません。</p>
     *
     * @param context 現在のコンテキスト。
     */
    void endNestedBlock(Context context);
}
