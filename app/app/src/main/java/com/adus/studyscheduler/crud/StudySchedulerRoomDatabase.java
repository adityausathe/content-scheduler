package com.adus.studyscheduler.crud;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.adus.studyscheduler.crud.dao.CalendarDao;
import com.adus.studyscheduler.crud.dao.ContentDao;
import com.adus.studyscheduler.crud.dao.ScheduledContentDao;
import com.adus.studyscheduler.crud.dao.StaticContentDao;
import com.adus.studyscheduler.crud.entity.CalendarDayEntity;
import com.adus.studyscheduler.crud.entity.ContentEntity;
import com.adus.studyscheduler.crud.entity.ScheduledContentEntity;
import com.adus.studyscheduler.crud.entity.StaticContentEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TypeConverters(value = {FieldTypeConverters.class})
@Database(entities = {StaticContentEntity.class, ContentEntity.class, ScheduledContentEntity.class, CalendarDayEntity.class},
        version = 1, exportSchema = false)
public abstract class StudySchedulerRoomDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile StudySchedulerRoomDatabase INSTANCE;
    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
//            seedData();
        }


    };

    public static StudySchedulerRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StudySchedulerRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            StudySchedulerRoomDatabase.class, "study_scheduler_db")
//                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract StaticContentDao staticContentDao();

    public abstract ContentDao contentDao();

    public abstract ScheduledContentDao scheduledContentDao();

    public abstract CalendarDao calendarDao();
}
