package org.seasar.kvasir.page.ability.timer;

public class CronField
{
    public static final String EVERY = "*";

    private static final String DELIM_RANGE = "-";

    private static final String DELIM_EVERY = "/";

    private static final CronField INSTANCE_EVERY = new CronField(EVERY);


    public static CronField every()
    {
        return INSTANCE_EVERY;
    }


    public static CronField every(int skip)
    {
        return new CronField(EVERY, skip);
    }


    public static CronField every(int from, int to, int skip)
    {
        return new CronField(from, to, skip);
    }


    public static CronField every(DayOfWeek from, DayOfWeek to, int skip)
    {
        return new CronField(from.getId(), to.getId(), skip);
    }


    public static CronField of(int value)
    {
        return new CronField(String.valueOf(value));
    }


    public static CronField of(DayOfWeek value)
    {
        return new CronField(String.valueOf(value.getId()));
    }


    public static CronField of(int from, int to)
    {
        return new CronField(from, to);
    }


    public static CronField of(DayOfWeek from, DayOfWeek to)
    {
        return new CronField(from.getId(), to.getId());
    }


    public static CronField parse(String string)
    {
        if (string == null) {
            return null;
        }

        int skip = 1;
        int slash = string.indexOf(DELIM_EVERY);
        if (slash >= 0) {
            skip = Integer.parseInt(string.substring(slash + 1));
            string = string.substring(0, slash);
        }

        int hyphen = string.indexOf(DELIM_RANGE);
        if (hyphen >= 0) {
            int from = Integer.parseInt(string.substring(0, hyphen));
            int to = Integer.parseInt(string.substring(hyphen + 1));
            return every(from, to, skip);
        } else {
            if (EVERY.equals(string)) {
                return every(skip);
            } else {
                return of(Integer.parseInt(string));
            }
        }
    }


    private boolean every_;

    private int from_;

    private int to_;

    private int skip_;


    private CronField(String value)
    {
        this(value, 1);
    }


    private CronField(String value, int skip)
    {
        if (EVERY.equals(value)) {
            every_ = true;
        } else {
            from_ = to_ = Integer.parseInt(value);
        }
        skip_ = skip;
    }


    private CronField(int from, int to)
    {
        this(from, to, 1);
    }


    private CronField(int from, int to, int skip)
    {
        from_ = from;
        to_ = to;
        skip_ = skip;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (every_) {
            sb.append(EVERY);
        } else {
            if (from_ == to_) {
                sb.append(from_);
            } else {
                sb.append(from_).append(DELIM_RANGE).append(to_);
            }
        }
        if (skip_ > 1) {
            sb.append(DELIM_EVERY).append(skip_);
        }
        return sb.toString();
    }


    public boolean isMatched(int value)
    {
        if (every_) {
            return value % skip_ == 0;
        } else {
            return value >= from_ && value <= to_
                && (value - from_) % skip_ == 0;
        }
    }


    public boolean isMatched(DayOfWeek value)
    {
        return isMatched(value.getId());
    }
}
