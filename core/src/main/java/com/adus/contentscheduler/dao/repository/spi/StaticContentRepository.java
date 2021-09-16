package com.adus.contentscheduler.dao.repository.spi;

import com.adus.contentscheduler.dao.entity.StaticContent;

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
     * finds shallow(un-hydrated) content-tree(only root-node populated) of all the programmes
     *
     * @return list of programmes' content-nodes
     */
    List<StaticContent> findAllProgrammes();
}
