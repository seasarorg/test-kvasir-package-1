package org.seasar.kvasir.cms.kdiary.kdiary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.seasar.kvasir.page.Page;


public class KdiaryDate
{
    private Calendar calendar_;

    private String year_;

    private String month_;

    private String day_;


    public KdiaryDate()
    {
        this(new Date());
    }


    public KdiaryDate(Date date)
    {
        initialize(date);
    }


    /**
     * "yyyyMMdd"形式の日付文字列からDateDtoオブジェクトを構築します。
     *
     * @param yyyyMMdd "yyyyMMdd"形式の日付文字列。
     */
    public KdiaryDate(String yyyyMMdd)
    {
        initialize(toDate(yyyyMMdd));
    }


    public KdiaryDate(Page article)
    {
        String name = article.getName();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            name = name.substring(0, dot);
        }
        String parentPathname = article.getParentPathname();
        initialize(toDate(parentPathname.substring(parentPathname
            .lastIndexOf('/') + 1)
            + name));
    }


    public KdiaryDate(String year, String month, String day)
    {
        initialize(year, month, day);
    }


    void initialize(String year, String month, String day)
    {
        year_ = year;
        month_ = addPadding(month);
        day_ = addPadding(day);
        calendar_ = Calendar.getInstance();
        calendar_.set(Calendar.YEAR, Integer.parseInt(year));
        calendar_.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        calendar_.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
    }


    void initialize(Date date)
    {
        calendar_ = Calendar.getInstance();
        calendar_.setTime(date);
        year_ = String.valueOf(calendar_.get(Calendar.YEAR));
        month_ = addPadding(String.valueOf(calendar_.get(Calendar.MONTH) + 1));
        day_ = addPadding(String.valueOf(calendar_.get(Calendar.DAY_OF_MONTH)));
    }


    Date toDate(String yyyyMMdd)
    {
        try {
            return new SimpleDateFormat("yyyyMMdd").parse(yyyyMMdd);
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Illegal date format: "
                + yyyyMMdd);
        }
    }


    String addPadding(String string)
    {
        if (string.length() > 1) {
            return string;
        } else {
            return "0" + string;
        }
    }


    public String getDay()
    {
        return day_;
    }


    public String getMonth()
    {
        return month_;
    }


    public String getYear()
    {
        return year_;
    }


    public Calendar getCalendar()
    {
        return calendar_;
    }


    public String format(String format)
    {
        return new SimpleDateFormat(format).format(calendar_.getTime());
    }


    public String getMonthDirectoryName()
    {
        return year_ + month_;
    }


    public String getArticleName()
    {
        return day_ + ".html";
    }


    public String getString()
    {
        return year_ + month_ + day_;
    }
}
