package com.adus.studyscheduler.crud.repository;

import com.adus.contentscheduler.dao.entity.Calendar;
import com.adus.contentscheduler.dao.entity.DaySchedule;
import com.adus.contentscheduler.dao.repository.spi.CalendarRepository;
import com.adus.studyscheduler.crud.EntityBatch;
import com.adus.studyscheduler.crud.StudySchedulerRoomDatabase;
import com.adus.studyscheduler.crud.dao.CalendarDao;
import com.adus.studyscheduler.crud.entity.CalendarDayEntity;
import com.adus.studyscheduler.crud.entity.ScheduledContentEntity;
import com.adus.studyscheduler.crud.entity.ScheduledContentWithCalendarDay;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.adus.studyscheduler.crud.EntityMapper.calendarDateToCalendarDayEntity;
import static com.adus.studyscheduler.crud.EntityMapper.convertToDaySchedule;
import static com.adus.studyscheduler.crud.EntityMapper.convertToDaySchedules;
import static com.adus.studyscheduler.crud.EntityMapper.convertToScheduledContentEntity;

public class CalendarRepositoryImpl implements CalendarRepository {
    private final CalendarDao calendarDao;

    CalendarRepositoryImpl(StudySchedulerRoomDatabase database) {
        calendarDao = database.calendarDao();
    }

    @Override
    public Calendar getCalendar() {
        List<CalendarDayEntity> allCalendarDays = calendarDao.findAllCalendarDays();

        List<DaySchedule> daySchedules = allCalendarDays.stream()
                // todo: hydrate mapped scheduled content data
                .map(entity -> convertToDaySchedules(entity, Collections.emptyList()))
                .collect(Collectors.toList());

        Calendar calendar = new Calendar();
        calendar.setDaySchedules(daySchedules);
        return calendar;
    }


    @Override
    public Calendar getCalendarSlice(Date from, Date to) {
        List<ScheduledContentWithCalendarDay> scheduledContentWithCalendarDays =
                calendarDao.findScheduledContentFilledCalendarDays(from, to);

        Map<CalendarDayEntity, List<ScheduledContentWithCalendarDay>> dayWiseContentEntities =
                scheduledContentWithCalendarDays
                        .stream()
                        .collect(Collectors.groupingBy(ScheduledContentWithCalendarDay::getCalendarDayEntity,
                                () -> new TreeMap<>(Comparator.comparing(CalendarDayEntity::getDate)), Collectors.toList()));

        List<DaySchedule> daySchedules = dayWiseContentEntities.entrySet()
                .stream()
                .map(dayEntitiesEntry -> convertToDaySchedule(dayEntitiesEntry.getKey(), dayEntitiesEntry.getValue()))
                .collect(Collectors.toList());

        Calendar calendar = new Calendar();
        calendar.setDaySchedules(daySchedules);
        return calendar;
    }


    @Override
    public void saveCalendar(Calendar calendar) {
        EntityBatch<CalendarDayEntity> calendarDayEntityBatch = EntityBatch.create();
        EntityBatch<ScheduledContentEntity> scheduledContentEntityBatch = EntityBatch.create();

        calendar.getDaySchedules()
                .forEach(daySchedule -> {
                    CalendarDayEntity calendarDayEntity = calendarDateToCalendarDayEntity(daySchedule.getCalendarDate());
                    List<ScheduledContentEntity> scheduledContentEntities = daySchedule.getScheduledContents()
                            .stream()
                            .map(scheduledContent -> convertToScheduledContentEntity(calendarDayEntity, scheduledContent))
                            .collect(Collectors.toList());
                    if (daySchedule.isDeleted()) {
                        calendarDayEntityBatch.getEntitiesForDelete().add(calendarDayEntity);
                        scheduledContentEntityBatch.getEntitiesForDelete().addAll(scheduledContentEntities);
                    } else {
                        calendarDayEntityBatch.getEntitiesForSave().add(calendarDayEntity);
                        scheduledContentEntityBatch.getEntitiesForSave().addAll(scheduledContentEntities);
                    }
                });

        calendarDao.saveAndDelete(calendarDayEntityBatch, scheduledContentEntityBatch);
    }


}
