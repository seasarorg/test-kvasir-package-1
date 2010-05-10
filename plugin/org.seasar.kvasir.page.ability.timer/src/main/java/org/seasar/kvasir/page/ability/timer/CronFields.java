package org.seasar.kvasir.page.ability.timer;

import java.util.ArrayList;
import java.util.List;


public class CronFields
{
    public static CronFields every()
    {
        return of(CronField.every());
    }


    public static CronFields every(int skip)
    {
        return of(CronField.every(skip));
    }


    public static CronFields every(int from, int to, int skip)
    {
        return of(CronField.every(from, to, skip));
    }


    public static CronFields every(DayOfWeek from, DayOfWeek to, int skip)
    {
        return of(CronField.every(from, to, skip));
    }


    public static CronFields of(int value)
    {
        return of(CronField.of(value));
    }


    public static CronFields of(DayOfWeek value)
    {
        return of(CronField.of(value));
    }


    public static CronFields of(int from, int to)
    {
        return of(CronField.of(from, to));
    }


    public static CronFields of(DayOfWeek from, DayOfWeek to)
    {
        return of(CronField.of(from, to));
    }


    public static CronFields of(CronField... fields)
    {
        return new CronFields(fields);
    }


    public static CronFields parse(String string)
    {
        if (string == null) {
            return null;
        }

        List<CronField> fields = new ArrayList<CronField>();
        for (String tkn : string.split(",")) {
            fields.add(CronField.parse(tkn));
        }
        return of(fields.toArray(new CronField[0]));
    }


    private CronField[] fields_;


    private CronFields(CronField... fields)
    {
        fields_ = fields;
    }


    public CronField[] getFields()
    {
        return fields_;
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (CronField field : fields_) {
            sb.append(delim).append(field);
            delim = ",";
        }
        return sb.toString();
    }


    public boolean isMatched(int value)
    {
        for (CronField field : fields_) {
            if (field.isMatched(value)) {
                return true;
            }
        }
        return false;
    }


    public boolean isMatched(DayOfWeek value)
    {
        return isMatched(value.getId());
    }
}
