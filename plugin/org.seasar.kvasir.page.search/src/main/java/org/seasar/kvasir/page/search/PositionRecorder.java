package org.seasar.kvasir.page.search;

public interface PositionRecorder
{
    int UNKNOWN = -1;


    int getActualPosition(int virtualPositon);


    void record(boolean available);


    void skip();


    void rewind();


    int getCurrentVirtualPosition();
}
