package org.seasar.kvasir.cms.toolbox.toolbox.dto;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.List;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class SearchResultIndicatorDto
{
    public static final int DISPLAYPAGERANGE_ALL = -1;

    private int entriesCount_;

    private int unit_;

    private int pagesCount_;

    private int lastEntryPosition_;

    private Element[] elements_;

    private int currentPagePosition_;

    private int previousOffset_;

    private int nextOffset_;


    public SearchResultIndicatorDto(int entriesCount, int unit,
        int currentEntryPosition, int displayPageRange)
    {
        entriesCount_ = entriesCount;
        unit_ = unit;
        currentPagePosition_ = currentEntryPosition / unit;
        previousOffset_ = max(0, currentEntryPosition - unit);
        nextOffset_ = currentEntryPosition + unit;

        pagesCount_ = (entriesCount + unit - 1) / unit;
        lastEntryPosition_ = (pagesCount_ == 0) ? 0 : (pagesCount_ - 1) * unit;

        if (displayPageRange <= 0) {
            displayPageRange = pagesCount_;
        }

        int lastPagePositionForFirstPageRange = min(displayPageRange,
            pagesCount_);
        int firstPagePositionForLastPageRange = max(pagesCount_
            - displayPageRange, lastPagePositionForFirstPageRange);

        int firstPagePositionForCurrentPageRange = max(currentPagePosition_
            - displayPageRange + 1, lastPagePositionForFirstPageRange);
        int lastPagePositionForCurrentPageRange = min(currentPagePosition_
            + displayPageRange, firstPagePositionForLastPageRange);

        List<Element> list = new ArrayList<Element>();
        for (int pageIdx = 0; pageIdx < lastPagePositionForFirstPageRange; pageIdx++) {
            list.add(new Element(pageIdx + 1, pageIdx * unit,
                pageIdx == currentPagePosition_));
        }
        if (lastPagePositionForFirstPageRange < firstPagePositionForCurrentPageRange) {
            list.add(null);
        }
        for (int pageIdx = firstPagePositionForCurrentPageRange; pageIdx < lastPagePositionForCurrentPageRange; pageIdx++) {
            list.add(new Element(pageIdx + 1, pageIdx * unit,
                pageIdx == currentPagePosition_));
        }
        if (lastPagePositionForCurrentPageRange < firstPagePositionForLastPageRange) {
            list.add(null);
        }
        for (int pageIdx = firstPagePositionForLastPageRange; pageIdx < pagesCount_; pageIdx++) {
            list.add(new Element(pageIdx + 1, pageIdx * unit,
                pageIdx == currentPagePosition_));
        }
        elements_ = list.toArray(new Element[0]);
    }


    /*
     * public scope methods
     */

    public boolean isFirstPage()
    {
        return currentPagePosition_ == 0;
    }


    public boolean isLastPage()
    {
        return currentPagePosition_ == pagesCount_ - 1;
    }


    public int getEntriesCount()
    {
        return entriesCount_;
    }


    public int getUnit()
    {
        return unit_;
    }


    public int getPreviousOffset()
    {
        return previousOffset_;
    }


    public int getNextOffset()
    {
        return nextOffset_;
    }


    public int getLastEntryPosition()
    {
        return lastEntryPosition_;
    }


    public Element[] getElements()
    {
        return elements_;
    }


    /*
     * inner classes
     */

    public static class Element
    {
        private int index_;

        private int offset_;

        private boolean current_;


        public Element(int index, int offset, boolean current)
        {
            index_ = index;
            offset_ = offset;
            current_ = current;
        }


        public int getIndex()
        {
            return index_;
        }


        public int getOffset()
        {
            return offset_;
        }


        public boolean isCurrent()
        {
            return current_;
        }
    }
}
