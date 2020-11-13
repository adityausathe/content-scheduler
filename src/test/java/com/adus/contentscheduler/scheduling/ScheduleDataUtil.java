package com.adus.contentscheduler.scheduling;

import com.adus.contentscheduler.commons.ContentType;
import com.adus.contentscheduler.commons.Rating;
import com.adus.contentscheduler.commons.entity.Content;
import com.adus.contentscheduler.commons.entity.ContentAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ScheduleDataUtil {

    public static SchedulableContent cProgrammingSubject() {
        return SchedulableContent.initializeFrom(cProgrammingSubjectContent());
    }

    private static Content cProgrammingSubjectContent() {
        Content cProgramming = createContent("C Programming", Rating.DEFAULT, ContentType.SUBJECT);
        List<Content> subContents = new ArrayList<>();
        subContents.add(createContent("Introduction", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR));
        subContents.add(createContent("Data Types & Keywords", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR));
        subContents.add(createContent("Variables", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR));
        subContents.add(createContent("Expressions", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR));
        subContents.add(createContent("Conditionals", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR));
        subContents.add(createContent("Loops", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR));
        subContents.add(createContent("Functions", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR));
        subContents.add(createContent("Pointers", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR));
        cProgramming.setSubContents(subContents);

        Content loops = findSubContent(cProgramming, "Loops");
        loops.setSubContents(
                Arrays.asList(
                        createContent("for loop", Rating.HIGH, ContentType.OTHER_MORE_GRANULAR),
                        createContent("while loop", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR),
                        createContent("do while loop", Rating.LOW, ContentType.OTHER_MORE_GRANULAR)
                )
        );
        return cProgramming;
    }


    public static SchedulableContent osProgrammingSubject() {
        return SchedulableContent.initializeFrom(osProgrammingSubjectContent());
    }

    private static Content osProgrammingSubjectContent() {
        Content osProgramming = createContent("OS Programming", Rating.DEFAULT, ContentType.SUBJECT);

        List<Content> subContents = new ArrayList<>();
        subContents.add(createContent("Introduction", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR));
        subContents.add(createContent("Computer Architecture Basics", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR));
        subContents.add(createContent("Memory Management", Rating.HIGH, ContentType.OTHER_MORE_GRANULAR));
        subContents.add(createContent("Scheduling", Rating.HIGH, ContentType.OTHER_MORE_GRANULAR));
        subContents.add(createConcurrencyChapter());
        subContents.add(createFileSystem());
        subContents.add(createContent("IO", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR));
        osProgramming.setSubContents(subContents);
        return osProgramming;
    }

    private static Content createFileSystem() {
        Content fileSystem = createContent("File System", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR);
        fileSystem.setSubContents(
                Arrays.asList(
                        createContent("Structure", Rating.HIGH, ContentType.OTHER_MORE_GRANULAR),
                        createContent("Examples", Rating.DEFAULT, ContentType.OTHER_MORE_GRANULAR)
                )
        );
        return fileSystem;
    }

    private static Content createConcurrencyChapter() {
        Content scheduling = createContent("Concurrency", Rating.HIGH, ContentType.OTHER_MORE_GRANULAR);
        scheduling.setSubContents(
                Arrays.asList(
                        createContent("Mutex", Rating.HIGH, ContentType.OTHER_MORE_GRANULAR),
                        createContent("Semaphores", Rating.HIGH, ContentType.OTHER_MORE_GRANULAR),
                        createContent("Message Passing", Rating.LOW, ContentType.OTHER_MORE_GRANULAR)
                )
        );
        return scheduling;
    }

    private static Content createContent(String id, Rating rating, ContentType contentType) {
        ContentAttributes contentAttributes = new ContentAttributes();
        contentAttributes.setId(id);
        contentAttributes.setName(id);
        contentAttributes.setContentType(contentType);
        contentAttributes.setRating(rating);
        Content content = new Content(contentAttributes);
        content.setSubContents(new ArrayList<>());
        return content;
    }

    public static Content findSubContent(Content content, String subContentName) {
        return content.getSubContents().stream()
                .filter(sub -> sub.getContentAttributes().getId().equals(subContentName))
                .findAny().orElseGet(() -> content.getSubContents().stream()
                        .map(sub -> findSubContent(sub, subContentName))
                        .filter(Objects::nonNull).findAny().orElse(null));
    }

    public static SchedulableContent findSubContent(SchedulableContent schedulableContent, String subContentName) {
        return schedulableContent.getSchedulableSubContents().stream()
                .filter(sub -> sub.exportContent().getContentAttributes().getId().equals(subContentName))
                .findAny().orElseGet(() -> schedulableContent.getSchedulableSubContents().stream()
                        .map(sub -> findSubContent(sub, subContentName))
                        .filter(Objects::nonNull).findAny().orElse(null));
    }

    public static SchedulableContent createProgramme() {
        Content programme = createContent("Comp Engg Sem 4", Rating.DEFAULT, ContentType.PROGRAMME);
        programme.setSubContents(
                Arrays.asList(
                        cProgrammingSubjectContent(),
                        osProgrammingSubjectContent()
                )
        );
        return SchedulableContent.initializeFrom(programme);
    }
}
