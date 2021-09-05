package com.adus.studyscheduler.crud.repository;

import com.adus.contentscheduler.dao.entity.StaticContent;
import com.adus.contentscheduler.dao.repository.spi.StaticContentRepository;
import com.adus.studyscheduler.crud.EntityMapper;
import com.adus.studyscheduler.crud.StudySchedulerRoomDatabase;
import com.adus.studyscheduler.crud.dao.StaticContentDao;
import com.adus.studyscheduler.crud.entity.StaticContentEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StaticContentRepositoryImpl implements StaticContentRepository {
    private static final Comparator<StaticContent> CONTENT_NAME_COMPARATOR =
            Comparator.comparing(staticContent -> staticContent.getContentAttributes().getName());

    private final StaticContentDao staticContentDao;

    StaticContentRepositoryImpl(StudySchedulerRoomDatabase database) {
        staticContentDao = database.staticContentDao();
    }

    @Override
    public StaticContent findContentById(String rootId) {
        List<StaticContentEntity> staticContentEntities = staticContentDao.getContentHierarchy(rootId);

        Map<String, StaticContent> entityLookup = staticContentEntities.stream()
                .map(EntityMapper::staticContentEntityToStaticContent)
                .collect(Collectors.toMap(staticContent -> staticContent.getContentAttributes().getId(), Function.identity()));

        // add sub-contents to programme-content
        staticContentEntities.stream()
                .filter(entity -> !entity.getId().equals(rootId))
                .forEach(entity -> entityLookup.get(entity.getParent())
                        .getSubContents().add(entityLookup.get(entity.getId())));

        StaticContent staticContent = entityLookup.get(rootId);
        staticContent.getSubContents()
                .sort(CONTENT_NAME_COMPARATOR);
        return staticContent;
    }


    @Override
    public List<StaticContent> findAllProgrammes() {
        List<StaticContentEntity> contents = staticContentDao.getShallowProgrammeContents();
        return contents.stream()
                .map(EntityMapper::staticContentEntityToStaticContent)
                .sorted(CONTENT_NAME_COMPARATOR)
                .collect(Collectors.toList());
    }
}
