package org.seasar.kvasir.util.parametrized;

import java.util.Map;


public class ParametrizedTextUtils
{
    /*
     * constructors
     */

    private ParametrizedTextUtils()
    {
    }


    /*
     * static scope methods
     */

    /**
     * 指定されたパラメータ化テキストを評価した結果を返します。
     * <p>指定されたパラメータ化テキスト中のパラメータ置換指定を、
     * パラメータの値を保持しているMapから取り出した値で置き換えます。
     * </p>
     * <p>パラメータ置換指定は、<code>${<em>key</em>}</code>
     * という形式の文字列です。指定された<em>key</em>
     * 文字列をキーとして<code>param</code>から値を取り出し、
     * 取り出した値でパラメータ置換指定を置換します。
     * 値が存在しない場合は<code>null</code>という文字列に置換します。
     * </p>
     * <p>パラメータ置換指定は入れ子にすることができます。
     * 例えば<code>key</code>の値が<code>value</code>で、
     * <code>key.value</code>の値が<code>1</code>
     * である場合、
     * <code>${key.${key}}</code>の評価結果は<code>1</code>になります。
     * </p>
     *  
     * @param expression 評価するパラメータ化テキスト。
     * @param param パラメータが格納されているMapオブジェクト。
     * @return 評価結果。
     */
    public static String evaluate(String expression, Map param)
    {
        return evaluate(expression, param, false, "null");
    }


    /**
     * 指定されたパラメータ化テキストを評価した結果を返します。
     * <p>{@link #evaluate(String, Map)}と同じですが、
     * パラメータ化テキスト中のパラメータ置換指定のパラメータ名について、
     * 大文字小文字を区別しません。
     * すなわち<code>${key}</code>と<code>${Key}</code>
     * を同じものとみなします。
     * なお<code>param</code>はキー名を小文字で保持している必要があります。
     * </p>
     *  
     * @param expression 評価するパラメータ化テキスト。
     * @param param パラメータが格納されているMapオブジェクト。
     * @return 評価結果。
     */ 
    public static String evaluateIgnoreCase(String expression, Map param)
    {
        return evaluate(expression, param, true, "null");
    }


    /**
     * 指定されたパラメータ化テキストを評価した結果を返します。
     * <p>指定されたパラメータ化テキスト中のパラメータ置換指定を、
     * パラメータの値を保持しているMapから取り出した値で置き換えます。
     * </p>
     * <p>パラメータ置換指定は、<code>${<em>key</em>}</code>
     * という形式の文字列です。指定された<em>key</em>
     * 文字列をキーとして<code>param</code>から値を取り出し、
     * 取り出した値でパラメータ置換指定を置換します。
     * 値が存在しない場合は
     * <code>nullValue</code>で指定された文字列に置換します。
     * </p>
     * <p>パラメータ置換指定は入れ子にすることができます。
     * 例えば<code>key</code>の値が<code>value</code>で、
     * <code>key.value</code>の値が<code>1</code>
     * である場合、
     * <code>${key.${key}}</code>の評価結果は<code>1</code>になります。
     * </p>
     * <p><code>ignoreCase</code>がtrueである場合は、
     * パラメータ化テキスト中のパラメータ置換指定のパラメータ名について、
     * 大文字小文字を区別しません。
     * すなわち<code>${key}</code>と<code>${Key}</code>
     * を同じものとみなします。
     * なお<code>param</code>はキー名を小文字で保持している必要があります。
     * </p>
     *  
     * @param expression 評価するパラメータ化テキスト。
     * @param param パラメータが格納されているMapオブジェクト。
     * @param ignoreCase パラメータ名の大文字小文字を区別するかどうか。
     * trueの場合、<code>param</code>
     * にはキー名を小文字で格納しておく必要があります。
     * @param nullValue 値が存在しない場合に使用される文字列。
     * nullを指定するとパラメータ置換指定をそのまま残します。
     * @return 評価結果。
     */ 
    public static String evaluate(String expression, Map param,
        boolean ignoreCase, String nullValue)
    {
        // 高速化のため。
        if (expression.indexOf("\\") < 0
        && expression.indexOf("${") < 0 && expression.indexOf("$[") < 0) {
            return expression;
        }

        StringBuffer sb = new StringBuffer();

        VariableTokenizer vt = new VariableTokenizer(expression, true);
        while (vt.hasMoreTokens()) {
            VariableTokenizer.Token tkn = vt.nextToken();
            String value = tkn.getValue();
            if (tkn.getType() == VariableTokenizer.TYPE_VARIABLE) {
                if (value.length() >= 3) {
                    String name = evaluate(
                        value.substring(2, value.length() - 1),
                        param, ignoreCase, nullValue);
                    if (ignoreCase) {
                        name = name.toLowerCase();
                    }
                    Object obj = param.get(name);
                    String str;
                    if (obj == null) {
                        str = (nullValue == null ? value : nullValue);
                    } else {
                        str = obj.toString();
                    }
                    sb.append(str);
                } else {
                    // 構文エラーの場合はこうなり得る。
                    sb.append(value);
                }
            } else if (tkn.getType() == VariableTokenizer.TYPE_EXPRESSION) {
                if (value.length() >= 3) {
                    String expr = evaluate(
                        value.substring(2, value.length() - 1),
                        param, ignoreCase, nullValue);
                    Object obj = new ScriptExpression(ignoreCase)
                        .evaluateSafely(expr, param, null, null);
                    String str;
                    if (obj == null) {
                        str = (nullValue == null ? value : nullValue);
                    } else {
                        str = obj.toString();
                    }
                    sb.append(str);
                } else {
                    // 構文エラーの場合はこうなり得る。
                    sb.append(value);
                }
            } else {
                sb.append(value);
            }
        }

        return sb.toString();
    }
}
