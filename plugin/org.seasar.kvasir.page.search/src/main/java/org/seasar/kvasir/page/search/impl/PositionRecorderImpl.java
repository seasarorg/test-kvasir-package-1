package org.seasar.kvasir.page.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.seasar.kvasir.page.search.PositionRecorder;


public class PositionRecorderImpl
    implements PositionRecorder
{
    private static final int UNIT = 256;

    private int virtualPosition_ = 0;

    private int actualPosition_ = 0;

    private int blockLength_ = 0;

    private List<int[]> virtualBlockList_ = new ArrayList<int[]>();


    public int getActualPosition(int virtualPosition)
    {
        if (virtualPosition >= virtualPosition_) {
            return UNKNOWN;
        } else {
            return get(virtualPosition);
        }
    }


    public void record(boolean available)
    {
        if (available) {
            set(virtualPosition_, actualPosition_);
            virtualPosition_++;
        }
        actualPosition_++;
    }


    public void rewind()
    {
        virtualPosition_ = 0;
        actualPosition_ = 0;
    }


    public void skip()
    {
        if (actualPosition_ == get(virtualPosition_)) {
            virtualPosition_++;
        }
        actualPosition_++;
    }


    public int getCurrentVirtualPosition()
    {
        return virtualPosition_;
    }


    private int get(int virtualPosition)
    {
        resizeBlock(virtualPosition);
        return virtualBlockList_.get(virtualPosition / UNIT)[virtualPosition
            % UNIT];
    }


    private void set(int virtualPosition, int actualPosition)
    {
        resizeBlock(virtualPosition);
        virtualBlockList_.get(virtualPosition / UNIT)[(virtualPosition % UNIT)] = actualPosition;
    }


    private void resizeBlock(int size)
    {
        if (size < blockLength_) {
            return;
        }
        int y = size / UNIT;
        for (int i = virtualBlockList_.size(); i <= y; i++) {
            virtualBlockList_.add(new int[UNIT]);
        }
        blockLength_ = virtualBlockList_.size() * UNIT;
    }
}
