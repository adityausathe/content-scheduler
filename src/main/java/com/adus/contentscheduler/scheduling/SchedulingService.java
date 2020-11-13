package com.adus.contentscheduler.scheduling;

import com.adus.contentscheduler.commons.entity.Calendar;
import com.adus.contentscheduler.commons.entity.Content;
import com.adus.contentscheduler.commons.repository.CalendarRepository;
import com.adus.contentscheduler.commons.repository.ContentRepository;

public class SchedulingService {

    private final ContentRepository contentRepository;
    private final CalendarRepository calendarRepository;

    public SchedulingService(ContentRepository contentRepository, CalendarRepository calendarRepository) {
        this.contentRepository = contentRepository;
        this.calendarRepository = calendarRepository;
    }

    public void generateSchedule() {
        Content programme = contentRepository.findProgramme();
        SchedulableContent schedulableContent = SchedulableContent.initializeFrom(programme);
        SchedulableCalendar schedulableCalendar = SchedulableCalendar.initializeFrom(calendarRepository.getCalendar());

        ScheduleCreator scheduleCreator = new ScheduleCreator(schedulableContent, schedulableCalendar);
        scheduleCreator.create();

        Calendar calendar = schedulableCalendar.exportCalendar();
        calendarRepository.saveCalendar(calendar);
    }

}
