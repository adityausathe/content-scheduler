package com.adus.studyscheduler.crud.repository;

import com.adus.contentscheduler.dao.entity.Content;
import com.adus.contentscheduler.dao.repository.spi.ContentRepository;
import com.adus.studyscheduler.crud.EntityBatch;
import com.adus.studyscheduler.crud.EntityMapper;
import com.adus.studyscheduler.crud.StudySchedulerRoomDatabase;
import com.adus.studyscheduler.crud.dao.ContentDao;
import com.adus.studyscheduler.crud.entity.ContentEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ContentRepositoryImpl implements ContentRepository {
    private static final Comparator<Content> CONTENT_NAME_COMPARATOR =
            Comparator.comparing(content -> content.getContentAttributes().getName());

    private final ContentDao contentDao;

    ContentRepositoryImpl(StudySchedulerRoomDatabase database) {
        contentDao = database.contentDao();
    }

    @Override
    public Content findProgramme() {
        String programmeId = contentDao.findProgrammeId();
        List<ContentEntity> contentEntities = contentDao.getContentHierarchy(programmeId);

        Map<String, Content> entityLookup = contentEntities
                .stream()
                .map(EntityMapper::contentEntityToContent)
                .collect(Collectors.toMap(content -> content.getContentAttributes().getId(), Function.identity()));

        // add sub-contents to programme-content
        contentEntities.stream()
                .filter(entity -> !entity.getId().equals(programmeId))
                .forEach(entity -> entityLookup.get(entity.getParent())
                        .getSubContents().add(entityLookup.get(entity.getId())));

        Content programmeContent = entityLookup.get(programmeId);

        programmeContent.getSubContents()
                .sort(CONTENT_NAME_COMPARATOR);
        return programmeContent;
    }

    @Override
    public void saveContent(Content content) {
        EntityBatch<ContentEntity> contentEntityBatch = EntityBatch.create();
        flatten(content, null, contentEntityBatch);

        contentDao.saveAndDeleteContentAndItsReferences(contentEntityBatch);
    }

    private void flatten(Content content, String parentId, EntityBatch<ContentEntity> contentEntityBatch) {
        ContentEntity entity = EntityMapper.contentToContentEntity(content, parentId);
        if (content.isDeleted()) {
            contentEntityBatch.getEntitiesForDelete().add(entity);
        } else {
            contentEntityBatch.getEntitiesForSave().add(entity);
        }
        content.getSubContents()
                .forEach(subContent -> flatten(subContent, content.getContentAttributes().getId(), contentEntityBatch));
    }

}
