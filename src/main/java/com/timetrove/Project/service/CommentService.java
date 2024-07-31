package com.timetrove.Project.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.timetrove.Project.domain.Board;
import com.timetrove.Project.domain.Comment;
import com.timetrove.Project.domain.User;
import com.timetrove.Project.dto.CommentDto;
import com.timetrove.Project.repository.BoardRepository;
import com.timetrove.Project.repository.UserRepository;
import com.timetrove.Project.repository.querydsl.CommentRepository;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;
	
    @Transactional
    public CommentDto createComment(CommentDto commentDto) {
        Board board = boardRepository.findById(commentDto.getNo()).orElseThrow(() -> new ResourceNotFoundException("Board not found"));
        User user = userRepository.findById(commentDto.getUserCode()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = Comment.builder()
                .board(board)
                .content(commentDto.getContent())
                .user(user)
                .parent(commentDto.getParentId() != null ?
                        commentRepository.findById(commentDto.getParentId()).orElseThrow(() -> new ResourceNotFoundException("Comment not found")): null)
                .build();

        commentRepository.save(comment);

        return commentDto.convertCommentToDto(comment);
    }
    
    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);

        return commentDto.convertCommentToDto(comment);
    }
    
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
        
        commentRepository.delete(comment);
    }
	
    /**
     * sns 댓글 조회 메서드
     * 각각 게시글에 속한 댓글 조회(부모 댓글 기준으로 정렬)
     * @param : Long BoardId
     */
    public List<CommentDto> findCommentListByNo(Long no) {
        List<Comment> commentList = commentRepository.findCommentByNo(no);
        //entity <==> dto 변환
        List<CommentDto> commentDtoList = commentList.stream().map(comment -> CommentDto.convertCommentToDto(comment))
                .collect(Collectors.toList());
        //대댓글 중첩구조 변환후 리턴
        return convertNestedStructure(commentDtoList);
    }
    
    /**
     * sns 댓글 <==> 대댓글 대댓글의 중첩구조 변환 메서드
     * @param : List<SnsComment> snsCommentList
     */
    private List<CommentDto> convertNestedStructure(List<CommentDto> CommentDtoList) {
        List<CommentDto> result = new ArrayList<>();
        Map<Long, CommentDto> map = new HashMap<>();

        CommentDtoList.stream().forEach(c -> {
            map.put(c.getId(), c);
            if(c.getParentId() != null) {
                map.get(c.getParentId()).getChildren().add(c);
            }
            else {
                result.add(c);
            }
        });
        printChildComments(result);
        return result;
    }
    
    private void printChildComments(List<CommentDto> commentDtoList) {
        for (CommentDto commentDto : commentDtoList) {
            // 2.1. 현재 코멘트 출력
            System.out.println("  - " + commentDto);

            // 2.2. 자식 코멘트가 있으면 재귀적으로 출력
            if (!commentDto.getChildren().isEmpty()) {
                printChildComments(commentDto.getChildren());
            }
        }
    }
    
    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
}
