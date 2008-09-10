package org.seasar.kvasir.cms.toolbox.toolbox.pop;

import java.util.Map;

import org.seasar.kvasir.cms.pop.PopContext;


public interface PopLogic
{
    /**
     * POPをレンダリングするための準備をします。
     * <p>通常、POPをレンダリングをするために必要なオブジェクトを準備して
     * <code>popScope</code>にputします。
     * </p>
     * <p>{@link PopContext#isInPreviewMode()}がtrueの場合はプレビューモードのため、
     * システムの状態を変更しないように注意して下さい。
     * </p>
     * 
     * @param context コンテキスト。
     * @param args 引数。
     * @param popScope POPの現在のロケールにおけるプロパティ値（プレビューモードの時は、
     * プレビュー対象のプロパティ値）が全て入ったMapオブジェクト。
     * プロパティ値を参照する場合は{@link #getProperty(Map, String)}
     * を使うなどしてこのMapから値を取り出すようにして下さい。
     */
    void process(PopContext context, String[] args, Map<String, Object> popScope);
}
