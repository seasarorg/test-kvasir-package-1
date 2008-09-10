package org.seasar.kvasir.util.wiki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.seasar.kvasir.util.html.HTMLUtils;


/**
 * Wikiの書式をHTML形式に変換するためのクラスです。
 * <p>このクラスでは、解釈するWikiの書式を自由に設定することができます。
 * {@link #registerLineEvaluator(LineEvaluator)}や
 * {@link #registerBlockEvaluator(BlockEvaluator)}を使って、
 * 解釈したい書式のための{@link #LineEvaluator}や
 * {@link #BlockEvaluator}を登録して下さい。
 * なおEvaluatorの登録が終わった後には必ず{@link #start()}
 * を呼び出して下さい。</p>
 * <p>
 * <p>このクラスはスレッドセーフです。
 * また、このクラスのサブクラスはスレッドセーフである必要があります。</p>
 */
public class WikiEngine
{
    private Map         lineEvaluatorMap_ = new HashMap();
    private String[]    begins_ = new String[0];
    private String      beginChars_ = "";
    private LinkedList  blockEvaluatorList_ = new LinkedList();
    private boolean     start_ = false;


    /*
     * constructors
     */

    public WikiEngine()
    {
        registerBlockEvaluator(new ParagraphEvaluator());
        registerBlockEvaluator(new BreakEvaluator());
    }


    /*
     * public scope methods
     */

    public final void start()
    {
        start_ = true;
    }


    /**
     * <p>このメソッドは{@link #start()}
     * メソッドの呼び出し以降に呼び出してください。</p>
     */
    public final String evaluate(String text)
    {
        if (!start_) {
            throw new IllegalStateException(
                "Can't call this method since this object is not fixed");
        }

        Context context = new Context(this);
        generateHeader(context);
    
        BufferedReader in = new BufferedReader(new StringReader(text));
        try {
            String line;
            LinkedList statusList = new LinkedList(); 
            while ((line = in.readLine()) != null) {
                evaluateBlock(context, statusList, line);
            }
            if (!statusList.isEmpty()) {
                BlockEvaluator status = (BlockEvaluator)statusList.removeLast();
                status.terminate(context);
                status = (statusList.isEmpty() ? (BlockEvaluator)null
                        : (BlockEvaluator)statusList.removeLast());
                if (status != null) {
                    status.endNestedBlock(context);
                    status.terminate(context);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable t) {
                    ;
                }
            }
        }

        generateFooter(context);
    
        return context.getString();
    }


    /**
     * 指定された文字が指定された文字列中に現れる位置を返します。
     * <p><code>str</code>の<code>pos</code>番目の位置（0オリジン）
     * 以降の、<code>ch</code>が現れる最初の位置を返します。
     * この時LineEvaluatorによって解釈される部分文字列はスキップされます。</p>
     * <p>このメソッドは{@link #start()}
     * メソッドの呼び出し以降に呼び出してください。</p>
     */
    public final int indexOfSkipLineEvaluators(String str, char ch, int pos)
    {
        int len = str.length();
        for (int i = pos; i < len; i++) {
            char c = str.charAt(i);
            if (beginChars_.indexOf(c) >= 0) {
                // Evaluatorの開始かもしれないので、Evaluatorの開始かどうか
                // 調べる。
                String begin = null;
                int beginLen = 0;
                LOOP: for (int j = 0; j < begins_.length; j++) {
                    beginLen = begins_[j].length();
                    if (len - i < beginLen) {
                        continue LOOP;
                    }
                    for (int k = 0; k < beginLen; k++) {
                        if (str.charAt(i + k) != begins_[j].charAt(k)) {
                            continue LOOP;
                        }
                    }
                    begin = begins_[j];
                    break;
                }
                if (begin != null) {
                    // LineEvaluatorの開始だった。
                    LineEvaluator evaluator = getLineEvaluator(begin);
                    String end = evaluator.getEnd();
                    int endPos = str.indexOf(end, i + beginLen);
                    if (endPos < 0) {
                        // Evaluatorが開始されたが終了されていないので、
                        // 指定文字は見つからないことになる。
                        return -1;
                    } else {
                        // Evaluatorが見つかったのでスキップする。
                        i = endPos + end.length() - 1;
                        continue;
                    }
                }
            }

            // Evaluatorが見つからなかったので指定文字かどうかをチェックする。
            if (c == ch) {
                // 見つかった。
                return i;
            }
        }

        return -1;
    }


    /**
     * <p>このメソッドは{@link #start()}
     * メソッドの呼び出し以降に呼び出してください。</p>
     */
    public final void evaluateLine(Context context, PrintWriter writer,
        String line)
    {
        int len = line.length();
        int pre = 0;
        for (int i = 0; i < len; i++) {
            char ch = line.charAt(i);
            if (beginChars_.indexOf(ch) < 0) {
                continue;
            }
            String begin = null;
            int beginLen = 0;
            LOOP: for (int j = 0; j < begins_.length; j++) {
                beginLen = begins_[j].length();
                if (len - i < beginLen) {
                    continue LOOP;
                }
                for (int k = 0; k < beginLen; k++) {
                    if (line.charAt(i + k) != begins_[j].charAt(k)) {
                        continue LOOP;
                    }
                }
                begin = begins_[j];
                break;
            }
            if (begin == null) {
                continue;
            }

            LineEvaluator evaluator = getLineEvaluator(begin);
            String end = evaluator.getEnd();

            writer.print(HTMLUtils.filter(line.substring(pre, i)));
            int endPos = line.indexOf(end, i + beginLen);
            if (endPos < 0) {
                pre = i;
                break;
            }

            evaluator.evaluate(
                context, writer, line.substring(i + beginLen, endPos));

            pre = endPos + end.length();
            i = pre - 1;
        }
        writer.print(HTMLUtils.filter(line.substring(pre)));
    }


    public final String evaluateLine(Context context, String line)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        evaluateLine(context, pw, line);
        pw.flush();
        return sw.toString();
    }


    /**
     * <p>このメソッドは{@link #start()}
     * メソッドを呼び出す前に呼び出してください。</p>
     */
    public final WikiEngine registerLineEvaluator(LineEvaluator evaluator)
    {
        if (start_) {
            throw new IllegalStateException(
                "Can't register since this object is fixed");
        }

        String begin = evaluator.getBegin();
        if (lineEvaluatorMap_.containsKey(begin)) {
            // 既に登録されていれば置換する。
            lineEvaluatorMap_.put(begin, evaluator);
        } else {
            lineEvaluatorMap_.put(begin, evaluator);

            char b = begin.charAt(0);
            if (beginChars_.indexOf(b) < 0) {
                beginChars_ += b;
            }

            String[] begins = new String[begins_.length + 1];
            int i = 0;
            for (; i < begins_.length; i++) {
                begins[i] = begins_[i];
            }
            begins[i] = begin;
            begins_ = begins;
        }
        return this;
    }


    /**
     * <p>このメソッドは{@link #start()}
     * メソッドを呼び出す前に呼び出してください。</p>
     */
    public final WikiEngine unregisterLineEvaluator(String begin)
    {
        if (start_) {
            throw new IllegalStateException(
                "Can't unregister since this object is fixed");
        }

        LineEvaluator evaluator = (LineEvaluator)lineEvaluatorMap_.get(begin);
        if (evaluator != null) {
            lineEvaluatorMap_.remove(begin);
            StringBuffer sb = new StringBuffer();
            Set bset = new HashSet();
            List list = new ArrayList(begins_.length - 1);
            for (int i = 0; i < begins_.length; i++) {
                if (begin.equals(begins_[i])) {
                    continue;
                }
                list.add(begins_[i]);
                String b = begins_[i].substring(0, 1);
                if (!bset.contains(b)) {
                    sb.append(b);
                    bset.add(b);
                }
            }
            begins_ = (String[])list.toArray(new String[0]);
            beginChars_ = sb.toString();
        }
        return this;
    }


    /**
     * <p>このメソッドは{@link #start()}
     * メソッドを呼び出す前に呼び出してください。</p>
     */
    public final WikiEngine registerBlockEvaluator(BlockEvaluator evaluator)
    {
        if (start_) {
            throw new IllegalStateException(
                "Can't register since this object is fixed");
        }

        blockEvaluatorList_.addFirst(evaluator);
        return this;
    }


    /*
     * protected scope methods
     */

    /**
     * <p>このメソッドは{@link #start()}
     * メソッドの呼び出し以降に呼び出してください。</p>
     */
    protected final LineEvaluator getLineEvaluator(String begin)
    {
        return (LineEvaluator)lineEvaluatorMap_.get(begin);
    }

    protected void generateHeader(Context context)
    {
        // 必要に応じてオーバライドして下さい。
    }


    protected void generateFooter(Context context)
    {
        // 必要に応じてオーバライドして下さい。
    }


    /*
     * private methods
     */

    private void evaluateBlock(Context context, LinkedList statusList,
        String line)
    {
        BlockEvaluator status = (statusList.isEmpty()
            ? (BlockEvaluator)null : (BlockEvaluator)statusList.removeLast());

        BlockEvaluator evaluator = null;
        for (Iterator itr = blockEvaluatorList_.iterator();
        itr.hasNext(); ) {
            BlockEvaluator e = (BlockEvaluator)itr.next();
            if (e.shouldEvaluate(context, line)) {
                evaluator = e;
                break;
            }
        }
        
        if (evaluator == null) {
            throw new IllegalArgumentException(
                "Can't find an evaluator to parse this line: " + line);
        }

        boolean continuing = (status == evaluator);
        if (!continuing) {
            if (status != null) {
                if (statusList.isEmpty()
                && evaluator.canBeNested() && status.canNest()) {
                    // ネストは1段しかできない。
                    // これは、A-B-Aの2回目のAを最初のAの続きにすべきか
                    // Bの入れ子にすべきか判定できないため。
                    status.beginNestedBlock(context, line);
                    statusList.add(status);
                } else {
                    status.terminate(context);
                    status = (statusList.isEmpty() ? (BlockEvaluator)null
                            : (BlockEvaluator)statusList.removeLast());
                    if (status != null) {
                        status.endNestedBlock(context);
                        if (status == evaluator) {
                            continuing = true;
                        } else {
                            status.terminate(context);
                        }
                    }
                }
            }
            status = evaluator;
        }
        if (!status.evaluate(context, line, !continuing)) {
            status.terminate(context);
            status = (statusList.isEmpty() ? (BlockEvaluator)null
                    : (BlockEvaluator)statusList.removeLast());
            if (status != null) {
                status.endNestedBlock(context);
            }
        }

        if (status != null) {
            statusList.add(status);
        }
    }
}
