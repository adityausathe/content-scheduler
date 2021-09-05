package com.adus.studyscheduler.crud;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EntityBatch<E> {
    private final List<E> entitiesForSave;
    private final List<E> entitiesForDelete;

    public static <E> EntityBatch<E> create() {
        return new EntityBatch<>(new ArrayList<>(), new ArrayList<>());
    }
}
