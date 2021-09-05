package com.adus.contentscheduler.dao.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Content extends BaseEntity {
    private ContentAttributes contentAttributes;
    private Float timeRestriction;
    private Float studyTime;
    private List<Content> subContents;

    public Content(ContentAttributes contentAttributes) {
        this.contentAttributes = contentAttributes;
    }

    public Content() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content that = (Content) o;
        return contentAttributes.equals(that.contentAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentAttributes);
    }
}
