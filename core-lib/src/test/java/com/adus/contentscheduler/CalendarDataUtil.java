package com.adus.contentscheduler;

import com.adus.contentscheduler.dao.entity.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class CalendarDataUtil {
    public static final java.util.Date JULY_13 = toDate(LocalDate.of(2020, 7, 13));
    public static final Date JULY_14 = toDate(LocalDate.of(2020, 7, 14));


    public static Calendar create(LocalDate from, LocalDate to) {
        Calendar calendar = new Calendar();

        List<DaySchedule> daySchedules = LongStream.range(from.toEpochDay(), to.plusDays(1).toEpochDay())
                .mapToObj(LocalDate::ofEpochDay)
                .map(date -> {
                    CalendarDate calendarDate = new CalendarDate();
                    calendarDate.setDate(toDate(date));
                    DaySchedule daySchedule = new DaySchedule();
                    daySchedule.setCalendarDate(calendarDate);
                    daySchedule.setScheduledContents(new ArrayList<>());
                    return daySchedule;
                })
                .collect(Collectors.toList());
        calendar.setDaySchedules(daySchedules);
        return calendar;
    }

    public static Calendar blankCalendar() {
        Calendar calendar = new Calendar();
        calendar.setDaySchedules(new ArrayList<>());
        return calendar;
    }

    private static java.util.Date toDate(LocalDate localDate) {
        return java.sql.Date.valueOf(localDate);
    }

    public static void addScheduledContent(Calendar calendar, Date date, String contentId, float duration) {
        calendar.getDaySchedules()
                .stream()
                .filter(daySchedule -> daySchedule.getCalendarDate().getDate().equals(date))
                .forEach(daySchedule -> {
                    ScheduledContent scheduledContent = new ScheduledContent();
                    scheduledContent.setContent(createContent(contentId));
                    scheduledContent.setDuration(duration);
                    daySchedule.getScheduledContents().add(scheduledContent);
                });
    }

    private static Content createContent(String contentId) {
        ContentAttributes contentAttributes = new ContentAttributes();
        contentAttributes.setId(contentId);
        return new Content(contentAttributes);
    }

    public static DaySchedule findDaySchedule(Calendar calendar, LocalDate date) {
        return calendar.getDaySchedules().stream()
                .filter(daySchedule -> daySchedule.getCalendarDate().getDate().equals(toDate(date)))
                .findAny().orElse(null);
    }
}
