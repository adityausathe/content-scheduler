package com.adus.contentscheduler.calendarmanagement;

import com.adus.contentscheduler.dao.entity.Calendar;
import com.adus.contentscheduler.dao.entity.DaySchedule;

import java.util.*;
import java.util.stream.Collectors;

public class CalendarView {
    private final Calendar calendar;
    private final SortedMap<Date, CalendarDayView> calendarDayViews;

    private CalendarView(Calendar calendar, SortedMap<Date, CalendarDayView> calendarDayViews) {
        this.calendar = calendar;
        this.calendarDayViews = calendarDayViews;
    }

    public static CalendarView initializeFrom(Calendar calendar) {
        SortedMap<Date, CalendarDayView> calendarDayViews = new TreeMap<>();
        calendar.getDaySchedules()
                .forEach(daySchedule -> {
                    CalendarDayView dayView = CalendarDayView.initializeFrom(daySchedule);
                    calendarDayViews.put(daySchedule.getCalendarDate().getDate(), dayView);
                });
        return new CalendarView(calendar, calendarDayViews);
    }

    public void addDay(Date date) {
        this.calendarDayViews.put(date, new CalendarDayView(date));
    }

    Collection<CalendarDayView> getCalendarDayViews() {
        return calendarDayViews.values();
    }

    Calendar exportCalendar() {
        if (isDirty()) {
            List<DaySchedule> daySchedules = calendarDayViews.values()
                    .stream().map(CalendarDayView::exportDaySchedule)
                    .collect(Collectors.toList());
            calendar.setDaySchedules(daySchedules);
        }
        return calendar;
    }

    boolean isDirty() {
        return calendarDayViews.values().stream().anyMatch(CalendarDayView::isDirty);
    }

    public CalendarDayView getDay(Date date) {
        return calendarDayViews.get(date);
    }
}
