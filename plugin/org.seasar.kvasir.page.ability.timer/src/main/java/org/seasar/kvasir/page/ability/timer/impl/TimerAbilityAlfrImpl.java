package org.seasar.kvasir.page.ability.timer.impl;

import java.util.ArrayList;
import java.util.Date;
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
import org.seasar.kvasir.page.ability.timer.Schedule;
import org.seasar.kvasir.page.ability.timer.ScheduleMold;
import org.seasar.kvasir.page.ability.timer.ScheduleStatus;
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

        if (mold.getStatus() != null) {
            dto.setStatusEnum(mold.getStatus());
        }

        if (mold.getScheduledDate() == null) {
            throw new IllegalArgumentException(
                "scheduledDate must be specified");
        }
        dto.setScheduledDate(mold.getScheduledDate());

        if (mold.getComponent() == null) {
            throw new IllegalArgumentException("component must be specified");
        }
        dto.setComponent(mold.getComponent());

        if (mold.getBeginDate() != null) {
            dto.setBeginDate(mold.getBeginDate());
        }

        if (mold.getFinishDate() != null) {
            dto.setFinishDate(mold.getFinishDate());
        }

        if (mold.getErrorInformation() != null) {
            dto.setErrorInformation(mold.getErrorInformation());
        }

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
    public Schedule[] getSchedules(Page page, ScheduleStatus status)
    {
        return toSchedules(scheduleDao_.selectListByPageIdAndStatus(page
            .getId(), status.getId()));
    }


    @RequiredTx
    public Schedule[] getSchedulesAndChangeStatus(ScheduleStatus fromStatus,
        ScheduleStatus toStatus)
    {
        ScheduleDto[] dtos = scheduleDao_
            .selectListForUpdateByStatus(fromStatus.getId());
        for (ScheduleDto dto : dtos) {
            scheduleDao_.updateStatusByPageIdAndId(toStatus.getId(), dto
                .getPageId(), dto.getId());
        }
        return toSchedules(dtos);
    }


    @RequiredTx
    public void removeSchedule(Page page, int id)
    {
        scheduleDao_.deleteByPageIdAndId(page.getId(), id);
    }


    @RequiredTx
    public void removeSchedules(Page page, ScheduleStatus status)
    {
        scheduleDao_.deleteByPageIdAndStatus(page.getId(), status.getId());
    }


    @RequiredTx
    public void removeSchedules(ScheduleStatus status)
    {
        scheduleDao_.deleteByStatus(status.getId());
    }


    @RequiredTx
    public boolean cancelSchedule(Page page, int id)
    {
        return scheduleDao_.updateByPageIdAndIdToCancel(page.getId(), id) > 0;
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
                    attr.setString(SUBNAME_ERRORINFORMATION, schedule
                        .getErrorInformation());
                    attr.setString(SUBNAME_SCHEDULEDDATE, String
                        .valueOf(schedule.getScheduledDate().getTime()));
                    attr.setString(SUBNAME_COMPONENT, schedule.getComponent());
                    attr.setString(SUBNAME_BEGINDATE,
                        schedule.getBeginDate() != null ? String
                            .valueOf(schedule.getBeginDate().getTime()) : null);
                    attr
                        .setString(SUBNAME_FINISHDATE,
                            schedule.getFinishDate() != null ? String
                                .valueOf(schedule.getFinishDate().getTime())
                                : null);
                    attr.setString(SUBNAME_STATUS, String.valueOf(schedule
                        .getStatus().getId()));
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

        long scheduledDateLong = PropertyUtils.valueOf(attr
            .getString(SUBNAME_SCHEDULEDDATE), 0L);
        if (scheduledDateLong == 0L) {
            return;
        }
        mold.setScheduledDate(new Date(scheduledDateLong));

        ScheduleStatus status = ScheduleStatus.enumOf(PropertyUtils.valueOf(
            attr.getString(SUBNAME_STATUS), -1));
        if (status == null) {
            return;
        }
        mold.setStatus(status);

        String component = attr.getString(SUBNAME_COMPONENT);
        if (component == null) {
            return;
        }
        mold.setComponent(component);

        long beginDateLong = PropertyUtils.valueOf(attr
            .getString(SUBNAME_BEGINDATE), 0L);
        if (beginDateLong != 0L) {
            mold.setBeginDate(new Date(beginDateLong));
        }

        long finishDateLong = PropertyUtils.valueOf(attr
            .getString(SUBNAME_FINISHDATE), 0L);
        if (finishDateLong != 0L) {
            mold.setFinishDate(new Date(finishDateLong));
        }

        String errorInformation = attr.getString(SUBNAME_ERRORINFORMATION);
        if (errorInformation != null) {
            mold.setErrorInformation(errorInformation);
        }

        addSchedule(page, mold);
    }
}
