package com.adus.contentscheduler.commons.repository;

import com.adus.contentscheduler.commons.entity.Calendar;

import java.util.Date;

public interface CalendarRepository {
    /**
     * fetches calendar contents. Includes deep fetching of Content Associations.
     *
     * @return calendar with schedules
     */
    Calendar getCalendar();

    /**
     * fetches slice of calendar. Includes deep fetching of Content Associations.
     *
     * @return calendar with schedules
     */
    Calendar getCalendarSlice(Date from, Date to);

    /**
     * saves the calendar. Modifications to Content associations will be ignored.
     *
     * @param calendar newly created or updated calendar instance
     */
    void saveCalendar(Calendar calendar);
}
