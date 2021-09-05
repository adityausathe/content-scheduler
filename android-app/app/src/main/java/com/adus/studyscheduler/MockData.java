package com.adus.studyscheduler;

import android.content.Context;

import com.adus.studyscheduler.crud.StudySchedulerRoomDatabase;
import com.adus.studyscheduler.crud.dao.CalendarDao;
import com.adus.studyscheduler.crud.dao.ContentDao;
import com.adus.studyscheduler.crud.entity.CalendarDayEntity;
import com.adus.studyscheduler.crud.entity.ContentEntity;

import java.util.Arrays;
import java.util.Date;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MockData {

    // todo: apply Room's db-migrations instead
    public static void setupSeedDataInDatabase(Context applicationContext) {
        StudySchedulerRoomDatabase.databaseWriteExecutor.execute(() -> {
            ContentDao contentDao = StudySchedulerRoomDatabase.getDatabase(applicationContext).contentDao();

//                createAndPersistStaticContentEntity(contentDao, "Class X", null, "PROGRAMME");
            createAndPersistStaticContentEntity(contentDao, "Class XI", null, "PROGRAMME");
            createAndPersistStaticContentEntity(contentDao, "PHYSICS", "Class XI", "SUBJECT");
            createAndPersistStaticContentEntity(contentDao, "CHEMISTRY", "Class XI", "SUBJECT");
            createAndPersistStaticContentEntity(contentDao, "Statics", "PHYSICS", "OTHER_MORE_GRANULAR");
            createAndPersistStaticContentEntity(contentDao, "Kinematics", "PHYSICS", "OTHER_MORE_GRANULAR");

            CalendarDao calendarDao = StudySchedulerRoomDatabase.getDatabase(applicationContext).calendarDao();

            CalendarDayEntity calendarDayEntity = new CalendarDayEntity();
            calendarDayEntity.setDate(new Date());
            calendarDayEntity.setAvailableTime(2.0f);
            calendarDayEntity.setIsHoliday(false);
            calendarDao.save(Arrays.asList(calendarDayEntity));
        });
    }

    private static void createAndPersistStaticContentEntity(ContentDao contentDao, String id, String parent, String contentType) {
        ContentEntity staticContentEntity = new ContentEntity();
        staticContentEntity.setId(id);
        staticContentEntity.setName(id);
        staticContentEntity.setContentType(contentType);
        staticContentEntity.setRating("DEFAULT");
        staticContentEntity.setParent(parent);
        contentDao.save(Arrays.asList(staticContentEntity));
    }
}
