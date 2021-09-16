package com.adus.contentscheduler.contentpersonalization;

import com.adus.contentscheduler.dao.repository.spi.ContentRepository;
import com.adus.contentscheduler.dao.repository.spi.StaticContentRepository;

import java.util.List;
import java.util.stream.Collectors;

public class ContentPersonalizationService {
    private final ContentRepository contentRepository;
    private final StaticContentRepository staticContentRepository;

    public ContentPersonalizationService(ContentRepository contentRepository, StaticContentRepository staticContentRepository) {
        this.contentRepository = contentRepository;
        this.staticContentRepository = staticContentRepository;
    }

    public List<PersonalizableContent> findAllProgrammes() {
        return staticContentRepository.findAllProgrammes()
                .stream()
                .map(PersonalizableContent::createFrom)
                .collect(Collectors.toList());
    }

    public PersonalizableContent findProgramme(String programmeContentId) {
        return PersonalizableContent.createFrom(staticContentRepository.findContentById(programmeContentId));
    }

    public void savePersonalizedProgramme(PersonalizableContent programme) {
        if (programme.isDirty()) {
            contentRepository.saveContent(programme.exportContent());
        }
    }
}
