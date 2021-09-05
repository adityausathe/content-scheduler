package com.adus.studyscheduler.crud.dao;

import androidx.annotation.VisibleForTesting;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.adus.studyscheduler.crud.EntityBatch;
import com.adus.studyscheduler.crud.StudySchedulerRoomDatabase;
import com.adus.studyscheduler.crud.entity.CalendarDayEntity;
import com.adus.studyscheduler.crud.entity.ScheduledContentEntity;
import com.adus.studyscheduler.crud.entity.ScheduledContentWithCalendarDay;

import java.util.Date;
import java.util.List;

@Dao
public abstract class CalendarDao {
    private final ScheduledContentDao scheduledContentDao;

    CalendarDao(StudySchedulerRoomDatabase database) {
        scheduledContentDao = database.scheduledContentDao();
    }

    @Query("select calendar_day.*, content.*, scheduled_content.duration as duration " +
            "from calendar_day " +
            "left join scheduled_content on calendar_day.date = scheduled_content.date " +
            "left join content on scheduled_content.content_id = content.id " +
            "where calendar_day.date between :from and :to " +
            "order by date")
    public abstract List<ScheduledContentWithCalendarDay> findScheduledContentFilledCalendarDays(Date from, Date to);

    @Query("select * from calendar_day order by date")
    public abstract List<CalendarDayEntity> findAllCalendarDays();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void save(List<CalendarDayEntity> entities);

    @Delete
    public abstract void delete(List<CalendarDayEntity> entities);

    @VisibleForTesting
    @Query("delete from calendar_day")
    public abstract void deleteAll();

    @Transaction
    public void saveAndDelete(EntityBatch<CalendarDayEntity> calendarDayEntityBatch,
                              EntityBatch<ScheduledContentEntity> scheduledContentEntityBatch) {
        save(calendarDayEntityBatch.getEntitiesForSave());
        scheduledContentDao.save(scheduledContentEntityBatch.getEntitiesForSave());

        scheduledContentDao.delete(scheduledContentEntityBatch.getEntitiesForDelete());
        delete(calendarDayEntityBatch.getEntitiesForDelete());
    }

}
