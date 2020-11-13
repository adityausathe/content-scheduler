package com.adus.contentscheduler.calendarmanagement;

import com.adus.contentscheduler.commons.entity.CalendarDate;
import com.adus.contentscheduler.commons.entity.DaySchedule;
import com.adus.contentscheduler.commons.entity.ScheduledContent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarDayView implements Comparable<CalendarDayView> {
    private final DaySchedule daySchedule;
    private final List<ScheduledContentView> scheduledContentViews;
    private Float availableTime;
    private boolean isHoliday;
    private boolean isDirty;

    public CalendarDayView(Date date) {
        this.daySchedule = createDaySchedule(date);
        this.isHoliday = false;
        this.scheduledContentViews = new ArrayList<>();
        markDirty();
    }

    private CalendarDayView(DaySchedule daySchedule, List<ScheduledContentView> scheduledContentViews) {
        this.daySchedule = daySchedule;
        this.scheduledContentViews = scheduledContentViews;
        this.isHoliday = daySchedule.getCalendarDate().isHoliday();
        this.availableTime = daySchedule.getCalendarDate().getAvailableTime();
    }

    public static CalendarDayView initializeFrom(DaySchedule daySchedule) {
        List<ScheduledContentView> scheduledContents = daySchedule.getScheduledContents().stream()
                .map(ScheduledContentView::new)
                .collect(Collectors.toList());
        return new CalendarDayView(daySchedule, scheduledContents);
    }

    private DaySchedule createDaySchedule(Date date) {
        DaySchedule daySchedule = new DaySchedule();
        CalendarDate calendarDate = new CalendarDate();
        calendarDate.setDate(date);
        daySchedule.setCalendarDate(calendarDate);
        return daySchedule;
    }

    public void markHoliday() {
        isHoliday = true;
        markDirty();
    }

    public void unMarkHoliday() {
        isHoliday = false;
        markDirty();
    }

    public Float getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(Float availableTime) {
        this.availableTime = availableTime;
        markDirty();
    }

    public boolean isHoliday() {
        return isHoliday;
    }

    private void markDirty() {
        isDirty = true;
    }

    boolean isDirty() {
        return isDirty || isScheduledContentViewsChanged();
    }

    DaySchedule exportDaySchedule() {
        if (this.isDirty) {
            daySchedule.getCalendarDate().setAvailableTime(availableTime);
            daySchedule.getCalendarDate().setHoliday(isHoliday);
        }
        if (isScheduledContentViewsChanged()) {
            List<ScheduledContent> scheduledContents = scheduledContentViews.stream()
                    .map(ScheduledContentView::exportScheduledContent)
                    .collect(Collectors.toList());
            daySchedule.setScheduledContents(scheduledContents);
        }
        return daySchedule;
    }

    private boolean isScheduledContentViewsChanged() {
        return scheduledContentViews.stream().anyMatch(ScheduledContentView::isDirty);
    }

    public List<ScheduledContentView> getScheduledContentViews() {
        return scheduledContentViews;
    }

    @Override
    public int compareTo(CalendarDayView that) {
        return this.daySchedule.getCalendarDate().getDate().compareTo(that.daySchedule.getCalendarDate().getDate());
    }
}
