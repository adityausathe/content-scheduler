package com.adus.contentscheduler.scheduling;

import com.adus.contentscheduler.commons.entity.Calendar;
import com.adus.contentscheduler.commons.entity.DaySchedule;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

class SchedulableCalendar {
    private List<SchedulableDay> schedulableDays;
    private float availableTime;

    private SchedulableCalendar(List<SchedulableDay> schedulableDays, float availableTime) {
        this.schedulableDays = schedulableDays;
        this.availableTime = availableTime;
    }

    static SchedulableCalendar initializeFrom(Calendar calendar) {
        List<SchedulableDay> schedulableDays = calendar.getDaySchedules().stream()
                .map(DaySchedule::getCalendarDate)
                .filter(calendarDate -> !calendarDate.isHoliday())
                .map(SchedulableDay::new)
                .collect(Collectors.toList());
        float availableTime = schedulableDays.stream()
                .map(SchedulableDay::getRemainingTime)
                .reduce((f1, f2) -> f1 += f2).orElse(0f);
        return new SchedulableCalendar(schedulableDays, availableTime);
    }

    float getAvailableTime() {
        return availableTime;
    }

    Iterator<SchedulableDay> iterator() {
        return schedulableDays.iterator();
    }

    Calendar exportCalendar() {
        List<DaySchedule> daySchedules = schedulableDays.stream()
                .map(SchedulableDay::exportDaySchedule)
                .collect(Collectors.toList());
        Calendar calendar = new Calendar();
        calendar.setDaySchedules(daySchedules);
        return calendar;
    }
}
