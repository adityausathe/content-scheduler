package com.adus.contentscheduler.calendarmanagement;

import com.adus.contentscheduler.dao.repository.spi.CalendarRepository;

public class CalendarManagementService {
    private final CalendarRepository calendarRepository;

    public CalendarManagementService(CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }

    public CalendarView getCalendarView() {
        return CalendarView.initializeFrom(calendarRepository.getCalendar());
    }

    public void saveCalendarView(CalendarView calendarView) {
        if (calendarView.isDirty()) {
            calendarRepository.saveCalendar(calendarView.exportCalendar());
        }
    }

}
