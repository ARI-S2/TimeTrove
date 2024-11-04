package com.timetrove.Project.repository.querydsl;

import com.timetrove.Project.domain.Watch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WatchRepositoryCustom {

    Page<Watch> findWatchesWithFilter(String searchWord, String filter, Pageable pageable);
}
