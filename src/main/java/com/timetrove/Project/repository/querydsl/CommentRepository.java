package com.timetrove.Project.repository.querydsl;

import java.util.List;

import com.timetrove.Project.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long>, CustomCommentRepository {
    List<Comment> findByUserUserCodeOrderByCreatedAtDesc(Long userCode);
}
