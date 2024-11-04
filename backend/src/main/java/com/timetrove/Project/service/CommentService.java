package com.timetrove.Project.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * 특정 게시글에 속한 댓글을 조회하고 부모 댓글 기준으로 정렬
     * @param: Long no
     * @return 댓글 리스트 DTO
     */
    public List<CommentDto> findCommentListByNo(Long no) {
        List<Comment> commentList = commentRepository.findCommentByNo(no);
        List<CommentDto> commentDtoList = commentList.stream()
                .map(CommentDto::convertCommentToDto)
                .collect(Collectors.toList());
        return convertNestedStructure(commentDtoList);
    }

    /**
     * 대댓글 중첩 구조 변환 메서드
     * 댓글 리스트를 부모-자식 관계로 정리
     * @param: List<CommentDto> commentDtoList
     * @return 구조화 된 댓글 리스트 DTO
     */
    private List<CommentDto> convertNestedStructure(List<CommentDto> commentDtoList) {
        List<CommentDto> result = new ArrayList<>();
        Map<Long, CommentDto> map = new HashMap<>();

        commentDtoList.forEach(c -> {
            map.put(c.getId(), c);
            if (c.getParentId() != null) {
                map.get(c.getParentId()).getChildren().add(c);
            } else {
                result.add(c);
            }
        });

        printChildComments(result);
        return result;
    }

    /**
     * 자식 댓글을 출력하는 메서드
     * @param: List<CommentDto> commentDtoList
     */
    private void printChildComments(List<CommentDto> commentDtoList) {
        for (CommentDto commentDto : commentDtoList) {
            System.out.println("  - " + commentDto);
            if (!commentDto.getChildren().isEmpty()) {
                printChildComments(commentDto.getChildren());
            }
        }
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

