package com.adus.studyscheduler.crud;

import androidx.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lombok.SneakyThrows;

public class FieldTypeConverters {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    @TypeConverter
    @SneakyThrows
    public Date stringToDate(String dateStr) {
        return simpleDateFormat.parse(dateStr);
    }

    @TypeConverter
    public String stringToDate(Date date) {
        return simpleDateFormat.format(date);
    }
}
