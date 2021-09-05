package com.adus.studyscheduler.crud.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity(tableName = "content")
public class ContentEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "content_type")
    private String contentType;
    @ColumnInfo(name = "rating")
    private String rating;
    @ColumnInfo(name = "time_restriction")
    private Float timeRestriction;
    @ColumnInfo(name = "study_time")
    private Float studyTime;

    @ColumnInfo(name = "parent_id")
    private String parent;

}

