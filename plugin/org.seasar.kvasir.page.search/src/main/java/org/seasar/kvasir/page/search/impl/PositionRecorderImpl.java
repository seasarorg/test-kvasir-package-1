package org.seasar.kvasir.page.search.impl;

import java.util.SortedMap;
import java.util.TreeMap;

import org.seasar.kvasir.page.search.PositionRecorder;


public class PositionRecorderImpl
    implements PositionRecorder
{
    private SortedMap<Integer, Integer> cookedRawMap_ = new TreeMap<Integer, Integer>();


    public PositionRecorderImpl()
    {
        cookedRawMap_.put(0, 0);
    }


    public int getRawPosition(int cookedPositon)
    {
        SortedMap<Integer, Integer> map = cookedRawMap_.subMap(0,
            cookedPositon + 1);
        int nearestCookedPosition = map.lastKey();
        int nearestRawPosition = map.get(nearestCookedPosition);
        return nearestRawPosition + (cookedPositon - nearestCookedPosition);
    }


    public void record(int cookedPosition, int rawPosition)
    {
        cookedRawMap_.put(cookedPosition, rawPosition);
    }
}
