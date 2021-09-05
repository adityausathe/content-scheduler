package com.adus.studyscheduler.crud.repository;

import com.adus.contentscheduler.dao.ContentType;
import com.adus.contentscheduler.dao.entity.Content;
import com.adus.studyscheduler.crud.entity.ContentEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.adus.studyscheduler.crud.repository.DataIntegrationTestHelper.setupContextForContentRepo;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ContentRepositoryImplTest {
    private DataIntegrationTestHelper.TestSetupContext testSetupContext;
    private ContentRepositoryImpl contentRepository;

    @Before
    public void setUp() {
        testSetupContext = setupContextForContentRepo();
        contentRepository = new ContentRepositoryImpl(testSetupContext.getDatabase());
    }

    @After
    public void tearDown() {
        if (testSetupContext.getDatabase() != null) {
            testSetupContext.getDatabase().close();
        }
    }

    @Test
    public void findProgramme() {
        Content programme = contentRepository.findProgramme();

        ContentEntity programmeContentEntity = testSetupContext.getContentEntities()
                .stream()
                .filter(contentEntity -> contentEntity.getContentType().equals(ContentType.PROGRAMME.name()))
                .findFirst().orElseThrow(IllegalStateException::new);
        List<ContentEntity> subjectContentEntities = testSetupContext.getContentEntities()
                .stream()
                .filter(contentEntity -> !contentEntity.getContentType().equals(ContentType.PROGRAMME.name()))
                .collect(Collectors.toList());

        assertEquals(programmeContentEntity.getId(), programme.getContentAttributes().getId());
        assertEquals(programmeContentEntity.getContentType(), programme.getContentAttributes().getContentType().name());
        assertEquals(programmeContentEntity.getTimeRestriction(), programme.getTimeRestriction());

        assertProgrammeSubjects(subjectContentEntities, programme);
    }

    @Test
    public void saveContent() {
        // Fetch
        Content programmeContent = contentRepository.findProgramme();

        // Update
        programmeContent.setStudyTime(489.5f);
        programmeContent.getSubContents().get(0).setStudyTime(150f);
        programmeContent.getSubContents().get(1).setTimeRestriction(160f);
        programmeContent.getSubContents().get(2).setDeleted(true);

        // Save
        contentRepository.saveContent(programmeContent);

        // Verify
        Content updatedProgrammeContent = contentRepository.findProgramme();
        assertEquals(489.5f, updatedProgrammeContent.getStudyTime(), 0.01);
        assertEquals(2, updatedProgrammeContent.getSubContents().size());
        assertEquals(150f, updatedProgrammeContent.getSubContents().get(0).getStudyTime(), 0.01);
        assertEquals(160f, updatedProgrammeContent.getSubContents().get(1).getTimeRestriction(), 0.01);
    }

    private void assertProgrammeSubjects(List<ContentEntity> expected, Content program) {
        assertEquals(expected.size(), program.getSubContents().size());
        IntStream
                .range(0, expected.size())
                .forEach(i -> {
                    Content content = program.getSubContents().get(i);
                    ContentEntity contentEntity = expected.get(i);

                    assertEquals(contentEntity.getId(), content.getContentAttributes().getId());
                    assertEquals(contentEntity.getContentType(), content.getContentAttributes().getContentType().name());
                    assertEquals(contentEntity.getRating(), content.getContentAttributes().getRating().name());
                });
    }
}