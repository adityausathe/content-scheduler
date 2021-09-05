package com.adus.studyscheduler.crud.repository;

import android.app.Application;

import com.adus.contentscheduler.dao.repository.spi.CalendarRepository;
import com.adus.contentscheduler.dao.repository.spi.ContentRepository;
import com.adus.contentscheduler.dao.repository.spi.StaticContentRepository;
import com.adus.studyscheduler.crud.StudySchedulerRoomDatabase;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RepositoryFactory {

    public static StaticContentRepository staticContentRepository(Application application) {
        return new StaticContentRepositoryImpl(StudySchedulerRoomDatabase.getDatabase(application));
    }

    public static ContentRepository contentRepository(Application application) {
        return new ContentRepositoryImpl(StudySchedulerRoomDatabase.getDatabase(application));
    }

    public static CalendarRepository calendarRepository(Application application) {
        return new CalendarRepositoryImpl(StudySchedulerRoomDatabase.getDatabase(application));
    }
}
