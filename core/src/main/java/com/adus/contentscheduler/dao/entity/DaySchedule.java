package com.adus.contentscheduler.dao.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DaySchedule extends BaseEntity {
    private CalendarDate calendarDate;
    private List<ScheduledContent> scheduledContents;
}
