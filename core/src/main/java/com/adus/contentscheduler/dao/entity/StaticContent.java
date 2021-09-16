package com.adus.contentscheduler.dao.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class StaticContent extends BaseEntity {
    private ContentAttributes contentAttributes;
    private List<StaticContent> subContents;

    public StaticContent(ContentAttributes contentAttributes) {
        this.contentAttributes = contentAttributes;
    }

    public StaticContent() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticContent that = (StaticContent) o;
        return contentAttributes.equals(that.contentAttributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contentAttributes);
    }
}
