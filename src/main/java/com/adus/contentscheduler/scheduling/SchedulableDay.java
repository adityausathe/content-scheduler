package com.adus.contentscheduler.scheduling;

import com.adus.contentscheduler.dao.Constants;
import com.adus.contentscheduler.dao.entity.CalendarDate;
import com.adus.contentscheduler.dao.entity.DaySchedule;
import com.adus.contentscheduler.dao.entity.ScheduledContent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

class SchedulableDay {
    private final CalendarDate calendarDate;
    private final Date date;
    private final List<SchedulableContent> contentsToStudy;
    private float remainingTime;

    SchedulableDay(CalendarDate calendarDate) {
        this.calendarDate = calendarDate;
        this.date = calendarDate.getDate();
        this.remainingTime = calendarDate.getAvailableTime();
        this.contentsToStudy = new ArrayList<>();
    }

    boolean isSlotAvailable() {
        return remainingTime > Constants.EFFECTIVE_ZERO;
    }

    void scheduleContent(SchedulableContent schedulableContent, SchedulableContent.TimeRequirement timeRequirement) {
        if (timeRequirement.getTimeRequired() - remainingTime > Constants.EFFECTIVE_ZERO) {
            timeRequirement.setTimeRequired(timeRequirement.getTimeRequired() - remainingTime);
            remainingTime = 0;
        } else {
            remainingTime -= timeRequirement.getTimeRequired();
            timeRequirement.setTimeRequired(0);
        }
        contentsToStudy.add(schedulableContent);
    }

    List<SchedulableContent> getContentsToStudy() {
        return contentsToStudy;
    }

    Date getDate() {
        return date;
    }

    float getRemainingTime() {
        return remainingTime;
    }

    DaySchedule exportDaySchedule() {
        List<ScheduledContent> scheduledContents = contentsToStudy.stream().map(schedulableContent -> {
            ScheduledContent scheduledContent = new ScheduledContent();
            scheduledContent.setContent(schedulableContent.exportContent());
            return scheduledContent;
        }).collect(Collectors.toList());

        DaySchedule daySchedule = new DaySchedule();
        daySchedule.setCalendarDate(calendarDate);
        daySchedule.setScheduledContents(scheduledContents);
        return daySchedule;
    }
}
