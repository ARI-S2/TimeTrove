package com.timetrove.Project.dto;

import com.timetrove.Project.domain.Board;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {
    private Long no;

    @NonNull
    private String subject;

    @NonNull
    private String content;

    @NonNull
    private String createdAt;

    @NonNull
    private Long userCode;

    private String nickname;
    private String profileImg;

    public static BoardDto convertBoardToDto(Board board) {
        return BoardDto.builder()
                .no(board.getNo())
                .subject(board.getSubject())
                .content(board.getContent())
                .createdAt(board.getCreatedAt().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .userCode(board.getUser().getUserCode())
                .nickname(board.getUser().getKakaoNickname())
                .profileImg(board.getUser().getKakaoProfileImg())
                .build();
    }
}

