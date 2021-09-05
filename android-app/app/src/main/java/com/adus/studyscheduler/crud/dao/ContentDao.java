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
import com.adus.studyscheduler.crud.entity.ContentEntity;

import java.util.List;
import java.util.stream.Collectors;

@Dao
public abstract class ContentDao {

    private final ScheduledContentDao scheduledContentDao;

    ContentDao(StudySchedulerRoomDatabase database) {
        scheduledContentDao = database.scheduledContentDao();
    }

    @Query("with RECURSIVE" +
            " parent_info(parent_id) as (" +
            "   VALUES(:rootId) union" +
            "   SELECT id from content, parent_info where content.parent_id = parent_info.parent_id" +
            " )" +
            " select * from content where content.id in parent_info")
    public abstract List<ContentEntity> getContentHierarchy(String rootId);

    @Query("select id from content where content_type = 'PROGRAMME'")
    public abstract String findProgrammeId();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void save(List<ContentEntity> contentEntities);

    @Delete
    public abstract void delete(List<ContentEntity> contentEntities);

    @Transaction
    public void saveAndDeleteContentAndItsReferences(EntityBatch<ContentEntity> contentEntityBatch) {
        save(contentEntityBatch.getEntitiesForSave());
        List<String> contentIdsForDelete = contentEntityBatch.getEntitiesForDelete().stream()
                .map(ContentEntity::getId)
                .collect(Collectors.toList());
        scheduledContentDao.deleteByContentIds(contentIdsForDelete);
        delete(contentEntityBatch.getEntitiesForDelete());
    }

    @VisibleForTesting
    @Query("delete from content")
    public abstract void deleteAll();
}
