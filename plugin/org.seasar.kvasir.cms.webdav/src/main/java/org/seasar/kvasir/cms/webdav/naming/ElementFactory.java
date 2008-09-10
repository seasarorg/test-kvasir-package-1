package org.seasar.kvasir.cms.webdav.naming;

import java.io.InputStream;

import javax.naming.NamingException;

import org.seasar.kvasir.cms.webdav.naming.impl.ElementDirContext;


public interface ElementFactory<T>
{
    void setDirContext(ElementDirContext<T> dirContext);


    /**
     * 指定されたパスに対応するElementを生成可能であれば生成します。
     * <p>指定されたパスがこのElementFactoryによって扱われる形式ではない場合は
     * nullを返します。
     * </p>
     *
     * @param path パス。
     * @return Elementオブジェクト。
     * @throws NamingException
     */
    Element<T> newInstance(String path)
        throws NamingException;


    /**
     * 指定されたElementの子Elementのパスを返します。
     * <p>実際の子Elementのパスのうち、
     * このElementFactoryが扱う形式のパスだけを返します。
     * このElementFactoryが扱う形式の子パスが存在しない場合は空の配列を返します。
     * </p>
     *
     * @param element Element。
     * @return 子パス。
     * @throws NamingException
     */
    String[] getChildPaths(Element<T> element)
        throws NamingException;


    /**
     * 指定されたElementを削除します。
     * <p>指定されたElementのパスは、このElementFactoryによって扱われる形式である必要があります。
     * そうでない形式のパスを持つElementを指定した場合の動作は不定です。
     * </p>
     *
     * @param element Element。
     * @throws NamingException
     */
    void delete(Element<T> element)
        throws NamingException;


    /**
     * 指定されたElementのコンテントを設定します。
     * <p>指定されたElementのパスは、このElementFactoryによって扱われる形式である必要があります。
     * そうでない形式のパスを持つElementを指定した場合の動作は不定です。
     * </p>
     *
     * @param element Element。
     * @param encoding コンテントがテキストの場合の文字エンコーディング。
     * @param in コンテントを表すInputStream。
     * @throws NamingException
     */
    void setContent(Element<T> element, String encoding, InputStream in)
        throws NamingException;


    /**
     * 指定されたElementに対応するリソースを移動します。
     * <p>指定されたElementのパスは、このElementFactoryによって扱われる形式である必要があります。
     * そうでない形式のパスを持つElementを指定した場合の動作は不定です。
     * 移動先を表すElementのパスは、このElementFactoryによって扱われる形式でなくても構いません。
     * </p>
     *
     * @param element 移動するElement。
     * @param element 移動先を表すElement。移動後のElementのパスは、このElementのパスと同じになります。
     * @throws NamingException
     */
    void move(Element<T> element, Element<T> destination)
        throws NamingException;


    /**
     * 指定されたElementを生成します。
     * <p>指定されたElementのパスは、このElementFactoryによって扱われる形式である必要があります。
     * そうでない形式のパスを持つElementを指定した場合の動作は不定です。
     * </p>
     *
     * @param element Element。
     * @param encoding コンテントがテキストの場合の文字エンコーディング。
     * @param in 生成するElementのコンテントをあらわすInputStream。
     * Elementがコレクションの場合はnullを指定して下さい。
     * @throws NamingException
     */
    void create(Element<T> element, String encoding, InputStream in)
        throws NamingException;
}
