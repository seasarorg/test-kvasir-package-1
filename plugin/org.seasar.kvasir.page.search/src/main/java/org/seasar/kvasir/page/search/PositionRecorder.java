package org.seasar.kvasir.page.search;

public interface PositionRecorder
{
    int getRawPosition(int cookedPositon);


    void record(int cookedPosition, int rawPosition);
}
