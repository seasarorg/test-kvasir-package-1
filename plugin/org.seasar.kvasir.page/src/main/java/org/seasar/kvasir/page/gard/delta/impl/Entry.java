package org.seasar.kvasir.page.gard.delta.impl;

import java.util.ArrayList;
import java.util.List;

import org.seasar.kvasir.page.gard.delta.Delta;


/**
 * 
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。</p>
 * 
 * @author YOKOTA Takehiko
 */
@SuppressWarnings("unchecked")
class Entry
{
    private List<Delta>[] lists_ = new List[Delta.TYPES_COUNT];


    public Entry()
    {
        for (int i = 0; i < lists_.length; i++) {
            lists_[i] = new ArrayList<Delta>();
        }
    }


    public void addDelta(Delta delta)
    {
        lists_[delta.getType()].add(delta);
    }


    public Delta[] getDeltas(int type)
    {
        return lists_[type].toArray(new Delta[0]);
    }
}
