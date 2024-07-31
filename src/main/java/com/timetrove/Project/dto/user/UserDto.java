package com.timetrove.Project.dto.user;

import java.sql.Timestamp;


import com.timetrove.Project.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long userCode;
    private String kakaoProfileImg;
    private String kakaoNickname;
    private String userRole;
    private Timestamp createTime;

    // 정적 메서드로 엔티티를 DTO로 변환
    public static UserDto convertUserToDto(User user) {
        return UserDto.builder()
                .userCode(user.getUserCode())
                .kakaoProfileImg(user.getKakaoProfileImg())
                .kakaoNickname(user.getKakaoNickname())
                .userRole(user.getUserRole())
                .createTime(user.getCreateTime())
                .build();
    }
}
