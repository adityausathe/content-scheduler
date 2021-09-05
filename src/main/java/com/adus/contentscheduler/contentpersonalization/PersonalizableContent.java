package com.adus.contentscheduler.contentpersonalization;

import com.adus.contentscheduler.dao.Rating;
import com.adus.contentscheduler.dao.entity.Content;
import com.adus.contentscheduler.dao.entity.ContentAttributes;
import com.adus.contentscheduler.dao.entity.StaticContent;

import java.util.List;
import java.util.stream.Collectors;

public class PersonalizableContent {
    private final ContentAttributes attributes;
    private final List<PersonalizableContent> personalizableSubContents;
    private Float timeRestriction;
    private boolean isDirty;
    private boolean selected;

    private PersonalizableContent(Content content, List<PersonalizableContent> personalizableSubContents) {
        attributes = content.getContentAttributes();
        timeRestriction = content.getTimeRestriction();
        this.personalizableSubContents = personalizableSubContents;
        selected = true;
    }

    public PersonalizableContent(StaticContent staticContent, List<PersonalizableContent> personalizableSubContents) {
        attributes = staticContent.getContentAttributes();
        this.personalizableSubContents = personalizableSubContents;
        selected = false;
    }

    public static PersonalizableContent reinstateFrom(Content contentRoot) {
        List<PersonalizableContent> personalizableSubContents = contentRoot.getSubContents().stream()
                .map(PersonalizableContent::reinstateFrom)
                .collect(Collectors.toList());
        return new PersonalizableContent(contentRoot, personalizableSubContents);
    }

    public static PersonalizableContent createFrom(StaticContent contentRoot) {
        List<PersonalizableContent> personalizableSubContents = contentRoot.getSubContents().stream()
                .map(PersonalizableContent::createFrom)
                .collect(Collectors.toList());
        return new PersonalizableContent(contentRoot, personalizableSubContents);
    }

    public List<PersonalizableContent> getPersonalizableSubContents() {
        return personalizableSubContents;
    }

    public void select() {
        personalizableSubContents.forEach(PersonalizableContent::select);
        this.selected = true;
        markDirty();
    }

    public boolean isSelected() {
        return selected;
    }

    public void unSelect() {
        personalizableSubContents.forEach(PersonalizableContent::unSelect);
        this.selected = false;
        markDirty();
    }

    public void setRating(Rating rating) {
        attributes.setRating(rating);
        markDirty();
    }

    public Float getTimeRestriction() {
        return timeRestriction;
    }

    public void setTimeRestriction(Float timeRestriction) {
        this.timeRestriction = timeRestriction;
        markDirty();
    }

    public String getName() {
        return attributes.getName();
    }

    private void markDirty() {
        isDirty = true;
    }

    boolean isDirty() {
        return this.isDirty || personalizableSubContents.stream().anyMatch(PersonalizableContent::isDirty);
    }

    Content exportContent() {
        Content content = new Content(attributes);
        content.setTimeRestriction(timeRestriction);
        if (!selected) {
            content.setDeleted(true);
        }
        List<Content> subContents = personalizableSubContents.stream()
                .filter(PersonalizableContent::isDirty).map(PersonalizableContent::exportContent)
                .collect(Collectors.toList());
        content.setSubContents(subContents);
        return content;
    }
}
