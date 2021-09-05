package com.adus.studyscheduler.crud.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@Entity(tableName = "calendar_day")
public class CalendarDayEntity {
    @EqualsAndHashCode.Include
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "date")
    private Date date;
    @ColumnInfo(name = "available_time")
    private Float availableTime;
    @ColumnInfo(name = "is_holiday")
    private Boolean isHoliday;

}
