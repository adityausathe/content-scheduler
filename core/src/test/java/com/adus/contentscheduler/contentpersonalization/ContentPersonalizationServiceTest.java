package com.adus.contentscheduler.contentpersonalization;

import com.adus.contentscheduler.dao.Rating;
import com.adus.contentscheduler.dao.entity.Content;
import com.adus.contentscheduler.dao.entity.StaticContent;
import com.adus.contentscheduler.dao.repository.spi.ContentRepository;
import com.adus.contentscheduler.dao.repository.spi.StaticContentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.adus.contentscheduler.contentpersonalization.ContentDataUtil.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentPersonalizationServiceTest {

    @Mock
    private StaticContentRepository staticContentRepository;
    @Mock
    private ContentRepository contentRepository;
    @InjectMocks
    private ContentPersonalizationService contentPersonalizationService;

    @Test
    void findsAllProgrammes() {
        // 3 programmes
        when(staticContentRepository.findAllProgrammes()).thenReturn(ContentDataUtil.createStaticContentProgrammes());

        List<PersonalizableContent> allProgrammes = contentPersonalizationService.findAllProgrammes();
        assertEquals(3, allProgrammes.size());
        // export and assert
        assertEquals(Arrays.asList(ContentDataUtil.CLASS_5_TH_CBSE, ContentDataUtil.CLASS_6_TH_CBSE, ContentDataUtil.CLASS_7_TH_CBSE)
                , allProgrammes.stream()
                        .map(PersonalizableContent::exportContent)
                        .map(content -> content.getContentAttributes().getId())
                        .collect(Collectors.toList()));
    }

    @Test
    void findsContent() {
        StaticContent class5thContent = ContentDataUtil.createStaticContentProgrammes().get(0); // CLASS_5_TH_CBSE
        ContentDataUtil.addStaticContentSubjects(class5thContent); // [Science, Math, English, Social-Sciences]
        when(staticContentRepository.findContentById(ContentDataUtil.CLASS_5_TH_CBSE)).thenReturn(class5thContent);

        PersonalizableContent programmeContentTree = contentPersonalizationService.findProgramme(ContentDataUtil.CLASS_5_TH_CBSE);
        assertFalse(programmeContentTree.isDirty());

        assertEquals(ContentDataUtil.CLASS_5_TH_CBSE, programmeContentTree.getName());
        assertEquals(Arrays.asList(SCIENCE, MATH, ENGLISH, SOCIAL_SCIENCES),
                programmeContentTree.getPersonalizableSubContents().stream()
                        .map(PersonalizableContent::getName)
                        .collect(Collectors.toList()));
    }

    @Test
    void savesPersonalizedProgramme() {
        StaticContent class5thContent = ContentDataUtil.createStaticContentProgrammes().get(0); // CLASS_5_TH_CBSE
        ContentDataUtil.addStaticContentSubjects(class5thContent); // [Science, Math, English, Social-Sciences]
        PersonalizableContent class5thPersonalizableContent = PersonalizableContent.createFrom(class5thContent);

        // select programme tree
        class5thPersonalizableContent.select();

        // make changes to the programme's content-tree
        class5thPersonalizableContent.getPersonalizableSubContents().get(2) // English
                .setTimeRestriction(30f);
        class5thPersonalizableContent.getPersonalizableSubContents().get(1) // Math
                .setRating(Rating.LOW);
        assertTrue(class5thPersonalizableContent.isDirty());

        contentPersonalizationService.savePersonalizedProgramme(class5thPersonalizableContent);

        ArgumentCaptor<Content> contentCaptor = ArgumentCaptor.forClass(Content.class);
        verify(contentRepository).saveContent(contentCaptor.capture());

        assertEquals(ContentDataUtil.CLASS_5_TH_CBSE, contentCaptor.getValue().getContentAttributes().getId());
        assertEquals(Arrays.asList(SCIENCE, MATH, ENGLISH, SOCIAL_SCIENCES),
                contentCaptor.getValue().getSubContents().stream()
                        .map(contentParam -> contentParam.getContentAttributes().getId())
                        .collect(Collectors.toList()));

        assertEquals(30f, contentCaptor.getValue().getSubContents().get(2).getTimeRestriction()); // English
        assertEquals(Rating.LOW, contentCaptor.getValue().getSubContents().get(1).getContentAttributes().getRating()); // Math
    }

    @Test
    void updatesPersonalizedProgramme() {
        Content class5thContent = ContentDataUtil.createContentProgrammes().get(0); // CLASS_5_TH_CBSE
        ContentDataUtil.addContentSubjects(class5thContent); // [Science, Math, English, Social-Sciences]

        PersonalizableContent class5thPersonalizableContent = PersonalizableContent.reinstateFrom(class5thContent);

        // UNSELECT programme's content-tree
        class5thPersonalizableContent.unSelect();

        assertTrue(class5thPersonalizableContent.isDirty());

        contentPersonalizationService.savePersonalizedProgramme(class5thPersonalizableContent);

        ArgumentCaptor<Content> contentCaptor = ArgumentCaptor.forClass(Content.class);
        verify(contentRepository).saveContent(contentCaptor.capture());

        assertEquals(ContentDataUtil.CLASS_5_TH_CBSE, contentCaptor.getValue().getContentAttributes().getId());
        assertTrue(contentCaptor.getValue().isDeleted());
        contentCaptor.getValue().getSubContents()
                .forEach(subject -> assertTrue(subject.isDeleted()));
    }
}