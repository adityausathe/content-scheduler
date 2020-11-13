package com.adus.contentscheduler.commons.entity;

import com.adus.contentscheduler.commons.ContentType;
import com.adus.contentscheduler.commons.Rating;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class ContentAttributes {
    protected String id;
    protected String name;
    protected ContentType contentType;
    protected Rating rating;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentAttributes attributes = (ContentAttributes) o;
        return Objects.equals(id, attributes.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
