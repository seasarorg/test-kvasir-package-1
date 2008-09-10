package org.seasar.kvasir.page.search.lucene;

import org.apache.lucene.document.Document;
import org.seasar.kvasir.page.Page;


/**
 * Luceneの検索インデックスに登録するためにページから
 * <code>org.apache.lucene.document.Document</code>
 * オブジェクトを作成するためのインタフェースです。
 *
 * @author YOKOTA Takehiko
 */
public interface DocumentCreator
{
    String FIELD_ID = "id";

    String FIELD_VARIANT = "variant";

    String FIELD_TYPE = "type";

    String FIELD_CREATEDATE = "createDate";

    String FIELD_MODIFYDATE = "modifyDate";

    String FIELD_SIZE = "size";

    String FIELD_LABEL = "label";

    String FIELD_DESCRIPTION = "description";

    String FIELD_BODY = "body";

    String FIELD_SUMMARY = "summary";


    /**
     * 指定されたページをインデックスに登録するための
     * <code>org.apache.lucene.document.Document</code>
     * オブジェクトを生成します。
     * <p>ページが複数ロケール用のデータを持っている場合は、
     * 複数のDocumentオブジェクトが生成されることがあります。
     * </p>
     *
     * @param page ページ。
     * @return <code>org.apache.lucene.document.Document</code>
     * オブジェクトの配列。
     * nullが返されることはありません。
     */
    Document[] newDocuments(Page page);
}
