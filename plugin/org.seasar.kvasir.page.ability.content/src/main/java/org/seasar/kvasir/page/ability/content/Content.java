package org.seasar.kvasir.page.ability.content;

import java.io.InputStream;
import java.util.Date;

import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.io.Resource;


/**
 * Pageのコンテントの1ブロックを表すインタフェースです。
 * <p>1つのContentオブジェクトはコンテントのあるバリアントのあるリビジョンのものを表します。
 * </p>
 * 
 * <p><b>同期化：</b>
 * このインタフェースの実装クラスはスレッドセーフである必要はありません。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public interface Content
{
    String VARNAME_THIS = "this";


    /**
     * IDを返します。
     * 
     * @return ID。
     */
    int getId();


    /**
     * リビジョン番号を返します。
     * 
     * @return リビジョン番号。
     */
    int getRevisionNumber();


    /**
     * バリアントを返します。
     * 
     * @return バリアント。
     */
    String getVariant();


    /**
     * 作成日時を返します。
     * 
     * @return 作成日時。
     */
    Date getCreateDate();


    /**
     * 更新日時を返します。
     * 
     * @return 更新日時。
     */
    Date getModifyDate();


    /**
     * メディアタイプを返します。
     * <p>Pageのコンテントは途中でメディアタイプを変えることができるため、
     * コンテントのブロックにもメディアタイプが紐付けられています。
     * </p>
     * 
     * @return メディアタイプ。
     */
    String getMediaType();


    /**
     * 文字エンコーディングを返します。
     * <p>このコンテントブロックがテキストである場合のみ意味があります。
     * </p>
     * 
     * @return 文字エンコーディング。
     */
    String getEncoding();


    /**
     * 本文を表す{@link InputStream}を返します。
     * 
     * @return 本文を表すInputStream。
     */
    InputStream getBodyInputStream();


    /**
     * 本文を表すバイトの配列を返します。
     * 
     * @return 本文を表すバイトの配列。
     */
    byte[] getBodyBytes();


    /**
     * 本文を表す文字列を返します。
     * <p>このコンテントブロックがテキストでない場合は呼び出さないで下さい。
     * </p>
     * 
     * @return 本文を表す文字列。
     */
    String getBodyString();


    /**
     * 本文を表すリソースを返します。
     * 
     * @return 本文を表すリソース。
     */
    Resource getBodyResource();


    /**
     * 本文をHTML形式にレンダリングした文字列を返します。
     * <p>メディアタイプに従った{@link ContentHandler}を使って、
     * 本文をHTML形式にレンダリングします。
     * </p>
     * <p>返されるHTML文字列は&lt;html&gt;タグや&lt;body&gt;
     * タグ等を持ちません。</p>
     * 
     * @param resolver レンダリングに用いられるVariableResolver。
     * nullを指定することもできます。
     * @return 本文をHTML形式にレンダリングした文字列。
     * @see ContentHandler
     */
    String getBodyHTMLString(VariableResolver resolver);
}
