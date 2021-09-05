package com.adus.studyscheduler.crud.entity;

import androidx.room.Embedded;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class ScheduledContentWithCalendarDay {
    @Embedded
    private CalendarDayEntity calendarDayEntity;
    @Embedded
    private ContentEntity contentEntity;

    private Float duration;
}
