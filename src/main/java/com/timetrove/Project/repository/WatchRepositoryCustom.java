package com.timetrove.Project.repository;

import com.timetrove.Project.domain.Watch;

import java.util.List;

public interface WatchRepositoryCustom {
    List<Watch> watchFindData(String searchWord, int start, String filter);
}
