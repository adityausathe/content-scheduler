package com.adus.studyscheduler.crud.repository;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.adus.contentscheduler.dao.ContentType;
import com.adus.contentscheduler.dao.Rating;
import com.adus.studyscheduler.crud.StudySchedulerRoomDatabase;
import com.adus.studyscheduler.crud.entity.CalendarDayEntity;
import com.adus.studyscheduler.crud.entity.ContentEntity;
import com.adus.studyscheduler.crud.entity.ScheduledContentEntity;
import com.adus.studyscheduler.crud.entity.StaticContentEntity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
class DataIntegrationTestHelper {

    static final Date DT_2021_02_12 = toDate("2021-02-12");
    static final Date DT_2021_02_13 = toDate("2021-02-13");
    static final Date DT_2021_02_15 = toDate("2021-02-15");
    static final String PROGRAMME_BS = "BS";
    static final String PROGRAMME_MS = "MS";
    static final String SUBJECT_C_103 = "C103";
    static final String SUBJECT_C_102 = "C102";
    static final String SUBJECT_C_101 = "C101";

    static TestSetupContext setupContextForCalendarRepo() {
        StudySchedulerRoomDatabase database = getDatabase();

        List<ContentEntity> contentEntities = setupContentEntities(database);

        List<CalendarDayEntity> calendarDayEntities = Arrays.asList(
                calendarDay(DataIntegrationTestHelper.DT_2021_02_12, 6f, false),
                calendarDay(DataIntegrationTestHelper.DT_2021_02_13, 7f, false),
                calendarDay(DataIntegrationTestHelper.DT_2021_02_15, 0f, true)

        );
        database.calendarDao().deleteAll();
        database.calendarDao().save(calendarDayEntities);

        List<ScheduledContentEntity> scheduledContentEntities = Arrays.asList(
                scheduledContent(SUBJECT_C_101, DataIntegrationTestHelper.DT_2021_02_12, 2.5f),
                scheduledContent(SUBJECT_C_102, DataIntegrationTestHelper.DT_2021_02_12, 3.5f),
                scheduledContent(SUBJECT_C_103, DataIntegrationTestHelper.DT_2021_02_13, 7f),
                scheduledContent(SUBJECT_C_101, DataIntegrationTestHelper.DT_2021_02_15, 0f)
        );
        database.scheduledContentDao().deleteAll();
        database.scheduledContentDao().save(scheduledContentEntities);

        return TestSetupContext.builder()
                .database(database)
                .contentEntities(contentEntities)
                .calendarDayEntities(calendarDayEntities)
                .scheduledContentEntities(scheduledContentEntities)
                .build();
    }

    static TestSetupContext setupContextForContentRepo() {
        StudySchedulerRoomDatabase database = getDatabase();
        List<ContentEntity> contentEntities = setupContentEntities(database);

        return TestSetupContext.builder()
                .database(database)
                .contentEntities(contentEntities)
                .build();
    }

    static TestSetupContext setupContextForStaticContentRepo() {
        StudySchedulerRoomDatabase database = getDatabase();
        List<StaticContentEntity> staticContentEntities = setupStaticContentEntities(database);

        return TestSetupContext.builder()
                .database(database)
                .staticContentEntities(staticContentEntities)
                .build();
    }

    private static List<StaticContentEntity> setupStaticContentEntities(StudySchedulerRoomDatabase database) {
        List<StaticContentEntity> staticContentEntities = Arrays.asList(
                staticContent(PROGRAMME_MS, ContentType.PROGRAMME),
                staticContent(PROGRAMME_BS, ContentType.PROGRAMME),
                staticContent(SUBJECT_C_103, ContentType.SUBJECT),
                staticContent(SUBJECT_C_102, ContentType.SUBJECT),
                staticContent(SUBJECT_C_101, ContentType.SUBJECT)
        );
        database.staticContentDao().deleteAll();
        database.staticContentDao().save(staticContentEntities);
        return staticContentEntities;
    }

    private static List<ContentEntity> setupContentEntities(StudySchedulerRoomDatabase database) {
        List<ContentEntity> contentEntities = Arrays.asList(
                content(PROGRAMME_BS, ContentType.PROGRAMME),
                content(SUBJECT_C_101, ContentType.SUBJECT),
                content(SUBJECT_C_102, ContentType.SUBJECT),
                content(SUBJECT_C_103, ContentType.SUBJECT)
        );
        database.contentDao().deleteAll();
        database.contentDao().save(contentEntities);
        return contentEntities;
    }

    private static StudySchedulerRoomDatabase getDatabase() {
        return Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(), StudySchedulerRoomDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    private static StaticContentEntity staticContent(String contentId, ContentType contentType) {
        StaticContentEntity entity = new StaticContentEntity();
        entity.setId(contentId);
        entity.setName(contentId);
        entity.setContentType(contentType.name());
        entity.setParent(contentType == ContentType.PROGRAMME ? null : PROGRAMME_BS);
        entity.setRating(Rating.DEFAULT.name());
        return entity;
    }

    private static ContentEntity content(String contentId, ContentType contentType) {
        ContentEntity entity = new ContentEntity();
        entity.setId(contentId);
        entity.setName(contentId);
        entity.setContentType(contentType.name());
        entity.setParent(contentType == ContentType.PROGRAMME ? null : PROGRAMME_BS);
        entity.setRating(Rating.DEFAULT.name());
        entity.setTimeRestriction(contentType == ContentType.PROGRAMME ? 500f : null);
        return entity;
    }

    @SneakyThrows
    private static CalendarDayEntity calendarDay(Date date, float availableTime, boolean isHoliday) {
        CalendarDayEntity entity = new CalendarDayEntity();
        entity.setDate(date);
        entity.setAvailableTime(availableTime);
        entity.setIsHoliday(isHoliday);
        return entity;
    }

    @SneakyThrows
    private static ScheduledContentEntity scheduledContent(String contentId, Date date, float duration) {
        ScheduledContentEntity entity = new ScheduledContentEntity();
        entity.setContentId(contentId);
        entity.setDate(date);
        entity.setDuration(duration);
        return entity;
    }

    @SneakyThrows
    private static Date toDate(String dateStr) {
        return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
    }

    @Builder
    @Data
    static class TestSetupContext {
        private StudySchedulerRoomDatabase database;
        private List<StaticContentEntity> staticContentEntities;
        private List<ContentEntity> contentEntities;
        private List<CalendarDayEntity> calendarDayEntities;
        private List<ScheduledContentEntity> scheduledContentEntities;
    }
}
