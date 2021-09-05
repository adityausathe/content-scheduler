package com.adus.studyscheduler.crud.repository;

import com.adus.contentscheduler.dao.entity.Calendar;
import com.adus.contentscheduler.dao.entity.DaySchedule;
import com.adus.contentscheduler.dao.entity.ScheduledContent;
import com.adus.contentscheduler.dao.repository.spi.CalendarRepository;
import com.adus.studyscheduler.crud.entity.CalendarDayEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.stream.IntStream;

import static com.adus.studyscheduler.crud.repository.DataIntegrationTestHelper.DT_2021_02_12;
import static com.adus.studyscheduler.crud.repository.DataIntegrationTestHelper.DT_2021_02_13;
import static com.adus.studyscheduler.crud.repository.DataIntegrationTestHelper.setupContextForCalendarRepo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class CalendarRepositoryImplTest {

    private CalendarRepository calendarRepository;
    private DataIntegrationTestHelper.TestSetupContext testSetupContext;

    @Before
    public void setUp() {
        testSetupContext = setupContextForCalendarRepo();
        calendarRepository = new CalendarRepositoryImpl(testSetupContext.getDatabase());
    }

    @After
    public void tearDown() {
        if (testSetupContext.getDatabase() != null) {
            testSetupContext.getDatabase().close();
        }
    }

    @Test
    public void getCalendar() {
        assertCalendarData(testSetupContext.getCalendarDayEntities(),
                calendarRepository.getCalendar());
    }

    @Test
    public void getCalendarSlice() {
        assertCalendarData(testSetupContext.getCalendarDayEntities().subList(0, 2),
                calendarRepository.getCalendarSlice(DT_2021_02_12, DT_2021_02_13));
    }

    @Test
    public void saveCalendar() {
        // Fetch
        Calendar calendar = calendarRepository.getCalendarSlice(DT_2021_02_12, DT_2021_02_13);

        // Update
        calendar.getDaySchedules()
                .stream()
                .filter(daySchedule -> daySchedule.getCalendarDate().getDate().equals(DT_2021_02_13))
                .forEach(daySchedule -> daySchedule.setDeleted(true));
        calendar.getDaySchedules()
                .stream()
                .filter(daySchedule -> daySchedule.getCalendarDate().getDate().equals(DT_2021_02_12))
                .forEach(daySchedule -> daySchedule.getScheduledContents().get(0).setDuration(9.9f));

        // Save
        calendarRepository.saveCalendar(calendar);

        // Verify
        Calendar updatedCalendarSlice = calendarRepository.getCalendarSlice(DT_2021_02_12, DT_2021_02_12);
        ScheduledContent scheduledContent_2021_02_12 = updatedCalendarSlice
                .getDaySchedules().get(0)
                .getScheduledContents().get(0);
        assertEquals(9.9f, scheduledContent_2021_02_12.getDuration(), 0.01);

        List<DaySchedule> daySchedules_2021_02_13 = calendarRepository.getCalendarSlice(DT_2021_02_13, DT_2021_02_13)
                .getDaySchedules();
        assertTrue(daySchedules_2021_02_13.isEmpty());
    }

    private void assertCalendarData(List<CalendarDayEntity> expected, Calendar actual) {
        assertEquals(expected.size(), actual.getDaySchedules().size());
        IntStream
                .range(0, expected.size())
                .forEach(i -> {
                    DaySchedule daySchedule = actual.getDaySchedules().get(i);
                    CalendarDayEntity calendarDayEntity = expected.get(i);

                    assertEquals(calendarDayEntity.getDate(), daySchedule.getCalendarDate().getDate());
                    assertEquals(calendarDayEntity.getAvailableTime(), daySchedule.getCalendarDate().getAvailableTime());
                    assertEquals(calendarDayEntity.getIsHoliday(), daySchedule.getCalendarDate().isHoliday());
                });
    }

}