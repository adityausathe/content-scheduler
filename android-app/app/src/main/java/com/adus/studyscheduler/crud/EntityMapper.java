package com.adus.studyscheduler.crud;

import com.adus.contentscheduler.dao.ContentType;
import com.adus.contentscheduler.dao.Rating;
import com.adus.contentscheduler.dao.entity.CalendarDate;
import com.adus.contentscheduler.dao.entity.Content;
import com.adus.contentscheduler.dao.entity.ContentAttributes;
import com.adus.contentscheduler.dao.entity.DaySchedule;
import com.adus.contentscheduler.dao.entity.ScheduledContent;
import com.adus.contentscheduler.dao.entity.StaticContent;
import com.adus.studyscheduler.crud.entity.CalendarDayEntity;
import com.adus.studyscheduler.crud.entity.ContentEntity;
import com.adus.studyscheduler.crud.entity.ScheduledContentEntity;
import com.adus.studyscheduler.crud.entity.ScheduledContentWithCalendarDay;
import com.adus.studyscheduler.crud.entity.StaticContentEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityMapper {
    private EntityMapper() {
        // utility class
    }

    public static StaticContent staticContentEntityToStaticContent(StaticContentEntity entity) {
        ContentAttributes attributes = new ContentAttributes();
        attributes.setId(entity.getId());
        attributes.setName(entity.getName());
        attributes.setContentType(ContentType.valueOf(entity.getContentType()));
        attributes.setRating(Rating.valueOf(entity.getRating()));
        StaticContent staticContent = new StaticContent(attributes);
        staticContent.setSubContents(new ArrayList<>());
        return staticContent;
    }

    public static Content contentEntityToContent(ContentEntity contentEntity) {
        ContentAttributes attributes = new ContentAttributes();
        attributes.setId(contentEntity.getId());
        attributes.setName(contentEntity.getName());
        attributes.setContentType(ContentType.valueOf(contentEntity.getContentType()));
        attributes.setRating(Rating.valueOf(contentEntity.getRating()));
        Content content = new Content(attributes);
        content.setTimeRestriction(contentEntity.getTimeRestriction());
        content.setStudyTime(contentEntity.getStudyTime());
        content.setSubContents(new ArrayList<>());
        return content;
    }

    public static ContentEntity contentToContentEntity(Content content, String parentId) {
        ContentEntity contentEntity = new ContentEntity();
        ContentAttributes contentAttributes = content.getContentAttributes();
        contentEntity.setId(contentAttributes.getId());
        contentEntity.setName(contentAttributes.getName());
        contentEntity.setContentType(contentAttributes.getContentType().name());
        contentEntity.setRating(contentAttributes.getRating().name());
        contentEntity.setTimeRestriction(content.getTimeRestriction());
        contentEntity.setStudyTime(content.getStudyTime());
        contentEntity.setParent(parentId);
        return contentEntity;
    }

    private static CalendarDate calendarDayToCalendarDate(CalendarDayEntity entity) {
        CalendarDate calendarDate = new CalendarDate();
        calendarDate.setDate(entity.getDate());
        calendarDate.setAvailableTime(entity.getAvailableTime());
        calendarDate.setHoliday(entity.getIsHoliday());
        return calendarDate;
    }

    public static ScheduledContentEntity convertToScheduledContentEntity(CalendarDayEntity calendarDayEntity, ScheduledContent scheduledContent) {
        ScheduledContentEntity scheduledContentEntity = new ScheduledContentEntity();
        scheduledContentEntity.setDate(calendarDayEntity.getDate());
        scheduledContentEntity.setContentId(scheduledContent.getContent().getContentAttributes().getId());
        scheduledContentEntity.setDuration(scheduledContent.getDuration());
        return scheduledContentEntity;
    }

    public static CalendarDayEntity calendarDateToCalendarDayEntity(CalendarDate calendarDate) {
        CalendarDayEntity calendarDayEntity = new CalendarDayEntity();
        calendarDayEntity.setDate(calendarDate.getDate());
        calendarDayEntity.setAvailableTime(calendarDate.getAvailableTime());
        calendarDayEntity.setIsHoliday(calendarDate.isHoliday());
        return calendarDayEntity;
    }

    public static DaySchedule convertToDaySchedules(CalendarDayEntity entity, List<ScheduledContent> scheduledContents) {
        CalendarDate calendarDate = calendarDayToCalendarDate(entity);

        DaySchedule daySchedule = new DaySchedule();
        daySchedule.setCalendarDate(calendarDate);
        daySchedule.setScheduledContents(scheduledContents);
        return daySchedule;
    }


    public static DaySchedule convertToDaySchedule(CalendarDayEntity calendarDay, List<ScheduledContentWithCalendarDay> scheduledContent) {
        DaySchedule daySchedule = new DaySchedule();
        daySchedule.setCalendarDate(calendarDayToCalendarDate(calendarDay));
        List<ScheduledContent> scheduledContents = scheduledContent
                .stream()
                .map(EntityMapper::joinedCalendarEntityToScheduledContent)
                .collect(Collectors.toList());
        daySchedule.setScheduledContents(scheduledContents);
        return daySchedule;
    }

    public static ScheduledContent joinedCalendarEntityToScheduledContent(ScheduledContentWithCalendarDay joinedEntity) {
        ScheduledContent scheduledContent = new ScheduledContent();
        scheduledContent.setContent(contentEntityToContent(joinedEntity.getContentEntity()));
        scheduledContent.setDuration(joinedEntity.getDuration());
        return scheduledContent;
    }
}
