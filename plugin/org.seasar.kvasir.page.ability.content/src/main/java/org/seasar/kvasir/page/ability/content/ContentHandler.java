package org.seasar.kvasir.page.ability.content;

import java.io.InputStream;

import org.seasar.kvasir.util.el.VariableResolver;


/**
 * 生のコンテントを操作するためのインタフェースです。
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要があります。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface ContentHandler
{
    /**
     * VariableResolverにHttpServletRequestオブジェクトを格納するためのキー文字列です。
     */
    String VARNAME_REQUEST = "request";

    /**
     * VariableResolverにHttpServletResponseオブジェクトを格納するためのキー文字列です。
     */
    String VARNAME_RESPONSE = "response";

    /**
     * VariableResolverにServletContextオブジェクトを格納するためのキー文字列です。
     */
    String VARNAME_APPLICATION = "application";


    /**
     * 指定された入力ストリームをHTML文字列に変換して返します。
     * <p><code>in</code>で表されるコンテントをHTML文字列に変換して返します。
     * </p>
     * <p>入力ストリームとしてnullが指定された場合はnullを返します。</p>
     * <p><code>encoding</code>はこのContentHandlerが<code>text/*</code>
     * タイプのコンテントを扱う場合のみ使用されます。
     * nullが指定された場合はUTF-8と見なします。</p>
     * <p>このContentHandlerがコンテント中のパラメータを置換する機能を持つ場合、
     * <code>resolver</code>で指定されたVariableResolver
     * を用いてパラメータを実際の値に置き換えます。
     * このContentHandler
     * がコンテント中のパラメータを置換する機能を持たない場合は
     * <code>resolver</code>は無視されます。
     * </p>
     * <p>返されるHTML文字列は&lt;html&gt;タグや&lt;body&gt;
     * タグ等を持ちません。</p>
     *
     * @param in 入力ストリーム。nullを指定することもできます。nullでない場合、
     * 処理が正常に行なわれたかどうかに関わらず、指定された入力ストリームはクローズされます。
     * @param encoding 文字エンコーディング。nullを指定することもできます。
     * @param type コンテントのメディアタイプ。
     * nullを指定した場合はこのContentHandlerのタイプが使用されます。
     * @param resolver コンテント中のパラメータを置換するための
     * VariableResolver。nullを指定することもできます。
     * @return コンテントをHTML文字列に変換した結果。
     */
    String toHTML(InputStream in, String encoding, String type,
        VariableResolver resolver);


    /**
     * 指定されたコンパイル結果をHTML文字列に変換して返します。
     * 
     * @param compiled コンパイル結果。
     * nullを指定してはいけません。
     * このContentHandlerの内部表現にコンパイルした結果を指定するようにして下さい。
     * @param resolver コンテント中のパラメータを置換するための
     * VariableResolver。nullを指定することもできます。
     * @return コンテントをHTML文字列に変換した結果。
     */
    String toHTML(Object compiled, VariableResolver resolver);


    /**
     * 指定された入力ストリームをこのContentHandlerの内部表現にコンパイルします。
     * 
     * @param in 入力ストリーム。
     * nullを指定してはいけません。
     * @param encoding 文字エンコーディング。
     * nullを指定してはいけません。
     * @param type コンテントのメディアタイプ。
     * nullを指定した場合はこのContentHandlerのタイプが使用されます。
     * @return コンパイル結果。
     */
    Object compile(InputStream in, String encoding, String type);
}
