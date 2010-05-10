package org.seasar.kvasir.page.ability.timer.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.extension.tx.annotation.RequiredTx;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.seasar.kvasir.page.Page;
import org.seasar.kvasir.page.Processable;
import org.seasar.kvasir.page.ProcessableRuntimeException;
import org.seasar.kvasir.page.ability.AbstractPageAbilityAlfr;
import org.seasar.kvasir.page.ability.Attribute;
import org.seasar.kvasir.page.ability.AttributeFilter;
import org.seasar.kvasir.page.ability.PageAbility;
import org.seasar.kvasir.page.ability.timer.CronField;
import org.seasar.kvasir.page.ability.timer.CronFields;
import org.seasar.kvasir.page.ability.timer.Schedule;
import org.seasar.kvasir.page.ability.timer.ScheduleMold;
import org.seasar.kvasir.page.ability.timer.TimerAbility;
import org.seasar.kvasir.page.ability.timer.TimerAbilityAlfr;
import org.seasar.kvasir.page.ability.timer.dao.ScheduleDao;
import org.seasar.kvasir.page.ability.timer.dao.ScheduleDto;
import org.seasar.kvasir.util.PropertyUtils;


/**
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。
 * </p>
 *
 * @author YOKOTA Takehiko
 */
public class TimerAbilityAlfrImpl extends AbstractPageAbilityAlfr
    implements TimerAbilityAlfr
{
    private static final String[] VARIANTS = new String[] { Page.VARIANT_DEFAULT };

    @Binding(bindingType = BindingType.MUST)
    protected ScheduleDao scheduleDao_;


    /*
     * AbstractPageAbilityAlfr
     */

    @Override
    protected boolean doStart()
    {
        return true;
    }


    @Override
    protected void doStop()
    {
    }


    /*
     * TimerAbilityAlfr
     */

    @RequiredTx
    public void addSchedule(Page page, ScheduleMold mold)
    {
        final ScheduleDto dto = new ScheduleDto();

        dto.setPageId(page.getId());

        dto.setDayOfWeek(mold.getDayOfWeek() != null ? mold.getDayOfWeek()
            .toString() : CronField.EVERY);
        dto.setYear(mold.getYear() != null ? mold.getYear().toString()
            : CronField.EVERY);
        dto.setMonth(mold.getMonth() != null ? mold.getMonth().toString()
            : CronField.EVERY);
        dto.setDay(mold.getDay() != null ? mold.getDay().toString()
            : CronField.EVERY);
        dto.setHour(mold.getHour() != null ? mold.getHour().toString()
            : CronField.EVERY);
        dto.setMinute(mold.getMinute() != null ? mold.getMinute().toString()
            : CronField.EVERY);

        if (mold.getPluginId() == null) {
            throw new IllegalArgumentException("pluginId must be specified");
        }
        dto.setPluginId(mold.getPluginId());

        if (mold.getComponent() == null) {
            throw new IllegalArgumentException("component must be specified");
        }
        dto.setComponent(mold.getComponent());

        dto.setParameter(mold.getParameter());

        dto.setEnabled(mold.getEnabled() == null
            || mold.getEnabled().booleanValue() ? ScheduleDto.TRUE
            : ScheduleDto.FALSE);

        // 処理自体の排他制御の必要性はないと思われるが、addする際にPageオブジェクトが
        // 存在することを保証したいのでロックしている。
        page.runWithLocking(new Processable<Object>() {
            public Object process()
                throws ProcessableRuntimeException
            {
                scheduleDao_.insert(dto);
                return null;
            }
        });
    }


    @RequiredTx
    public void clearSchedules(Page page)
    {
        scheduleDao_.deleteByPageId(page.getId());
    }


    @RequiredTx
    public void clearSchedules()
    {
        scheduleDao_.delete();
    }


    @RequiredTx
    public Schedule getSchedule(Page page, int id)
    {
        return toSchedule(scheduleDao_.selectByPageIdAndId(page.getId(), id));
    }


    @RequiredTx
    public Schedule[] getSchedules(Page page)
    {
        return toSchedules(scheduleDao_.selectListByPageId(page.getId()));
    }


    Schedule toSchedule(ScheduleDto dto)
    {
        if (dto == null) {
            return null;
        }
        return new ScheduleImpl(dto);
    }


    Schedule[] toSchedules(ScheduleDto[] dtos)
    {
        Schedule[] schedules = new Schedule[dtos.length];
        for (int i = 0; i < schedules.length; i++) {
            schedules[i] = toSchedule(dtos[i]);
        }
        return schedules;
    }


    @RequiredTx
    public Schedule[] getEnabledSchedules()
    {
        return toSchedules(scheduleDao_.selectEnabledList());
    }


    public void enableSchedule(Page page, int id, boolean enabled)
    {
        scheduleDao_.updateEnabledByPageIdAndId(enabled ? ScheduleDto.TRUE
            : ScheduleDto.FALSE, page.getId(), id);
    }


    @RequiredTx
    public void removeSchedule(Page page, int id)
    {
        scheduleDao_.deleteByPageIdAndId(page.getId(), id);
    }


    /*
     * PageAbilityAlfr
     */

    public Iterator<String> attributeNames(Page page, String variant,
        AttributeFilter filter)
    {
        List<String> list = new ArrayList<String>();
        if (Page.VARIANT_DEFAULT.equals(variant)) {
            for (Schedule schedule : getSchedules(page)) {
                list.add(String.valueOf(schedule.getId()));
            }
        }
        return list.iterator();
    }


    public void clearAttributes(Page page)
    {
        clearSchedules(page);
    }


    public boolean containsAttribute(Page page, String name, String variant)
    {
        if (Page.VARIANT_DEFAULT.equals(variant)) {
            for (Schedule schedule : getSchedules(page)) {
                if (name.equals(String.valueOf(schedule.getId()))) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }


    public void create(Page page)
    {
    }


    public void delete(Page page)
    {
        clearSchedules(page);
    }


    public PageAbility getAbility(Page page)
    {
        return new TimerAbilityImpl(this, page);
    }


    public Class<? extends PageAbility> getAbilityInterfaceClass()
    {
        return TimerAbility.class;
    }


    public Attribute getAttribute(Page page, String name, String variant)
    {
        if (Page.VARIANT_DEFAULT.equals(variant)) {
            Attribute attr = null;
            for (Schedule schedule : getSchedules(page)) {
                if (name.equals(String.valueOf(schedule.getId()))) {
                    attr = new Attribute();
                    attr.setString(SUBNAME_DAYOFWEEK, schedule.getDayOfWeek()
                        .toString());
                    attr.setString(SUBNAME_YEAR, schedule.getYear().toString());
                    attr.setString(SUBNAME_MONTH, schedule.getMonth()
                        .toString());
                    attr.setString(SUBNAME_DAY, schedule.getDay().toString());
                    attr.setString(SUBNAME_HOUR, schedule.getHour().toString());
                    attr.setString(SUBNAME_MINUTE, schedule.getMinute()
                        .toString());
                    attr.setString(SUBNAME_PLUGINID, schedule.getPluginId());
                    attr.setString(SUBNAME_COMPONENT, schedule.getComponent());
                    if (schedule.getParameter() != null) {
                        attr.setString(SUBNAME_PARAMETER, schedule
                            .getParameter());
                    }
                    attr.setString(SUBNAME_ENABLED, String.valueOf(schedule
                        .isEnabled()));
                    break;
                }
            }
            return attr;
        } else {
            return null;
        }
    }


    public String getShortId()
    {
        return SHORTID;
    }


    public String[] getVariants(Page page)
    {
        return VARIANTS;
    }


    public void removeAttribute(Page page, String name, String variant)
    {
        int sequence;
        try {
            sequence = Integer.parseInt(name);
        } catch (NumberFormatException ex) {
            return;
        }

        removeSchedule(page, sequence);
    }


    public void setAttribute(Page page, String name, String variant,
        Attribute attr)
    {
        ScheduleMold mold = new ScheduleMold();

        mold.setDayOfWeek(CronFields.parse(attr.getString(SUBNAME_DAYOFWEEK)));
        mold.setYear(CronFields.parse(attr.getString(SUBNAME_YEAR)));
        mold.setMonth(CronFields.parse(attr.getString(SUBNAME_MONTH)));
        mold.setDay(CronFields.parse(attr.getString(SUBNAME_DAY)));
        mold.setHour(CronFields.parse(attr.getString(SUBNAME_HOUR)));
        mold.setMinute(CronFields.parse(attr.getString(SUBNAME_MINUTE)));

        String pluginId = attr.getString(SUBNAME_PLUGINID);
        if (pluginId == null) {
            return;
        }
        mold.setPluginId(pluginId);

        String component = attr.getString(SUBNAME_COMPONENT);
        if (component == null) {
            return;
        }
        mold.setComponent(component);

        mold.setParameter(attr.getString(SUBNAME_PARAMETER));

        mold.setEnabled(PropertyUtils.valueOf(attr.getString(SUBNAME_ENABLED),
            true));

        addSchedule(page, mold);
    }
}
