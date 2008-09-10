package org.seasar.kvasir.util.collection;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Enumeration;


/**
 * @author YOKOTA Takehiko
 */
public interface PropertyHandler
    extends PropertyReader
{
    String getProperty(String name);


    void setProperty(String name, String value);


    void removeProperty(String name);


    void clearProperties();


    int size();


    boolean containsPropertyName(String name);


    Enumeration propertyNames();


    /**
     * 指定された入力ストリームからプロパティを読み込みます。
     * <p>入力ストリームは読み込みの成功・失敗に関わらず、
     * 自動的にクローズされます。</p>
     * 
     * @param in 入力ストリーム。
     * @throws IOException 読み込みに失敗した場合。
     */
    void load(Reader in)
        throws IOException;


    /**
     * 指定された出力ストリームにプロパティを書き出します。
     * <p>出力ストリーム自動的にクローズされません。</p>
     * 
     * @param out 出力ストリーム。
     * @throws IOException 書き出しに失敗した場合。
     */
    void store(Writer out)
        throws IOException;
}
