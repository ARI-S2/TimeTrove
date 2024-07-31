package com.timetrove.Project.dto;

import com.timetrove.Project.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    @NonNull
    private Long no;
    @NonNull
    private String content;
    @NonNull
    private Long userCode;
    
    private String nickname;
    private String profileImg;
    private Long parentId;
    private String subject;
    @Builder.Default
    private List<CommentDto> children = new ArrayList<>();
    private String createdAt;

    public static CommentDto convertCommentToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .no(comment.getBoard().getNo())
                .content(comment.getContent())
                .userCode(comment.getUser().getUserCode())
                .nickname(comment.getUser().getKakaoNickname())
                .profileImg(comment.getUser().getKakaoProfileImg())
                .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                .subject(comment.getBoard().getSubject())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
