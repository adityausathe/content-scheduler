package com.adus.contentscheduler.commons.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Calendar {
    List<DaySchedule> daySchedules;
}
