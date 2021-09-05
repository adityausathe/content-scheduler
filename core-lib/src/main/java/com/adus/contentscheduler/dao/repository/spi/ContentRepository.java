package com.adus.contentscheduler.dao.repository.spi;

import com.adus.contentscheduler.dao.entity.Content;

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
