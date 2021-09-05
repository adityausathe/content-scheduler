package com.adus.contentscheduler.calendarmanagement;

import com.adus.contentscheduler.dao.entity.ScheduledContent;

public class ScheduledContentView {
    private final ScheduledContent scheduledContent;
    private float duration;
    private boolean isDirty;

    ScheduledContentView(ScheduledContent scheduledContent) {
        this.scheduledContent = scheduledContent;
        this.duration = scheduledContent.getDuration();
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
        isDirty = true;
    }

    public String getContentName() {
        return scheduledContent.getContent().getContentAttributes().getName();
    }

    public Float getContentStudyTime() {
        return scheduledContent.getContent().getStudyTime();
    }

    boolean isDirty() {
        return isDirty;
    }

    ScheduledContent exportScheduledContent() {
        scheduledContent.setDuration(duration);
        return scheduledContent;
    }
}
