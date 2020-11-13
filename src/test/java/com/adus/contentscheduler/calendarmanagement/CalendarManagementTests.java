package com.adus.contentscheduler.calendarmanagement;

import com.adus.contentscheduler.CalendarDataUtil;
import com.adus.contentscheduler.commons.entity.Calendar;
import com.adus.contentscheduler.commons.repository.CalendarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarManagementTests {
    @Mock
    private CalendarRepository calendarRepository;
    @InjectMocks
    private CalendarManagementService calendarManagementService;

    @Test
    void createsCalendarForTheFirstTime() {
        when(calendarRepository.getCalendar()).thenReturn(CalendarDataUtil.blankCalendar());

        CalendarView calendarView = calendarManagementService.getCalendarView();

        assertTrue(calendarView.getCalendarDayViews().isEmpty());
        assertFalse(calendarView.isDirty());

        calendarView.addDay(CalendarDataUtil.JULY_13);

        assertEquals(1, calendarView.getCalendarDayViews().size());
        assertTrue(calendarView.isDirty());

        Calendar exportedCalendar = calendarView.exportCalendar();
        assertEquals(1, exportedCalendar.getDaySchedules().size());
        assertEquals(CalendarDataUtil.JULY_13, exportedCalendar.getDaySchedules().get(0).getCalendarDate().getDate());
    }

    @Test
    void updatesCalendar() {
        // 3 days
        Calendar calendar = CalendarDataUtil.create(LocalDate.of(2020, 7, 13), LocalDate.of(2020, 7, 15));
        when(calendarRepository.getCalendar()).thenReturn(calendar);

        CalendarView calendarView = calendarManagementService.getCalendarView();

        assertEquals(3, calendarView.getCalendarDayViews().size());
        assertFalse(calendarView.isDirty());

        CalendarDayView july13DayView = calendarView.getDay(CalendarDataUtil.JULY_13);
        july13DayView.setAvailableTime(4f);
        CalendarDayView july14DayView = calendarView.getDay(CalendarDataUtil.JULY_14);
        july14DayView.markHoliday();
        assertTrue(calendarView.isDirty());


        Calendar exportedCalendar = calendarView.exportCalendar();
        assertEquals(3, exportedCalendar.getDaySchedules().size());

        assertEquals(CalendarDataUtil.JULY_13, exportedCalendar.getDaySchedules().get(0).getCalendarDate().getDate());
        assertEquals(4f, exportedCalendar.getDaySchedules().get(0).getCalendarDate().getAvailableTime());
        assertEquals(CalendarDataUtil.JULY_14, exportedCalendar.getDaySchedules().get(1).getCalendarDate().getDate());
        assertTrue(exportedCalendar.getDaySchedules().get(1).getCalendarDate().isHoliday());
    }

    @Test
    void loadsAndUpdatesCalendarWithScheduledContents() {
        // 3 days
        Calendar calendar = CalendarDataUtil.create(LocalDate.of(2020, 7, 13), LocalDate.of(2020, 7, 15));
        CalendarDataUtil.addScheduledContent(calendar, CalendarDataUtil.JULY_13, "Pointers", 3f);

        when(calendarRepository.getCalendar()).thenReturn(calendar);

        CalendarView calendarView = calendarManagementService.getCalendarView();
        assertEquals(3, calendarView.getCalendarDayViews().size());

        // change study duration for the scheduledContent
        calendarView.getDay(CalendarDataUtil.JULY_13)
                .getScheduledContentViews().get(0)
                .setDuration(2f);
        assertTrue(calendarView.isDirty());

        Calendar exportedCalendar = calendarView.exportCalendar();
        assertEquals(3, exportedCalendar.getDaySchedules().size());

        assertEquals(CalendarDataUtil.JULY_13, exportedCalendar.getDaySchedules().get(0).getCalendarDate().getDate());

        assertEquals(1, exportedCalendar.getDaySchedules().get(0).getScheduledContents().size());
        assertEquals("Pointers", exportedCalendar.getDaySchedules().get(0).getScheduledContents().get(0).getContent().getContentAttributes().getId());
        assertEquals(2f, exportedCalendar.getDaySchedules().get(0).getScheduledContents().get(0).getDuration());
    }

}