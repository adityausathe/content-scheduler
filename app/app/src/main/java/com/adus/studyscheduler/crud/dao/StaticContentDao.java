package com.adus.studyscheduler.crud.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.adus.studyscheduler.crud.entity.StaticContentEntity;

import java.util.List;

@Dao
public interface StaticContentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(List<StaticContentEntity> staticContentEntity);

    @Query("DELETE FROM static_content")
    void deleteAll();

    @Query("SELECT * from static_content where content_type = 'PROGRAMME'")
    List<StaticContentEntity> getShallowProgrammeContents();

    @Query("with RECURSIVE" +
            " parent_info(parent_id) as (" +
            "   VALUES(:rootId) union" +
            "   SELECT id from static_content, parent_info where static_content.parent_id = parent_info.parent_id" +
            " )" +
            " select * from static_content where static_content.id in parent_info")
    List<StaticContentEntity> getContentHierarchy(String rootId);
}
