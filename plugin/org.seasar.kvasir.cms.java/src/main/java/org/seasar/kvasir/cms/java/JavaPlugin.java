package org.seasar.kvasir.cms.java;

import java.io.Reader;

import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.Plugin;


public interface JavaPlugin
    extends Plugin<EmptySettings>
{
    /**
     * 指定されたclass bodyをコンパイルしてClassオブジェクトを作成して返します。
     * 
     * @param classBody class body。class bodyについての説明は
     * <a href="http://www.janino.net/use.html#class_body_evaluator">Janinoのサイト</a>
     * を参照して下さい。
     * @param superClass 作成するクラスの親クラス。
     * @param classLoader コンパイルの際に使用されるクラスローダ。
     * @return コンパイル結果のClassオブジェクト。
     * @throws CompileException コンパイルに失敗した場合。
     */
    <S> Class<S> compileClassBody(Reader classBody, Class<S> superClass,
        ClassLoader classLoader)
        throws CompileException;


    /**
     * 指定されたJavaのソースコードをコンパイルしてClassオブジェクトを作成します。
     * <p>このメソッドが返すのは作成したClassオブジェクトを保持するクラスローダです。
     * 
     * @param javaSource Javaのソースコード。
     * @param classLoader コンパイルの際に使用されるクラスローダ。
     * @return コンパイル結果のClassオブジェクトを保持するクラスローダ。
     * @throws CompileException コンパイルに失敗した場合。
     */
    ClassLoader compile(Reader javaSource, ClassLoader classLoader)
        throws CompileException;
}
