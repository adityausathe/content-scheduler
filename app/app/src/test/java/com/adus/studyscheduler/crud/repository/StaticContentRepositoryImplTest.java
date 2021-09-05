package com.adus.studyscheduler.crud.repository;

import com.adus.contentscheduler.dao.ContentType;
import com.adus.contentscheduler.dao.Rating;
import com.adus.contentscheduler.dao.entity.StaticContent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.adus.studyscheduler.crud.repository.DataIntegrationTestHelper.PROGRAMME_BS;
import static com.adus.studyscheduler.crud.repository.DataIntegrationTestHelper.PROGRAMME_MS;
import static com.adus.studyscheduler.crud.repository.DataIntegrationTestHelper.SUBJECT_C_101;
import static com.adus.studyscheduler.crud.repository.DataIntegrationTestHelper.SUBJECT_C_102;
import static com.adus.studyscheduler.crud.repository.DataIntegrationTestHelper.SUBJECT_C_103;
import static com.adus.studyscheduler.crud.repository.DataIntegrationTestHelper.setupContextForStaticContentRepo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class StaticContentRepositoryImplTest {
    private DataIntegrationTestHelper.TestSetupContext testSetupContext;
    private StaticContentRepositoryImpl staticContentRepository;

    @Before
    public void setUp() {
        testSetupContext = setupContextForStaticContentRepo();
        staticContentRepository = new StaticContentRepositoryImpl(testSetupContext.getDatabase());
    }

    @After
    public void tearDown() {
        if (testSetupContext.getDatabase() != null) {
            testSetupContext.getDatabase().close();
        }
    }

    @Test
    public void findContentById() {
        StaticContent programmeContent = staticContentRepository.findContentById(PROGRAMME_BS);

        assertEquals(PROGRAMME_BS, programmeContent.getContentAttributes().getId());
        assertEquals(PROGRAMME_BS, programmeContent.getContentAttributes().getName());
        assertEquals(ContentType.PROGRAMME, programmeContent.getContentAttributes().getContentType());
        assertEquals(Rating.DEFAULT, programmeContent.getContentAttributes().getRating());

        assertEquals(3, programmeContent.getSubContents().size());
        assertEquals(
                Arrays.asList(SUBJECT_C_101, SUBJECT_C_102, SUBJECT_C_103),
                programmeContent.getSubContents()
                        .stream()
                        .map(sub -> sub.getContentAttributes().getId())
                        .collect(Collectors.toList())
        );
    }

    @Test
    public void findAllProgrammes() {
        List<StaticContent> allProgrammes = staticContentRepository.findAllProgrammes();

        assertTrue(allProgrammes.stream()
                .allMatch(staticContent -> staticContent.getContentAttributes().getContentType() == ContentType.PROGRAMME));
        assertEquals(Arrays.asList(PROGRAMME_BS, PROGRAMME_MS),
                allProgrammes.stream()
                        .map(staticContent -> staticContent.getContentAttributes().getId())
                        .collect(Collectors.toList()));
    }
}