package com.timetrove.Project.service;

import java.util.*;
import java.util.stream.Collectors;

import com.timetrove.Project.common.enumType.ErrorCode;
import com.timetrove.Project.common.exception.EntityNotFoundException;
import com.timetrove.Project.domain.Board;
import com.timetrove.Project.domain.Comment;
import com.timetrove.Project.domain.User;
import com.timetrove.Project.dto.CommentDto;
import com.timetrove.Project.repository.BoardRepository;
import com.timetrove.Project.repository.UserRepository;
import com.timetrove.Project.repository.querydsl.CommentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;

    /**
     * 댓글 생성 메서드
     * 새로운 댓글을 생성하고 저장
     * @param: CommentDto commentDto
     * @return 생성된 댓글의 DTO
     */
    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        Board board = boardRepository.findById(commentDto.getNo())
                .orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.BOARD_NOT_FOUND));
        User user = userRepository.findById(commentDto.getUserCode())
                .orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .board(board)
                .content(commentDto.getContent())
                .user(user)
                .parent(commentDto.getParentId() != null ? findCommentByIdOrThrow(commentDto.getParentId()) : null)
                .build();

        commentRepository.save(comment);
        return CommentDto.convertCommentToDto(comment);
    }

    /**
     * 댓글 수정 메서드
     * 특정 ID의 댓글을 수정
     * @param: Long commentId, CommentDto commentDto
     * @return 수정된 댓글의 DTO
     */
    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = findCommentByIdOrThrow(commentId);
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
        return CommentDto.convertCommentToDto(comment);
    }

    /**
     * 댓글 삭제 메서드
     * 특정 ID의 댓글을 삭제
     * @param: Long commentId
     */
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = findCommentByIdOrThrow(commentId);
        commentRepository.delete(comment);
    }

    /**
     * 댓글 조회 메서드
     * 특정 게시글에 속한 댓글을 계층구조로 조회
     * @param: Long no
     * @return 댓글 리스트 DTO
     */
    public List<CommentDto> findCommentListByNo(Long no) {
        // 루트 댓글과 자식 댓글들을 한 번에 조회
        List<Comment> rootComments = commentRepository.findCommentByNo(no);
        return convertNestedStructure(rootComments);
    }

    /**
     * 대댓글 중첩 구조 변환 메서드
     * 댓글 엔티티를 DTO로 변환하며 계층구조와 정렬을 처리
     * @param: List<Comment> comments
     * @return 계층구조가 적용된 댓글 리스트 DTO
     */
    private List<CommentDto> convertNestedStructure(List<Comment> comments) {
        Map<Long, CommentDto> commentMap = new HashMap<>();
        List<CommentDto> rootComments = new ArrayList<>();

        for (Comment comment : comments) {
            CommentDto dto = CommentDto.convertCommentToDto(comment);
            commentMap.put(comment.getId(), dto);

            if (comment.getParent() == null) {
                rootComments.add(dto);
            } else {
                CommentDto parentDto = commentMap.get(comment.getParent().getId());
                if (parentDto != null) {
                    parentDto.getChildren().add(dto);
                }
            }
        }

        return rootComments;
    }

    /**
     * 댓글 출력을 위한 메서드 (디버깅/로깅 용도)
     */
    private void printChildComments(List<CommentDto> comments) {
        comments.forEach(comment -> {
            System.out.println("댓글 ID: " + comment.getId());
            if (!comment.getChildren().isEmpty()) {
                printChildComments(comment.getChildren());
            }
        });
    }

    /**
     * 최근 작성된 댓글 목록 조회 메서드
     * 특정 사용자에 대한 최신 댓글을 조회
     * @param: Long userCode
     * @return 댓글 리스트 DTO
     */
    public List<CommentDto> getRecentComments(Long userCode) {
        return commentRepository.findByUserUserCodeOrderByCreatedAtDesc(userCode).stream()
                .map(CommentDto::convertCommentToDto)
                .collect(Collectors.toList());
    }

    /**
     * 댓글 ID로 댓글을 조회하는 헬퍼 메서드
     * 댓글이 없을 시 예외 발생 (COMMENT_NOT_FOUND)
     * @param: Long commentId
     * @return Comment 엔티티
     */
    private Comment findCommentByIdOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(HttpStatus.NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND));
    }
}

