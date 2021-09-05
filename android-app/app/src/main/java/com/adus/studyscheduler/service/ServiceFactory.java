package com.adus.studyscheduler.service;

import android.app.Application;

import com.adus.contentscheduler.calendarmanagement.CalendarManagementService;
import com.adus.contentscheduler.contentpersonalization.ContentPersonalizationService;
import com.adus.contentscheduler.scheduling.SchedulingService;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.adus.studyscheduler.crud.repository.RepositoryFactory.calendarRepository;
import static com.adus.studyscheduler.crud.repository.RepositoryFactory.contentRepository;
import static com.adus.studyscheduler.crud.repository.RepositoryFactory.staticContentRepository;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceFactory {

    public static SchedulingService schedulingService(Application application) {
        return new SchedulingService(contentRepository(application), calendarRepository(application));
    }

    public static CalendarManagementService calendarManagementService(Application application) {
        return new CalendarManagementService(calendarRepository(application));
    }

    public static ContentPersonalizationService contentPersonalizationService(Application application) {
        return new ContentPersonalizationService(contentRepository(application), staticContentRepository(application));
    }

}
