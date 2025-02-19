package com.timetrove.Project.repository.querydsl;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.timetrove.Project.domain.Comment;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import static com.timetrove.Project.domain.QComment.comment;


@RequiredArgsConstructor
@Repository
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    //댓글 및 대댓글 조회 , n+1 문제 방지, 부모 댓글의 ID(parent.id) 기준으로 오름차순으로 정렬하며, 부모 댓글이 없는 경우(nulls)는 리스트의 가장 앞에 위치
    @Override
    public List<Comment> findCommentByNo(Long no) {
        return jpaQueryFactory
                .selectFrom(comment)
                .leftJoin(comment.children).fetchJoin() // 자식 댓글까지 한 번에 조회
                .where(comment.board.no.eq(no))
                .orderBy(
                        comment.parent.id.asc().nullsFirst(), // 부모 댓글 먼저 정렬
                        comment.createdAt.asc() // 같은 깊이에서는 작성순 정렬
                )
                .distinct() // 중복 제거
                .fetch();
    }

}
