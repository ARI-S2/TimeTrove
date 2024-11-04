package com.timetrove.Project.repository.querydsl;

import com.timetrove.Project.domain.Comment;

import java.util.List;

public interface CommentRepositoryCustom {
	
    List<Comment> findCommentByNo(Long no);

}
