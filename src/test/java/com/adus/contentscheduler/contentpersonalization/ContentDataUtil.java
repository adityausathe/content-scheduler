package com.adus.contentscheduler.contentpersonalization;

import com.adus.contentscheduler.commons.ContentType;
import com.adus.contentscheduler.commons.Rating;
import com.adus.contentscheduler.commons.entity.Content;
import com.adus.contentscheduler.commons.entity.ContentAttributes;
import com.adus.contentscheduler.commons.entity.StaticContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContentDataUtil {

    public static final String CLASS_5_TH_CBSE = "Class 5th CBSE";
    public static final String CLASS_6_TH_CBSE = "Class 6th CBSE";
    public static final String CLASS_7_TH_CBSE = "Class 7th CBSE";
    public static final String SCIENCE = "Science";
    public static final String MATH = "Math";
    public static final String ENGLISH = "English";
    public static final String SOCIAL_SCIENCES = "Social-Sciences";

    public static List<StaticContent> createStaticContentProgrammes() {
        return Arrays.asList(
                staticContent(createContentAttributes(CLASS_5_TH_CBSE, null, ContentType.PROGRAMME)),
                staticContent(createContentAttributes(CLASS_6_TH_CBSE, null, ContentType.PROGRAMME)),
                staticContent(createContentAttributes(CLASS_7_TH_CBSE, null, ContentType.PROGRAMME))
        );
    }

    public static List<Content> createContentProgrammes() {
        return Arrays.asList(
                content(createContentAttributes(CLASS_5_TH_CBSE, null, ContentType.PROGRAMME)),
                content(createContentAttributes(CLASS_6_TH_CBSE, null, ContentType.PROGRAMME)),
                content(createContentAttributes(CLASS_7_TH_CBSE, null, ContentType.PROGRAMME))
        );
    }

    private static Content content(ContentAttributes contentAttributes) {
        Content content = new Content(contentAttributes);
        content.setSubContents(new ArrayList<>());
        return content;
    }

    private static StaticContent staticContent(ContentAttributes contentAttributes) {
        StaticContent staticContent = new StaticContent();
        staticContent.setContentAttributes(contentAttributes);
        staticContent.setSubContents(new ArrayList<>());
        return staticContent;
    }

    public static void addStaticContentSubjects(StaticContent programme) {
        List<StaticContent> subjects = Arrays.asList(
                staticContent(createContentAttributes(SCIENCE, Rating.HIGH, ContentType.SUBJECT)),
                staticContent(createContentAttributes(MATH, Rating.HIGH, ContentType.SUBJECT)),
                staticContent(createContentAttributes(ENGLISH, Rating.DEFAULT, ContentType.SUBJECT)),
                staticContent(createContentAttributes(SOCIAL_SCIENCES, Rating.DEFAULT, ContentType.SUBJECT))
        );
        programme.getSubContents().addAll(subjects);
    }

    public static void addContentSubjects(Content programme) {
        List<Content> subjects = Arrays.asList(
                content(createContentAttributes(SCIENCE, Rating.HIGH, ContentType.SUBJECT)),
                content(createContentAttributes(MATH, Rating.HIGH, ContentType.SUBJECT)),
                content(createContentAttributes(ENGLISH, Rating.DEFAULT, ContentType.SUBJECT)),
                content(createContentAttributes(SOCIAL_SCIENCES, Rating.DEFAULT, ContentType.SUBJECT))
        );
        programme.getSubContents().addAll(subjects);
    }

    public static ContentAttributes createContentAttributes(String id, Rating rating, ContentType contentType) {
        ContentAttributes contentAttributes = new ContentAttributes();
        contentAttributes.setId(id);
        contentAttributes.setName(id);
        contentAttributes.setContentType(contentType);
        contentAttributes.setRating(rating);
        return contentAttributes;
    }
}
