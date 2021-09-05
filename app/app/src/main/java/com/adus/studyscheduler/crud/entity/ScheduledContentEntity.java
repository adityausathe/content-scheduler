package com.adus.studyscheduler.crud.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity(tableName = "scheduled_content", primaryKeys = {"date", "content_id"})
public class ScheduledContentEntity {

    @NonNull
    @ColumnInfo(name = "date")
    private Date date;
    @NonNull
    @ColumnInfo(name = "content_id")
    private String contentId;
    @NonNull
    @ColumnInfo(name = "duration")
    private Float duration;

}

