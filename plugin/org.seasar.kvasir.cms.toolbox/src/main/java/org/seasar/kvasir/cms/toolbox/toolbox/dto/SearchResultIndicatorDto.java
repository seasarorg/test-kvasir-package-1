package org.seasar.kvasir.cms.toolbox.toolbox.dto;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフではありません。
 * </p>
 * 
 * @author YOKOTA Takehiko
 */
public class SearchResultIndicatorDto
{
    private int         total_;
    private int         unit_;
    private int         count_;
    private int         lastPosition_;
    private Element[]   elements_;


    public SearchResultIndicatorDto(int total, int unit, int currentPosition)
    {
        total_ = total;
        unit_ = unit;

        count_ = (total + unit - 1) / unit;
        lastPosition_ = (count_ == 0) ? 0
            : (count_ - 1) * unit;
        elements_ = new Element[count_];
        for (int i = 0, number = 1; i < count_; i++, number++) {
            int firstPosition = i * unit;
            elements_[i] = new Element(number, firstPosition,
                (currentPosition >= firstPosition
                && currentPosition < firstPosition + unit));
        }
    }


    /*
     * public scope methods
     */

    public int getTotal()
    {
        return total_;
    }


    public int getUnit()
    {
        return unit_;
    }


    public int getLastPosition()
    {
        return lastPosition_;
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
        private int         number_;
        private int         position_;
        private boolean     current_;

        public Element(int number, int position, boolean current)
        {
            number_ = number;
            position_ = position;
            current_ = current;
        }

        public int getNumber()
        {
            return number_;
        }

        public int getPosition()
        {
            return position_;
        }

        public boolean isCurrent()
        {
            return current_;
        }
    }
}
