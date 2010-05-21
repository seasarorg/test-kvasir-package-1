package org.seasar.kvasir.base.mime;

public interface MimeMappings
{
    MimeMapping[] getMappings();


    /**
     * 指定されたファイル名に対応するMimeTypeを返します。
     * 対応するMimeTypeが見つからなかった場合はnullを返します。
     * 
     * @param file ファイル名。
     * nullを指定してはいけません。
     * @return MimeType。
     */
    String getMimeType(String file);


    String getExtension(String mimeType);
}
