package com.adus.contentscheduler.commons.repository;

import com.adus.contentscheduler.commons.entity.StaticContent;

import java.util.List;

public interface StaticContentRepository {
    /**
     * finds content-tree by contentId
     *
     * @param contentId id of the content
     * @return content-tree root
     */
    StaticContent findContentById(String contentId);

    /**
     * finds shallow-content of all programmes
     *
     * @return content nodes
     */
    List<StaticContent> findAllProgrammes();
}
