package com.adus.contentscheduler.commons.repository;

import com.adus.contentscheduler.commons.entity.Content;

public interface ContentRepository {
    /**
     * finds contents of registered programme
     *
     * @return whole content-tree of the programme
     */
    Content findProgramme();

    /**
     * Saves content-tree
     *
     * @param content content-tree root
     */
    void saveContent(Content content);
}
