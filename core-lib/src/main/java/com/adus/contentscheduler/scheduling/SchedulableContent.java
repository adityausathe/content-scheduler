package com.adus.contentscheduler.scheduling;

import com.adus.contentscheduler.dao.Constants;
import com.adus.contentscheduler.dao.ContentType;
import com.adus.contentscheduler.dao.Rating;
import com.adus.contentscheduler.dao.entity.Content;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

class SchedulableContent {
    private final Content content;
    private final Rating rating;
    private final List<SchedulableContent> schedulableSubContents;

    private Float timeRestriction;

    // these get updated during the lifetime of this object
    private float availableUnRestrictedTime;
    private float studyTime;

    private SchedulableContent(Content entity, List<SchedulableContent> schedulableSubContents) {
        this.content = entity;
        this.schedulableSubContents = schedulableSubContents;
        this.rating = entity.getContentAttributes().getRating();
        this.timeRestriction = entity.getTimeRestriction();
    }

    public static SchedulableContent initializeFrom(Content contentRoot) {
        List<SchedulableContent> schedulableSubContents = contentRoot.getSubContents().stream()
                .map(SchedulableContent::initializeFrom)
                .collect(Collectors.toList());
        return new SchedulableContent(contentRoot, schedulableSubContents);
    }

    void performUnRestrictedAllocation(UnrestrictedTimeAllocator allocator) {
        studyTime = allocator.allocate(rating);
        float availableTimeForSubContent = timeRestriction != null ? timeRestriction : studyTime;
        UnrestrictedTimeAllocator subContentAllocator = new UnrestrictedTimeAllocator(schedulableSubContents, availableTimeForSubContent);
        schedulableSubContents.forEach(subContent -> subContent.performUnRestrictedAllocation(subContentAllocator));
    }

    float propagateUnrestrictedTimeComponent() {
        if (schedulableSubContents.isEmpty()) {
            availableUnRestrictedTime = studyTime;
        } else {
            availableUnRestrictedTime = doOpOnSubContentsAndCollectResult(SchedulableContent::propagateUnrestrictedTimeComponent);
        }
        return (timeRestriction != null) ? 0f : availableUnRestrictedTime;
    }

    private void performResidueAllocation(ResidualTimeAllocator residualTimeAllocator) {
        if (timeRestriction != null) {
            return;
        }
        float residue = residualTimeAllocator.allocate(availableUnRestrictedTime);
        if (Math.abs(residue) < Constants.EFFECTIVE_ZERO) {
            return;
        }
        studyTime += residue;
        ResidualTimeAllocator subContentAllocator = new ResidualTimeAllocator(residue, availableUnRestrictedTime);
        schedulableSubContents.forEach(subContent -> subContent.performResidueAllocation(subContentAllocator));
    }

    float adjustResidue() {
        Float residueFromChildren = doOpOnSubContentsAndCollectResult(SchedulableContent::adjustResidue);
        if (timeRestriction == null) {
            return residueFromChildren;
        }
        if (availableUnRestrictedTime > Constants.EFFECTIVE_ZERO && residueFromChildren != 0) {
            ResidualTimeAllocator residualTimeAllocator = new ResidualTimeAllocator(residueFromChildren, availableUnRestrictedTime);
            schedulableSubContents.forEach(subContent -> subContent.performResidueAllocation(residualTimeAllocator));
        }
        float residue = studyTime - timeRestriction;
        studyTime = timeRestriction;
        return residue;
    }

    float synchronizeStudyTimes() {
        if (schedulableSubContents.isEmpty())
            return studyTime;
        studyTime = doOpOnSubContentsAndCollectResult(SchedulableContent::synchronizeStudyTimes);
        return studyTime;
    }

    private Float doOpOnSubContentsAndCollectResult(Function<SchedulableContent, Float> operation) {
        return schedulableSubContents.stream().map(operation)
                .reduce((f1, f2) -> f1 += f2).orElse(0f);
    }

    void selectContentTypeNodes(ContentType contentType, List<SchedulableContent> holder) {
        if (contentType.equals(this.content.getContentAttributes().getContentType())) {
            holder.add(this);
            return;
        }
        schedulableSubContents.forEach(subContent -> subContent.selectContentTypeNodes(contentType, holder));
    }

    void selectMostGranularContentNodes(List<SchedulableContent> holder) {
        if (schedulableSubContents.isEmpty()) {
            holder.add(this);
        }
        schedulableSubContents.forEach(subContent -> subContent.selectMostGranularContentNodes(holder));
    }

    TimeRequirement getTimeRequirement() {
        return new TimeRequirement(studyTime);
    }

    Content exportContent() {
        updateContent();
        schedulableSubContents.forEach(SchedulableContent::updateContent);
        return content;
    }

    private void updateContent() {
        content.setStudyTime(studyTime);
    }

    public Rating getRating() {
        return rating;
    }

    /*
     * data accessors for testing
     */
    void setTimeRestriction(Float timeRestriction) {
        this.timeRestriction = timeRestriction;
    }

    float getStudyTime() {
        return studyTime;
    }

    List<SchedulableContent> getSchedulableSubContents() {
        return schedulableSubContents;
    }

    @Setter
    @Getter
    static class TimeRequirement {
        private float timeRequired;

        private TimeRequirement(float timeRequired) {
            this.timeRequired = timeRequired;
        }
    }
}
