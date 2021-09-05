package com.adus.contentscheduler.scheduling;

import com.adus.contentscheduler.dao.Rating;

import java.util.Collections;
import java.util.List;

class UnrestrictedTimeAllocator {
    private final int ratingsSum;
    private final float availableTime;

    UnrestrictedTimeAllocator(List<SchedulableContent> candidates, float availableTime) {
        this.ratingsSum = candidates.stream().map(SchedulableContent::getRating).mapToInt(Rating::getScore).sum();
        this.availableTime = availableTime;
    }

    UnrestrictedTimeAllocator(SchedulableContent candidate, float availableTime) {
        this(Collections.singletonList(candidate), availableTime);
    }

    float allocate(Rating rating) {
        return ((float) rating.getScore() / ratingsSum) * availableTime;
    }
}
