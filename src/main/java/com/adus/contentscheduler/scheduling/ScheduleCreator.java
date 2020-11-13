package com.adus.contentscheduler.scheduling;

import com.adus.contentscheduler.commons.Constants;
import com.adus.contentscheduler.commons.ContentType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class ScheduleCreator {
    private int stickinessFactor = Constants.STICKINESS_FACTOR;
    private SchedulableContent schedulableContent;
    private float availableTime;
    private SchedulableCalendar schedulableCalendar;
    private TimeAllocationStrategy timeAllocationStrategy;

    ScheduleCreator(SchedulableContent schedulableContent, SchedulableCalendar calendar, float availableTime, TimeAllocationStrategy timeAllocationStrategy) {
        this.schedulableContent = schedulableContent;
        this.availableTime = availableTime;
        this.schedulableCalendar = calendar;
        this.timeAllocationStrategy = timeAllocationStrategy;
    }

    ScheduleCreator(SchedulableContent schedulableContent, SchedulableCalendar schedulableCalendar) {
        this.schedulableContent = schedulableContent;
        this.schedulableCalendar = schedulableCalendar;
        this.availableTime = schedulableCalendar.getAvailableTime();
        this.timeAllocationStrategy = new TimeAllocationStrategy.TimeRestrictionsAwareTimeAllocationStrategy(schedulableContent);
    }

    void create() {
        timeAllocationStrategy.allocate(availableTime);
        prepareScheduleAndFillCalendar();
    }

    private void prepareScheduleAndFillCalendar() {
        Map<SchedulableContent, List<SchedulableContent>> subjectWiseMostGranularContent = findSubjectWiseContent();

        List<Iterator<SchedulableContent>> sideToSideIterableStream = subjectWiseMostGranularContent.values()
                .stream().map(List::stream).map(Stream::iterator).collect(Collectors.toList());
        int maxSubContentSize = subjectWiseMostGranularContent.values()
                .stream().mapToInt(List::size).max().orElse(0);

        Iterator<SchedulableDay> calendarIterator = schedulableCalendar.iterator();
        CalendarCursor calendarCursor = new CalendarCursor();
        calendarCursor.set(calendarIterator.next());

        IntStream.rangeClosed(1, maxSubContentSize)
                .forEach(ignored -> {
                    List<SchedulableContent> sliceOfSchedulableContentFromAllSubjects = prepareContentSlice(sideToSideIterableStream);

                    sliceOfSchedulableContentFromAllSubjects.forEach(subContent ->
                            scheduleContent(subContent, calendarIterator, calendarCursor));
                });
    }

    private void scheduleContent(SchedulableContent subContent, Iterator<SchedulableDay> calendarIterator, CalendarCursor calendarCursor) {
        SchedulableContent.TimeRequirement timeRequirement = subContent.getTimeRequirement();
        while (timeRequirement.getTimeRequired() > Constants.EFFECTIVE_ZERO) {
            SchedulableDay schedulableDay = calendarCursor.get();
            if (schedulableDay.isSlotAvailable())
                schedulableDay.scheduleContent(subContent, timeRequirement);
            else
                calendarCursor.set(calendarIterator.next());
        }
    }

    private List<SchedulableContent> prepareContentSlice(List<Iterator<SchedulableContent>> sideToSideIterableStream) {
        List<SchedulableContent> sliceOfSchedulableContentFromAllSubjects = new ArrayList<>();
        sideToSideIterableStream.forEach(iterator -> {
            IntStream.rangeClosed(1, getStickinessFactor())
                    .forEach(ign -> {
                        if (iterator.hasNext()) {
                            sliceOfSchedulableContentFromAllSubjects.add(iterator.next());
                        }
                    });
        });
        return sliceOfSchedulableContentFromAllSubjects;
    }

    private int getStickinessFactor() {
        return stickinessFactor;
    }

    void setStickinessFactor(int stickinessFactor) {
        this.stickinessFactor = stickinessFactor;
    }

    private Map<SchedulableContent, List<SchedulableContent>> findSubjectWiseContent() {
        List<SchedulableContent> allSubjects = new ArrayList<>();
        schedulableContent.selectContentTypeNodes(ContentType.SUBJECT, allSubjects);

        Map<SchedulableContent, List<SchedulableContent>> subjectWiseMostGranularContent = new LinkedHashMap<>();
        allSubjects.forEach(subject -> {
            List<SchedulableContent> mostGranular = new ArrayList<>();
            subject.selectMostGranularContentNodes(mostGranular);
            subjectWiseMostGranularContent.put(subject, mostGranular);
        });
        return subjectWiseMostGranularContent;
    }

    private static class CalendarCursor {
        private SchedulableDay current;

        SchedulableDay get() {
            return current;
        }

        void set(SchedulableDay current) {
            this.current = current;
        }
    }
}
