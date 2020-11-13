package com.adus.contentscheduler.commons.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CalendarDate {
    // PK
    private Date date;
    private Float availableTime;
    private boolean isHoliday;
}
