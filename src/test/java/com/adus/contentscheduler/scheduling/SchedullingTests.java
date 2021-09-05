package com.adus.contentscheduler.scheduling;

import com.adus.contentscheduler.CalendarDataUtil;
import com.adus.contentscheduler.dao.Constants;
import com.adus.contentscheduler.dao.entity.Calendar;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

import static com.adus.contentscheduler.scheduling.ScheduleDataUtil.findSubContent;
import static org.junit.jupiter.api.Assertions.*;

class SchedullingTests {

    @DisplayName("Basic Test to check allocation of time-slots")
    @Test
    void test_DefaultRating() {
        SchedulableContent cProgramming = ScheduleDataUtil.cProgrammingSubject();

        cProgramming.performUnRestrictedAllocation(new UnrestrictedTimeAllocator(cProgramming, 40f));

        assertEquals(40f, cProgramming.getStudyTime());
        cProgramming.exportContent().getSubContents()
                .forEach(subContent -> assertEquals(5f, subContent.getStudyTime()));
    }

    @DisplayName("Test if rating is considered while allocating time-slots")
    @Test
    void test_MixedRating() {
        SchedulableContent cProgramming = ScheduleDataUtil.cProgrammingSubject();

        SchedulableContent for_loop = findSubContent(cProgramming, "for loop");
        SchedulableContent while_loop = findSubContent(cProgramming, "while loop");
        SchedulableContent do_while_loop = findSubContent(cProgramming, "do while loop");

        cProgramming.performUnRestrictedAllocation(new UnrestrictedTimeAllocator(cProgramming, 40f));

        assertEquals(40f, cProgramming.getStudyTime());
        cProgramming.exportContent().getSubContents()
                .forEach(subContent -> assertEquals(5f, subContent.getStudyTime()));

        assertEquals(((float) 3 / 6) * 5, for_loop.getStudyTime(), 0.01);
        assertEquals(((float) 2 / 6) * 5, while_loop.getStudyTime(), 0.01);
        assertEquals(((float) 1 / 6) * 5, do_while_loop.getStudyTime(), 0.01);
    }

    @DisplayName("Single subject scheduling test")
    @Test
    void test_Scheduling_SingleSubject() {
        SchedulableContent cProgramming = ScheduleDataUtil.cProgrammingSubject();

        Calendar calendar = CalendarDataUtil.create(LocalDate.of(2020, Month.JULY, 10),
                LocalDate.of(2020, Month.JULY, 16));

        CalendarDataUtil.findDaySchedule(calendar, LocalDate.of(2020, Month.JULY, 11))
                .getCalendarDate().setHoliday(true);
        CalendarDataUtil.findDaySchedule(calendar, LocalDate.of(2020, Month.JULY, 12))
                .getCalendarDate().setHoliday(true);
        calendar.getDaySchedules().forEach(daySchedule -> daySchedule.getCalendarDate().setAvailableTime(8f));

        SchedulableCalendar schedulableCalendar = SchedulableCalendar.initializeFrom(calendar);

        ScheduleCreator scheduleCreator = new ScheduleCreator(cProgramming, schedulableCalendar, 40f, new TimeAllocationStrategy.UnrestrictedTimeAllocationStrategy(cProgramming));
        scheduleCreator.create();

        Iterator<SchedulableDay> calendarIterator = schedulableCalendar.iterator();
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 10), false, calendarIterator,
                cProgramming, "Introduction", "Data Types & Keywords");
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 13), false, calendarIterator,
                cProgramming, "Data Types & Keywords", "Variables", "Expressions");
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 14), false, calendarIterator,
                cProgramming, "Expressions", "Conditionals");
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 15), false, calendarIterator,
                cProgramming, "Conditionals", "for loop", "while loop", "do while loop", "Functions");
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 16), true, calendarIterator,
                cProgramming, "Functions", "Pointers");

    }

    @DisplayName("MultiSubject scheduling with one item of same subject(stickiness=1) in a round robin fashion")
    @Test
    void test_Scheduling_MultiSubjects() {
        SchedulableContent programme = ScheduleDataUtil.createProgramme();
        SchedulableContent cProgramming = ScheduleDataUtil.findSubContent(programme, "C Programming");
        SchedulableContent osProgramming = ScheduleDataUtil.findSubContent(programme, "OS Programming");

        Calendar calendar = CalendarDataUtil.create(LocalDate.of(2020, Month.JULY, 10),
                LocalDate.of(2020, Month.JULY, 16));

        CalendarDataUtil.findDaySchedule(calendar, LocalDate.of(2020, Month.JULY, 12))
                .getCalendarDate().setHoliday(true);
        calendar.getDaySchedules().forEach(daySchedule -> daySchedule.getCalendarDate().setAvailableTime(10f));

        SchedulableCalendar schedulableCalendar = SchedulableCalendar.initializeFrom(calendar);

        ScheduleCreator scheduleCreator = new ScheduleCreator(programme, schedulableCalendar, 60f, new TimeAllocationStrategy.UnrestrictedTimeAllocationStrategy(programme));
        scheduleCreator.create();

        Iterator<SchedulableDay> calendarIterator = schedulableCalendar.iterator();
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 10), false, calendarIterator,
                findSubContent(cProgramming, "Introduction"),
                findSubContent(osProgramming, "Introduction"),
                findSubContent(cProgramming, "Data Types & Keywords")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 11), false, calendarIterator,
                findSubContent(cProgramming, "Data Types & Keywords"),
                findSubContent(osProgramming, "Computer Architecture Basics"),
                findSubContent(cProgramming, "Variables"),
                findSubContent(osProgramming, "Memory Management")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 13), false, calendarIterator,
                findSubContent(osProgramming, "Memory Management"),
                findSubContent(cProgramming, "Expressions"),
                findSubContent(osProgramming, "Scheduling")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 14), false, calendarIterator,
                findSubContent(osProgramming, "Scheduling"),
                findSubContent(cProgramming, "Conditionals"),
                findSubContent(osProgramming, "Mutex"),
                findSubContent(cProgramming, "for loop")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 15), false, calendarIterator,
                findSubContent(cProgramming, "for loop"),
                findSubContent(osProgramming, "Semaphores"),
                findSubContent(cProgramming, "while loop"),
                findSubContent(osProgramming, "Message Passing"),
                findSubContent(cProgramming, "do while loop"),
                findSubContent(osProgramming, "Structure"),
                findSubContent(cProgramming, "Functions")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 16), false, calendarIterator,
                findSubContent(cProgramming, "Functions"),
                findSubContent(osProgramming, "Examples"),
                findSubContent(cProgramming, "Pointers"),
                findSubContent(osProgramming, "IO")
        );

    }

    @DisplayName("MultiSubject scheduling with two items of same subject(stickiness=2) in a round robin fashion")
    @Test
    void test_Scheduling_MultiSubjects_StickinessFactor2() {
        SchedulableContent programme = ScheduleDataUtil.createProgramme();
        SchedulableContent cProgramming = ScheduleDataUtil.findSubContent(programme, "C Programming");
        SchedulableContent osProgramming = ScheduleDataUtil.findSubContent(programme, "OS Programming");

        Calendar calendar = CalendarDataUtil.create(LocalDate.of(2020, Month.JULY, 10),
                LocalDate.of(2020, Month.JULY, 16));

        CalendarDataUtil.findDaySchedule(calendar, LocalDate.of(2020, Month.JULY, 12))
                .getCalendarDate().setHoliday(true);
        calendar.getDaySchedules().forEach(daySchedule -> daySchedule.getCalendarDate().setAvailableTime(10f));

        SchedulableCalendar schedulableCalendar = SchedulableCalendar.initializeFrom(calendar);

        ScheduleCreator scheduleCreator = new ScheduleCreator(programme, schedulableCalendar, 60f, new TimeAllocationStrategy.UnrestrictedTimeAllocationStrategy(programme));
        scheduleCreator.setStickinessFactor(2);
        scheduleCreator.create();

        Iterator<SchedulableDay> calendarIterator = schedulableCalendar.iterator();
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 10), false, calendarIterator,
                findSubContent(cProgramming, "Introduction"),
                findSubContent(cProgramming, "Data Types & Keywords"),
                findSubContent(osProgramming, "Introduction")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 11), false, calendarIterator,
                findSubContent(osProgramming, "Introduction"),
                findSubContent(osProgramming, "Computer Architecture Basics"),
                findSubContent(cProgramming, "Variables"),
                findSubContent(cProgramming, "Expressions")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 13), false, calendarIterator,
                findSubContent(cProgramming, "Expressions"),
                findSubContent(osProgramming, "Memory Management"),
                findSubContent(osProgramming, "Scheduling")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 14), false, calendarIterator,
                findSubContent(osProgramming, "Scheduling"),
                findSubContent(cProgramming, "Conditionals"),
                findSubContent(cProgramming, "for loop"),
                findSubContent(osProgramming, "Mutex")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 15), false, calendarIterator,
                findSubContent(osProgramming, "Mutex"),
                findSubContent(osProgramming, "Semaphores"),
                findSubContent(cProgramming, "while loop"),
                findSubContent(cProgramming, "do while loop"),
                findSubContent(osProgramming, "Message Passing"),
                findSubContent(osProgramming, "Structure"),
                findSubContent(cProgramming, "Functions")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 16), false, calendarIterator,
                findSubContent(cProgramming, "Functions"),
                findSubContent(cProgramming, "Pointers"),
                findSubContent(osProgramming, "Examples"),
                findSubContent(osProgramming, "IO")
        );

    }

    @DisplayName("Restricted Time Allocation")
    @Test
    void test_RestrictedTimeAllocation() {
        SchedulableContent programme = ScheduleDataUtil.createProgramme();
        SchedulableContent cProgramming = ScheduleDataUtil.findSubContent(programme, "C Programming");
        SchedulableContent osProgramming = ScheduleDataUtil.findSubContent(programme, "OS Programming");

        // add restrictions
        SchedulableContent functions = findSubContent(cProgramming, "Functions");
        functions.setTimeRestriction(6f);

        SchedulableContent loops = findSubContent(cProgramming, "Loops");
        loops.setTimeRestriction(4f);

        SchedulableContent concurrency = findSubContent(osProgramming, "Concurrency");
        concurrency.setTimeRestriction(20f);

        SchedulableContent examples = findSubContent(osProgramming, "Examples");
        examples.setTimeRestriction(2f);

        programme.setTimeRestriction(100f);

        // perform allocation
        programme.performUnRestrictedAllocation(new UnrestrictedTimeAllocator(programme, 100f));
        programme.propagateUnrestrictedTimeComponent();
        programme.adjustResidue();
        programme.synchronizeStudyTimes();

        // hard timings
        assertEquals(6f, functions.getStudyTime());
        assertEquals(4f, loops.getStudyTime());
        assertEquals(20f, concurrency.getStudyTime());
        assertEquals(2f, examples.getStudyTime());

        // derived timings
        assertEquals(43.41f, cProgramming.getStudyTime(), Constants.EFFECTIVE_ZERO);
        assertEquals(56.59f, osProgramming.getStudyTime(), Constants.EFFECTIVE_ZERO);
        assertEquals(5.24f, findSubContent(osProgramming, "Computer Architecture Basics").getStudyTime(), Constants.EFFECTIVE_ZERO);
        assertEquals(7.86f, findSubContent(osProgramming, "Memory Management").getStudyTime(), Constants.EFFECTIVE_ZERO);
        assertEquals(5.14f, findSubContent(osProgramming, "File System").getStudyTime(), Constants.EFFECTIVE_ZERO);
        assertEquals(3.14f, findSubContent(osProgramming, "Structure").getStudyTime(), Constants.EFFECTIVE_ZERO);
    }

    @DisplayName("Restricted Time Allocation - Tight constraints at subsequent levels")
    @Test
    void test_RestrictedTimeAllocation_Tight_Constraints() {
        SchedulableContent programme = ScheduleDataUtil.createProgramme();
        SchedulableContent cProgramming = ScheduleDataUtil.findSubContent(programme, "C Programming");
        SchedulableContent osProgramming = ScheduleDataUtil.findSubContent(programme, "OS Programming");

        // add restrictions
        SchedulableContent fileSystem = findSubContent(osProgramming, "File System");
        fileSystem.setTimeRestriction(8f);

        SchedulableContent structure = findSubContent(osProgramming, "Structure");
        structure.setTimeRestriction(5f);

        SchedulableContent examples = findSubContent(osProgramming, "Examples");
        examples.setTimeRestriction(3f);

        programme.setTimeRestriction(100f);

        // perform allocation
        programme.performUnRestrictedAllocation(new UnrestrictedTimeAllocator(programme, 100f));
        programme.propagateUnrestrictedTimeComponent();
        programme.adjustResidue();
        programme.synchronizeStudyTimes();

        // hard timings
        assertEquals(8f, fileSystem.getStudyTime());
        assertEquals(5f, structure.getStudyTime());
        assertEquals(3f, examples.getStudyTime());

        // derived timings
        assertEquals(48.87f, cProgramming.getStudyTime(), Constants.EFFECTIVE_ZERO);
        assertEquals(51.12f, osProgramming.getStudyTime(), Constants.EFFECTIVE_ZERO);
    }

    @DisplayName("Restricted Time Allocation - Infeasible constraints")
    @Test
    void test_RestrictedTimeAllocation_Infeasible_Constraints() {
        SchedulableContent programme = ScheduleDataUtil.createProgramme();
        SchedulableContent cProgramming = ScheduleDataUtil.findSubContent(programme, "C Programming");
        SchedulableContent osProgramming = ScheduleDataUtil.findSubContent(programme, "OS Programming");

        // add restrictions
        SchedulableContent functions = findSubContent(cProgramming, "Functions");
        functions.setTimeRestriction(6f);

        SchedulableContent loops = findSubContent(cProgramming, "Loops");
        loops.setTimeRestriction(4f);

        SchedulableContent concurrency = findSubContent(osProgramming, "Concurrency");
        concurrency.setTimeRestriction(20f);

        SchedulableContent examples = findSubContent(osProgramming, "Examples");
        examples.setTimeRestriction(2f);

        programme.setTimeRestriction(30f);

        // perform allocation
        programme.performUnRestrictedAllocation(new UnrestrictedTimeAllocator(programme, 100f));
        programme.propagateUnrestrictedTimeComponent();

        RuntimeException runtimeException = assertThrows(RuntimeException.class, programme::adjustResidue);
        assertEquals("Infeasible constraints", runtimeException.getMessage());
    }

    @DisplayName("Restricted MultiSubject scheduling with one items of same subject(stickiness=1) in a round robin fashion")
    @Test
    void test_Restricted_Scheduling_MultiSubjects_StickinessFactor1() {
        SchedulableContent programme = ScheduleDataUtil.createProgramme();
        SchedulableContent cProgramming = ScheduleDataUtil.findSubContent(programme, "C Programming");
        SchedulableContent osProgramming = ScheduleDataUtil.findSubContent(programme, "OS Programming");

        // add restrictions
        SchedulableContent functions = findSubContent(cProgramming, "Functions");
        functions.setTimeRestriction(6f);

        SchedulableContent loops = findSubContent(cProgramming, "Loops");
        loops.setTimeRestriction(4f);

        SchedulableContent concurrency = findSubContent(osProgramming, "Concurrency");
        concurrency.setTimeRestriction(12f);

        SchedulableContent examples = findSubContent(osProgramming, "Examples");
        examples.setTimeRestriction(2f);

        programme.setTimeRestriction(60f);

        Calendar calendar = CalendarDataUtil.create(LocalDate.of(2020, Month.JULY, 10),
                LocalDate.of(2020, Month.JULY, 16));

        CalendarDataUtil.findDaySchedule(calendar, LocalDate.of(2020, Month.JULY, 12))
                .getCalendarDate().setHoliday(true);
        calendar.getDaySchedules().forEach(daySchedule -> daySchedule.getCalendarDate().setAvailableTime(10f));

        SchedulableCalendar schedulableCalendar = SchedulableCalendar.initializeFrom(calendar);

        ScheduleCreator scheduleCreator = new ScheduleCreator(programme, schedulableCalendar, 60f, new TimeAllocationStrategy.TimeRestrictionsAwareTimeAllocationStrategy(programme));
        scheduleCreator.create();

        Iterator<SchedulableDay> calendarIterator = schedulableCalendar.iterator();
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 10), false, calendarIterator,
                findSubContent(cProgramming, "Introduction"),
                findSubContent(osProgramming, "Introduction"),
                findSubContent(cProgramming, "Data Types & Keywords"),
                findSubContent(osProgramming, "Computer Architecture Basics")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 11), false, calendarIterator,
                findSubContent(osProgramming, "Computer Architecture Basics"),
                findSubContent(cProgramming, "Variables"),
                findSubContent(osProgramming, "Memory Management"),
                findSubContent(cProgramming, "Expressions")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 13), false, calendarIterator,
                findSubContent(cProgramming, "Expressions"),
                findSubContent(osProgramming, "Scheduling"),
                findSubContent(cProgramming, "Conditionals"),
                findSubContent(osProgramming, "Mutex")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 14), false, calendarIterator,
                findSubContent(osProgramming, "Mutex"),
                findSubContent(cProgramming, "for loop"),
                findSubContent(osProgramming, "Semaphores")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 15), false, calendarIterator,
                findSubContent(osProgramming, "Semaphores"),
                findSubContent(cProgramming, "while loop"),
                findSubContent(osProgramming, "Message Passing"),
                findSubContent(cProgramming, "do while loop"),
                findSubContent(osProgramming, "Structure"),
                findSubContent(cProgramming, "Functions")
        );
        assertStudyDayContent(LocalDate.of(2020, Month.JULY, 16), false, calendarIterator,
                findSubContent(cProgramming, "Functions"),
                findSubContent(osProgramming, "Examples"),
                findSubContent(cProgramming, "Pointers"),
                findSubContent(osProgramming, "IO")
        );

    }

    private void assertStudyDayContent(LocalDate date, boolean allowedToHaveSlackTime, Iterator<SchedulableDay> calendarIterator, SchedulableContent... subSchedulableContents) {
        SchedulableDay schedulableDay = calendarIterator.next();
        assertEquals(java.sql.Date.valueOf(date), schedulableDay.getDate());

        if (allowedToHaveSlackTime) assertTrue(schedulableDay.getRemainingTime() >= 0);
        else assertEquals(0f, schedulableDay.getRemainingTime(), 0.01);

        assertEquals(Arrays.asList(subSchedulableContents), schedulableDay.getContentsToStudy());
    }

    private void assertStudyDayContent(LocalDate date, boolean allowedToHaveSlackTime, Iterator<SchedulableDay> calendarIterator, SchedulableContent cProgramming, String... subContents) {
        SchedulableDay schedulableDay = calendarIterator.next();
        assertEquals(java.sql.Date.valueOf(date), schedulableDay.getDate());

        if (allowedToHaveSlackTime) assertTrue(schedulableDay.getRemainingTime() >= 0);
        else assertEquals(0, schedulableDay.getRemainingTime());

        assertEquals(Arrays.stream(subContents).map(sub -> findSubContent(cProgramming, sub)).collect(Collectors.toList())
                , schedulableDay.getContentsToStudy());
    }
}
