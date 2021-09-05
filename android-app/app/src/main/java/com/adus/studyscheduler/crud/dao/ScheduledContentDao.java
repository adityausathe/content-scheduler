package com.adus.studyscheduler.crud.dao;

import androidx.annotation.VisibleForTesting;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.adus.studyscheduler.crud.entity.ScheduledContentEntity;

import java.util.List;

@Dao
public interface ScheduledContentDao {

    @Query("DELETE FROM scheduled_content where content_id in (:contentIds)")
    void deleteByContentIds(List<String> contentIds);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(List<ScheduledContentEntity> entities);

    @Delete
    void delete(List<ScheduledContentEntity> entities);

    @VisibleForTesting
    @Query("delete from scheduled_content")
    void deleteAll();
}
